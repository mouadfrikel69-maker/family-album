import { motion } from "framer-motion";
import Avatar from "../Avatar";
import Polaroid from "../Polaroid";
import PhotoTile from "../PhotoTile";
import { MEMBERS, PHOTOS } from "../data";
import { BottomTabs, ScreenShell, TopBar } from "./Chrome";

type Props = {
  highlightLikeOnFirst?: boolean;
};

export default function HomeScreen({ highlightLikeOnFirst = false }: Props) {
  return (
    <ScreenShell>
      <TopBar title="The Hassan family" />
      <div className="px-4">
        <div className="flex items-center gap-3 overflow-hidden pb-3">
          {MEMBERS.map((m) => (
            <div key={m.id} className="flex flex-col items-center gap-1">
              <Avatar name={m.name} size={36} />
              <span className="text-[9px] text-ink-soft">{m.name}</span>
            </div>
          ))}
        </div>
        <div className="px-1">
          <h2 className="font-serif text-[20px] leading-tight text-ink">
            today, <span className="italic text-terracotta">together</span>
          </h2>
          <p className="font-script text-[13px] text-ink-mocha">
            made for the people who feel like home
          </p>
        </div>
      </div>

      <div className="mt-3 flex flex-col gap-5 overflow-hidden px-5 pb-24">
        {PHOTOS.slice(0, 3).map((p, i) => {
          const author = MEMBERS.find((m) => m.id === p.authorId)!;
          return (
            <motion.div
              key={p.id}
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.05 * i }}
            >
              <Polaroid
                rotation={i % 2 === 0 ? -1.5 : 1.5}
                caption={p.caption}
                author={`${author.name} · ${p.ago}`}
              >
                <PhotoTile seed={p.seed} className="aspect-[5/4] w-full" />
              </Polaroid>
              <div className="mt-1.5 flex items-center justify-between px-2 text-[11px] text-ink-mocha">
                <div className="flex items-center gap-3">
                  <span className={`flex items-center gap-1 ${p.liked || (highlightLikeOnFirst && i === 0) ? "text-terracotta" : ""}`}>
                    <HeartGlyph filled={p.liked || (highlightLikeOnFirst && i === 0)} />
                    {p.likes + (highlightLikeOnFirst && i === 0 ? 1 : 0)}
                  </span>
                  <span className="flex items-center gap-1">
                    <ChatGlyph /> {p.comments}
                  </span>
                </div>
                {p.album && (
                  <span className="rounded-full border border-blush bg-polaroid px-2 py-0.5 text-[9px] uppercase tracking-wider text-ink-soft">
                    {p.album}
                  </span>
                )}
              </div>
            </motion.div>
          );
        })}
      </div>
      <BottomTabs active="home" />
    </ScreenShell>
  );
}

function HeartGlyph({ filled = false }: { filled?: boolean }) {
  return (
    <svg width="13" height="12" viewBox="0 0 13 12" fill="none">
      <path
        d="M6.5 11s-5-3-5-6.5A2.5 2.5 0 0 1 6.5 2 2.5 2.5 0 0 1 11.5 4.5C11.5 8 6.5 11 6.5 11Z"
        stroke="currentColor"
        strokeWidth="1.3"
        strokeLinejoin="round"
        fill={filled ? "currentColor" : "none"}
      />
    </svg>
  );
}
function ChatGlyph() {
  return (
    <svg width="13" height="12" viewBox="0 0 13 12" fill="none">
      <path d="M2 3a1 1 0 0 1 1-1h7a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1H6L3 10V8a1 1 0 0 1-1-1V3Z" stroke="currentColor" strokeWidth="1.3" strokeLinejoin="round" />
    </svg>
  );
}
