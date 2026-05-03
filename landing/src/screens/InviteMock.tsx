import { AnimatePresence, motion } from 'framer-motion'
import { useEffect, useState } from 'react'
import { ScreenPaper } from '../components/ScreenPaper'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'

const INVITE_CODE = 'YHM4-DRZK-9X2P-TVA8'
const INVITE_LINK = 'https://kin.family/join/YHM4-DRZK-9X2P-TVA8'

type Props = {
  /** When true, runs a copy / share / "code copied!" animation loop. */
  animated?: boolean
}

export function InviteMock({ animated = true }: Props) {
  const [step, setStep] = useState<'idle' | 'copied' | 'shared'>('idle')

  useEffect(() => {
    if (!animated) return
    let cancel = false
    const cycle = async () => {
      while (!cancel) {
        await wait(2200)
        if (cancel) break
        setStep('copied')
        await wait(1800)
        if (cancel) break
        setStep('shared')
        await wait(2200)
        if (cancel) break
        setStep('idle')
      }
    }
    cycle()
    return () => {
      cancel = true
    }
  }, [animated])

  return (
    <ScreenPaper>
      <div className="flex flex-col h-full px-5 pt-2">
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
            <Wordmark size={22} showLabel={false} />
          </div>
        </div>

        <div className="mt-2 text-center">
          <h2
            className="font-headline italic"
            style={{ fontSize: 20, color: 'var(--color-ink)', lineHeight: 1.1 }}
          >
            Bring everyone in.
          </h2>
          <p className="mt-1 text-[10px]" style={{ color: 'var(--color-mocha)' }}>
            Share with your family — anyone with the code joins.
          </p>
          <div className="mt-1.5 flex justify-center">
            <Tagline>a quiet circle, just for you</Tagline>
          </div>
        </div>

        {/* QR card */}
        <div
          className="mt-3 rounded-[18px] p-4 flex flex-col items-center"
          style={{
            background: 'var(--color-polaroid)',
            border: '1px solid var(--color-blush)',
          }}
        >
          <div
            className="rounded-[10px] p-2"
            style={{ background: 'var(--color-cream)' }}
          >
            <FakeQr size={108} />
          </div>
          <div
            className="mt-3 px-3 py-1.5 rounded-md font-mono text-[12px] tracking-widest"
            style={{
              background: 'var(--color-cream)',
              color: 'var(--color-ink)',
              border: '1px dashed var(--color-washi)',
            }}
          >
            {INVITE_CODE}
          </div>
          <div className="mt-1 text-[10px]" style={{ color: 'var(--color-softbrown)' }}>
            {INVITE_LINK}
          </div>
        </div>

        {/* Action buttons */}
        <div className="mt-3 flex flex-col gap-2 px-1 relative">
          <ActionRow
            kind="copy"
            label="Copy invite link"
            highlighted={step === 'copied'}
          />
          <ActionRow
            kind="share"
            label="Share to family chat"
            highlighted={step === 'shared'}
          />
          <ActionRow kind="mail" label="Email it to Grandma" />

          {/* Floating toast */}
          <AnimatePresence>
            {step === 'copied' && (
              <motion.div
                key="copied-toast"
                initial={{ opacity: 0, y: 10, scale: 0.95 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                exit={{ opacity: 0, y: -8, scale: 0.95 }}
                transition={{ duration: 0.4 }}
                className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-7 rounded-full px-3 py-1.5 flex items-center gap-1.5"
                style={{ background: 'var(--color-ink)', color: 'var(--color-warmwhite)' }}
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                  <path d="M5 12l5 5L20 7" />
                </svg>
                <span className="font-display italic text-[11px]">Copied · paste anywhere</span>
              </motion.div>
            )}
            {step === 'shared' && (
              <motion.div
                key="share-sheet"
                initial={{ y: 80, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                exit={{ y: 100, opacity: 0 }}
                transition={{ duration: 0.45, type: 'spring', damping: 18 }}
                className="absolute -bottom-2 left-0 right-0 rounded-t-[22px] p-3 pb-4 z-10"
                style={{
                  background: 'var(--color-polaroid)',
                  boxShadow: '0 -10px 30px -8px rgba(61, 46, 38, 0.25)',
                }}
              >
                <div
                  className="mx-auto w-10 h-1 rounded-full mb-2"
                  style={{ background: 'var(--color-blush)' }}
                />
                <div
                  className="text-[11px] text-center mb-2 font-headline"
                  style={{ color: 'var(--color-ink)' }}
                >
                  Share with…
                </div>
                <div className="flex justify-around">
                  {[
                    { c: '#25D366', l: 'WhatsApp' },
                    { c: '#0088CC', l: 'Telegram' },
                    { c: '#34B7F1', l: 'Messages' },
                    { c: '#EA4335', l: 'Mail' },
                  ].map((s) => (
                    <div key={s.l} className="flex flex-col items-center gap-1">
                      <div
                        className="w-9 h-9 rounded-2xl"
                        style={{ background: s.c, opacity: 0.9 }}
                      />
                      <span className="text-[9px]" style={{ color: 'var(--color-mocha)' }}>
                        {s.l}
                      </span>
                    </div>
                  ))}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </ScreenPaper>
  )
}

function ActionRow({
  kind,
  label,
  highlighted,
}: {
  kind: 'copy' | 'share' | 'mail'
  label: string
  highlighted?: boolean
}) {
  const icon =
    kind === 'copy' ? (
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <rect x="9" y="9" width="13" height="13" rx="2" />
        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
      </svg>
    ) : kind === 'share' ? (
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <circle cx="18" cy="5" r="3" />
        <circle cx="6" cy="12" r="3" />
        <circle cx="18" cy="19" r="3" />
        <path d="m8.6 13.5 6.8 4M15.4 6.5l-6.8 4" />
      </svg>
    ) : (
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <rect x="3" y="5" width="18" height="14" rx="2" />
        <path d="m3 7 9 6 9-6" />
      </svg>
    )
  return (
    <motion.div
      animate={
        highlighted
          ? { scale: [1, 1.02, 1], boxShadow: '0 8px 24px -10px rgba(199, 107, 74, 0.55)' }
          : { scale: 1 }
      }
      transition={{ duration: 0.6 }}
      className="rounded-full px-4 py-2.5 flex items-center gap-3"
      style={{
        background: highlighted ? 'var(--color-terracotta)' : 'var(--color-polaroid)',
        color: highlighted ? 'var(--color-warmwhite)' : 'var(--color-ink)',
        border: highlighted ? 'none' : '1px solid var(--color-blush)',
      }}
    >
      <span style={{ color: highlighted ? 'var(--color-warmwhite)' : 'var(--color-terracotta)' }}>
        {icon}
      </span>
      <span className="font-headline text-[12px]">{label}</span>
    </motion.div>
  )
}

// A friendly faux-QR pattern (small grid). Not a real scannable QR — just visuals.
function FakeQr({ size = 96 }: { size?: number }) {
  const grid = 11
  const cell = size / grid
  const seed = (i: number, j: number) => (Math.sin(i * 7.13 + j * 3.71) + 1) / 2 > 0.55
  const isFinder = (i: number, j: number) => {
    const inFinder = (gi: number, gj: number) =>
      gi >= 0 && gi < 3 && gj >= 0 && gj < 3
    return (
      inFinder(i, j) ||
      inFinder(i, j - (grid - 3)) ||
      inFinder(i - (grid - 3), j)
    )
  }
  const dots: { x: number; y: number }[] = []
  for (let i = 0; i < grid; i++) {
    for (let j = 0; j < grid; j++) {
      if (isFinder(i, j)) continue
      if (seed(i, j)) dots.push({ x: j * cell, y: i * cell })
    }
  }
  return (
    <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} aria-hidden="true">
      {dots.map((d, i) => (
        <rect key={i} x={d.x + cell * 0.1} y={d.y + cell * 0.1} width={cell * 0.8} height={cell * 0.8} fill="#3D2E26" />
      ))}
      {/* Three finder squares */}
      {[
        [0, 0],
        [0, (grid - 3) * cell],
        [(grid - 3) * cell, 0],
      ].map(([y, x], i) => (
        <g key={i}>
          <rect x={x} y={y} width={cell * 3} height={cell * 3} fill="#3D2E26" />
          <rect x={x + cell * 0.4} y={y + cell * 0.4} width={cell * 2.2} height={cell * 2.2} fill="#FAF4EC" />
          <rect x={x + cell * 0.9} y={y + cell * 0.9} width={cell * 1.2} height={cell * 1.2} fill="#3D2E26" />
        </g>
      ))}
    </svg>
  )
}

const wait = (ms: number) => new Promise<void>((r) => setTimeout(r, ms))
