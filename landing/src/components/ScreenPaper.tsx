import type { ReactNode } from 'react'

type Props = { children: ReactNode; className?: string }

/**
 * The cream paper background used inside every phone-mockup screen.
 * Matches the PaperBackground composable: cream base + warm radial glow + speckles.
 */
export function ScreenPaper({ children, className = '' }: Props) {
  return <div className={`paper relative w-full h-full ${className}`}>{children}</div>
}
