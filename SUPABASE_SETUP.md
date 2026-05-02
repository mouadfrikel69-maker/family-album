# Supabase setup for Kin

Kin uses Supabase to power **family sync** (members on different phones see
each other's photos) and **share links** (a public URL for one photo). There's
no real auth — devices joining the same `invite_code` belong to the same
"family" and can read/write that family's photos.

## 1. Create a Supabase project

1. Sign in at [supabase.com](https://supabase.com) (free tier is fine).
2. Create a new project. Wait ~1 minute for it to provision.
3. Go to **Project Settings → API** and copy:
   - **Project URL** → `SUPABASE_URL`
   - **Project API keys → anon public** → `SUPABASE_ANON_KEY`

Paste those two values into
`android/app/src/main/java/com/rork/kin/data/SupabaseConfig.kt`.

## 2. Run the SQL

Open **SQL Editor → New query** in Supabase and run this once:

```sql
create table if not exists family_photos (
  id uuid primary key default gen_random_uuid(),
  invite_code text not null,
  local_id text not null,
  author_name text not null,
  author_initials text not null,
  author_color bigint not null,
  caption text not null default '',
  image_url text not null,
  taken_on date not null default current_date,
  created_at timestamptz not null default now()
);

create index if not exists family_photos_invite_idx
  on family_photos (invite_code, created_at desc);

alter table family_photos enable row level security;

-- Open policies: anyone with the anon key + the family's invite_code can use it.
-- (No auth — privacy comes from keeping the invite_code secret within a family.)
drop policy if exists "kin read"   on family_photos;
drop policy if exists "kin write"  on family_photos;
create policy "kin read"  on family_photos for select using (true);
create policy "kin write" on family_photos for insert with check (true);
```

## 3. Create the storage bucket

1. Go to **Storage → Create bucket**.
2. Name: `kin-photos`
3. Toggle **Public bucket** ON. Click **Create**.
4. Open the new bucket → **Policies** → **New policy** → **For full customization**:

   - Policy name: `kin uploads`
   - Allowed operation: `INSERT`
   - Target roles: `anon, authenticated`
   - USING / WITH CHECK expression: `bucket_id = 'kin-photos'`

   Repeat with operation `SELECT` (and same expression) so reads work too.

## 4. Done

Rebuild Kin. When a family member uploads a photo it's pushed to your
Supabase project; pulling to refresh on another device sees the same photos.
The Share button on a photo copies a public URL and opens the system share
sheet.

> Privacy note: anyone with the `invite_code` can read all photos for that
> family. Keep invite codes private. For stricter privacy, swap to Supabase
> Auth + per-user RLS later.
