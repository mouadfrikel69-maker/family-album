import { AnimatePresence, motion } from 'framer-motion'
import { useEffect, useState } from 'react'
import { PhotoScene } from '../components/PhotoScene'
import { ScreenPaper } from '../components/ScreenPaper'
import { Wordmark } from '../components/Wordmark'
import { SAMPLE_ALBUMS } from '../data/sampleData'

type Props = { animated?: boolean }

const SCENES: import('../data/sampleData').SceneKey[] = [
  'sunset-beach',
  'birthday-cake',
  'mountain-hike',
  'kitchen-window',
  'flower-field',
  'kids-yard',
  'grandma-tea',
  'rainy-cafe',
]

export function UploadMock({ animated = true }: Props) {
  const [picked, setPicked] = useState<number | null>(null)
  const [progress, setProgress] = useState(0)
  const [done, setDone] = useState(false)

  useEffect(() => {
    if (!animated) return
    let cancel = false
    const cycle = async () => {
      while (!cancel) {
        setPicked(null)
        setProgress(0)
        setDone(false)
        await wait(1400)
        if (cancel) break
        setPicked(1)
        await wait(1100)
        if (cancel) break
        for (let p = 0; p <= 100; p += 12) {
          if (cancel) break
          setProgress(p)
          await wait(70)
        }
        setProgress(100)
        await wait(400)
        if (cancel) break
        setDone(true)
        await wait(2000)
      }
    }
    cycle()
    return () => {
      cancel = true
    }
  }, [animated])

  return (
    <ScreenPaper>
      <div className="flex flex-col h-full">
        {/* Header */}
        <div className="px-4 pt-2 flex items-center gap-2">
          <button
            aria-label="Back"
            className="rounded-full p-1.5"
            style={{ background: 'var(--color-polaroid)' }}
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M19 12H5" />
              <path d="m12 19-7-7 7-7" />
            </svg>
          </button>
          <div className="flex-1 flex justify-center -ml-6">
            <Wordmark size={20} showLabel={false} />
          </div>
        </div>

        <div className="text-center mt-1">
          <h2
            className="font-headline italic"
            style={{ fontSize: 18, color: 'var(--color-ink)', lineHeight: 1.1 }}
          >
            Add to the album.
          </h2>
          <p className="text-[10px]" style={{ color: 'var(--color-mocha)' }}>
            Pick from gallery or take a new one.
          </p>
        </div>

        {/* Picker grid */}
        <div className="px-3 mt-2 grid grid-cols-3 gap-1.5">
          {SCENES.map((s, i) => (
            <motion.div
              key={i}
              animate={picked === i ? { scale: 0.95 } : { scale: 1 }}
              transition={{ duration: 0.18 }}
              className="relative overflow-hidden rounded-[8px] aspect-square"
              style={{
                background: 'var(--color-blush)',
                outline:
                  picked === i ? '2px solid var(--color-terracotta)' : 'none',
                outlineOffset: picked === i ? -2 : 0,
              }}
            >
              <PhotoScene scene={s} />
              {picked === i && (
                <span
                  className="absolute top-1 right-1 w-4 h-4 rounded-full flex items-center justify-center"
                  style={{ background: 'var(--color-terracotta)', color: 'var(--color-warmwhite)' }}
                >
                  <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <path d="M5 12l5 5L20 7" />
                  </svg>
                </span>
              )}
            </motion.div>
          ))}
        </div>

        {/* Caption + album */}
        <div className="px-4 mt-3">
          <div
            className="rounded-[14px] p-3"
            style={{ background: 'var(--color-polaroid)' }}
          >
            <div className="text-[10px] uppercase tracking-wider" style={{ color: 'var(--color-softbrown)' }}>
              caption
            </div>
            <div className="font-display text-[12px] mt-0.5" style={{ color: 'var(--color-ink)' }}>
              "first hike of the season"
            </div>
            <div className="mt-2 flex items-center gap-1.5 flex-wrap">
              {SAMPLE_ALBUMS.map((a, i) => (
                <span
                  key={a.id}
                  className="px-2 py-0.5 rounded-full text-[10px] font-headline"
                  style={{
                    background: i === 2 ? a.accent : 'var(--color-cream)',
                    color: i === 2 ? 'var(--color-warmwhite)' : 'var(--color-ink)',
                    border: i === 2 ? 'none' : '1px solid var(--color-blush)',
                  }}
                >
                  {a.name}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Progress */}
        <div className="px-4 mt-auto pb-4 relative">
          <div
            className="h-2 rounded-full overflow-hidden"
            style={{ background: 'var(--color-blush)' }}
          >
            <motion.div
              animate={{ width: `${progress}%` }}
              transition={{ ease: 'linear', duration: 0.06 }}
              className="h-full rounded-full gradient-terracotta"
              style={{ width: `${progress}%` }}
            />
          </div>
          <div className="mt-2 flex items-center justify-between text-[10px]" style={{ color: 'var(--color-mocha)' }}>
            <span>
              {done ? 'done · saved on device' : picked == null ? 'pick a photo' : `uploading… ${progress}%`}
            </span>
            <button
              className="px-3 py-1.5 rounded-full font-headline text-[11px] gradient-terracotta"
              style={{ color: 'var(--color-warmwhite)' }}
            >
              Save
            </button>
          </div>

          <AnimatePresence>
            {done && (
              <motion.div
                key="upload-toast"
                initial={{ y: 16, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                exit={{ y: 16, opacity: 0 }}
                className="absolute -top-3 left-3 right-3 rounded-full px-3 py-1.5 flex items-center gap-1.5 -translate-y-full"
                style={{ background: 'var(--color-ink)', color: 'var(--color-warmwhite)' }}
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                  <path d="M5 12l5 5L20 7" />
                </svg>
                <span className="font-display italic text-[11px]">added to Cabin trip</span>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </ScreenPaper>
  )
}

const wait = (ms: number) => new Promise<void>((r) => setTimeout(r, ms))
