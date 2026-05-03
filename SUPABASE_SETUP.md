# Supabase setup for Kin

Kin uses Supabase for **authentication**, **per-family row-level security**,
and **private file storage** with signed URLs. The previous "anyone with the
invite code wins" model has been replaced with real auth + RLS.

## 1. Create a Supabase project

1. Sign in at [supabase.com](https://supabase.com).
2. Create a new project. Wait ~1 minute for it to provision.
3. Go to **Project Settings → API** and copy:
   - **Project URL** → `SUPABASE_URL`
   - **Project API keys → anon public** → `SUPABASE_ANON_KEY`

Paste those values into
`android/app/src/main/java/com/rork/kin/data/SupabaseConfig.kt`.

> Never paste the `service_role` key into the app. It bypasses RLS.

## 2. Authentication

In **Authentication → Providers**, keep **Email** enabled. Disable any
provider you don't use. For production:

- Turn **Confirm email** ON.
- Set **Minimum password length** to 8.
- Add your domain to **Site URL** if you also run a web client.
- Optionally enable **rate limiting** under Project Settings → Auth.

## 3. Schema, indices, and RLS

Open **SQL Editor → New query** and run this once:

```sql
-- =========================================================
-- families: one row per family
-- =========================================================
create table if not exists families (
  id uuid primary key default gen_random_uuid(),
  name text not null check (char_length(name) between 1 and 60),
  invite_code text not null unique check (char_length(invite_code) between 6 and 40),
  created_by uuid not null references auth.users(id) on delete cascade,
  created_at timestamptz not null default now()
);

-- =========================================================
-- family_members: who belongs to which family + their role
-- =========================================================
create type family_role as enum ('admin', 'member', 'viewer');

create table if not exists family_members (
  family_id uuid not null references families(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  role family_role not null default 'member',
  display_name text not null check (char_length(display_name) between 1 and 60),
  relationship text default '' check (char_length(relationship) <= 30),
  initials text default '' check (char_length(initials) <= 4),
  avatar_color bigint default 0,
  joined_at timestamptz not null default now(),
  primary key (family_id, user_id)
);

create index if not exists family_members_user_idx
  on family_members (user_id);

-- =========================================================
-- family_photos: every photo belongs to exactly one family
-- =========================================================
create table if not exists family_photos (
  id uuid primary key default gen_random_uuid(),
  family_id uuid not null references families(id) on delete cascade,
  invite_code text not null,
  local_id text not null,
  author_id uuid not null references auth.users(id) on delete cascade,
  author_name text not null check (char_length(author_name) <= 60),
  author_initials text not null check (char_length(author_initials) <= 4),
  author_color bigint not null,
  caption text not null default '' check (char_length(caption) <= 500),
  image_url text not null check (char_length(image_url) <= 1000),
  taken_on date not null default current_date,
  created_at timestamptz not null default now()
);

create index if not exists family_photos_family_idx
  on family_photos (family_id, created_at desc);
create index if not exists family_photos_invite_idx
  on family_photos (invite_code, created_at desc);

-- =========================================================
-- helper: is the caller a member of this family?
-- =========================================================
create or replace function is_family_member(fid uuid)
returns boolean language sql stable security definer as $$
  select exists (
    select 1 from family_members
    where family_id = fid and user_id = auth.uid()
  );
$$;

-- =========================================================
-- enable RLS on everything
-- =========================================================
alter table families        enable row level security;
alter table family_members  enable row level security;
alter table family_photos   enable row level security;

-- =========================================================
-- families: members can read their family; only the creator can update/delete it
-- =========================================================
drop policy if exists "families read"   on families;
drop policy if exists "families create" on families;
drop policy if exists "families update" on families;
drop policy if exists "families delete" on families;

create policy "families read" on families
  for select using (is_family_member(id));

create policy "families create" on families
  for insert with check (auth.uid() = created_by);

create policy "families update" on families
  for update using (auth.uid() = created_by) with check (auth.uid() = created_by);

create policy "families delete" on families
  for delete using (auth.uid() = created_by);

-- =========================================================
-- family_members: members can see each other; users insert their own row
-- (joining a family by invite_code happens server-side via an RPC, see below)
-- =========================================================
drop policy if exists "members read"   on family_members;
drop policy if exists "members insert" on family_members;
drop policy if exists "members update" on family_members;
drop policy if exists "members delete" on family_members;

create policy "members read" on family_members
  for select using (is_family_member(family_id));

create policy "members insert" on family_members
  for insert with check (user_id = auth.uid());

create policy "members update" on family_members
  for update using (user_id = auth.uid()) with check (user_id = auth.uid());

create policy "members delete" on family_members
  for delete using (
    user_id = auth.uid()
    or exists (
      select 1 from families f
      where f.id = family_members.family_id and f.created_by = auth.uid()
    )
  );

-- =========================================================
-- family_photos: only members can read; only members can insert as themselves;
-- only the author can update/delete their own photo.
-- =========================================================
drop policy if exists "photos read"   on family_photos;
drop policy if exists "photos insert" on family_photos;
drop policy if exists "photos update" on family_photos;
drop policy if exists "photos delete" on family_photos;

create policy "photos read" on family_photos
  for select using (is_family_member(family_id));

create policy "photos insert" on family_photos
  for insert with check (
    is_family_member(family_id) and author_id = auth.uid()
  );

create policy "photos update" on family_photos
  for update using (author_id = auth.uid()) with check (author_id = auth.uid());

create policy "photos delete" on family_photos
  for delete using (author_id = auth.uid());

-- =========================================================
-- secure "join by invite_code" RPC
-- (lets a user attach themselves to a family without leaking other rows)
-- =========================================================
create or replace function join_family(code text, name text, rel text default '')
returns uuid
language plpgsql security definer as $$
declare
  fid uuid;
begin
  if auth.uid() is null then
    raise exception 'not authenticated';
  end if;
  select id into fid from families where invite_code = code limit 1;
  if fid is null then
    raise exception 'invalid invite code';
  end if;
  insert into family_members(family_id, user_id, role, display_name, relationship)
  values (fid, auth.uid(), 'member', left(coalesce(name, ''), 60), left(coalesce(rel, ''), 30))
  on conflict (family_id, user_id) do update
    set display_name = excluded.display_name,
        relationship = excluded.relationship;
  return fid;
end;
$$;

revoke all on function join_family(text, text, text) from public;
grant execute on function join_family(text, text, text) to authenticated;
```

## 4. Private storage bucket + RLS

1. **Storage → Create bucket**:
   - Name: `kin-photos`
   - **Public bucket: OFF** (private — reads via signed URLs only).
2. **Storage → Policies → New policy** (for the `kin-photos` bucket):

```sql
-- Authenticated members can upload only into their family's folder.
-- Convention: object path = "<invite_code>/<uuid>.jpg".
create policy "kin storage upload"
on storage.objects for insert
to authenticated
with check (
  bucket_id = 'kin-photos'
  and exists (
    select 1 from families f
    join family_members m on m.family_id = f.id
    where m.user_id = auth.uid()
      and split_part(name, '/', 1) = lower(replace(f.invite_code, ' ', '-'))
  )
);

create policy "kin storage read"
on storage.objects for select
to authenticated
using (
  bucket_id = 'kin-photos'
  and exists (
    select 1 from families f
    join family_members m on m.family_id = f.id
    where m.user_id = auth.uid()
      and split_part(name, '/', 1) = lower(replace(f.invite_code, ' ', '-'))
  )
);

create policy "kin storage delete own"
on storage.objects for delete
to authenticated
using (
  bucket_id = 'kin-photos'
  and owner = auth.uid()
);
```

## 5. What this gives you

| Layer | Protection |
|---|---|
| Transport | TLS-only (network security config); cleartext disabled. |
| Auth | Email + password via Supabase GoTrue; JWTs in EncryptedSharedPreferences. |
| Authorisation | Per-table RLS keyed off `family_members.user_id = auth.uid()`. |
| Storage | Private bucket; reads via 60s signed URLs; writes scoped to caller's family path. |
| Local data | EncryptedSharedPreferences (AES-256-GCM, Android Keystore). |
| Backups | Auto-backup + device transfer disabled — secrets never leave the device. |
| Input | Length caps + unicode sanitisation client- and server-side. |
| Invites | Cryptographic 16-char codes; share code privately. |

## 6. Done

Rebuild Kin. Sign up with email/password, create a family (you become the
creator + first member), then invite others by sharing the invite code.
Other members open the app, sign up, paste the code — and only then can
they see your family's photos.
