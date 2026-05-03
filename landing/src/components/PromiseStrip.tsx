type Props = { className?: string; small?: boolean }

const dots = [
  { color: 'var(--color-terracotta)', label: 'private' },
  { color: 'var(--color-washi)', label: 'ad-free' },
  { color: 'var(--color-sage)', label: 'just family' },
]

export function PromiseStrip({ className = '', small = false }: Props) {
  return (
    <div
      className={`inline-flex items-center gap-3 ${className}`}
      aria-label="Kin promise: private, ad-free, just family"
    >
      {dots.map((d, i) => (
        <span key={d.label} className="inline-flex items-center gap-3">
          <span className="inline-flex items-center gap-1.5">
            <span
              className="inline-block rounded-full"
              style={{ background: d.color, width: small ? 6 : 8, height: small ? 6 : 8 }}
            />
            <span
              className="font-display text-mocha"
              style={{ fontSize: small ? 12 : 14, color: 'var(--color-mocha)' }}
            >
              {d.label}
            </span>
          </span>
          {i < dots.length - 1 && (
            <span className="text-fadedink" style={{ color: 'var(--color-fadedink)' }}>
              ·
            </span>
          )}
        </span>
      ))}
    </div>
  )
}
