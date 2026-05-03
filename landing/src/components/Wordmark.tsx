import { motion } from 'framer-motion'

type Props = {
  size?: number
  showLabel?: boolean
  className?: string
}

export function Wordmark({ size = 28, showLabel = true, className = '' }: Props) {
  return (
    <div className={`inline-flex items-center gap-2 ${className}`}>
      <motion.div
        className="gradient-badge inline-flex items-center justify-center rounded-full text-warmwhite shadow-paper"
        style={{ width: size, height: size }}
        whileHover={{ rotate: -6, scale: 1.04 }}
        transition={{ type: 'spring', stiffness: 200, damping: 12 }}
        aria-hidden="true"
      >
        <span
          className="font-headline italic"
          style={{ fontSize: size * 0.55, lineHeight: 1, color: 'var(--color-warmwhite)' }}
        >
          k
        </span>
      </motion.div>
      {showLabel && (
        <span className="font-headline text-ink" style={{ fontSize: size * 0.7 }}>
          kin
        </span>
      )}
    </div>
  )
}
