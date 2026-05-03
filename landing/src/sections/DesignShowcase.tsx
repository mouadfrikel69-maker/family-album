import { motion } from 'framer-motion'
import { Polaroid } from '../components/Polaroid'
import { PhotoScene } from '../components/PhotoScene'
import { Tagline } from '../components/Tagline'

const SWATCHES = [
  { name: 'CreamPaper', hex: '#FAF4EC', text: 'var(--color-ink)' },
  { name: 'PolaroidWhite', hex: '#FFFCF6', text: 'var(--color-ink)' },
  { name: 'Terracotta', hex: '#C76B4A', text: '#FFFBF5' },
  { name: 'TerracottaDeep', hex: '#A04E33', text: '#FFFBF5' },
  { name: 'DustyRose', hex: '#E8B4A0', text: 'var(--color-ink)' },
  { name: 'BlushPink', hex: '#F4DBCF', text: 'var(--color-ink)' },
  { name: 'WashiTan', hex: '#D4A574', text: 'var(--color-ink)' },
  { name: 'SageMist', hex: '#B8C4A8', text: 'var(--color-ink)' },
  { name: 'InkBrown', hex: '#3D2E26', text: '#FFFBF5' },
  { name: 'Mocha', hex: '#6B5544', text: '#FFFBF5' },
] as const

export function DesignShowcase() {
  return (
    <section id="design" className="py-16 sm:py-24" style={{ background: '#F1E4D6' }}>
      <div className="max-w-6xl mx-auto px-6 sm:px-10 grid lg:grid-cols-2 gap-12 items-center">
        <div>
          <div
            className="font-script text-[20px]"
            style={{ color: 'var(--color-terracotta-deep)' }}
          >
            design notes
          </div>
          <h2
            className="mt-2 font-headline leading-[1.1]"
            style={{
              color: 'var(--color-ink)',
              fontSize: 'clamp(28px, 4vw, 44px)',
            }}
          >
            An open scrapbook,
            <br />
            <span className="font-display text-terracotta-deep">on warm paper.</span>
          </h2>
          <p
            className="mt-4 max-w-md"
            style={{ color: 'var(--color-mocha)', fontSize: 16, lineHeight: 1.55 }}
          >
            Polaroids with washi tape. Italic serif headlines. Cream paper speckles. Eight
            warm tones derived from a stable hash so a person always has the same colour.
          </p>

          {/* Swatch strip */}
          <div className="mt-6 grid grid-cols-5 gap-2 max-w-md">
            {SWATCHES.map((s) => (
              <div
                key={s.hex}
                className="rounded-[12px] p-2.5 shadow-paper"
                style={{
                  background: s.hex,
                  color: s.text,
                  border: '1px solid rgba(61, 46, 38, 0.06)',
                }}
              >
                <div className="text-[10px] font-headline">{s.name}</div>
                <div className="text-[10px] font-mono" style={{ opacity: 0.85 }}>
                  {s.hex}
                </div>
              </div>
            ))}
          </div>

          <div className="mt-6">
            <Tagline>terracotta on cream — always</Tagline>
          </div>
        </div>

        {/* Polaroid stack */}
        <div className="relative h-[420px] sm:h-[460px]">
          <motion.div
            initial={{ opacity: 0, y: 30, rotate: -10 }}
            whileInView={{ opacity: 1, y: 0, rotate: -8 }}
            transition={{ duration: 0.7 }}
            viewport={{ once: true }}
            className="absolute top-2 left-2 sm:left-10"
          >
            <Polaroid rotation={-8} tape="left" caption="Sunday lunch" byline="— Mom · this morning" drift>
              <PhotoScene scene="kitchen-window" />
            </Polaroid>
          </motion.div>
          <motion.div
            initial={{ opacity: 0, y: 30, rotate: 10 }}
            whileInView={{ opacity: 1, y: 0, rotate: 6 }}
            transition={{ duration: 0.7, delay: 0.1 }}
            viewport={{ once: true }}
            className="absolute top-12 right-2 sm:right-10"
          >
            <Polaroid rotation={6} tape="right" caption="first hike of the season" byline="— Dad · yesterday" size={200} drift>
              <PhotoScene scene="mountain-hike" />
            </Polaroid>
          </motion.div>
          <motion.div
            initial={{ opacity: 0, y: 40, rotate: -3 }}
            whileInView={{ opacity: 1, y: 0, rotate: -2 }}
            transition={{ duration: 0.7, delay: 0.2 }}
            viewport={{ once: true }}
            className="absolute bottom-2 left-1/2 -translate-x-1/2"
          >
            <Polaroid rotation={-2} tape="left" caption="Yusuf turned eight today" byline="— Lina · 2 hours ago" drift>
              <PhotoScene scene="birthday-cake" />
            </Polaroid>
          </motion.div>
        </div>
      </div>
    </section>
  )
}
