import Polaroid from "../Polaroid";
import PhotoTile from "../PhotoTile";
import { BottomTabs, ScreenShell, TopBar } from "./Chrome";

const memories = [
  { year: "2 years ago", caption: "the cake we still talk about", seed: "memory-cake" },
  { year: "5 years ago", caption: "first day at the new house", seed: "memory-house" },
  { year: "8 years ago", caption: "Adam's tiny shoes", seed: "memory-shoes" },
];

export default function MemoriesScreen() {
  return (
    <ScreenShell>
      <TopBar title="The Hassan family" />
      <div className="px-5">
        <h2 className="font-serif text-[20px] leading-tight text-ink">memories</h2>
        <p className="font-script text-[13px] text-ink-mocha">
          this week, in previous years
        </p>
      </div>
      <div className="mt-3 flex flex-col gap-5 overflow-hidden px-6 pb-24">
        {memories.map((m, i) => (
          <div key={m.year} className="flex flex-col items-center">
            <span className="mb-2 rounded-full border border-blush bg-polaroid px-3 py-0.5 text-[10px] uppercase tracking-[0.2em] text-ink-soft shadow-soft">
              {m.year}
            </span>
            <Polaroid
              rotation={i % 2 === 0 ? 2 : -2}
              caption={m.caption}
              author={i === 0 ? "Mom" : i === 1 ? "Dad" : "Teta"}
            >
              <PhotoTile seed={m.seed} className="aspect-[5/4] w-44" />
            </Polaroid>
          </div>
        ))}
      </div>
      <BottomTabs active="memories" />
    </ScreenShell>
  );
}
