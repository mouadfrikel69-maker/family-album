import { useMemo } from "react";

type Props = {
  content: string;
  size?: number;
  fg?: string;
  bg?: string;
};

// A decorative QR-like grid (deterministic from content). We don't need a
// real scannable QR for the landing — we just want the visual rhythm of one.
function seeded(seed: string) {
  let h = 2166136261;
  for (let i = 0; i < seed.length; i++) {
    h ^= seed.charCodeAt(i);
    h = Math.imul(h, 16777619);
  }
  let s = h >>> 0;
  return () => {
    s = (s * 1664525 + 1013904223) >>> 0;
    return s / 0xffffffff;
  };
}

export default function QrCode({
  content,
  size = 160,
  fg = "#3D2E26",
  bg = "transparent",
}: Props) {
  const grid = 21;
  const cells = useMemo(() => {
    const rng = seeded(content);
    return Array.from({ length: grid * grid }, () => rng() > 0.5);
  }, [content]);

  const isFinder = (r: number, c: number) => {
    const inTopLeft = r < 7 && c < 7;
    const inTopRight = r < 7 && c >= grid - 7;
    const inBottomLeft = r >= grid - 7 && c < 7;
    return inTopLeft || inTopRight || inBottomLeft;
  };

  const cell = size / grid;

  return (
    <svg
      width={size}
      height={size}
      viewBox={`0 0 ${grid} ${grid}`}
      shapeRendering="crispEdges"
      style={{ background: bg }}
      role="img"
      aria-label="QR invite code"
    >
      {cells.map((on, idx) => {
        const r = Math.floor(idx / grid);
        const c = idx % grid;
        if (isFinder(r, c)) return null;
        if (!on) return null;
        return (
          <rect
            key={idx}
            x={c}
            y={r}
            width={1}
            height={1}
            rx={0.15}
            ry={0.15}
            fill={fg}
          />
        );
      })}
      {/* Three finder squares */}
      {[
        [0, 0],
        [0, grid - 7],
        [grid - 7, 0],
      ].map(([r, c], i) => (
        <g key={i} transform={`translate(${c}, ${r})`}>
          <rect width={7} height={7} rx={1.4} ry={1.4} fill={fg} />
          <rect x={1} y={1} width={5} height={5} rx={1} ry={1} fill="#FFFCF6" />
          <rect x={2} y={2} width={3} height={3} rx={0.5} ry={0.5} fill={fg} />
        </g>
      ))}
      <title>{content}</title>
      {/* invisible to suppress unused */}
      <desc style={{ display: "none" }}>{cell}</desc>
    </svg>
  );
}
