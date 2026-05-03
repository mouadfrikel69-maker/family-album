import Wordmark from "../Wordmark";
import { ScreenShell } from "./Chrome";

export default function AuthScreen() {
  return (
    <ScreenShell>
      <div className="flex h-full flex-col px-6 pb-8 pt-6">
        <div className="flex items-center gap-2">
          <button type="button" className="grid h-9 w-9 place-items-center rounded-full bg-polaroid text-ink-mocha shadow-soft" aria-label="Back">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M9 2 4 7l5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </button>
          <div className="ml-1 flex flex-col">
            <Wordmark size={20} />
          </div>
        </div>
        <div className="mt-6">
          <h1 className="font-serif text-[24px] italic leading-tight text-ink">welcome back</h1>
          <p className="font-script text-[13px] text-ink-mocha">a quiet circle, just for you</p>
        </div>
        <div className="mt-6 space-y-3">
          <Field label="email" value="lina@kin.family" />
          <Field label="password" value="••••••••" />
          <button type="button" className="btn-primary mt-2 w-full">
            sign in
          </button>
          <p className="text-center text-[11px] text-ink-mocha">
            no account?{" "}
            <span className="font-medium text-terracotta">create one</span>
          </p>
        </div>
        <div className="mt-auto rounded-2xl border border-blush bg-polaroid/70 p-3 text-[10px] leading-relaxed text-ink-mocha">
          <span className="font-semibold text-ink">private by design.</span>{" "}
          encrypted token storage, screen-capture blocked, no third-party sdk.
        </div>
      </div>
    </ScreenShell>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <label className="block">
      <span className="mb-1 block text-[10px] uppercase tracking-[0.18em] text-ink-soft">
        {label}
      </span>
      <span className="block rounded-2xl border border-blush bg-polaroid px-4 py-3 text-[13px] text-ink shadow-soft">
        {value}
      </span>
    </label>
  );
}
