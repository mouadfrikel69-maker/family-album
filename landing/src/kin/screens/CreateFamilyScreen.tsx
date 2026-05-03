import { ScreenShell } from "./Chrome";

export default function CreateFamilyScreen() {
  return (
    <ScreenShell>
      <div className="flex h-full flex-col px-6 pb-8 pt-6">
        <h1 className="font-serif text-[22px] italic leading-tight text-ink">
          create or join a family
        </h1>
        <p className="font-script text-[13px] text-ink-mocha">one little circle per person</p>

        <div className="mt-5 rounded-2xl border border-blush bg-polaroid p-4 shadow-soft">
          <p className="text-[10px] uppercase tracking-[0.18em] text-ink-soft">create a family</p>
          <p className="mt-1.5 font-serif text-[15px] text-ink">The Hassan family</p>
          <p className="mt-2 text-[11px] text-ink-mocha">
            we'll generate a private 16-character invite code for you.
          </p>
          <button type="button" className="btn-primary mt-3 w-full !py-2.5 text-sm">
            create family
          </button>
        </div>

        <div className="my-5 flex items-center gap-3 text-[10px] uppercase tracking-[0.2em] text-ink-faded">
          <span className="h-px flex-1 bg-blush" /> or <span className="h-px flex-1 bg-blush" />
        </div>

        <div className="rounded-2xl border border-blush bg-polaroid p-4 shadow-soft">
          <p className="text-[10px] uppercase tracking-[0.18em] text-ink-soft">join a family</p>
          <p className="mt-2 font-mono text-[12px] tracking-[0.18em] text-ink">K7M3-X9PQ-RT4N-Z2H8</p>
          <button type="button" className="btn-secondary mt-3 w-full !py-2.5 text-sm">
            join with code
          </button>
        </div>
      </div>
    </ScreenShell>
  );
}
