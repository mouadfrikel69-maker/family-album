# Slim Supabase down to auth + family/invite only

Since photos and albums live on the device, your Supabase project only needs to handle **accounts** and **family membership / invites**. Here's exactly what to keep, what to delete, and the clean SQL to paste in.

## What stays in Supabase
- **Authentication** (Email + password) — unchanged.
- **`families`** table — one row per family (name, invite code, creator).
- **`family_members`** table — who belongs to which family, their role and display name.
- **`is_family_member`** helper function — used by security rules.
- **`join_family`** function — lets an invited person attach themselves to a family using the invite code.
- **Row‑level security policies** for `families` and `family_members` only.

## What to remove from Supabase
- **`family_photos`** table (and its 4 policies).
- **`kin-photos` Storage bucket** (and its 3 policies).
- Any leftover indices on `family_photos`.

## Step 1 — Clean up old objects (Table Editor / SQL Editor)
Run this once in **SQL Editor → New query** to drop everything photo‑ and storage‑related:

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

## Step 2 — Verify the tables that should remain
In **Table Editor** you should now see only:
- `families`
- `family_members`

…plus Supabase's built‑in `auth.users` (managed automatically — don't touch it).

## Step 3 — Make sure RLS + policies are in place
If you skipped the policy step before, paste this whole block into **SQL Editor** and run it. It is safe to re‑run.

```sql
-- enable row-level security
alter table families       enable row level security;
alter table family_members enable row level security;

-- helper: is the caller a member of this family?
create or replace function is_family_member(fid uuid)
returns boolean language sql stable security definer as $$
  select exists (
    select 1 from family_members
    where family_id = fid and user_id = auth.uid()
  );
$$;

-- families policies
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

-- family_members policies
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

-- secure join-by-invite-code RPC
create or replace function join_family(code text, name text, rel text default '')
returns uuid
language plpgsql security definer as $$
declare fid uuid;
begin
  if auth.uid() is null then raise exception 'not authenticated'; end if;
  select id into fid from families where invite_code = code limit 1;
  if fid is null then raise exception 'invalid invite code'; end if;
  insert into family_members(family_id, user_id, role, display_name, relationship)
  values (fid, auth.uid(), 'member', left(coalesce(name,''),60), left(coalesce(rel,''),30))
  on conflict (family_id, user_id) do update
    set display_name = excluded.display_name,
        relationship = excluded.relationship;
  return fid;
end;
$$;

revoke all on function join_family(text, text, text) from public;
grant execute on function join_family(text, text, text) to authenticated;
```

## Step 4 — Update the setup doc
I'll also trim `SUPABASE_SETUP.md` so it no longer mentions `family_photos`, the `kin-photos` bucket, or storage policies — matching the new "local‑only photos" model.

## After approval
1. Run the cleanup SQL (Step 1).
2. Delete the `kin-photos` bucket in Storage.
3. Run the verification SQL (Step 3) — re‑running it is harmless.
4. I'll update `SUPABASE_SETUP.md` to reflect the slimmer setup.