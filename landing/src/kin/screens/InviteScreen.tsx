import { motion } from "framer-motion";
import QrCode from "../QrCode";
import { INVITE_CODE, INVITE_LINK } from "../data";
import { ScreenShell } from "./Chrome";

type Props = {
  copied?: boolean;
  showShare?: boolean;
};

export default function InviteScreen({ copied, showShare }: Props) {
  return (
    <ScreenShell>
      <div className="flex h-full flex-col px-5 pb-6 pt-4">
        <div className="flex items-center justify-between">
          <button type="button" className="grid h-9 w-9 place-items-center rounded-full bg-polaroid text-ink-mocha shadow-soft">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M9 2 4 7l5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </button>
          <span className="font-serif text-[14px] italic text-ink">bring everyone in</span>
          <span className="h-9 w-9" />
        </div>

        <h1 className="mt-3 font-serif text-[22px] italic leading-tight text-ink">
          share with your family
        </h1>
        <p className="font-script text-[13px] text-ink-mocha">
          a quiet circle, just for you
        </p>

        <motion.div
          initial={{ scale: 0.96, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="kin-card mt-4 flex flex-col items-center p-4"
        >
          <div className="rounded-2xl bg-cream p-3">
            <QrCode content={INVITE_LINK} size={132} />
          </div>
          <div className="mt-3 text-center">
            <p className="font-mono text-[13px] tracking-[0.22em] text-ink">{INVITE_CODE}</p>
            <p className="mt-1 break-all text-[10px] text-ink-soft">{INVITE_LINK}</p>
          </div>
        </motion.div>

        <div className="mt-4 grid grid-cols-3 gap-2">
          <ActionPill label="copy" active={copied} icon={<CopyIcon />} />
          <ActionPill label="share" active={showShare} icon={<ShareIcon />} />
          <ActionPill label="mail" icon={<MailIcon />} />
        </div>

        {copied && (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="mt-3 self-center rounded-full bg-ink px-3 py-1.5 text-[11px] font-medium text-polaroid shadow-soft"
          >
            link copied
          </motion.div>
        )}

        {showShare && (
          <motion.div
            initial={{ y: 80, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            className="absolute bottom-0 left-0 right-0 rounded-t-3xl border border-blush bg-polaroid p-4 shadow-phone"
          >
            <span className="mx-auto mb-3 block h-1 w-10 rounded-full bg-blush" />
            <p className="text-[10px] uppercase tracking-[0.18em] text-ink-soft">share invite via</p>
            <div className="mt-3 grid grid-cols-4 gap-3">
              {["Messages", "WhatsApp", "Mail", "More"].map((s, i) => (
                <div key={s} className="flex flex-col items-center gap-1.5">
                  <div
                    className="grid h-12 w-12 place-items-center rounded-2xl text-polaroid shadow-soft"
                    style={{
                      background: ["#C76B4A", "#B8C4A8", "#D4A574", "#8B7560"][i],
                    }}
                  >
                    <ShareIcon />
                  </div>
                  <span className="text-[10px] text-ink-mocha">{s}</span>
                </div>
              ))}
            </div>
          </motion.div>
        )}
      </div>
    </ScreenShell>
  );
}

function ActionPill({
  label,
  icon,
  active,
}: {
  label: string;
  icon: React.ReactNode;
  active?: boolean;
}) {
  return (
    <div
      className={`flex flex-col items-center gap-1.5 rounded-2xl border bg-polaroid p-2.5 shadow-soft transition ${
        active ? "border-terracotta text-terracotta" : "border-blush text-ink-mocha"
      }`}
    >
      <span className="grid h-8 w-8 place-items-center rounded-xl bg-blush/40">{icon}</span>
      <span className="text-[10px] font-medium uppercase tracking-wider">{label}</span>
    </div>
  );
}
function CopyIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <rect x="3" y="3" width="8" height="8" rx="1.5" stroke="currentColor" strokeWidth="1.4" />
      <rect x="5" y="1" width="8" height="8" rx="1.5" stroke="currentColor" strokeWidth="1.4" />
    </svg>
  );
}
function ShareIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <path d="M7 2v7M7 2 4 5M7 2l3 3" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M2 8v3a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V8" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
    </svg>
  );
}
function MailIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <rect x="2" y="3" width="10" height="8" rx="1.5" stroke="currentColor" strokeWidth="1.4" />
      <path d="m2.5 4 4.5 3.5L11.5 4" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" />
    </svg>
  );
}
