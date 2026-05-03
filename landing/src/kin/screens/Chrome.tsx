import type { ReactNode } from "react";
import StatusBar from "../StatusBar";

export function ScreenShell({
  children,
  className = "",
}: {
  children: ReactNode;
  className?: string;
}) {
  return (
    <div className={`relative h-full w-full paper-bg ${className}`}>
      <StatusBar />
      <div className="relative h-[calc(100%-22px)] overflow-hidden">{children}</div>
    </div>
  );
}

export function TopBar({
  title,
  onMenu = true,
  bell = true,
}: {
  title: string;
  onMenu?: boolean;
  bell?: boolean;
}) {
  return (
    <div className="flex items-center justify-between px-4 pt-2 pb-3">
      <button
        type="button"
        aria-label="Menu"
        className="grid h-9 w-9 place-items-center rounded-full bg-polaroid text-ink-mocha shadow-soft"
      >
        {onMenu ? (
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M3 4h10M3 8h10M3 12h10" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" />
          </svg>
        ) : (
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
            <path d="M9 2 4 7l5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        )}
      </button>
      <div className="flex flex-col items-center">
        <span className="text-[10px] uppercase tracking-[0.2em] text-ink-faded">our family</span>
        <span className="font-serif text-[15px] text-ink">{title}</span>
      </div>
      <button
        type="button"
        aria-label="Notifications"
        className="relative grid h-9 w-9 place-items-center rounded-full bg-polaroid text-ink-mocha shadow-soft"
      >
        {bell ? (
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M4 11V7.5A4 4 0 0 1 8 3.5v0a4 4 0 0 1 4 4V11l1 1H3l1-1Z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" />
            <path d="M6.5 13a1.5 1.5 0 0 0 3 0" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
          </svg>
        ) : null}
        <span className="absolute right-1.5 top-1.5 h-1.5 w-1.5 rounded-full bg-terracotta" />
      </button>
    </div>
  );
}

export function BottomTabs({ active }: { active: "home" | "albums" | "memories" | "you" }) {
  const tabs = [
    { id: "home" as const, label: "Home", icon: HomeIcon },
    { id: "albums" as const, label: "Albums", icon: AlbumIcon },
    { id: "plus" as const, label: "", icon: PlusIcon, mid: true },
    { id: "memories" as const, label: "Memories", icon: HeartIcon },
    { id: "you" as const, label: "You", icon: UserIcon },
  ];
  return (
    <div className="absolute bottom-0 left-0 right-0 z-20">
      <div className="mx-3 mb-3 flex items-center justify-around rounded-full border border-blush/80 bg-polaroid/95 py-1 shadow-polaroid backdrop-blur">
        {tabs.map((t) => {
          const Icon = t.icon;
          const isActive = t.id === active;
          if (t.mid) {
            return (
              <button
                type="button"
                key={t.id}
                aria-label="Add photo"
                className="grid h-11 w-11 -translate-y-3 place-items-center rounded-full bg-terracotta-gradient text-polaroid shadow-soft"
              >
                <Icon />
              </button>
            );
          }
          return (
            <button
              type="button"
              key={t.id}
              className={`flex flex-col items-center gap-0.5 px-2 py-1.5 transition ${isActive ? "text-terracotta" : "text-ink-faded"}`}
            >
              <Icon />
              <span className="text-[9px] font-medium tracking-wide">{t.label}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}

function HomeIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <path d="m2 7 6-5 6 5v6a1 1 0 0 1-1 1h-3v-4H6v4H3a1 1 0 0 1-1-1V7Z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" />
    </svg>
  );
}
function AlbumIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <rect x="2.5" y="2.5" width="11" height="11" rx="1.5" stroke="currentColor" strokeWidth="1.4" />
      <path d="M2.5 11 6 8l3 3 1.5-1.5L13.5 12" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" />
      <circle cx="6" cy="6" r="1" fill="currentColor" />
    </svg>
  );
}
function HeartIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <path d="M8 13s-5-3.2-5-7a3 3 0 0 1 5-2 3 3 0 0 1 5 2c0 3.8-5 7-5 7Z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" />
    </svg>
  );
}
function UserIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <circle cx="8" cy="6" r="2.5" stroke="currentColor" strokeWidth="1.4" />
      <path d="M3 13c1-2.2 3-3.3 5-3.3s4 1.1 5 3.3" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
    </svg>
  );
}
function PlusIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
      <path d="M9 3v12M3 9h12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </svg>
  );
}
