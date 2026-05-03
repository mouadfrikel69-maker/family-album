# Kin landing page

A static marketing site for the **Kin** family album Android app, built with Vite + React + TypeScript + Tailwind CSS v4 + Framer Motion.

The page recreates Kin's warm paper-album aesthetic on the web — every feature
(Hero walkthrough, Invite, Create Album, Add Members, Upload) is shown inside a
phone mockup with looping animations that mirror the actual Compose UI in
[`/android`](../android).

## Local development

```bash
cd landing
pnpm install
pnpm dev          # starts vite at http://localhost:5173
pnpm lint         # eslint (matches CI)
pnpm build        # tsc -b && vite build (matches CI)
pnpm preview      # serve the production build locally
```

## Folder layout

```
landing/
├── index.html                 — root HTML, fonts, meta
├── src/
│   ├── App.tsx                — composes all sections
│   ├── index.css              — Tailwind v4 theme + Kin palette
│   ├── components/            — Wordmark, Tagline, Polaroid, PhoneFrame, …
│   ├── screens/               — In-phone mocks (HomeFeed, Albums, Invite, …)
│   ├── sections/              — Page sections (Hero, Walkthrough, Footer, …)
│   └── data/sampleData.ts     — Sample family used inside the mocks
└── public/favicon.svg         — Kin "k" badge
```

## Design tokens

The landing page mirrors the palette and type system from
[`android/app/src/main/java/com/rork/kin/ui/theme/Color.kt`](../android/app/src/main/java/com/rork/kin/ui/theme/Color.kt)
exactly (CreamPaper, Terracotta, BlushPink, WashiTan, SageMist, InkBrown, …).

Typography:
- **Fraunces** — italic serif headlines (mirrors the in-app `displaySmall`).
- **Inter** — body sans-serif (mirrors `bodyLarge`, `labelMedium`).
- **Caveat** — script captions (mirrors `CaptionScript`).
