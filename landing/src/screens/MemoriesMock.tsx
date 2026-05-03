import { motion } from 'framer-motion'
import { PhotoScene } from '../components/PhotoScene'
import { ScreenPaper } from '../components/ScreenPaper'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'
import { BottomNav } from './HomeFeedMock'

export function MemoriesMock() {
  const items = [
    { year: '3 years ago', scene: 'mountain-hike' as const, caption: 'first hike with grandpa' },
    { year: '5 years ago', scene: 'birthday-cake' as const, caption: "Lina's 4th candle" },
    { year: 'last year', scene: 'kitchen-window' as const, caption: 'Ramadan, all of us at the table' },
  ]
  return (
    <ScreenPaper>
      <div className="flex flex-col h-full pb-16">
        <div className="px-4 pt-2 flex items-center gap-2">
          <Wordmark size={20} showLabel={false} />
          <span className="text-[10px]" style={{ color: 'var(--color-mocha)' }}>
            this week, in previous years
          </span>
        </div>
        <div className="px-4 mt-1">
          <h2
            className="font-headline"
            style={{ fontSize: 22, color: 'var(--color-ink)' }}
          >
            Memories
          </h2>
          <p className="text-[11px]" style={{ color: 'var(--color-mocha)' }}>
            Soft echoes from the family album.
          </p>
          <div className="mt-1">
            <Tagline>a little throwback</Tagline>
          </div>
        </div>

        <div className="px-4 mt-3 flex flex-col gap-3">
          {items.map((it, i) => (
            <motion.div
              key={i}
              animate={{ y: [0, -2, 0] }}
              transition={{
                duration: 5 + i,
                repeat: Infinity,
                ease: 'easeInOut',
                delay: i * 0.4,
              }}
              className="rounded-[10px] p-2 flex gap-3 items-center shadow-paper"
              style={{
                background: 'var(--color-polaroid)',
                transform: `rotate(${i % 2 === 0 ? -1.2 : 1.2}deg)`,
              }}
            >
              <div
                className="rounded-[4px] overflow-hidden flex-shrink-0"
                style={{ width: 72, height: 72, background: 'var(--color-blush)' }}
              >
                <PhotoScene scene={it.scene} />
              </div>
              <div className="flex-1">
                <div
                  className="text-[10px] uppercase tracking-wider"
                  style={{ color: 'var(--color-terracotta-deep)' }}
                >
                  {it.year} · this week
                </div>
                <div
                  className="font-display text-[13px] mt-0.5 leading-snug"
                  style={{ color: 'var(--color-ink)' }}
                >
                  {it.caption}
                </div>
                <div className="text-[9px] mt-1" style={{ color: 'var(--color-softbrown)' }}>
                  4 family members loved this
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
      <BottomNav active="memories" />
    </ScreenPaper>
  )
}
