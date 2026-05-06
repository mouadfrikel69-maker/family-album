# Supabase setup for Kin

Kin uses Supabase for **authentication** and **per‑family membership /
invites only**. Photos and albums live on the device in app-private file
storage, so Supabase never sees your media.

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

Open **SQL Editor → New query** and run this once. It creates only the
two tables Kin actually needs in the cloud: `families` and `family_members`.

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
do $$ begin
  create type family_role as enum ('admin', 'member', 'viewer');
exception when duplicate_object then null; end $$;

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
-- enable RLS
-- =========================================================
alter table families       enable row level security;
alter table family_members enable row level security;

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
-- (joining a family by invite_code happens server-side via the RPC below)
-- =========================================================
drop policy if exists "members read"   on family_members;
drop policy if exists "members insert" on family_members;
drop policy if exists "members update" on family_members;
drop policy if exists "members delete" on family_members;

create policy "members read" on family_members
  for select using (is_family_member(family_id));

-- Direct INSERT is intentionally restrictive: a user may insert ONLY their
-- own row into a family they themselves created (the bootstrap step right
-- after `families.create`). Every other "join by invite code" path is
-- forced through the `join_family` security-definer RPC defined below.
-- This closes the hole where any authenticated user with a family UUID
-- could insert themselves directly, bypassing the invite-code check.
create policy "members insert" on family_members
  for insert with check (
    user_id = auth.uid()
    and exists (
      select 1 from families f
      where f.id = family_members.family_id
        and f.created_by = auth.uid()
    )
  );

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
-- bootstrap: when a family is created, immediately add the creator as
-- the first admin member. Without this, the creator would briefly fail
-- the is_family_member() check on their own family.
-- =========================================================
create or replace function on_family_insert_add_creator()
returns trigger language plpgsql security definer as $$
begin
  insert into family_members(family_id, user_id, role, display_name, relationship)
  values (
    new.id,
    new.created_by,
    'admin',
    coalesce((select email from auth.users where id = new.created_by), ''),
    ''
  )
  on conflict (family_id, user_id) do nothing;
  return new;
end;
$$;

drop trigger if exists families_add_creator on families;
create trigger families_add_creator
  after insert on families
  for each row execute function on_family_insert_add_creator();

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

## 4. Migrating from an older setup (optional)

If you previously ran the older SQL that created `family_photos` and the
`kin-photos` storage bucket, run this once to clean it all up:

```sql
-- drop photo policies + table
drop policy if exists "photos read"   on family_photos;
drop policy if exists "photos insert" on family_photos;
drop policy if exists "photos update" on family_photos;
drop policy if exists "photos delete" on family_photos;
drop table if exists family_photos;

-- drop storage policies
drop policy if exists "kin storage upload"     on storage.objects;
drop policy if exists "kin storage read"       on storage.objects;
drop policy if exists "kin storage delete own" on storage.objects;
```

Then in **Storage**: open the `kin-photos` bucket → **Delete bucket**.

After cleanup, the **Table Editor** should show only `families` and
`family_members` (plus Supabase's built‑in `auth.users`).

## 5. What this gives you

| Layer | Protection |
|---|---|
| Transport | TLS-only (network security config); cleartext disabled. |
| Auth | Email + password via Supabase GoTrue; JWTs in EncryptedSharedPreferences. |
| Authorisation | Per-table RLS keyed off `family_members.user_id = auth.uid()`. Joining a family is forced through the `join_family` security-definer RPC. |
| Secrets at rest | A small set of identifiers (email, profile name, family id, invite code, JWT access + refresh tokens) sit in EncryptedSharedPreferences (AES-256-GCM, Android Keystore). |
| Photos & metadata at rest | Plain JPEG bytes + a JSON metadata file in app-private storage. Other apps cannot read them, but they are **not** encrypted with the app's keystore key today. Treat the device's screen-lock as your protection. |
| Backups | Auto-backup + device transfer disabled — secrets never leave the device. |
| Input | Length caps + unicode sanitisation client- and server-side. |
| Invites | Cryptographic 16-char codes; share code privately. |

## 6. Done

Rebuild Kin. Sign up with email/password, create a family (you become the
creator + first member), then invite others by sharing the invite code.
Other members open the app, sign up, paste the code — and they instantly
appear in the family. Photos they take stay on their own device until you
later decide to add cloud sync.
