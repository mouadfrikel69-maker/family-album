import { motion } from 'framer-motion'
import { ScreenPaper } from '../components/ScreenPaper'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'

type Props = { typed: string }

/** Profile-setup / Create family combined card used in the walkthrough. */
export function CreateFamilyMock({ typed }: Props) {
  return (
    <ScreenPaper>
      <div className="flex flex-col h-full px-5 pt-2">
        <div className="flex justify-center">
          <Wordmark size={22} />
        </div>

        <div className="mt-3 text-center">
          <h2
            className="font-headline italic"
            style={{ fontSize: 20, color: 'var(--color-ink)', lineHeight: 1.1 }}
          >
            One last step.
          </h2>
          <p className="mt-1 text-[11px]" style={{ color: 'var(--color-mocha)' }}>
            Start your family circle, or join one.
          </p>
          <div className="mt-1.5 flex justify-center">
            <Tagline>the people who feel like home</Tagline>
          </div>
        </div>

        {/* Choice card - selected */}
        <div
          className="mt-4 rounded-[18px] p-3 flex items-center gap-3"
          style={{
            background: 'var(--color-polaroid)',
            border: '2px solid var(--color-terracotta)',
            boxShadow: '0 6px 16px -8px rgba(199, 107, 74, 0.35)',
          }}
        >
          <div
            className="w-9 h-9 rounded-full flex items-center justify-center"
            style={{ background: 'var(--color-terracotta)', color: 'var(--color-warmwhite)' }}
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
              <path d="M3 12 12 3l9 9" />
              <path d="M5 10v10h6v-7h2v7h6V10" />
            </svg>
          </div>
          <div className="flex-1">
            <div className="font-headline text-[12px]" style={{ color: 'var(--color-ink)' }}>
              Start a new family
            </div>
            <div className="text-[10px]" style={{ color: 'var(--color-mocha)' }}>
              Create the album. Invite the rest later.
            </div>
          </div>
        </div>

        {/* Choice card - unselected */}
        <div
          className="mt-2 rounded-[18px] p-3 flex items-center gap-3"
          style={{
            background: 'var(--color-polaroid)',
            border: '1px solid var(--color-blush)',
          }}
        >
          <div
            className="w-9 h-9 rounded-full flex items-center justify-center"
            style={{ background: 'var(--color-washi)', color: 'var(--color-warmwhite)' }}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
              <rect x="3" y="5" width="18" height="14" rx="2" />
              <path d="m3 7 9 6 9-6" />
            </svg>
          </div>
          <div className="flex-1">
            <div className="font-headline text-[12px]" style={{ color: 'var(--color-ink)' }}>
              I have an invite code
            </div>
            <div className="text-[10px]" style={{ color: 'var(--color-mocha)' }}>
              Join the family album you were invited to.
            </div>
          </div>
        </div>

        {/* Family-name field with typing caret */}
        <label
          className="mt-4 block rounded-[14px] px-3.5 py-2.5"
          style={{
            background: 'var(--color-polaroid)',
            border: '1px solid var(--color-blush)',
          }}
        >
          <span
            className="block text-[10px] uppercase tracking-wider"
            style={{ color: 'var(--color-softbrown)' }}
          >
            family name
          </span>
          <div
            className="font-display text-[15px] flex items-center"
            style={{ color: 'var(--color-ink)' }}
          >
            <motion.span
              key={typed}
              initial={{ opacity: 0.6 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.15 }}
            >
              {typed}
            </motion.span>
            <motion.span
              className="inline-block w-[1.5px] h-4 align-middle ml-0.5"
              style={{ background: 'var(--color-terracotta)' }}
              animate={{ opacity: [1, 0.2, 1] }}
              transition={{ duration: 1, repeat: Infinity }}
            />
          </div>
        </label>

        <div className="mt-auto pb-4">
          <button
            className="w-full py-3 rounded-full gradient-terracotta shadow-polaroid font-semibold text-[13px]"
            style={{ color: 'var(--color-warmwhite)' }}
          >
            Create family
          </button>
        </div>
      </div>
    </ScreenPaper>
  )
}
