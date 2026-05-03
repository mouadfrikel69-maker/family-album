import { motion } from "framer-motion";
import PhotoTile from "../PhotoTile";
import { ALBUMS } from "../data";
import { accentFor } from "../palette";
import { BottomTabs, ScreenShell, TopBar } from "./Chrome";

type Props = {
  newAlbumName?: string;
  showDialog?: boolean;
  highlightAlbumIndex?: number;
  extraAlbum?: { name: string };
};

export default function AlbumsScreen({
  showDialog,
  newAlbumName,
  highlightAlbumIndex,
  extraAlbum,
}: Props) {
  const albums = extraAlbum
    ? [{ id: "new", name: extraAlbum.name, count: 1, seed: extraAlbum.name + "-seed" }, ...ALBUMS]
    : ALBUMS;
  return (
    <ScreenShell>
      <TopBar title="The Hassan family" />
      <div className="flex items-end justify-between px-5">
        <div>
          <h2 className="font-serif text-[20px] leading-tight text-ink">albums</h2>
          <p className="font-script text-[13px] text-ink-mocha">little chapters of us</p>
        </div>
        <button
          type="button"
          className="rounded-full border border-blush bg-polaroid px-3 py-1.5 text-[11px] font-medium text-ink shadow-soft"
        >
          + new
        </button>
      </div>
      <div className="grid grid-cols-2 gap-3 px-5 pb-24 pt-4">
        {albums.slice(0, 6).map((a, i) => (
          <motion.div
            key={a.id}
            initial={false}
            animate={{
              scale: highlightAlbumIndex === i ? 1.02 : 1,
              boxShadow:
                highlightAlbumIndex === i
                  ? "0 12px 24px -8px rgba(199, 107, 74, 0.45)"
                  : "0 6px 18px -4px rgba(61, 46, 38, 0.14)",
            }}
            className="overflow-hidden rounded-2xl border border-blush bg-polaroid"
          >
            <div className="relative">
              <PhotoTile seed={a.seed} className="aspect-[5/4] w-full" />
              <span
                className="absolute left-2 top-2 rounded-full px-2 py-0.5 text-[9px] font-semibold uppercase tracking-wider text-polaroid"
                style={{ background: accentFor(a.name) }}
              >
                {a.count}
              </span>
            </div>
            <div className="px-3 py-2">
              <p className="font-serif text-[13px] text-ink">{a.name}</p>
              <p className="text-[10px] text-ink-soft">{a.count} photos</p>
            </div>
          </motion.div>
        ))}
      </div>

      {showDialog && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="absolute inset-0 z-30 grid place-items-center bg-ink/40 backdrop-blur-sm"
        >
          <motion.div
            initial={{ scale: 0.9, y: 16, opacity: 0 }}
            animate={{ scale: 1, y: 0, opacity: 1 }}
            transition={{ type: "spring", stiffness: 220, damping: 22 }}
            className="kin-card w-[80%] p-5"
          >
            <p className="text-[10px] uppercase tracking-[0.18em] text-ink-soft">
              new album
            </p>
            <p className="mt-2 font-serif text-[15px] text-ink">name your chapter</p>
            <div className="mt-3 flex items-center justify-between rounded-xl border border-blush bg-warm px-3 py-2 text-[13px] text-ink">
              <span>{newAlbumName || "Cabin"}</span>
              <span className="block h-4 w-px bg-terracotta animate-pulse" />
            </div>
            <div className="mt-4 flex gap-2">
              <button className="btn-secondary flex-1 !py-2 text-[12px]">cancel</button>
              <button className="btn-primary flex-1 !py-2 text-[12px]">create</button>
            </div>
          </motion.div>
        </motion.div>
      )}

      <BottomTabs active="albums" />
    </ScreenShell>
  );
}
