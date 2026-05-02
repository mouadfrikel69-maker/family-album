# Remove demo data and wire the app to real Supabase accounts

## What's happening today
The members ("Sarah", "Eleanor", "Frank", "Tom", "Lily", "Noah") and all the photos, likes, comments and notifications you see are **placeholder demo content** baked into the app. It's there so the screens look alive on first launch, but it's not connected to any real account.

## What will change

### Start empty, fill with real data
- All hardcoded family members, photos, albums, comments, likes and notifications will be removed.
- On first launch the app will be completely empty until you (or someone you invite) actually does something.

### Real you, from sign‑up
- During sign‑up / onboarding you'll be asked for your **name**, **relationship tag** (Mom, Dad, Grandma…) and an optional **avatar photo**.
- This profile is saved to your Supabase account and used everywhere you currently see "You / Dad".

### Real family, from invites only
- Members list will only show people who actually joined your family through your invite code, QR code or shareable link.
- When someone joins, everyone in the family sees them appear automatically.

### Real activity, in real time
- Photos, albums, likes, comments and notifications all come from Supabase.
- A new photo from Mom shows up on your feed within seconds. A like or comment updates live.
- "On this day" memories pull only from your family's real past photos.

### Warm empty states
Before there's any real content, each screen shows a friendly illustrated empty state with a clear next step:
- **Home feed** — "No memories yet. Share your first photo or invite your family." with **Add photo** and **Invite family** buttons.
- **Albums** — "Create your first album to start collecting memories together."
- **Memories / Calendar** — "Your On This Day memories will appear here as your family adds photos."
- **Family members** — "It's just you for now. Invite your family to start sharing."
- **Notifications** — "All quiet. We'll let you know when family shares something."

### Profile, editable later
Your name, relationship and avatar can be edited any time from the Profile screen — changes sync to everyone in the family.

## Pages affected
- **Sign‑up / onboarding** — new step to collect name, relationship, optional avatar.
- **Home feed**, **Albums**, **Memories**, **Family members**, **Notifications**, **Profile** — all switch from demo data to live Supabase data with empty states.
- **Photo detail, comments, likes** — only show real interactions from real members.

## After this is done
The very first time you open the app you'll see your own name, an empty feed, and a big invitation to add your first photo or invite your family. Everything that appears after that will be 100% real.