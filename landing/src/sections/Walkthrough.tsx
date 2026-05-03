import { AnimatePresence, motion } from 'framer-motion'
import { useEffect, useRef, useState } from 'react'
import { PhoneFrame } from '../components/PhoneFrame'
import { AlbumsMock } from '../screens/AlbumsMock'
import { AuthMock } from '../screens/AuthMock'
import { CreateFamilyMock } from '../screens/CreateFamilyMock'
import { HomeFeedMock } from '../screens/HomeFeedMock'
import { InviteMock } from '../screens/InviteMock'
import { MembersMock } from '../screens/MembersMock'
import { MemoriesMock } from '../screens/MemoriesMock'
import { OnboardingMock } from '../screens/OnboardingMock'
import { UploadMock } from '../screens/UploadMock'

type StepKey =
  | 'onboarding'
  | 'auth'
  | 'create-family'
  | 'home'
  | 'albums'
  | 'memories'
  | 'invite'
  | 'members'
  | 'upload'

type Step = {
  key: StepKey
  label: string
  caption: string
  /** dwell time in ms */
  hold: number
  render: (animated: boolean) => React.ReactNode
}

// Family-name typing sequence used in the create-family step.
const FAMILY_NAME = 'Frikel family'

const STEPS: Step[] = [
  {
    key: 'onboarding',
    label: 'Open the app',
    caption: 'a soft welcome, no signup gate',
    hold: 4200,
    render: () => <OnboardingMock />,
  },
  {
    key: 'auth',
    label: 'Sign in',
    caption: 'email + password, secrets stay encrypted',
    hold: 4000,
    render: () => <AuthMock />,
  },
  {
    key: 'create-family',
    label: 'Create your family',
    caption: 'one circle, just yours',
    hold: 5200,
    render: (animated) => <CreateFamilyTyped animated={animated} />,
  },
  {
    key: 'home',
    label: 'The home album',
    caption: 'polaroids, not feeds',
    hold: 5400,
    render: (animated) => <HomeFeedMock animated={animated} />,
  },
  {
    key: 'albums',
    label: 'Albums',
    caption: 'a chapter for every season',
    hold: 5400,
    render: (animated) => <AlbumsMock animated={animated} />,
  },
  {
    key: 'memories',
    label: 'Memories',
    caption: 'this week, in previous years',
    hold: 4400,
    render: () => <MemoriesMock />,
  },
  {
    key: 'invite',
    label: 'Invite the family',
    caption: 'QR · code · link',
    hold: 5400,
    render: (animated) => <InviteMock animated={animated} />,
  },
  {
    key: 'members',
    label: 'Family members',
    caption: 'the people who feel like home',
    hold: 5200,
    render: (animated) => <MembersMock animated={animated} />,
  },
  {
    key: 'upload',
    label: 'Add a photo',
    caption: 'gallery or camera, stored on device',
    hold: 5200,
    render: (animated) => <UploadMock animated={animated} />,
  },
]

export function Walkthrough() {
  const [idx, setIdx] = useState(0)
  const [paused, setPaused] = useState(false)
  const timerRef = useRef<number | null>(null)

  useEffect(() => {
    if (paused) return
    const t = window.setTimeout(
      () => setIdx((i) => (i + 1) % STEPS.length),
      STEPS[idx].hold,
    )
    timerRef.current = t
    return () => window.clearTimeout(t)
  }, [idx, paused])

  const current = STEPS[idx]

  return (
    <div className="flex flex-col items-center gap-6 lg:flex-row lg:gap-10 lg:items-center">
      <div
        className="relative"
        onMouseEnter={() => setPaused(true)}
        onMouseLeave={() => setPaused(false)}
        onTouchStart={() => setPaused(true)}
        onTouchEnd={() => setPaused(false)}
      >
        <PhoneFrame width={290}>
          <AnimatePresence mode="wait">
            <motion.div
              key={current.key}
              initial={{ opacity: 0, x: 30 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -30 }}
              transition={{ duration: 0.55, ease: [0.22, 1, 0.36, 1] }}
              className="absolute inset-0"
            >
              {current.render(true)}
            </motion.div>
          </AnimatePresence>
        </PhoneFrame>

        {/* Progress bar around the bottom */}
        <div
          className="absolute -bottom-3 left-0 right-0 mx-auto h-1.5 rounded-full overflow-hidden"
          style={{ width: 240, background: 'var(--color-blush)' }}
        >
          <motion.div
            key={`${idx}-${paused ? 'p' : 'r'}`}
            className="h-full gradient-terracotta"
            initial={{ width: '0%' }}
            animate={paused ? { width: '0%' } : { width: '100%' }}
            transition={{ duration: paused ? 0 : current.hold / 1000, ease: 'linear' }}
          />
        </div>
      </div>

      {/* Step list (acts like a vertical-tab caption track) */}
      <div className="w-full lg:w-auto lg:max-w-xs">
        <div
          className="font-script text-[20px] mb-2"
          style={{ color: 'var(--color-terracotta-deep)' }}
        >
          a little tour
        </div>
        <div className="flex flex-col gap-1.5">
          {STEPS.map((s, i) => {
            const active = i === idx
            return (
              <button
                key={s.key}
                onClick={() => setIdx(i)}
                className="flex items-center gap-3 text-left rounded-xl px-3 py-2 transition-colors"
                style={{
                  background: active ? 'var(--color-polaroid)' : 'transparent',
                  border: active
                    ? '1px solid var(--color-blush)'
                    : '1px solid transparent',
                  boxShadow: active ? '0 6px 16px -10px rgba(61, 46, 38, 0.2)' : 'none',
                }}
              >
                <span
                  className="w-6 h-6 rounded-full inline-flex items-center justify-center text-[10px] font-headline flex-shrink-0"
                  style={{
                    background: active ? 'var(--color-terracotta)' : 'var(--color-cream)',
                    color: active ? 'var(--color-warmwhite)' : 'var(--color-mocha)',
                    border: active ? 'none' : '1px solid var(--color-blush)',
                  }}
                >
                  {String(i + 1).padStart(2, '0')}
                </span>
                <span className="flex-1">
                  <span
                    className="block font-headline text-[13px]"
                    style={{
                      color: active ? 'var(--color-ink)' : 'var(--color-mocha)',
                    }}
                  >
                    {s.label}
                  </span>
                  <span
                    className="block font-script text-[14px]"
                    style={{ color: 'var(--color-softbrown)', lineHeight: 1.1 }}
                  >
                    {s.caption}
                  </span>
                </span>
              </button>
            )
          })}
        </div>
        <div className="mt-3 text-[11px]" style={{ color: 'var(--color-softbrown)' }}>
          Tap a step to jump. Hover to pause.
        </div>
      </div>
    </div>
  )
}

/** A wrapper that drives the typing animation inside CreateFamilyMock. */
function CreateFamilyTyped({ animated }: { animated: boolean }) {
  const [typed, setTyped] = useState(animated ? '' : FAMILY_NAME)
  useEffect(() => {
    if (!animated) return
    let cancel = false
    let i = 0
    const tick = () => {
      if (cancel) return
      i++
      setTyped(FAMILY_NAME.slice(0, i))
      if (i < FAMILY_NAME.length) {
        window.setTimeout(tick, 110)
      }
    }
    const startTimer = window.setTimeout(tick, 800)
    return () => {
      cancel = true
      window.clearTimeout(startTimer)
    }
  }, [animated])
  return <CreateFamilyMock typed={typed} />
}
