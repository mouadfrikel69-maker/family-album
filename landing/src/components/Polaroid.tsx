import type { ReactNode } from 'react'

type Props = {
  rotation?: number
  tape?: 'left' | 'right' | 'none'
  size?: number
  children: ReactNode
  caption?: ReactNode
  byline?: ReactNode
  className?: string
  drift?: boolean
}

export function Polaroid({
  rotation = 0,
  tape = 'none',
  size = 220,
  children,
  caption,
  byline,
  className = '',
  drift = false,
}: Props) {
  return (
    <div
      className={`relative inline-block ${drift ? 'animate-drift' : ''} ${className}`}
      style={
        {
          transform: drift ? undefined : `rotate(${rotation}deg)`,
          ['--rot' as string]: `${rotation}deg`,
        } as React.CSSProperties
      }
    >
      {tape !== 'none' && (
        <span
          className="absolute -top-2 z-10 h-5 rounded-[2px]"
          style={{
            width: 56,
            background: 'rgba(212, 165, 116, 0.7)',
            border: '1px solid rgba(160, 78, 51, 0.18)',
            boxShadow: '0 1px 2px rgba(61, 46, 38, 0.15)',
            left: tape === 'left' ? 18 : undefined,
            right: tape === 'right' ? 18 : undefined,
            transform: tape === 'left' ? 'rotate(-8deg)' : 'rotate(7deg)',
          }}
          aria-hidden="true"
        />
      )}
      <div
        className="rounded-[4px] shadow-polaroid"
        style={{
          background: 'var(--color-polaroid)',
          padding: 10,
          paddingBottom: caption ? 6 : 10,
          width: size,
        }}
      >
        <div
          className="overflow-hidden rounded-[2px]"
          style={{ width: size - 20, height: size - 20, background: 'var(--color-blush)' }}
        >
          {children}
        </div>
        {(caption || byline) && (
          <div className="px-1 pt-2 pb-1.5">
            {caption && (
              <div
                className="font-display"
                style={{
                  color: 'var(--color-ink)',
                  fontSize: 14,
                  lineHeight: 1.25,
                }}
              >
                {caption}
              </div>
            )}
            {byline && (
              <div
                className="mt-0.5"
                style={{ color: 'var(--color-softbrown)', fontSize: 11 }}
              >
                {byline}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
