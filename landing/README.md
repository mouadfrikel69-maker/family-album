# Kin landing page

A static marketing site for **Kin**, the private family photo album described
in [`KIN_APP_SPEC.md`](../KIN_APP_SPEC.md). The page is a single-route React +
Vite app that runs entirely on cream paper — every screen of the app is
re-built as a React component and walked through inside an animated phone
mockup (a Framer Motion replacement for screen-recorded GIFs).

## Sections

- **Hero** — headline + animated phone walking through Onboarding → Auth →
  Profile → Create-family → Home → Albums → Memories.
- **The whole app, page by page** — a larger continuous walkthrough that
  cycles through every Kin screen in order (the "mockup phone video").
- **Invite link** — Members → Invite QR → tap copy → "link copied" → share
  sheet → home page after the new member joins.
- **Create an album** — Albums → "+ new" → dialog opens → letter-by-letter
  type "Cabin" → tap create → new album highlighted in the grid.
- **Add members** — Family strip on Home → Members directory → Invite share
  sheet → Members directory with the new member fading in at the top.
- **Add a memory (upload)** — Home → Photo picker → caption types itself →
  album selector → upload progress fills → polaroid lands on the feed.
- **Memories & detail** — Home → Memories → Photo detail → Notifications.
- **The kin promise** — private · ad-free · just family pillars.
- **Built calmly** — a small Kotlin / Compose / Supabase tech strip.
- **Download CTA + footer**.

## Stack

- Vite + React + TypeScript
- Tailwind CSS 3 (Kin warm palette mapped to `cream`, `terracotta`, `blush`,
  `washi`, `sage`, `ink`, etc.)
- Framer Motion for all animations
- Local procedural SVG "photos" — no external image assets

## Develop

```sh
cd landing
pnpm install
pnpm dev
```

Open <http://localhost:5173>.

## Build

```sh
pnpm build
pnpm preview
```

`pnpm build` runs `tsc -b && vite build`. Static output goes to `landing/dist`.

## Deploy

The output of `pnpm build` is a fully static site. Drop `landing/dist` on any
static host (Netlify, Vercel, GitHub Pages, S3, Cloudflare Pages…). No backend
required — no API calls happen at runtime.
