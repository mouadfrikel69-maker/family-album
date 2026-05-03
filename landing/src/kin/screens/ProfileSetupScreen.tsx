import Avatar from "../Avatar";
import { ScreenShell } from "./Chrome";

export default function ProfileSetupScreen() {
  return (
    <ScreenShell>
      <div className="flex h-full flex-col px-6 pb-8 pt-6">
        <h1 className="font-serif text-[22px] italic leading-tight text-ink">
          who shall we call you?
        </h1>
        <p className="font-script text-[13px] text-ink-mocha">a name, a relation, a colour</p>

        <div className="mt-5 flex items-center gap-4 rounded-2xl border border-blush bg-polaroid p-3 shadow-soft">
          <Avatar name="Lina" size={56} />
          <div>
            <p className="font-serif text-[15px] text-ink">Lina</p>
            <p className="text-[11px] text-ink-mocha">Mom · LM</p>
          </div>
        </div>

        <div className="mt-5 space-y-3">
          <Field label="display name" value="Lina" />
          <Field label="relationship" value="Mom" placeholder="optional" />
        </div>

        <button type="button" className="btn-primary mt-auto w-full">
          continue
        </button>
      </div>
    </ScreenShell>
  );
}

function Field({ label, value, placeholder }: { label: string; value: string; placeholder?: string }) {
  return (
    <label className="block">
      <span className="mb-1 block text-[10px] uppercase tracking-[0.18em] text-ink-soft">
        {label} {placeholder && <span className="font-normal text-ink-faded normal-case tracking-normal">({placeholder})</span>}
      </span>
      <span className="block rounded-2xl border border-blush bg-polaroid px-4 py-3 text-[13px] text-ink shadow-soft">
        {value}
      </span>
    </label>
  );
}
