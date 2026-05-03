# Kin — Family Album App: Full Specification

A complete blueprint for rebuilding **Kin**, a private, warm, paper-scrapbook-style family photo app for Android. This document captures every feature, screen, design choice, data model, security control, and backend rule used in the app.

> **One-line pitch:** A private, ad-free photo album where only your family can see your photos — kept on your device, not in the cloud.

---

## 1. Vision & product promise

| Pillar | Promise |
|---|---|
| **Private** | Photos and albums never leave the device. The cloud only knows who is in which family. |
| **Warm** | A hand-made paper-album aesthetic — terracotta accents on cream, polaroids, washi tape, italic captions. |
| **Family-only** | One family per account. Members join by an invite code shared privately. |
| **Ad-free, calm** | No feeds from strangers, no notifications from outside the family, no ads, no tracking. |

Tagline: *“private · ad-free · just family.”*

---

## 2. Feature list

### Onboarding & account
- Welcome / value-pitch onboarding screen.
- Email + password sign-up and sign-in (Supabase Auth / GoTrue).
- Session restore on app launch; encrypted token storage.
- Sign-out clears local secrets and returns to onboarding.
- Per-email login rate limiter with exponential lockout (defence-in-depth on top of Supabase rate limits).

### Profile setup
- Capture display name (1–60 chars, sanitised).
- Capture relationship label (e.g. “Mom”, “Brother”) — optional, ≤30 chars.
- Auto-derive 1–2 letter initials.
- Deterministic accent colour from name hash (8-tone warm palette).
- Avatar shown across the app.

### Family
- **Create** a family: pick a name, app generates a cryptographic 16-char invite code (Crockford alphabet, dash-grouped).
- **Join** a family: paste / scan an invite code.
- One family per user per device.

### Invite
- View invite code.
- Render the invite as a QR code (vector-clean, drawn on Compose Canvas via ZXing).
- Copy invite link to clipboard (`https://kin.family/join/{code}`).
- Share via OS share sheet (`Intent.ACTION_SEND`).
- Quick mail action.

### Home feed
- Reverse-chronological list of family photos as polaroid cards.
- Each card: photo, optional handwritten-style caption, author name + relative timestamp, like and comment buttons.
- Like (toggle a heart) and comment (threaded list).
- Top bar: hamburger → drawer, family name banner, bell → notifications.
- Horizontal strip of family-member avatars → opens Members screen.

### Albums
- Grid of albums, each with its own deterministic accent colour.
- Album detail screen showing the album's photos.
- Create new album from a dialog launched in the main scaffold.

### Memories
- “This week, in previous years” — surfaces photos within ±3 days of today's date from prior years.
- Styled as anniversary throwbacks.

### Members directory
- List of all family members with avatar, name, relationship and role badge (Admin / Member / Viewer).
- Quick action to share invite.

### Notifications
- Local in-app notifications:
  - **NewPhoto** — someone added a photo.
  - **Comment** — someone commented on your photo.
  - **Like** — someone liked your photo.
  - **NewMember** — someone joined the family.
  - **Memory** — a memory surfaced today.

### Photo detail
- Full-bleed photo, caption, like, threaded comments, “add comment” input.

### Upload
- **From gallery** — Android Photo Picker, up to 10 photos per upload, no permissions needed.
- **From camera** — system camera intent (`ActivityResultContracts.TakePicture`) writing into a FileProvider-backed staging file. Permission is requested at the moment of capture, with rationale + “open Settings” deep link if denied.
- Optional caption.
- Optional album selector.
- Upload progress bar.

### Settings
- Static settings screen (toggles + rows). Sign-out lives in the drawer.

### Privacy hardening
- `FLAG_SECURE` blocks screenshots and the recents thumbnail of family photos.
- All secrets in `EncryptedSharedPreferences`.
- Auto-backup, cloud-backup and device-transfer are all explicitly excluded.
- Cleartext HTTP disabled; system trust anchors only.

---

## 3. User flows

### First launch
```
Onboarding → Auth (sign up / sign in) → Profile setup → Create or Join family → Main app
```

### Create family
```
Profile setup → "Create" tab → enter family name → Kin generates 16-char invite code → Main → Drawer → Invite (share QR / link / code)
```

### Join family
```
Profile setup → "Join" tab → paste invite code → Kin calls join_family RPC on Supabase → user becomes a member → Main
```

### Upload photo
```
Main → bottom-bar "+" → choose Gallery or Camera
  Gallery → Photo Picker → up to 10 → Upload screen → caption + album → Save
  Camera  → permission check → system camera → captured file → Upload screen → caption + album → Save
```

### Sign-out
```
Drawer → Sign out → SupabaseAuth.signOut → SecureStore.clear → reset state → back to Onboarding (popUpTo(0))
```

---

## 4. Information architecture

- **Bottom tabs:** Home · Albums · Memories · You (Profile).
- **Drawer (left):** Members · Invite · Settings · Sign out.
- **Bottom bar centre action:** "+" opens the upload chooser sheet (Gallery / Camera).
- **Modal dialogs:** New album.
- **Full-screen routes:** Photo detail, Album detail, Members, Notifications, Settings, Invite, Upload (when active).

Navigation is a `NavHost` with `rememberNavController()` and a single hoisted `AppViewModel`. Slide-left enter / slide-right pop transitions globally.

| Route | Screen |
|---|---|
| `onboarding` (start) | Onboarding |
| `auth` | Email auth |
| `profile_setup` | Profile setup |
| `family` | Create / join family |
| `main` | Main scaffold (tabs + drawer + upload) |
| `photo/{id}` | Photo detail |
| `album/{id}` | Album detail |
| `members` | Members directory |
| `notifications` | Notifications |
| `settings` | Settings |
| `invite` | Invite (QR + share) |

---

## 5. Design system

Aesthetic: **“open scrapbook on warm paper.”** All-light theme — the brand *is* the warmth, regardless of system setting.

### 5.1 Colour palette

| Role | Name | Hex |
|---|---|---|
| Surface base | CreamPaper | `#FAF4EC` |
| Surface elev. | WarmWhite | `#FFFBF5` |
| Polaroid card | PolaroidWhite | `#FFFCF6` |
| Primary | Terracotta | `#C76B4A` |
| Primary deep | TerracottaDeep | `#A04E33` |
| Accent | DustyRose | `#E8B4A0` |
| Accent | BlushPink | `#F4DBCF` |
| Accent | WashiTan | `#D4A574` |
| Accent | SageMist | `#B8C4A8` |
| Ink (text) | InkBrown | `#3D2E26` |
| Ink (muted) | Mocha | `#6B5544` |
| Ink (softer) | SoftBrown | `#8B7560` |
| Ink (faded) | FadedInk | `#B5A696` |
| Shadow | ShadowSoft | `#1A3D2E26` (10% InkBrown) |

`AppTheme` forces the light scheme: `primary=Terracotta`, `background=CreamPaper`, `onBackground=InkBrown`.

### 5.2 Typography

- **Display / Headline:** serif. `displaySmall` is **italic** for soft moments.
- **Title / Body / Label:** sans-serif.
- **CaptionScript:** italic serif used for hand-written-feeling photo captions.

### 5.3 Components & motion

- **Polaroid** card — PolaroidWhite, 4 dp radius, 8 dp shadow, 10 dp inner padding, optional washi-tape strip on top, slight rotation, press-spring scale (~0.97).
- **KinCard** — rounded card, 22 dp radius, optional BlushPink hairline border, 8 dp shadow.
- **KinPrimaryButton** — terracotta horizontal gradient (`#D17A5C → Terracotta → #B35A3D`), 56 dp tall, pill, press-spring, optional trailing icon.
- **KinSecondaryButton** — outlined PolaroidWhite chip with BlushPink border.
- **KinWordmark** — gradient circular “k” badge (Terracotta → DustyRose) + “kin” wordmark.
- **KinScreenHeader** — back-circle + centred wordmark + serif title + subtitle + script tagline.
- **KinThinDivider** — `— label —` BlushPink hairline divider.
- **KinPromiseStrip** — coloured-dot strip: *private · ad-free · just family*.
- **KinTagline** — heart icon + italic-script tagline.
- **PaperBackground** — CreamPaper base + radial warm glow at top + 80 deterministic seeded speckles drawn on Canvas.
- **Avatar** — circular, deterministic warm tint per member, white border, white initials at ~38% of diameter.

Motion: slide page transitions, press-spring on touchables, gentle `AnimatedContent` cross-fade between tabs.

### 5.4 Deterministic accent palette

8 warm tones used to colour avatars and album tiles. Index is derived from a stable hash of the name/id so a person always has the same colour.

---

## 6. Data model

```kotlin
enum class Role { Admin, Member, Viewer }

data class Member(
  val id: String,
  val name: String,
  val relationship: String,
  val avatarColor: Long,
  val initials: String,
  val role: Role,
)

data class Comment(
  val id: String,
  val authorId: String,
  val text: String,
  val createdAt: String, // ISO
)

data class Photo(
  val id: String,
  val authorId: String,
  val caption: String,
  val createdAt: String,
  val takenOn: String?,
  val albumIds: Set<String>,
  val likedBy: Set<String>,
  val comments: List<Comment>,
  val taggedMemberIds: Set<String>,
)

data class Album(
  val id: String,
  val name: String,
  val accentColor: Long,
  val photoIds: Set<String>,
  // computed: dateRangeLabel ("May 2026")
)

data class Family(
  val id: String,
  val name: String,
  val inviteCode: String,
  val createdAt: String,
)

enum class NotifKind { NewPhoto, Comment, Like, NewMember, Memory }

data class Notification(
  val id: String,
  val kind: NotifKind,
  val title: String,
  val body: String,
  val createdAt: String,
  val read: Boolean,
)
```

---

## 7. Local-first storage

### 7.1 Encrypted preferences (`SecureStore`)

`EncryptedSharedPreferences` (file `kin_secure_prefs`) using:
- Values: **AES-256-GCM**.
- Keys: **AES-256-SIV**.
- Master key: AndroidX `MasterKey` with `AES256_GCM` scheme, backed by Android Keystore.
- Falls back to in-memory map if Keystore init fails — **never to plaintext disk**.

Stored keys:
- `KEY_ACCESS_TOKEN`, `KEY_REFRESH_TOKEN`, `KEY_USER_ID`, `KEY_USER_EMAIL`
- `KEY_INVITE_CODE`, `KEY_FAMILY_ID`, `KEY_FAMILY_NAME`
- `KEY_PROFILE_NAME`, `KEY_PROFILE_REL`, `KEY_PROFILE_INITIALS`, `KEY_PROFILE_COLOR`
- Auth-rate-limiter state (hashed-email keys; no raw email at rest).

### 7.2 Photos on disk (`LocalPhotoStore`)

- Permanent files: `filesDir/photos/img_{uuid}.jpg`.
- Camera staging: `cacheDir/capture/cap_{ts}.jpg`.
- Metadata: `filesDir/user_photos.json` (kotlinx-serialization list of `StoredPhoto`).
- API:
  - `newCaptureTarget(ctx) → (File, Uri)` via FileProvider.
  - `importFromUri(uri)` — copies a Photo Picker URI into permanent storage.
  - `importFromCapture(file)` — moves staging file into permanent storage and deletes the temp.
  - `loadAll`, `saveAll`, `delete`, `toPhoto(StoredPhoto)`.

`StoredPhoto`: `id, path, caption, authorId, createdAtIso, takenOnIso, albumId`.

Photos are rendered with Coil `AsyncImage` against `file://` URLs.

---

## 8. Backend (Supabase) — minimal cloud surface

Supabase is **only** used for:
1. **Authentication** (email + password via GoTrue).
2. **Family membership** so the right people can see they belong to the same family.

It does **not** store photos, albums, captions, comments or likes. Those live on the device.

### 8.1 Tables that exist

- `families` — one row per family (`id`, `name`, `invite_code`, `created_by`, `created_at`).
- `family_members` — composite PK `(family_id, user_id)` with `role`, `display_name`, `relationship`, `initials`, `avatar_color`, `joined_at`.
- Built-in `auth.users` (managed by Supabase).

### 8.2 Tables/buckets that must NOT exist

- ❌ `family_photos` table.
- ❌ `kin-photos` Storage bucket.
- ❌ Any storage policies (`kin storage upload/read/delete own`).

### 8.3 Functions

- `is_family_member(fid uuid) returns boolean` — security-definer helper used by RLS.
- `join_family(code text, name text, rel text default '') returns uuid` — security-definer RPC that lets an authenticated user attach to a family by invite code without leaking other rows. `revoke all ... from public; grant execute ... to authenticated;`.

### 8.4 Row-level security

RLS enabled on **both** tables.

- **`families`**
  - read: `is_family_member(id)`
  - insert: `auth.uid() = created_by`
  - update / delete: `auth.uid() = created_by`
- **`family_members`**
  - read: `is_family_member(family_id)`
  - insert: `user_id = auth.uid()`
  - update: `user_id = auth.uid()`
  - delete: own row, **or** the family creator may remove members.

### 8.5 SQL — single source of truth

```sql
-- families
create table if not exists families (
  id uuid primary key default gen_random_uuid(),
  name text not null check (char_length(name) between 1 and 60),
  invite_code text not null unique check (char_length(invite_code) between 6 and 40),
  created_by uuid not null references auth.users(id) on delete cascade,
  created_at timestamptz not null default now()
);

-- role enum
do $$ begin
  create type family_role as enum ('admin', 'member', 'viewer');
exception when duplicate_object then null; end $$;

-- family_members
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
create index if not exists family_members_user_idx on family_members (user_id);

-- helper
create or replace function is_family_member(fid uuid)
returns boolean language sql stable security definer as $$
  select exists (
    select 1 from family_members
    where family_id = fid and user_id = auth.uid()
  );
$$;

-- enable RLS
alter table families       enable row level security;
alter table family_members enable row level security;

-- families policies
drop policy if exists "families read"   on families;
drop policy if exists "families create" on families;
drop policy if exists "families update" on families;
drop policy if exists "families delete" on families;
create policy "families read"   on families for select using (is_family_member(id));
create policy "families create" on families for insert with check (auth.uid() = created_by);
create policy "families update" on families for update using (auth.uid() = created_by) with check (auth.uid() = created_by);
create policy "families delete" on families for delete using (auth.uid() = created_by);

-- family_members policies
drop policy if exists "members read"   on family_members;
drop policy if exists "members insert" on family_members;
drop policy if exists "members update" on family_members;
drop policy if exists "members delete" on family_members;
create policy "members read"   on family_members for select using (is_family_member(family_id));
create policy "members insert" on family_members for insert with check (user_id = auth.uid());
create policy "members update" on family_members for update using (user_id = auth.uid()) with check (user_id = auth.uid());
create policy "members delete" on family_members for delete using (
  user_id = auth.uid()
  or exists (select 1 from families f where f.id = family_members.family_id and f.created_by = auth.uid())
);

-- join-by-invite-code RPC
create or replace function join_family(code text, name text, rel text default '')
returns uuid language plpgsql security definer as $$
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

### 8.6 Auth provider settings

- **Email** provider enabled, all other providers disabled.
- **Confirm email** ON for production.
- **Minimum password length** 8.
- Server-side auth rate limiting enabled.

---

## 9. Security & privacy controls

| Layer | Control |
|---|---|
| Transport | TLS-only via `network_security_config.xml`. `usesCleartextTraffic="false"`. System trust anchors only — rejects user-installed CAs (MITM resistance on rooted devices). |
| Auth | Email + password via Supabase GoTrue. Tokens stored in `EncryptedSharedPreferences` only. Session restored on launch. |
| Authorisation | Per-table RLS keyed on `family_members.user_id = auth.uid()` via `is_family_member()`. Joining is mediated by the `join_family` RPC so users can't see other families. |
| Photos & albums | Stored **only** on device — `EncryptedSharedPreferences` for metadata about secrets, app-private `filesDir/photos/` for image bytes. Supabase never receives them. |
| Local data at rest | AES-256-GCM via Android Keystore (`MasterKey AES256_GCM`). |
| Backups | `allowBackup="false"`, `fullBackupContent` excludes everything, `dataExtractionRules` excludes both `cloud-backup` and `device-transfer`. Secrets never leave the device. |
| Screenshots | `FLAG_SECURE` set in `MainActivity` — blocks screenshots and recents-thumbnail leaks. |
| Input hygiene | `Validate` strips control chars, zero-width, bidi-override and BOM; enforces length caps everywhere (NAME 60, RELATIONSHIP 30, CAPTION 500, FAMILY 60, COMMENT 1000, EMAIL 254). |
| Password policy | 8–128 chars, must contain a letter and a digit; human-readable error message. |
| Invite-code entropy | `SecureRandom`, 16 chars from Crockford alphabet (no `0/O/1/I`), grouped `XXXX-XXXX-XXXX-XXXX`. ≈ 1.2 × 10²⁴ search space. Server checks uniqueness. |
| Brute-force resistance | `AuthRateLimiter`: per-email exponential lockout — 3 fails → 30 s, 5 → 2 m, 8 → 10 m, 12+ → up to 1 h cap. Hashed email key, no raw email at rest. |
| Privacy posture | No analytics, no ads, no third-party SDK telemetry. Only network calls are to your Supabase project. |

---

## 10. System design

```
┌─────────────────────────────────────────────────────────────────┐
│                          Android device                          │
│                                                                  │
│  ┌─────────────┐    ┌────────────────────────────────────────┐   │
│  │   UI tier   │    │  State tier (AppViewModel + StateFlow) │   │
│  │  Compose    │◄──►│  AppState { onboarded, authed, ...,    │   │
│  │  Material 3 │    │             family, members, photos }  │   │
│  └─────────────┘    └─────────────────┬──────────────────────┘   │
│                                       │                          │
│        ┌──────────────────────────────┼──────────────────────┐   │
│        │              Data tier (singletons)                 │   │
│        │  SecureStore   LocalPhotoStore   FamilyRepository   │   │
│        │  (encrypted)   (filesDir + JSON) (in-memory)        │   │
│        │  Validate      AuthRateLimiter   SupabaseAuth (Ktor)│   │
│        └────────────────────────────────────────┬────────────┘   │
│                                                 │                │
└─────────────────────────────────────────────────┼────────────────┘
                                                  │ HTTPS only
                                                  ▼
                            ┌──────────────────────────────────┐
                            │           Supabase               │
                            │   auth.users (GoTrue)            │
                            │   families                       │
                            │   family_members                 │
                            │   is_family_member()             │
                            │   join_family(code,name,rel)     │
                            │   RLS on every table             │
                            └──────────────────────────────────┘
```

What crosses the boundary: email, password, JWTs, family rows, member rows, invite codes.
What **never** crosses: photos, captions, likes, comments, albums, notifications.

---

## 11. Tech stack

| Area | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, Material Icons Extended |
| Navigation | Navigation Compose (single `NavHost`) |
| State | `ViewModel` + `MutableStateFlow` + `collectAsStateWithLifecycle` |
| Networking | Ktor (Android engine) + `kotlinx-serialization-json` + content negotiation |
| Images | Coil 3 (`coil-compose`, `coil-network-okhttp`) |
| DI | Koin (`koin-androidx-compose`) |
| QR | ZXing core (`com.google.zxing:core`) |
| Crypto | `androidx.security:security-crypto` (EncryptedSharedPreferences + MasterKey) |
| File sharing | `androidx.core.content.FileProvider` |
| Photo picking | `ActivityResultContracts.PickMultipleVisualMedia` (Android Photo Picker) |
| Camera | `ActivityResultContracts.TakePicture` (system intent — no CameraX preview) |
| Auth | Supabase GoTrue REST API (custom thin Ktor client) |
| Min / Target SDK | 24 / 36 (compileSdk 36) |
| Build | Gradle Kotlin DSL + Version Catalog |

Key versions (Version Catalog):
- AGP `8.13.2`, Kotlin `2.3.10`
- Compose BOM `2026.02.01`, Navigation Compose `2.9.7`
- Ktor `3.4.1`, Coil `3.4.0`, Koin `4.1.1`, ZXing `3.5.3`
- AndroidX Security Crypto `1.1.0-alpha06`

---

## 12. Permissions & manifest

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />

<uses-feature android:name="android.hardware.camera"     android:required="false" />
<uses-feature android:name="android.hardware.camera.any" android:required="false" />
```

Application flags:
- `allowBackup="false"`
- `fullBackupContent="@xml/backup_rules"`
- `dataExtractionRules="@xml/data_extraction_rules"`
- `usesCleartextTraffic="false"`
- `networkSecurityConfig="@xml/network_security_config"`

`FileProvider` declared with authority `${applicationId}.fileprovider`, paths from `@xml/file_paths` (exposes `files/photos/` and `cache/capture/`).

`res/xml/`:
- `backup_rules.xml` — exclude every domain (root / file / database / sharedpref / external).
- `data_extraction_rules.xml` — exclude everything from `cloud-backup` *and* `device-transfer`.
- `network_security_config.xml` — `cleartextTrafficPermitted=false`, system trust anchors only.
- `file_paths.xml` — `files-path name="photos" path="photos/"` and `cache-path name="capture" path="capture/"`.

---

## 13. Project structure

```
android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/rork/kin/
│       │   ├── MainActivity.kt              # FLAG_SECURE, edge-to-edge, AppTheme + AppNavigation
│       │   ├── data/
│       │   │   ├── Models.kt                # Member, Family, Photo, Album, Comment, Notification, Role, NotifKind
│       │   │   ├── FamilyRepository.kt      # in-memory currentUser + family
│       │   │   ├── Validate.kt              # email/password/invite/name sanitisation + invite-code generator
│       │   │   ├── SecureStore.kt           # EncryptedSharedPreferences wrapper
│       │   │   ├── SupabaseConfig.kt        # SUPABASE_URL + SUPABASE_ANON_KEY (paste-in)
│       │   │   ├── SupabaseAuth.kt          # Ktor GoTrue client (signUp/signIn/refresh/signOut/restore)
│       │   │   ├── AuthRateLimiter.kt       # per-email exponential lockout
│       │   │   └── LocalPhotoStore.kt       # on-device photo files + JSON metadata
│       │   └── ui/
│       │       ├── navigation/AppNavigation.kt
│       │       ├── state/AppViewModel.kt
│       │       ├── theme/{Color.kt, Type.kt, Theme.kt}
│       │       ├── components/
│       │       │   ├── Avatar.kt
│       │       │   ├── PaperBackground.kt
│       │       │   ├── Polaroid.kt
│       │       │   ├── QrCode.kt
│       │       │   └── KinDesign.kt         # Wordmark, KinCard, KinPrimaryButton, KinSecondaryButton, ScreenHeader, etc.
│       │       └── screens/
│       │           ├── OnboardingScreen.kt
│       │           ├── AuthScreen.kt
│       │           ├── ProfileSetupScreen.kt
│       │           ├── CreateJoinFamilyScreen.kt
│       │           ├── MainScaffold.kt      # tabs + drawer + upload chooser + new-album dialog
│       │           ├── HomeScreen.kt
│       │           ├── AlbumsScreen.kt
│       │           ├── AlbumDetailScreen.kt
│       │           ├── MemoriesScreen.kt
│       │           ├── ProfileScreen.kt
│       │           ├── MembersScreen.kt
│       │           ├── InviteScreen.kt
│       │           ├── NotificationsScreen.kt
│       │           ├── PhotoDetailScreen.kt
│       │           ├── SettingsScreen.kt
│       │           └── UploadScreen.kt
│       └── res/
│           ├── xml/{backup_rules, data_extraction_rules, network_security_config, file_paths}.xml
│           ├── mipmap-anydpi-v26/{ic_launcher, ic_launcher_round}.xml
│           └── values/{strings, themes}.xml
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/...
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 14. Build & run

### Prerequisites
- Android Studio (Iguana or newer recommended).
- JDK 11+ (Gradle is configured for `JavaVersion.VERSION_11`).
- An emulator or device on Android 7.0+ (API 24).

### Steps
1. Open Android Studio → **Open** → select the `android/` folder.
2. Let Gradle sync.
3. Open `app/src/main/java/com/rork/kin/data/SupabaseConfig.kt` and paste your Supabase **Project URL** and **anon public** key. (Leave blank to run in local-only mode — sign-in is skipped.)
4. Run the SQL in §8.5 in your Supabase **SQL Editor**.
5. In Supabase **Authentication → Providers**, keep **Email** on, others off; turn on **Confirm email** for production.
6. Press **Run ▶** in Android Studio.

> Never paste the `service_role` key into the app — it bypasses RLS.

---

## 15. Supabase setup checklist

- [ ] Create project → copy URL and anon key into `SupabaseConfig.kt`.
- [ ] Enable Email provider, set min password length to 8, turn on Confirm Email.
- [ ] Run the SQL from §8.5 once.
- [ ] In Table Editor, confirm only `families` and `family_members` exist (plus `auth.users`).
- [ ] Confirm RLS is **enabled** on both tables and the four policies per table are present.
- [ ] Confirm `is_family_member` and `join_family` exist under Database → Functions.
- [ ] Confirm there is **no** `family_photos` table and **no** `kin-photos` Storage bucket.

---

## 16. Out of scope (intentionally)

- Photo / album cloud sync — photos live only on device. (Multi-device syncing would require adding a server-side photo store with end-to-end encryption; this is intentionally not built.)
- Cross-family sharing or public feeds.
- Push notifications — only local in-app notifications.
- Web client.
- Analytics, ads, third-party SDKs.

---

## 17. Glossary

- **Invite code** — 16-char Crockford-alphabet string identifying a family, e.g. `K7M3-X9PQ-RT4N-Z2H8`.
- **Family creator** — the user who ran “Create family”; has elevated rights via RLS (`families.created_by = auth.uid()`).
- **Member** — a row in `family_members` linking a `user_id` to a `family_id` with a role.
- **Local-only photo** — image bytes stored under `filesDir/photos/`, metadata in `user_photos.json`. Never uploaded.

---

*This document is the canonical spec for the Kin app. If something in code disagrees with what's written here, treat the discrepancy as a bug to be reconciled.*
