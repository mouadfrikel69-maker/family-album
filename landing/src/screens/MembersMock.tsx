import { AnimatePresence, motion } from 'framer-motion'
import { useEffect, useState } from 'react'
import { Avatar } from '../components/Avatar'
import { ScreenPaper } from '../components/ScreenPaper'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'
import { SAMPLE_MEMBERS } from '../data/sampleData'

type Props = { animated?: boolean }

const NEW_MEMBER = {
  id: 'm-new',
  name: 'Aunt Salma',
  relationship: 'Aunt',
  initials: 'SA',
  color: '#D4A574',
  role: 'Member' as const,
}

export function MembersMock({ animated = true }: Props) {
  const [showNew, setShowNew] = useState(false)
  const [showToast, setShowToast] = useState(false)

  useEffect(() => {
    if (!animated) return
    let cancel = false
    const cycle = async () => {
      while (!cancel) {
        setShowNew(false)
        setShowToast(false)
        await wait(1600)
        if (cancel) break
        setShowNew(true)
        setShowToast(true)
        await wait(1600)
        if (cancel) break
        setShowToast(false)
        await wait(2200)
      }
    }
    cycle()
    return () => {
      cancel = true
    }
  }, [animated])

  const baseMembers = SAMPLE_MEMBERS

  return (
    <ScreenPaper>
      <div className="flex flex-col h-full">
        {/* Header */}
        <div className="px-4 pt-2">
          <div className="flex items-center gap-2">
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
          <div className="text-center mt-2">
            <h2
              className="font-headline italic"
              style={{ fontSize: 20, color: 'var(--color-ink)', lineHeight: 1.1 }}
            >
              Frikel family
            </h2>
            <div
              className="mt-0.5 text-[10px]"
              style={{ color: 'var(--color-mocha)' }}
            >
              {baseMembers.length + (showNew ? 1 : 0)} members · since 2026
            </div>
            <div className="mt-1 flex justify-center">
              <Tagline>the people who feel like home</Tagline>
            </div>
          </div>
        </div>

        {/* Invite shortcut */}
        <div className="px-4 mt-2">
          <div
            className="rounded-[14px] p-2.5 flex items-center gap-2"
            style={{ background: 'var(--color-polaroid)' }}
          >
            <div
              className="w-9 h-9 rounded-full flex items-center justify-center"
              style={{ background: 'var(--color-blush)', color: 'var(--color-terracotta-deep)' }}
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <line x1="19" y1="8" x2="19" y2="14" />
                <line x1="22" y1="11" x2="16" y2="11" />
              </svg>
            </div>
            <div className="flex-1">
              <div className="font-headline text-[12px]" style={{ color: 'var(--color-ink)' }}>
                Invite someone
              </div>
              <div className="text-[10px]" style={{ color: 'var(--color-mocha)' }}>
                Share the code so they can join.
              </div>
            </div>
            <span
              className="px-2.5 py-1 rounded-full font-headline text-[10px]"
              style={{
                background: 'var(--color-terracotta)',
                color: 'var(--color-warmwhite)',
              }}
            >
              Share
            </span>
          </div>
        </div>

        {/* Members list */}
        <div className="px-4 mt-2 flex-1 overflow-hidden relative">
          <AnimatePresence>
            {showNew && (
              <motion.div
                key="new-member"
                initial={{ opacity: 0, y: -16, scale: 0.92 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                exit={{ opacity: 0 }}
                transition={{ duration: 0.5, type: 'spring', damping: 14 }}
              >
                <MemberRow {...NEW_MEMBER} highlight />
              </motion.div>
            )}
          </AnimatePresence>
          <div className="flex flex-col gap-1.5 pt-1.5">
            {baseMembers.map((m) => (
              <MemberRow key={m.id} {...m} />
            ))}
          </div>

          {/* Toast */}
          <AnimatePresence>
            {showToast && (
              <motion.div
                key="toast"
                initial={{ y: 30, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                exit={{ y: 30, opacity: 0 }}
                transition={{ duration: 0.4 }}
                className="absolute bottom-3 left-3 right-3 rounded-full px-3.5 py-2 flex items-center gap-2 shadow-paper"
                style={{
                  background: 'var(--color-ink)',
                  color: 'var(--color-warmwhite)',
                }}
              >
                <span
                  className="inline-block w-1.5 h-1.5 rounded-full"
                  style={{ background: 'var(--color-sage)' }}
                />
                <span className="font-display italic text-[11px]">
                  Aunt Salma joined the family
                </span>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </ScreenPaper>
  )
}

function MemberRow({
  name,
  relationship,
  initials,
  color,
  role,
  highlight,
}: {
  name: string
  relationship: string
  initials: string
  color: string
  role: 'Admin' | 'Member' | 'Viewer'
  highlight?: boolean
}) {
  return (
    <motion.div
      animate={
        highlight
          ? {
              boxShadow: [
                '0 0 0 0 rgba(184, 196, 168, 0)',
                '0 0 0 6px rgba(184, 196, 168, 0.35)',
                '0 0 0 0 rgba(184, 196, 168, 0)',
              ],
            }
          : { boxShadow: '0 0 0 0 rgba(184, 196, 168, 0)' }
      }
      transition={{ duration: 1.4 }}
      className="rounded-[14px] p-2.5 flex items-center gap-2.5"
      style={{
        background: 'var(--color-polaroid)',
        border: highlight
          ? '1px solid var(--color-sage)'
          : '1px solid var(--color-blush)',
      }}
    >
      <Avatar initials={initials} color={color} size={36} />
      <div className="flex-1">
        <div className="font-headline text-[12px]" style={{ color: 'var(--color-ink)' }}>
          {name}
        </div>
        <div className="text-[10px]" style={{ color: 'var(--color-softbrown)' }}>
          {relationship}
        </div>
      </div>
      <span
        className="px-2 py-0.5 rounded-full text-[9px] font-headline"
        style={{
          background:
            role === 'Admin'
              ? 'var(--color-terracotta)'
              : role === 'Member'
                ? 'var(--color-blush)'
                : 'var(--color-sage)',
          color:
            role === 'Admin' ? 'var(--color-warmwhite)' : 'var(--color-ink)',
        }}
      >
        {role.toLowerCase()}
      </span>
    </motion.div>
  )
}

const wait = (ms: number) => new Promise<void>((r) => setTimeout(r, ms))
