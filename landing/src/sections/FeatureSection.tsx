import type { ReactNode } from "react";
import FeatureMockup, { type Frame } from "../kin/FeatureMockup";

type Props = {
  eyebrow: string;
  title: ReactNode;
  bullets: string[];
  frames: Frame[];
  reverse?: boolean;
  id?: string;
};

export function FeatureSection({
  eyebrow,
  title,
  bullets,
  frames,
  reverse = false,
  id,
}: Props) {
  return (
    <section id={id} className="relative">
      <div
        className={`mx-auto flex max-w-6xl flex-col items-center gap-12 px-6 py-20 lg:gap-16 lg:py-28 ${
          reverse ? "lg:flex-row-reverse" : "lg:flex-row"
        }`}
      >
        <div className="flex-1">
          <span className="pill">{eyebrow}</span>
          <h2 className="mt-4 font-serif text-[34px] leading-tight text-ink sm:text-[44px]">
            {title}
          </h2>
          <ul className="mt-6 space-y-3 text-[15px] leading-relaxed text-ink-mocha">
            {bullets.map((b) => (
              <li key={b} className="flex items-start gap-3">
                <span
                  aria-hidden
                  className="mt-2 h-1.5 w-1.5 flex-none rounded-full bg-terracotta"
                />
                <span>{b}</span>
              </li>
            ))}
          </ul>
        </div>
        <div className="flex-1 lg:flex lg:justify-center">
          <FeatureMockup frames={frames} width={280} />
        </div>
      </div>
    </section>
  );
}
