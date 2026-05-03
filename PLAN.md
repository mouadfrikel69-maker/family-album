# Add a complete KIN_APP_SPEC.md with the full app blueprint

I'll create a single comprehensive Markdown file at the project root called **`KIN_APP_SPEC.md`** that captures everything needed to rebuild the Kin family album app from scratch — features, design, screens, data, security, and system design.

## What the document will cover

- **Overview & vision** — what Kin is, who it's for, the "private warm scrapbook" promise.
- **Feature list** — onboarding, email auth, profile setup, create/join family, invite via code + QR + share link, home feed with likes & comments, albums, memories (this-week-in-prior-years), members directory, notifications, settings, photo detail, upload from gallery or camera, sign-out.
- **Screens & user flows** — every screen, what it shows, how the user moves between them, and the full auth → profile → family → main flow.
- **Design system** — the warm paper-album aesthetic, full colour palette with hex codes, typography (serif display + sans body + script captions), polaroid cards, washi-tape, speckled paper background, button styles, avatars, deterministic accent palette, motion (press-spring, slide transitions).
- **Information architecture** — bottom tabs (Home / Albums / Memories / You), drawer (members, invite, settings, sign-out), upload sheet, dialogs.
- **Data model** — Member, Family, Photo, Album, Comment, Notification, Roles.
- **Local-first storage** — encrypted preferences for secrets/profile/family, on-device photo files in app-private storage with JSON metadata, capture staging folder.
- **Backend (Supabase) scope** — only auth + `families` + `family_members` + helper function + `join_family` RPC + RLS policies; the exact SQL to run.
- **Security & privacy** — TLS-only, no cleartext, no auto-backup, no device-transfer, FLAG_SECURE, EncryptedSharedPreferences (AES-256-GCM), input sanitisation rules, password policy, invite-code entropy, per-email login rate limiter with exponential lockout, system trust anchors only.
- **System design diagram (text)** — client layers, Supabase boundary, what never leaves the device.
- **Tech stack** — Kotlin, Jetpack Compose, Material 3, Navigation Compose, ViewModel + StateFlow, Ktor, kotlinx-serialization, Coil, Koin, ZXing, AndroidX Security Crypto, FileProvider, Photo Picker, system camera intent.
- **Permissions & manifest** — INTERNET, CAMERA (optional feature), FileProvider, network security config, backup rules.
- **Project structure** — folder/file map of `data/`, `ui/screens/`, `ui/components/`, `ui/navigation/`, `ui/state/`, `ui/theme/`, plus `res/xml/` security files.
- **Build & run instructions** — Android Studio prerequisites, opening the `android/` project, paste Supabase URL + anon key into `SupabaseConfig.kt`, run on emulator or device.
- **Supabase setup checklist** — copy of the SQL, what tables/policies must exist, what must NOT exist (no photo table, no storage bucket).
- **Future / optional cloud sync** — note that photo cloud sync is intentionally out of scope.

The file will be written for someone rebuilding the same app — detailed enough to act as a one-stop spec, but skimmable with clear headings and tables.

## After approval
- Add `KIN_APP_SPEC.md` at the project root.
- No code changes. Existing `PLAN.md` and `SUPABASE_SETUP.md` remain untouched.