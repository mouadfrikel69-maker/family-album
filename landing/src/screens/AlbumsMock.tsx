import { AnimatePresence, motion } from 'framer-motion'
import { useEffect, useState } from 'react'
import { PhotoScene } from '../components/PhotoScene'
import { ScreenPaper } from '../components/ScreenPaper'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'
import { SAMPLE_ALBUMS } from '../data/sampleData'
import { BottomNav } from './HomeFeedMock'

type Props = { animated?: boolean }

const NEW_ALBUM_NAME = 'Eid 2026'
const TYPE_DELAY = 110
const HOLD_AFTER_TYPE = 1200
const PAUSE_BEFORE_RETYPE = 2400

export function AlbumsMock({ animated = true }: Props) {
  const [typed, setTyped] = useState('')
  const [showAlbum, setShowAlbum] = useState(false)
  const [pressed, setPressed] = useState(false)

  useEffect(() => {
    if (!animated) return
    let cancel = false

    const cycle = async () => {
      while (!cancel) {
        // 1) Type the new album name
        setShowAlbum(false)
        setTyped('')
        for (let i = 1; i <= NEW_ALBUM_NAME.length; i++) {
          if (cancel) return
          await wait(TYPE_DELAY)
          setTyped(NEW_ALBUM_NAME.slice(0, i))
        }
        await wait(HOLD_AFTER_TYPE)
        if (cancel) return

        // 2) Tap "Create"
        setPressed(true)
        await wait(220)
        if (cancel) return
        setPressed(false)

        // 3) New album tile flies in
        setShowAlbum(true)

        await wait(PAUSE_BEFORE_RETYPE)
        if (cancel) return
      }
    }
    cycle()
    return () => {
      cancel = true
    }
  }, [animated])

  return (
    <ScreenPaper>
      {/* Header */}
      <div className="px-4 pt-2">
        <div className="flex items-center gap-2">
          <Wordmark size={20} showLabel={false} />
          <span className="text-[10px]" style={{ color: 'var(--color-mocha)' }}>
            the family album
          </span>
        </div>
        <h2
          className="mt-1 font-headline"
          style={{ fontSize: 22, color: 'var(--color-ink)' }}
        >
          Albums
        </h2>
        <p className="mt-0.5 text-[11px]" style={{ color: 'var(--color-mocha)' }}>
          Bound by hand. Filled together.
        </p>
        <div className="mt-1">
          <Tagline>a chapter for every season</Tagline>
        </div>
      </div>

      {/* New album dialog (always visible — looks like an inline create card) */}
      <div className="px-4 mt-3">
        <div
          className="rounded-[16px] p-3 flex items-center gap-2"
          style={{ background: 'var(--color-polaroid)', border: '1px dashed var(--color-washi)' }}
        >
          <div
            className="w-8 h-8 rounded-full flex items-center justify-center"
            style={{ background: 'var(--color-terracotta)', color: 'var(--color-warmwhite)' }}
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" aria-hidden="true">
              <line x1="12" y1="5" x2="12" y2="19" />
              <line x1="5" y1="12" x2="19" y2="12" />
            </svg>
          </div>
          <div className="flex-1">
            <div
              className="font-display text-[13px] flex items-center"
              style={{ color: 'var(--color-ink)' }}
            >
              {typed || (
                <span style={{ color: 'var(--color-fadedink)' }}>album name…</span>
              )}
              <motion.span
                className="inline-block w-[1.5px] h-3.5 ml-0.5"
                style={{ background: 'var(--color-terracotta)' }}
                animate={{ opacity: [1, 0.2, 1] }}
                transition={{ duration: 0.9, repeat: Infinity }}
              />
            </div>
            <div className="text-[9px]" style={{ color: 'var(--color-softbrown)' }}>
              empty · 0 photos
            </div>
          </div>
          <motion.button
            animate={pressed ? { scale: 0.94 } : { scale: 1 }}
            transition={{ duration: 0.18 }}
            className="px-3 py-1.5 rounded-full font-headline text-[11px] gradient-terracotta"
            style={{ color: 'var(--color-warmwhite)' }}
          >
            Create
          </motion.button>
        </div>
      </div>

      {/* Album grid */}
      <div className="px-4 mt-3 grid grid-cols-2 gap-2.5 pb-20">
        <AnimatePresence>
          {showAlbum && (
            <motion.div
              key="new"
              initial={{ opacity: 0, scale: 0.7, rotate: -6, y: -10 }}
              animate={{ opacity: 1, scale: 1, rotate: -2, y: 0 }}
              exit={{ opacity: 0, scale: 0.8 }}
              transition={{ duration: 0.55, type: 'spring', damping: 14 }}
            >
              <AlbumTile
                name={NEW_ALBUM_NAME}
                accent="#A04E33"
                count={0}
                fresh
              />
            </motion.div>
          )}
        </AnimatePresence>
        {SAMPLE_ALBUMS.map((a) => (
          <AlbumTile key={a.id} name={a.name} accent={a.accent} count={a.count} />
        ))}
      </div>

      <BottomNav active="albums" />
    </ScreenPaper>
  )
}

function AlbumTile({
  name,
  accent,
  count,
  fresh,
}: {
  name: string
  accent: string
  count: number
  fresh?: boolean
}) {
  const scenes: import('../data/sampleData').SceneKey[] = [
    'sunset-beach',
    'birthday-cake',
    'mountain-hike',
    'flower-field',
    'kids-yard',
  ]
  const idx = Math.abs(name.length * 7) % scenes.length
  return (
    <div
      className="relative rounded-[10px] overflow-hidden shadow-paper"
      style={{
        background: 'var(--color-polaroid)',
        border: `1px solid ${accent}33`,
        padding: 6,
      }}
    >
      {fresh && (
        <span
          className="absolute -top-1 left-3 z-10 h-2.5 rounded-[1px]"
          style={{
            width: 28,
            background: 'rgba(212, 165, 116, 0.7)',
            border: '1px solid rgba(160, 78, 51, 0.18)',
            transform: 'rotate(-7deg)',
          }}
        />
      )}
      <div
        className="relative w-full overflow-hidden rounded-[6px]"
        style={{ aspectRatio: '4/3', background: accent }}
      >
        {!fresh && <PhotoScene scene={scenes[idx]} />}
        {fresh && (
          <div
            className="w-full h-full flex items-center justify-center font-script text-[15px]"
            style={{ background: `${accent}22`, color: accent }}
          >
            empty for now
          </div>
        )}
        <span
          className="absolute bottom-1 right-1 px-1.5 py-0.5 rounded text-[9px] font-medium"
          style={{
            background: 'rgba(255, 252, 246, 0.88)',
            color: 'var(--color-ink)',
          }}
        >
          {count} photos
        </span>
      </div>
      <div className="pt-1.5 pb-0.5 px-1">
        <div
          className="font-headline text-[12px] truncate"
          style={{ color: 'var(--color-ink)' }}
        >
          {name}
        </div>
        <div className="text-[9px]" style={{ color: 'var(--color-softbrown)' }}>
          May 2026
        </div>
      </div>
    </div>
  )
}

const wait = (ms: number) => new Promise<void>((r) => setTimeout(r, ms))
