import { useMemo } from "react";

type Props = {
  className?: string;
  speckles?: number;
  seed?: number;
};

function seededRandom(seed: number) {
  let s = seed >>> 0;
  return () => {
    s = (s * 1664525 + 1013904223) >>> 0;
    return s / 0xffffffff;
  };
}

export default function PaperBackground({
  className = "",
  speckles = 60,
  seed = 7,
}: Props) {
  const dots = useMemo(() => {
    const rng = seededRandom(seed);
    return Array.from({ length: speckles }, (_, i) => ({
      id: i,
      cx: rng() * 100,
      cy: rng() * 100,
      r: 0.4 + rng() * 0.9,
      o: 0.06 + rng() * 0.12,
    }));
  }, [speckles, seed]);

  return (
    <div className={`pointer-events-none absolute inset-0 ${className}`} aria-hidden>
      <div
        className="absolute inset-0"
        style={{
          background:
            "radial-gradient(ellipse 90% 50% at 50% -10%, rgba(232, 180, 160, 0.45) 0%, transparent 70%), #FAF4EC",
        }}
      />
      <svg className="absolute inset-0 h-full w-full" viewBox="0 0 100 100" preserveAspectRatio="none">
        {dots.map((d) => (
          <circle key={d.id} cx={d.cx} cy={d.cy} r={d.r} fill="#3D2E26" opacity={d.o} />
        ))}
      </svg>
    </div>
  );
}
