import { motion } from 'framer-motion'
import { PhotoScene } from '../components/PhotoScene'
import { PromiseStrip } from '../components/PromiseStrip'
import { ScreenPaper } from '../components/ScreenPaper'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'

export function OnboardingMock() {
  return (
    <ScreenPaper>
      <div className="flex flex-col h-full px-5 pt-2">
        <div className="flex items-center justify-between">
          <Wordmark size={22} />
          <span className="text-[11px]" style={{ color: 'var(--color-mocha)' }}>
            Skip
          </span>
        </div>

        <div className="flex-1 flex flex-col items-center justify-center text-center pb-2">
          <motion.div
            className="relative mb-4"
            animate={{ rotate: [-3, -1.5, -3], y: [0, -3, 0] }}
            transition={{ duration: 6, repeat: Infinity, ease: 'easeInOut' }}
          >
            <span
              className="absolute -top-2 left-4 z-10 h-4 rounded-[1px]"
              style={{
                width: 44,
                background: 'rgba(212, 165, 116, 0.7)',
                border: '1px solid rgba(160, 78, 51, 0.18)',
                transform: 'rotate(-7deg)',
              }}
            />
            <div
              className="rounded-[6px] shadow-polaroid"
              style={{ background: 'var(--color-polaroid)', padding: 10, width: 160 }}
            >
              <div
                className="overflow-hidden rounded-[2px]"
                style={{ width: 140, height: 140, background: 'var(--color-blush)' }}
              >
                <PhotoScene scene="kitchen-window" />
              </div>
              <div
                className="mt-2 font-display text-center"
                style={{ fontSize: 12, color: 'var(--color-ink)' }}
              >
                Sunday lunch
              </div>
            </div>
          </motion.div>

          <h2
            className="font-headline italic mt-2"
            style={{ fontSize: 18, color: 'var(--color-ink)', lineHeight: 1.2 }}
          >
            Your family memories,
            <br /> in one warm place.
          </h2>
          <p
            className="mt-2 text-[11px] px-3"
            style={{ color: 'var(--color-mocha)', lineHeight: 1.4 }}
          >
            No followers. No strangers. Just the people who already love your kids.
          </p>
          <div className="mt-3">
            <Tagline>made for the people who feel like home</Tagline>
          </div>
        </div>

        <div className="flex items-center justify-center gap-1.5 mb-3">
          <span className="block w-6 h-1.5 rounded-full" style={{ background: 'var(--color-terracotta)' }} />
          <span className="block w-1.5 h-1.5 rounded-full" style={{ background: 'var(--color-blush)' }} />
          <span className="block w-1.5 h-1.5 rounded-full" style={{ background: 'var(--color-blush)' }} />
        </div>

        <button
          className="w-full py-3 rounded-full gradient-terracotta shadow-polaroid font-semibold text-[13px]"
          style={{ color: 'var(--color-warmwhite)' }}
        >
          Get started
        </button>
        <div className="mt-3 mb-3 flex justify-center">
          <PromiseStrip small />
        </div>
      </div>
    </ScreenPaper>
  )
}
