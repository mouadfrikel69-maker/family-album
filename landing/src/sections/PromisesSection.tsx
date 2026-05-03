import { motion } from 'framer-motion'

// Use raw hex strings so we can compose them with hex-alpha suffixes (e.g. `${color}1f`).
const PROMISES = [
  {
    color: '#C76B4A', // Terracotta
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <rect x="3" y="11" width="18" height="11" rx="2" />
        <path d="M7 11V7a5 5 0 0 1 10 0v4" />
      </svg>
    ),
    title: 'Photos stay on your phone',
    body: 'Kin only stores who is in which family. Your photos, captions, comments and likes never leave the device.',
  },
  {
    color: '#D4A574', // WashiTan
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <path d="M3 8h18" />
        <path d="M3 16h18" />
        <circle cx="7" cy="8" r="1.6" fill="currentColor" />
        <circle cx="13" cy="16" r="1.6" fill="currentColor" />
      </svg>
    ),
    title: 'No ads, ever',
    body: 'Calm. Quiet. No suggested follows. No "for you" feed. No third-party tracking.',
  },
  {
    color: '#7A8E66', // SageMist deep — slightly darker for icon contrast
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
        <circle cx="9" cy="7" r="4" />
        <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
        <path d="M16 3.13a4 4 0 0 1 0 7.75" />
      </svg>
    ),
    title: 'One family per account',
    body: 'Each circle is invite-only via a 16-character code. Outsiders cannot stumble in — even by accident.',
  },
  {
    color: '#A04E33', // TerracottaDeep
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <path d="M12 2 4 6v6c0 5 3.4 9 8 10 4.6-1 8-5 8-10V6l-8-4Z" />
        <path d="m9 12 2 2 4-4" />
      </svg>
    ),
    title: 'Encrypted at rest',
    body: 'AES-256-GCM via the Android Keystore. Backup, device-transfer and screenshots are all blocked for family content.',
  },
]

export function PromisesSection() {
  return (
    <section id="promises" className="py-16 sm:py-24 relative paper">
      <div className="max-w-6xl mx-auto px-6 sm:px-10">
        <div className="max-w-2xl">
          <div
            className="font-script text-[20px]"
            style={{ color: 'var(--color-terracotta-deep)' }}
          >
            our promises to your family
          </div>
          <h2
            className="mt-2 font-headline leading-[1.1]"
            style={{
              color: 'var(--color-ink)',
              fontSize: 'clamp(28px, 4vw, 44px)',
            }}
          >
            Built like a private letter,
            <br />
            <span className="font-display text-terracotta-deep">not a public stage.</span>
          </h2>
          <p
            className="mt-4"
            style={{ color: 'var(--color-mocha)', fontSize: 16, lineHeight: 1.55 }}
          >
            Every choice in Kin — from the encrypted preferences to the FLAG_SECURE
            screenshot block — is bent toward keeping your family album exactly where it
            belongs: with your family.
          </p>
        </div>

        <div className="mt-10 grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {PROMISES.map((p, i) => (
            <motion.div
              key={p.title}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: i * 0.08 }}
              viewport={{ once: true, margin: '-60px' }}
              className="rounded-[18px] p-5 shadow-paper"
              style={{
                background: 'var(--color-polaroid)',
                border: '1px solid var(--color-blush)',
              }}
            >
              <span
                className="inline-flex w-10 h-10 rounded-full items-center justify-center mb-3"
                style={{ background: `${p.color}1f`, color: p.color }}
              >
                {p.icon}
              </span>
              <div
                className="font-headline"
                style={{ color: 'var(--color-ink)', fontSize: 17 }}
              >
                {p.title}
              </div>
              <div
                className="mt-1.5"
                style={{ color: 'var(--color-softbrown)', fontSize: 14, lineHeight: 1.5 }}
              >
                {p.body}
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  )
}
