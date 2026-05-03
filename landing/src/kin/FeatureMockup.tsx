import { AnimatePresence, motion, useReducedMotion } from "framer-motion";
import { useEffect, useRef, useState } from "react";
import PhoneMockup from "./PhoneMockup";

export type Frame = {
  /** Unique key per frame, used by AnimatePresence. */
  key: string;
  /** What to render inside the phone. */
  render: () => React.ReactNode;
  /** Milliseconds to hold this frame before advancing. */
  hold?: number;
  /** Optional caption shown below the phone for this frame. */
  caption?: string;
};

type Props = {
  frames: Frame[];
  width?: number;
  className?: string;
  showCaption?: boolean;
  /** When true, animates only when the mockup is in viewport. */
  pauseOffscreen?: boolean;
};

const DEFAULT_HOLD = 2400;

export default function FeatureMockup({
  frames,
  width = 280,
  className = "",
  showCaption = true,
  pauseOffscreen = true,
}: Props) {
  const [index, setIndex] = useState(0);
  const containerRef = useRef<HTMLDivElement>(null);
  const [visible, setVisible] = useState(!pauseOffscreen);
  const reduce = useReducedMotion();

  useEffect(() => {
    if (!pauseOffscreen) return;
    const node = containerRef.current;
    if (!node) return;
    const obs = new IntersectionObserver(
      (entries) => {
        for (const e of entries) setVisible(e.isIntersecting);
      },
      { threshold: 0.25 },
    );
    obs.observe(node);
    return () => obs.disconnect();
  }, [pauseOffscreen]);

  useEffect(() => {
    if (!visible) return;
    const current = frames[index];
    if (!current) return;
    const hold = reduce ? Math.max(2200, current.hold ?? DEFAULT_HOLD) : current.hold ?? DEFAULT_HOLD;
    const t = setTimeout(() => {
      setIndex((i) => (i + 1) % frames.length);
    }, hold);
    return () => clearTimeout(t);
  }, [index, frames, visible, reduce]);

  const frame = frames[index] ?? frames[0]!;

  return (
    <div ref={containerRef} className={`flex flex-col items-center ${className}`}>
      <PhoneMockup width={width}>
        <AnimatePresence mode="wait" initial={false}>
          <motion.div
            key={frame.key}
            initial={{ opacity: 0, scale: 0.985 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 1.01 }}
            transition={{ duration: 0.45, ease: [0.22, 0.61, 0.36, 1] }}
            className="absolute inset-0"
          >
            {frame.render()}
          </motion.div>
        </AnimatePresence>
      </PhoneMockup>
      {showCaption && (
        <div className="mt-5 h-5 text-center font-script text-[15px] text-ink-mocha">
          <AnimatePresence mode="wait" initial={false}>
            <motion.span
              key={frame.key + "-cap"}
              initial={{ opacity: 0, y: 4 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -4 }}
              transition={{ duration: 0.3 }}
              className="inline-block"
            >
              {frame.caption ?? ""}
            </motion.span>
          </AnimatePresence>
        </div>
      )}
      {/* Progress dots */}
      <div className="mt-3 flex items-center gap-1.5">
        {frames.map((f, i) => (
          <span
            key={f.key}
            aria-hidden
            className="h-1.5 rounded-full transition-all"
            style={{
              width: i === index ? 18 : 6,
              background: i === index ? "#C76B4A" : "#E8B4A0",
              opacity: i === index ? 1 : 0.55,
            }}
          />
        ))}
      </div>
    </div>
  );
}
