import type { ReactNode } from "react";

type Props = {
  children: ReactNode;
  className?: string;
  innerClassName?: string;
  width?: number;
  shadow?: boolean;
};

// Realistic phone mockup. Inner viewport size is fixed (260 x 540) so screens
// can lay themselves out predictably. Scale via the parent CSS.
export default function PhoneMockup({
  children,
  className = "",
  innerClassName = "",
  width = 280,
  shadow = true,
}: Props) {
  const height = Math.round(width * (580 / 280));
  return (
    <div
      className={`relative ${className}`}
      style={{ width, height }}
    >
      {shadow && (
        <div
          aria-hidden
          className="absolute -inset-3 rounded-[58px] blur-2xl opacity-60"
          style={{
            background:
              "radial-gradient(closest-side, rgba(167, 100, 73, 0.35), transparent 70%)",
          }}
        />
      )}
      <div
        className="relative h-full w-full rounded-[44px] p-[7px] shadow-phone"
        style={{
          background:
            "linear-gradient(160deg, #2a1f1a 0%, #3D2E26 40%, #1d1612 100%)",
        }}
      >
        <div
          className="relative h-full w-full overflow-hidden rounded-[38px] bg-cream"
          style={{
            boxShadow: "inset 0 0 0 1.5px rgba(255, 252, 246, 0.08)",
          }}
        >
          {/* Notch / pill */}
          <div className="pointer-events-none absolute left-1/2 top-2 z-30 -translate-x-1/2">
            <div className="h-5 w-24 rounded-full bg-[#1d1612]" />
          </div>
          {/* Side buttons */}
          <span className="absolute -left-[9px] top-24 h-10 w-[3px] rounded-l-md bg-[#1d1612]" />
          <span className="absolute -left-[9px] top-40 h-16 w-[3px] rounded-l-md bg-[#1d1612]" />
          <span className="absolute -right-[9px] top-32 h-20 w-[3px] rounded-r-md bg-[#1d1612]" />
          {/* Screen content */}
          <div className={`relative h-full w-full overflow-hidden ${innerClassName}`}>
            {children}
          </div>
        </div>
      </div>
    </div>
  );
}
