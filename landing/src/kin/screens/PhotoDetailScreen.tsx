import Avatar from "../Avatar";
import PhotoTile from "../PhotoTile";
import { ScreenShell } from "./Chrome";

export default function PhotoDetailScreen() {
  return (
    <ScreenShell>
      <div className="relative h-full w-full">
        <PhotoTile seed="orchard-sunday" className="absolute inset-0 h-full w-full" />
        <div className="pointer-events-none absolute inset-x-0 top-0 h-24 bg-gradient-to-b from-cream/90 to-transparent" />
        <div className="pointer-events-none absolute inset-x-0 bottom-0 h-1/2 bg-gradient-to-t from-cream via-cream/85 to-transparent" />

        <div className="absolute left-4 right-4 top-3 flex items-center justify-between">
          <button className="grid h-9 w-9 place-items-center rounded-full bg-polaroid/90 text-ink-mocha shadow-soft">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M9 2 4 7l5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </button>
          <span className="rounded-full bg-polaroid/90 px-3 py-1 text-[10px] uppercase tracking-wider text-ink-soft shadow-soft">
            Summer
          </span>
        </div>

        <div className="absolute bottom-4 left-4 right-4">
          <p className="font-script text-[18px] text-ink">sunday picnic at the orchard 🍑</p>
          <div className="mt-2 flex items-center gap-2">
            <Avatar name="Mom" size={26} />
            <span className="text-[11px] text-ink-mocha">Mom · 2h ago</span>
          </div>
          <div className="mt-3 flex flex-col gap-1.5">
            <Comment author="Dad" text="absolutely golden" />
            <Comment author="Yara" text="i miss this so much" />
          </div>
          <div className="mt-3 flex items-center gap-2 rounded-full border border-blush bg-polaroid px-3 py-2 text-[11px] text-ink-soft shadow-soft">
            <Avatar name="Lina" size={20} />
            <span>add a comment…</span>
            <span className="ml-auto text-terracotta">post</span>
          </div>
        </div>
      </div>
    </ScreenShell>
  );
}

function Comment({ author, text }: { author: string; text: string }) {
  return (
    <div className="flex items-center gap-2 rounded-full border border-blush bg-polaroid/90 px-2.5 py-1 text-[11px] shadow-soft">
      <Avatar name={author} size={20} />
      <span className="font-semibold text-ink">{author}</span>
      <span className="text-ink-mocha">{text}</span>
    </div>
  );
}
