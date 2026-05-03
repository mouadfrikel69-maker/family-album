import { motion } from 'framer-motion'
import { ScreenPaper } from '../components/ScreenPaper'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'

type Props = { typedEmail?: string; typedPassword?: string }

export function AuthMock({
  typedEmail = 'mom@frikel.family',
  typedPassword = '••••••••••',
}: Props) {
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
            <Wordmark size={22} />
          </div>
        </div>

        <div className="mt-6 text-center">
          <h2
            className="font-headline italic"
            style={{ fontSize: 22, color: 'var(--color-ink)', lineHeight: 1.1 }}
          >
            Welcome back.
          </h2>
          <p className="mt-1.5 text-[11px]" style={{ color: 'var(--color-mocha)' }}>
            Sign in to your family circle.
          </p>
          <div className="mt-2 flex justify-center">
            <Tagline>kept just between you</Tagline>
          </div>
        </div>

        <div className="mt-6 flex flex-col gap-3">
          <Field label="email">
            <motion.span
              key={typedEmail}
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.4 }}
            >
              {typedEmail}
              <span
                className="inline-block w-[1px] h-3.5 align-middle ml-0.5"
                style={{ background: 'var(--color-terracotta)' }}
              />
            </motion.span>
          </Field>
          <Field label="password">{typedPassword}</Field>
          <div className="text-right text-[10px]" style={{ color: 'var(--color-terracotta-deep)' }}>
            Forgot password?
          </div>
        </div>

        <div className="mt-auto pb-4">
          <button
            className="w-full py-3 rounded-full gradient-terracotta shadow-polaroid font-semibold text-[13px]"
            style={{ color: 'var(--color-warmwhite)' }}
          >
            Sign in
          </button>
          <p className="mt-3 text-center text-[10px]" style={{ color: 'var(--color-mocha)' }}>
            New here? <span style={{ color: 'var(--color-terracotta-deep)' }}>Create an account</span>
          </p>
        </div>
      </div>
    </ScreenPaper>
  )
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label
      className="block rounded-[14px] px-3.5 py-2.5"
      style={{
        background: 'var(--color-polaroid)',
        border: '1px solid var(--color-blush)',
      }}
    >
      <span
        className="block text-[10px] uppercase tracking-wider"
        style={{ color: 'var(--color-softbrown)' }}
      >
        {label}
      </span>
      <span
        className="block font-display text-[13px]"
        style={{ color: 'var(--color-ink)' }}
      >
        {children}
      </span>
    </label>
  )
}
