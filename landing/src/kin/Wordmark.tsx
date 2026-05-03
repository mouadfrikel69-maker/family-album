type Props = {
  size?: number;
  className?: string;
};

export default function Wordmark({ size = 28, className = "" }: Props) {
  const badge = size;
  return (
    <span className={`inline-flex items-center gap-2 ${className}`}>
      <span
        className="grid place-items-center rounded-full bg-k-badge text-polaroid font-serif italic font-semibold"
        style={{ width: badge, height: badge, fontSize: badge * 0.55, lineHeight: 1 }}
      >
        k
      </span>
      <span className="font-serif tracking-tight text-ink" style={{ fontSize: badge * 0.85 }}>
        kin
      </span>
    </span>
  );
}
