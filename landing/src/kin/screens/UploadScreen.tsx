import { motion } from "framer-motion";
import PhotoTile from "../PhotoTile";
import { ScreenShell } from "./Chrome";

type Props = {
  caption?: string;
  progress?: number; // 0..1
  album?: string;
};

export default function UploadScreen({ caption, progress = 0, album }: Props) {
  return (
    <ScreenShell>
      <div className="flex h-full flex-col px-5 pb-6 pt-4">
        <div className="flex items-center justify-between">
          <button className="grid h-9 w-9 place-items-center rounded-full bg-polaroid text-ink-mocha shadow-soft">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="m11 3-8 8M3 3l8 8" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" />
            </svg>
          </button>
          <span className="font-serif text-[14px] italic text-ink">add a memory</span>
          <button className="rounded-full bg-terracotta-gradient px-4 py-1.5 text-[11px] font-semibold text-polaroid shadow-soft">
            share
          </button>
        </div>

        <div className="mt-4 grid grid-cols-3 gap-2">
          {["a", "b", "c", "d", "e", "f"].map((s, i) => (
            <motion.div
              key={s}
              initial={false}
              animate={{
                scale: i === 0 ? 1.02 : 1,
              }}
              className={`relative overflow-hidden rounded-xl ${
                i === 0 ? "ring-2 ring-terracotta" : ""
              }`}
            >
              <PhotoTile seed={`grid-${s}`} className="aspect-square w-full" />
              {i === 0 && (
                <span className="absolute right-1 top-1 grid h-5 w-5 place-items-center rounded-full bg-terracotta text-[10px] font-bold text-polaroid">
                  ✓
                </span>
              )}
            </motion.div>
          ))}
        </div>

        <div className="mt-4 rounded-2xl border border-blush bg-polaroid p-3 shadow-soft">
          <p className="text-[10px] uppercase tracking-[0.18em] text-ink-soft">caption</p>
          <p className="mt-1 min-h-[20px] font-script text-[15px] text-ink">
            {caption || ""}
            <span className="ml-0.5 inline-block h-3.5 w-px translate-y-0.5 bg-terracotta animate-pulse align-middle" />
          </p>
        </div>

        <div className="mt-3 flex items-center justify-between rounded-2xl border border-blush bg-polaroid p-3 shadow-soft">
          <div>
            <p className="text-[10px] uppercase tracking-[0.18em] text-ink-soft">album</p>
            <p className="mt-1 font-serif text-[13px] text-ink">{album || "Summer"}</p>
          </div>
          <span className="rounded-full border border-blush px-2 py-0.5 text-[10px] text-ink-mocha">
            change
          </span>
        </div>

        {progress > 0 && (
          <div className="mt-3">
            <div className="flex items-center justify-between text-[10px] text-ink-soft">
              <span>uploading…</span>
              <span>{Math.round(progress * 100)}%</span>
            </div>
            <div className="mt-1 h-1.5 w-full overflow-hidden rounded-full bg-blush/60">
              <motion.div
                className="h-full bg-terracotta-gradient"
                initial={false}
                animate={{ width: `${Math.min(100, progress * 100)}%` }}
                transition={{ ease: "easeOut", duration: 0.4 }}
              />
            </div>
          </div>
        )}
      </div>
    </ScreenShell>
  );
}
