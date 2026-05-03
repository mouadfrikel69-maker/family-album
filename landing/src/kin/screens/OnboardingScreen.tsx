import Wordmark from "../Wordmark";
import { ScreenShell } from "./Chrome";

export default function OnboardingScreen() {
  return (
    <ScreenShell>
      <div className="flex h-full flex-col items-center justify-between px-6 pb-8 pt-10">
        <div className="flex flex-col items-center gap-4 text-center">
          <Wordmark size={36} />
          <span className="pill mt-2">private · ad-free · just family</span>
        </div>
        <div className="flex flex-col items-center text-center">
          <h1 className="font-serif text-[28px] italic leading-tight text-ink">
            a little album
            <br />
            for the people
            <br />
            who feel like home
          </h1>
          <p className="mt-3 max-w-[220px] text-[12px] leading-relaxed text-ink-mocha">
            no feeds. no strangers. just kin — kept warmly on your phone.
          </p>
        </div>
        <div className="w-full space-y-3">
          <button type="button" className="btn-primary w-full">
            get started
          </button>
          <button type="button" className="btn-secondary w-full !py-2.5 text-sm">
            i already have an account
          </button>
          <p className="pt-1 text-center font-script text-[13px] text-ink-mocha">
            warmth, kept close
          </p>
        </div>
      </div>
    </ScreenShell>
  );
}
