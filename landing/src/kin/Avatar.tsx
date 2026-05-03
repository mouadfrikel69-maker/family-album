import { accentFor, initialsFor } from "./palette";

type Props = {
  name: string;
  size?: number;
  ring?: boolean;
  className?: string;
};

export default function Avatar({ name, size = 36, ring = true, className = "" }: Props) {
  const bg = accentFor(name);
  const initials = initialsFor(name);
  return (
    <span
      className={`inline-grid place-items-center rounded-full font-semibold text-polaroid ${ring ? "ring-2 ring-polaroid" : ""} ${className}`}
      style={{
        width: size,
        height: size,
        background: bg,
        fontSize: size * 0.38,
        letterSpacing: "0.02em",
        boxShadow: "0 2px 6px -2px rgba(61, 46, 38, 0.25)",
      }}
      aria-label={name}
    >
      {initials}
    </span>
  );
}
