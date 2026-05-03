import type { ReactNode } from "react";

type Props = {
  caption?: string;
  author?: string;
  rotation?: number;
  washi?: boolean;
  className?: string;
  children: ReactNode;
};

export default function Polaroid({
  caption,
  author,
  rotation = 0,
  washi = true,
  className = "",
  children,
}: Props) {
  return (
    <div
      className={`relative bg-polaroid rounded-md shadow-polaroid ${className}`}
      style={{ transform: `rotate(${rotation}deg)`, padding: 8 }}
    >
      {washi && (
        <span
          className="absolute -top-2 left-1/2 h-4 w-16 -translate-x-1/2 rotate-[-3deg] washi-tape opacity-90"
          aria-hidden
        />
      )}
      <div className="overflow-hidden rounded-sm">{children}</div>
      {(caption || author) && (
        <div className="px-1 pt-2 pb-1">
          {caption && (
            <p className="font-script text-[15px] leading-tight text-ink">{caption}</p>
          )}
          {author && (
            <p className="mt-0.5 text-[10px] uppercase tracking-wider text-ink-soft">
              {author}
            </p>
          )}
        </div>
      )}
    </div>
  );
}
