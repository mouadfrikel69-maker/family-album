import { motion } from 'framer-motion'
import { PromiseStrip } from '../components/PromiseStrip'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'
import { Walkthrough } from './Walkthrough'

export function Hero() {
  return (
    <section className="relative pt-10 sm:pt-14 pb-16 sm:pb-20 paper" id="top">
      {/* Decorative scattered dots & washi accents */}
      <DecorativeAccents />

      <div className="relative max-w-6xl mx-auto px-6 sm:px-10 grid lg:grid-cols-[1fr_auto] items-center gap-10">
        <div>
          <motion.div
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.7 }}
          >
            <Wordmark size={36} />
          </motion.div>

          <motion.h1
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.05 }}
            className="font-headline mt-6 sm:mt-8 leading-[1.05]"
            style={{
              color: 'var(--color-ink)',
              fontSize: 'clamp(36px, 6vw, 64px)',
            }}
          >
            A private, paper-album
            <br />
            <span className="font-display text-terracotta-deep">just for the people</span>
            <br />
            who feel like home.
          </motion.h1>

          <motion.p
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.18 }}
            className="mt-5 max-w-xl"
            style={{ color: 'var(--color-mocha)', fontSize: 17, lineHeight: 1.55 }}
          >
            Kin is a warm Android photo album for one family — no followers, no ads, no
            cloud copies of your photos. Polaroids, not feeds; an invite code, not a public
            profile.
          </motion.p>

          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.3 }}
            className="mt-6 flex flex-wrap items-center gap-3"
          >
            <a
              href="#download"
              className="px-6 py-3 rounded-full gradient-terracotta shadow-paper font-semibold inline-flex items-center gap-2"
              style={{ color: 'var(--color-warmwhite)' }}
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                <path d="M3 20.5V3.5l13 8.5z" opacity="0.85" />
              </svg>
              Get it on Android
            </a>
            <a
              href="#tour"
              className="px-6 py-3 rounded-full font-semibold inline-flex items-center gap-2"
              style={{
                background: 'var(--color-polaroid)',
                color: 'var(--color-ink)',
                border: '1px solid var(--color-blush)',
              }}
            >
              See the tour
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" aria-hidden="true">
                <path d="M5 12h14" />
                <path d="m12 5 7 7-7 7" />
              </svg>
            </a>
          </motion.div>

          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.8, delay: 0.45 }}
            className="mt-5 flex flex-col gap-1"
          >
            <PromiseStrip />
            <Tagline>made in the warmth of an open scrapbook</Tagline>
          </motion.div>
        </div>

        <motion.div
          initial={{ opacity: 0, scale: 0.9, rotate: -1 }}
          animate={{ opacity: 1, scale: 1, rotate: 0 }}
          transition={{ duration: 0.8, delay: 0.2, ease: [0.22, 1, 0.36, 1] }}
          className="justify-self-center lg:justify-self-end"
          id="tour"
        >
          <Walkthrough />
        </motion.div>
      </div>
    </section>
  )
}

function DecorativeAccents() {
  return (
    <>
      {/* Washi-tape strips */}
      <span
        aria-hidden="true"
        className="hidden md:block absolute"
        style={{
          top: 28,
          left: -30,
          width: 160,
          height: 22,
          background: 'rgba(212, 165, 116, 0.55)',
          border: '1px solid rgba(160, 78, 51, 0.18)',
          transform: 'rotate(-8deg)',
        }}
      />
      <span
        aria-hidden="true"
        className="hidden md:block absolute"
        style={{
          top: 80,
          right: -60,
          width: 200,
          height: 22,
          background: 'rgba(184, 196, 168, 0.6)',
          border: '1px solid rgba(107, 85, 68, 0.18)',
          transform: 'rotate(7deg)',
        }}
      />

      {/* Subtle warm glow */}
      <span
        aria-hidden="true"
        className="absolute -top-40 left-1/2 -translate-x-1/2 w-[60vw] h-[40vw] -z-10 blur-3xl"
        style={{
          background:
            'radial-gradient(closest-side, rgba(244, 219, 207, 0.85), transparent 70%)',
        }}
      />
    </>
  )
}
