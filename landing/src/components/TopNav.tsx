import { useEffect, useState } from 'react'
import { Wordmark } from './Wordmark'

const LINKS = [
  { href: '#tour', label: 'Tour' },
  { href: '#invite', label: 'Invite' },
  { href: '#create-album', label: 'Albums' },
  { href: '#add-members', label: 'Members' },
  { href: '#design', label: 'Design' },
]

export function TopNav() {
  const [scrolled, setScrolled] = useState(false)
  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 24)
    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  return (
    <header
      className="sticky top-0 z-40 transition-all"
      style={{
        background: scrolled ? 'rgba(250, 244, 236, 0.85)' : 'transparent',
        backdropFilter: scrolled ? 'saturate(140%) blur(12px)' : 'none',
        WebkitBackdropFilter: scrolled ? 'saturate(140%) blur(12px)' : 'none',
        borderBottom: scrolled ? '1px solid rgba(244, 219, 207, 0.6)' : '1px solid transparent',
      }}
    >
      <div className="max-w-6xl mx-auto px-6 sm:px-10 py-3.5 flex items-center justify-between">
        <a href="#top" className="inline-flex items-center">
          <Wordmark size={26} />
        </a>
        <nav className="hidden md:flex items-center gap-1">
          {LINKS.map((l) => (
            <a
              key={l.href}
              href={l.href}
              className="px-3.5 py-1.5 rounded-full font-headline text-[14px] transition-colors"
              style={{ color: 'var(--color-mocha)' }}
            >
              {l.label}
            </a>
          ))}
        </nav>
        <a
          href="#download"
          className="px-4 py-2 rounded-full gradient-terracotta font-semibold text-[13px] inline-flex items-center gap-2"
          style={{ color: 'var(--color-warmwhite)' }}
        >
          Get Kin
        </a>
      </div>
    </header>
  )
}
