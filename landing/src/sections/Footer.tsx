import { PromiseStrip } from '../components/PromiseStrip'
import { Tagline } from '../components/Tagline'
import { Wordmark } from '../components/Wordmark'

export function Footer() {
  return (
    <footer
      id="download"
      className="relative pt-16 pb-10"
      style={{ background: 'var(--color-ink)', color: 'var(--color-warmwhite)' }}
    >
      <div className="max-w-6xl mx-auto px-6 sm:px-10">
        <div className="rounded-[26px] p-8 sm:p-12 relative overflow-hidden" style={{ background: '#2A1F19' }}>
          <span
            aria-hidden="true"
            className="absolute -top-10 -left-10 w-56 h-56 rounded-full blur-3xl"
            style={{ background: 'rgba(199, 107, 74, 0.4)' }}
          />
          <span
            aria-hidden="true"
            className="absolute -bottom-16 -right-10 w-72 h-72 rounded-full blur-3xl"
            style={{ background: 'rgba(184, 196, 168, 0.25)' }}
          />
          <div className="relative grid sm:grid-cols-[1fr_auto] gap-6 items-center">
            <div>
              <h2
                className="font-headline italic leading-[1.1]"
                style={{
                  fontSize: 'clamp(28px, 4vw, 44px)',
                }}
              >
                Bring everyone in.
              </h2>
              <p
                className="mt-3 max-w-lg"
                style={{ color: '#E8DCD0', fontSize: 16, lineHeight: 1.55 }}
              >
                Kin is free, ad-free, and Android-only by design. Install it on one phone in
                the family — share the invite code with the rest.
              </p>
              <div className="mt-5 flex flex-wrap gap-3">
                <a
                  href="https://github.com/mouadfrikel69-maker/family-album"
                  target="_blank"
                  rel="noreferrer"
                  className="px-6 py-3 rounded-full inline-flex items-center gap-2 font-semibold"
                  style={{ background: 'var(--color-warmwhite)', color: 'var(--color-ink)' }}
                >
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                    <path d="M12 .5C5.65.5.5 5.65.5 12a11.5 11.5 0 0 0 7.86 10.92c.58.1.79-.25.79-.56v-2.05c-3.2.7-3.88-1.37-3.88-1.37-.52-1.32-1.27-1.67-1.27-1.67-1.04-.71.08-.7.08-.7 1.15.08 1.76 1.18 1.76 1.18 1.02 1.74 2.68 1.24 3.34.95.1-.74.4-1.24.73-1.53-2.55-.29-5.23-1.27-5.23-5.66 0-1.25.45-2.27 1.18-3.07-.12-.29-.51-1.46.11-3.04 0 0 .96-.31 3.16 1.17a11 11 0 0 1 5.75 0c2.2-1.48 3.16-1.17 3.16-1.17.62 1.58.23 2.75.11 3.04.74.8 1.18 1.82 1.18 3.07 0 4.4-2.68 5.36-5.23 5.65.41.35.78 1.05.78 2.12v3.14c0 .31.21.67.8.56A11.5 11.5 0 0 0 23.5 12C23.5 5.65 18.35.5 12 .5Z" />
                  </svg>
                  Read the spec on GitHub
                </a>
                <a
                  href="#tour"
                  className="px-6 py-3 rounded-full inline-flex items-center gap-2 font-semibold"
                  style={{
                    background: 'transparent',
                    border: '1px solid rgba(255, 251, 245, 0.25)',
                    color: 'var(--color-warmwhite)',
                  }}
                >
                  Watch the tour again
                </a>
              </div>
            </div>

            <div className="flex flex-col items-start sm:items-end gap-3">
              <Wordmark size={28} />
              <Tagline>private · ad-free · just family</Tagline>
              <div
                className="font-mono text-[12px] px-3 py-1.5 rounded-md"
                style={{
                  background: 'rgba(255, 251, 245, 0.08)',
                  color: '#F4DBCF',
                  border: '1px dashed rgba(255, 251, 245, 0.2)',
                }}
              >
                YHM4-DRZK-9X2P-TVA8
              </div>
            </div>
          </div>
        </div>

        <div className="mt-8 grid sm:grid-cols-2 items-center gap-3">
          <div className="flex items-center gap-3 flex-wrap">
            <Wordmark size={24} />
            <span style={{ color: '#B5A696', fontSize: 13 }}>
              · the family album · made with care
            </span>
          </div>
          <div className="sm:justify-self-end" style={{ color: '#B5A696' }}>
            <PromiseStrip small />
          </div>
        </div>
      </div>
    </footer>
  )
}
