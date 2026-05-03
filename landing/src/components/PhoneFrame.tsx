import type { ReactNode } from 'react'

type Props = {
  children: ReactNode
  width?: number
  className?: string
  caption?: string
  glow?: boolean
}

/**
 * Phone-mockup bezel. The inner area is 9:19.5 like a modern Android phone.
 * All animated demos render inside this frame so they read as "GIFs in a phone".
 */
export function PhoneFrame({ children, width = 280, className = '', caption, glow = true }: Props) {
  const frameW = width
  const frameH = Math.round(width * 2.05)
  const screenW = frameW - 16
  const screenH = frameH - 16

  return (
    <div className={`flex flex-col items-center gap-3 ${className}`}>
      <div
        className="relative"
        style={{ width: frameW, height: frameH, perspective: '1200px' }}
      >
        {/* Soft warm glow behind the phone */}
        {glow && (
          <div
            aria-hidden="true"
            className="absolute -inset-10 -z-10 blur-2xl opacity-60"
            style={{
              background:
                'radial-gradient(circle at 50% 50%, var(--color-blush) 0%, transparent 65%)',
            }}
          />
        )}

        {/* Bezel */}
        <div
          className="absolute inset-0 rounded-[44px]"
          style={{
            background:
              'linear-gradient(160deg, #2a201a 0%, #1a120e 40%, #2a201a 100%)',
            padding: 8,
            boxShadow:
              '0 30px 60px -20px rgba(61, 46, 38, 0.45), 0 12px 24px -8px rgba(61, 46, 38, 0.30), inset 0 0 0 1.5px rgba(255, 251, 245, 0.06)',
          }}
        >
          {/* Inner ring */}
          <div
            className="relative w-full h-full rounded-[36px] overflow-hidden"
            style={{
              background: 'var(--color-cream)',
              boxShadow: 'inset 0 0 0 2px rgba(255, 251, 245, 0.08)',
              width: screenW,
              height: screenH,
            }}
          >
            {/* Status bar */}
            <div
              className="absolute top-0 left-0 right-0 h-7 z-30 flex items-center justify-between px-5 text-[11px] font-medium pointer-events-none"
              style={{ color: 'var(--color-ink)' }}
            >
              <span>9:41</span>
              <div className="flex items-center gap-1.5">
                {/* signal */}
                <svg width="14" height="10" viewBox="0 0 14 10" aria-hidden="true">
                  <rect x="0" y="7" width="2" height="3" fill="currentColor" />
                  <rect x="3" y="5" width="2" height="5" fill="currentColor" />
                  <rect x="6" y="3" width="2" height="7" fill="currentColor" />
                  <rect x="9" y="0" width="2" height="10" fill="currentColor" opacity="0.4" />
                </svg>
                {/* wifi */}
                <svg width="12" height="10" viewBox="0 0 12 10" fill="none" stroke="currentColor" strokeWidth="1.5" aria-hidden="true">
                  <path d="M1 4 Q6 0 11 4" />
                  <path d="M3 6 Q6 3 9 6" />
                  <circle cx="6" cy="8.5" r="0.8" fill="currentColor" />
                </svg>
                {/* battery */}
                <svg width="20" height="10" viewBox="0 0 20 10" aria-hidden="true">
                  <rect x="0.5" y="0.5" width="16" height="9" rx="2" fill="none" stroke="currentColor" />
                  <rect x="2" y="2" width="11" height="6" fill="currentColor" />
                  <rect x="17" y="3" width="2" height="4" rx="0.5" fill="currentColor" />
                </svg>
              </div>
            </div>
            {/* Notch / dynamic-island */}
            <div
              className="absolute top-1.5 left-1/2 -translate-x-1/2 z-40 rounded-full"
              style={{
                width: 96,
                height: 22,
                background: '#0c0805',
                boxShadow: 'inset 0 0 0 1px rgba(255, 251, 245, 0.05)',
              }}
              aria-hidden="true"
            />
            {/* Content */}
            <div className="absolute inset-0 pt-7">
              <div className="relative w-full h-full overflow-hidden">{children}</div>
            </div>
          </div>
        </div>

        {/* Side buttons */}
        <span
          aria-hidden="true"
          className="absolute -right-0.5 rounded-full"
          style={{ top: 110, width: 3, height: 50, background: '#0c0805' }}
        />
        <span
          aria-hidden="true"
          className="absolute -left-0.5 rounded-full"
          style={{ top: 90, width: 3, height: 30, background: '#0c0805' }}
        />
        <span
          aria-hidden="true"
          className="absolute -left-0.5 rounded-full"
          style={{ top: 130, width: 3, height: 50, background: '#0c0805' }}
        />
      </div>

      {caption && (
        <span
          className="font-script"
          style={{ color: 'var(--color-mocha)', fontSize: 18 }}
        >
          {caption}
        </span>
      )}
    </div>
  )
}
