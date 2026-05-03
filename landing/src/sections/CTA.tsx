export default function CTA() {
  return (
    <section
      id="download"
      className="relative overflow-hidden border-y border-blush/70 bg-cream"
    >
      <div className="mx-auto flex max-w-6xl flex-col items-center gap-6 px-6 py-24 text-center lg:py-32">
        <span className="pill">on the play store soon</span>
        <h2 className="font-serif text-[40px] leading-tight text-ink sm:text-[56px]">
          bring everyone in.
          <br />
          <span className="italic text-terracotta">keep them close.</span>
        </h2>
        <p className="max-w-xl text-[15px] text-ink-mocha">
          Kin is a small Android app for one private family. Photos stay on your device.
          The only thing that travels is the invite code.
        </p>
        <div className="flex flex-wrap items-center justify-center gap-3">
          <a className="btn-primary" href="https://github.com/mouadfrikel69-maker/family-album">
            <PlayGlyph /> get it on Android
          </a>
          <a
            className="btn-secondary"
            href="https://github.com/mouadfrikel69-maker/family-album"
            target="_blank"
            rel="noreferrer"
          >
            <GithubGlyph /> read the spec
          </a>
        </div>
        <p className="font-script text-[18px] text-ink-mocha">warmth, kept close.</p>
      </div>
    </section>
  );
}

function PlayGlyph() {
  return (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <path d="M3.5 2v10l8-5-8-5Z" fill="currentColor" />
    </svg>
  );
}
function GithubGlyph() {
  return (
    <svg width="15" height="15" viewBox="0 0 16 16" fill="currentColor">
      <path d="M8 0a8 8 0 0 0-2.5 15.6c.4.07.55-.17.55-.39v-1.4c-2.2.49-2.66-1.06-2.66-1.06-.36-.93-.88-1.18-.88-1.18-.72-.49.05-.48.05-.48.8.06 1.22.83 1.22.83.71 1.21 1.86.86 2.31.66.07-.52.28-.86.5-1.05-1.76-.2-3.6-.88-3.6-3.93 0-.87.31-1.58.82-2.13-.08-.21-.36-1.02.08-2.12 0 0 .67-.21 2.2.81A7.5 7.5 0 0 1 8 4c.68 0 1.37.09 2 .27 1.53-1.02 2.2-.81 2.2-.81.44 1.1.16 1.91.08 2.12.51.55.82 1.26.82 2.13 0 3.05-1.85 3.72-3.61 3.92.29.25.54.74.54 1.49v2.21c0 .22.15.47.55.39A8 8 0 0 0 8 0Z" />
    </svg>
  );
}
