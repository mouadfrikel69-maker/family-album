type Props = {
  initials: string
  color: string
  size?: number
  className?: string
  ring?: boolean
}

export function Avatar({ initials, color, size = 44, className = '', ring = true }: Props) {
  return (
    <span
      className={`inline-flex items-center justify-center rounded-full text-white font-headline ${className}`}
      style={{
        width: size,
        height: size,
        background: color,
        boxShadow: ring ? `0 0 0 3px var(--color-warmwhite), 0 4px 10px -4px ${color}66` : undefined,
        fontSize: size * 0.36,
        letterSpacing: 0.5,
      }}
      aria-hidden="true"
    >
      {initials}
    </span>
  )
}
