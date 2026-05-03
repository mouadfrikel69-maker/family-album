import { motion } from 'framer-motion'
import type { ReactNode } from 'react'
import { PhoneFrame } from '../components/PhoneFrame'

type Props = {
  id: string
  eyebrow: string
  title: ReactNode
  description: ReactNode
  bullets: { title: string; body: string }[]
  caption: string
  /** screen rendered inside the phone */
  screen: ReactNode
  /** flip phone to the right side */
  reverse?: boolean
  /** background tint for the section card */
  tint?: 'cream' | 'blush' | 'sage' | 'washi'
}

const TINTS: Record<NonNullable<Props['tint']>, string> = {
  cream: 'var(--color-cream)',
  blush: '#F9E6DC',
  sage: '#E2EAD6',
  washi: '#EFDFCA',
}

export function FeatureSection({
  id,
  eyebrow,
  title,
  description,
  bullets,
  caption,
  screen,
  reverse,
  tint = 'cream',
}: Props) {
  return (
    <section
      id={id}
      className="relative py-16 sm:py-24"
      style={{ background: TINTS[tint] }}
    >
      <div
        className={`relative max-w-6xl mx-auto px-6 sm:px-10 grid lg:grid-cols-2 items-center gap-12 ${
          reverse ? 'lg:[&>div:first-child]:order-2' : ''
        }`}
      >
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.7, ease: [0.22, 1, 0.36, 1] }}
          viewport={{ once: true, margin: '-80px' }}
        >
          <div
            className="font-script text-[20px] mb-2"
            style={{ color: 'var(--color-terracotta-deep)' }}
          >
            {eyebrow}
          </div>
          <h2
            className="font-headline leading-[1.1]"
            style={{
              color: 'var(--color-ink)',
              fontSize: 'clamp(28px, 4vw, 44px)',
            }}
          >
            {title}
          </h2>
          <p
            className="mt-4 max-w-md"
            style={{ color: 'var(--color-mocha)', fontSize: 16, lineHeight: 1.55 }}
          >
            {description}
          </p>
          <ul className="mt-6 flex flex-col gap-3.5">
            {bullets.map((b) => (
              <li key={b.title} className="flex gap-3">
                <span
                  className="mt-1 inline-flex items-center justify-center w-5 h-5 rounded-full flex-shrink-0"
                  style={{
                    background: 'var(--color-terracotta)',
                    color: 'var(--color-warmwhite)',
                  }}
                >
                  <svg
                    width="11"
                    height="11"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="3"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    aria-hidden="true"
                  >
                    <path d="M5 12l5 5L20 7" />
                  </svg>
                </span>
                <div>
                  <div
                    className="font-headline"
                    style={{ color: 'var(--color-ink)', fontSize: 15 }}
                  >
                    {b.title}
                  </div>
                  <div
                    style={{ color: 'var(--color-softbrown)', fontSize: 14, lineHeight: 1.5 }}
                  >
                    {b.body}
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.92, rotate: reverse ? -2 : 2 }}
          whileInView={{ opacity: 1, scale: 1, rotate: 0 }}
          transition={{ duration: 0.7, ease: [0.22, 1, 0.36, 1] }}
          viewport={{ once: true, margin: '-80px' }}
          className="justify-self-center"
        >
          <PhoneFrame width={270} caption={caption}>
            {screen}
          </PhoneFrame>
        </motion.div>
      </div>
    </section>
  )
}
