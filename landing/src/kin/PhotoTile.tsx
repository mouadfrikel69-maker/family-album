type Props = {
  seed: string;
  className?: string;
  style?: React.CSSProperties;
};

// Procedural warm "photo" tile — soft scenes that fit Kin's palette without
// any real photography. Generated deterministically per seed.
function pick<T>(seed: number, list: T[]): T {
  return list[seed % list.length]!;
}

function hash(s: string): number {
  let h = 0;
  for (let i = 0; i < s.length; i++) h = (h * 31 + s.charCodeAt(i)) | 0;
  return Math.abs(h);
}

const SCENES = [
  // sunset park
  ["#F4DBCF", "#E8B4A0", "#D4A574", "#B8C4A8"],
  // golden hour table
  ["#FFFBF5", "#F4DBCF", "#D4A574", "#A04E33"],
  // garden
  ["#E8B4A0", "#B8C4A8", "#D4A574", "#3D2E26"],
  // window light
  ["#FFFCF6", "#F4DBCF", "#E8B4A0", "#8B7560"],
  // hearth
  ["#D17A5C", "#C76B4A", "#A04E33", "#3D2E26"],
  // morning sage
  ["#FFFBF5", "#B8C4A8", "#E8B4A0", "#6B5544"],
];

export default function PhotoTile({ seed, className = "", style }: Props) {
  const h = hash(seed);
  const palette = pick(h, SCENES);
  const horizon = 38 + (h % 22);
  const sunCx = 25 + (h % 50);
  const sunR = 8 + (h % 8);
  const tilt = -8 + (h % 16);
  const stripes = (h % 5) + 3;

  return (
    <svg
      viewBox="0 0 100 100"
      preserveAspectRatio="xMidYMid slice"
      className={className}
      style={style}
      aria-hidden
    >
      <defs>
        <linearGradient id={`sky-${seed}`} x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor={palette[0]} />
          <stop offset="100%" stopColor={palette[1]} />
        </linearGradient>
        <linearGradient id={`ground-${seed}`} x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor={palette[2]} />
          <stop offset="100%" stopColor={palette[3]} />
        </linearGradient>
      </defs>
      <rect x="0" y="0" width="100" height={horizon} fill={`url(#sky-${seed})`} />
      <rect x="0" y={horizon} width="100" height={100 - horizon} fill={`url(#ground-${seed})`} />
      <circle cx={sunCx} cy={horizon - sunR + 2} r={sunR} fill={palette[1]} opacity="0.85" />
      <circle cx={sunCx} cy={horizon - sunR + 2} r={sunR * 0.55} fill={palette[0]} opacity="0.9" />
      {/* hills/stripes */}
      {Array.from({ length: stripes }).map((_, i) => {
        const y = horizon + ((100 - horizon) / stripes) * i;
        const amp = 2 + ((h + i) % 4);
        return (
          <path
            key={i}
            d={`M 0 ${y} Q 25 ${y - amp} 50 ${y} T 100 ${y} L 100 ${y + 6} L 0 ${y + 6} Z`}
            fill={palette[3]}
            opacity={0.08 + i * 0.04}
          />
        );
      })}
      {/* small silhouette */}
      <g transform={`translate(${50 + (h % 20) - 10}, ${horizon - 4}) rotate(${tilt})`}>
        <rect x="-1" y="-10" width="2" height="10" fill={palette[3]} opacity="0.55" />
        <circle cx="0" cy="-12" r="2" fill={palette[3]} opacity="0.55" />
      </g>
      {/* warm vignette */}
      <rect
        x="0"
        y="0"
        width="100"
        height="100"
        fill="url(#vignette)"
        opacity="0.18"
      />
      <radialGradient id="vignette">
        <stop offset="60%" stopColor="transparent" />
        <stop offset="100%" stopColor="#3D2E26" />
      </radialGradient>
    </svg>
  );
}
