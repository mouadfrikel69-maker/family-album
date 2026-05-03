import { motion } from 'framer-motion'
import { Avatar } from '../components/Avatar'
import { PhotoScene } from '../components/PhotoScene'
import { ScreenPaper } from '../components/ScreenPaper'
import { Wordmark } from '../components/Wordmark'
import { SAMPLE_MEMBERS, SAMPLE_PHOTOS } from '../data/sampleData'

type Props = {
  /** Animate likes / comment heart pulses */
  animated?: boolean
}

export function HomeFeedMock({ animated = true }: Props) {
  return (
    <ScreenPaper>
      {/* Top bar */}
      <div className="flex items-center justify-between px-4 pt-2">
        <button
          aria-label="Open menu"
          className="rounded-full p-1.5"
          style={{ background: 'var(--color-polaroid)' }}
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round">
            <line x1="4" y1="6" x2="20" y2="6" />
            <line x1="4" y1="12" x2="20" y2="12" />
            <line x1="4" y1="18" x2="20" y2="18" />
          </svg>
        </button>
        <div className="flex flex-col items-center">
          <Wordmark size={20} showLabel={false} />
          <span className="font-headline text-[13px]" style={{ color: 'var(--color-ink)' }}>
            Frikel family
          </span>
        </div>
        <button
          aria-label="Notifications"
          className="rounded-full p-1.5 relative"
          style={{ background: 'var(--color-polaroid)' }}
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9" />
            <path d="M13.7 21a2 2 0 0 1-3.4 0" />
          </svg>
          <span
            className="absolute top-0.5 right-0.5 w-2 h-2 rounded-full"
            style={{ background: 'var(--color-terracotta)' }}
          />
        </button>
      </div>

      {/* Avatar strip */}
      <div className="mt-3 flex gap-2 px-3 overflow-hidden">
        {SAMPLE_MEMBERS.slice(0, 6).map((m) => (
          <div key={m.id} className="flex flex-col items-center min-w-[44px]">
            <Avatar initials={m.initials} color={m.color} size={36} />
            <span
              className="mt-1 text-[9px] truncate w-full text-center"
              style={{ color: 'var(--color-mocha)' }}
            >
              {m.relationship}
            </span>
          </div>
        ))}
      </div>

      {/* Greeting */}
      <div className="px-4 mt-2">
        <span
          className="font-headline italic"
          style={{ color: 'var(--color-ink)', fontSize: 18 }}
        >
          Today, with the people who feel like home.
        </span>
      </div>

      {/* Polaroid feed */}
      <div className="px-4 mt-3 flex flex-col gap-4 pb-16">
        {SAMPLE_PHOTOS.map((p, idx) => {
          const author = SAMPLE_MEMBERS.find((m) => m.id === p.authorId)!
          return (
            <FeedPolaroid
              key={p.id}
              author={author}
              caption={p.caption}
              scene={p.scene}
              tape={p.tape}
              rotation={p.rotation}
              animated={animated}
              delay={idx * 0.4}
            />
          )
        })}
      </div>

      {/* Bottom nav */}
      <BottomNav active="home" />
    </ScreenPaper>
  )
}

type FeedPolaroidProps = {
  author: { name: string; relationship: string; color: string; initials: string }
  caption: string
  scene: import('../data/sampleData').SceneKey
  tape: 'left' | 'right' | 'none'
  rotation: number
  animated: boolean
  delay: number
}

function FeedPolaroid({ author, caption, scene, tape, rotation, animated, delay }: FeedPolaroidProps) {
  return (
    <div
      className="rounded-[8px] shadow-polaroid"
      style={{
        background: 'var(--color-polaroid)',
        padding: 10,
        transform: `rotate(${rotation}deg)`,
      }}
    >
      {/* Header */}
      <div className="flex items-center gap-2 mb-2">
        <Avatar initials={author.initials} color={author.color} size={24} ring={false} />
        <div className="flex flex-col leading-tight">
          <span className="font-headline text-[12px]" style={{ color: 'var(--color-ink)' }}>
            {author.relationship}
          </span>
          <span className="text-[10px]" style={{ color: 'var(--color-fadedink)' }}>
            just now
          </span>
        </div>
      </div>
      {/* Photo with washi tape */}
      <div className="relative">
        {tape !== 'none' && (
          <span
            className="absolute -top-1.5 z-10 h-3 rounded-[1px]"
            style={{
              width: 36,
              background: 'rgba(212, 165, 116, 0.7)',
              border: '1px solid rgba(160, 78, 51, 0.18)',
              left: tape === 'left' ? 12 : undefined,
              right: tape === 'right' ? 12 : undefined,
              transform: tape === 'left' ? 'rotate(-8deg)' : 'rotate(7deg)',
            }}
          />
        )}
        <div
          className="overflow-hidden rounded-[2px]"
          style={{ aspectRatio: '4/3', background: 'var(--color-blush)' }}
        >
          <PhotoScene scene={scene} />
        </div>
      </div>
      {/* Caption */}
      <div
        className="mt-2 font-display"
        style={{ color: 'var(--color-ink)', fontSize: 13, lineHeight: 1.3 }}
      >
        {caption}
      </div>
      {/* Actions */}
      <div className="mt-2 flex items-center gap-3">
        <motion.button
          aria-label="Like"
          className="flex items-center gap-1"
          animate={
            animated
              ? { scale: [1, 1.18, 1], color: ['#8B7560', '#C76B4A', '#C76B4A'] }
              : undefined
          }
          transition={{ duration: 1.6, delay, repeat: Infinity, repeatDelay: 4 }}
        >
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
            <path d="M12 21s-7.5-5-9.5-9.5C1 8 4 5 7 5c2 0 4 1 5 3 1-2 3-3 5-3 3 0 6 3 4.5 6.5C19.5 16 12 21 12 21z" />
          </svg>
          <span className="text-[11px]" style={{ color: 'var(--color-mocha)' }}>3</span>
        </motion.button>
        <button aria-label="Comment" className="flex items-center gap-1" style={{ color: 'var(--color-softbrown)' }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
            <path d="M21 11.5a8.4 8.4 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.4 8.4 0 0 1-3.8-.9L3 21l1.9-5.7a8.4 8.4 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.4 8.4 0 0 1 3.8-.9h.5a8.5 8.5 0 0 1 8 8v.5z" />
          </svg>
          <span className="text-[11px]" style={{ color: 'var(--color-mocha)' }}>2</span>
        </button>
      </div>
    </div>
  )
}

type NavProps = { active: 'home' | 'albums' | 'memories' | 'you' }

export function BottomNav({ active }: NavProps) {
  const items: { key: NavProps['active']; label: string; icon: React.ReactNode }[] = [
    {
      key: 'home',
      label: 'Home',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
          <path d="M3 12 12 3l9 9" />
          <path d="M5 10v10h14V10" />
        </svg>
      ),
    },
    {
      key: 'albums',
      label: 'Albums',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
          <rect x="3" y="3" width="8" height="8" rx="1.5" />
          <rect x="13" y="3" width="8" height="8" rx="1.5" />
          <rect x="3" y="13" width="8" height="8" rx="1.5" />
          <rect x="13" y="13" width="8" height="8" rx="1.5" />
        </svg>
      ),
    },
    {
      key: 'memories',
      label: 'Memories',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
          <path d="M12 21s-7.5-5-9.5-9.5C1 8 4 5 7 5c2 0 4 1 5 3 1-2 3-3 5-3 3 0 6 3 4.5 6.5C19.5 16 12 21 12 21z" />
        </svg>
      ),
    },
    {
      key: 'you',
      label: 'You',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
          <circle cx="12" cy="8" r="4" />
          <path d="M4 21c1.5-4 4.5-6 8-6s6.5 2 8 6" />
        </svg>
      ),
    },
  ]
  return (
    <div
      className="absolute bottom-0 left-0 right-0 flex justify-around items-end px-3 pb-3 pt-2"
      style={{
        background:
          'linear-gradient(to top, var(--color-polaroid) 60%, rgba(255, 252, 246, 0))',
        borderTop: '1px solid rgba(244, 219, 207, 0.6)',
      }}
    >
      {items.slice(0, 2).map((it) => (
        <NavBtn key={it.key} label={it.label} icon={it.icon} active={it.key === active} />
      ))}
      {/* Center plus */}
      <button
        aria-label="Add photo"
        className="-mt-7 rounded-full gradient-terracotta shadow-polaroid"
        style={{ width: 46, height: 46, color: 'var(--color-warmwhite)' }}
      >
        <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" className="mx-auto" aria-hidden="true">
          <line x1="12" y1="5" x2="12" y2="19" />
          <line x1="5" y1="12" x2="19" y2="12" />
        </svg>
      </button>
      {items.slice(2).map((it) => (
        <NavBtn key={it.key} label={it.label} icon={it.icon} active={it.key === active} />
      ))}
    </div>
  )
}

function NavBtn({
  label,
  icon,
  active,
}: {
  label: string
  icon: React.ReactNode
  active: boolean
}) {
  return (
    <button className="flex flex-col items-center gap-0.5">
      <span style={{ color: active ? 'var(--color-terracotta)' : 'var(--color-softbrown)' }}>
        {icon}
      </span>
      <span
        className="text-[9px]"
        style={{ color: active ? 'var(--color-terracotta-deep)' : 'var(--color-softbrown)', fontWeight: active ? 600 : 400 }}
      >
        {label}
      </span>
    </button>
  )
}
