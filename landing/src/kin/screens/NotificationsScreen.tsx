import Avatar from "../Avatar";
import { ScreenShell } from "./Chrome";

const items = [
  { who: "Yara", what: "added a new photo", album: "Summer", ago: "12m" },
  { who: "Dad", what: "commented on your photo", album: undefined, ago: "1h" },
  { who: "Mom", what: "liked your photo", album: undefined, ago: "3h" },
  { who: "Adam", what: "joined the family", album: undefined, ago: "yesterday" },
  { who: "Memory", what: "this week, 2 years ago", album: undefined, ago: "today" },
];

export default function NotificationsScreen() {
  return (
    <ScreenShell>
      <div className="flex h-full flex-col px-5 pb-6 pt-4">
        <div className="flex items-center gap-3">
          <button className="grid h-9 w-9 place-items-center rounded-full bg-polaroid text-ink-mocha shadow-soft">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M9 2 4 7l5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </button>
          <span className="font-serif text-[15px] italic text-ink">a few quiet things</span>
        </div>
        <p className="mt-1 font-script text-[13px] text-ink-mocha">no ads. no strangers.</p>

        <div className="mt-4 flex flex-col gap-2">
          {items.map((it, i) => (
            <div
              key={i}
              className="flex items-center gap-3 rounded-2xl border border-blush bg-polaroid px-3 py-2.5 shadow-soft"
            >
              <Avatar name={it.who} size={36} />
              <div className="flex-1">
                <p className="text-[12px] text-ink">
                  <span className="font-semibold">{it.who}</span> {it.what}
                </p>
                <p className="text-[10px] text-ink-soft">{it.ago}</p>
              </div>
              {i === 0 && (
                <span className="h-2 w-2 rounded-full bg-terracotta" aria-label="unread" />
              )}
            </div>
          ))}
        </div>
      </div>
    </ScreenShell>
  );
}
