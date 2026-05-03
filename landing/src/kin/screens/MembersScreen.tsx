import { motion } from "framer-motion";
import Avatar from "../Avatar";
import { MEMBERS } from "../data";
import { BottomTabs, ScreenShell, TopBar } from "./Chrome";

type Props = {
  newMember?: { name: string; relationship: string };
  highlightInvite?: boolean;
};

export default function MembersScreen({ newMember, highlightInvite }: Props) {
  const list = newMember
    ? [{ id: "new", name: newMember.name, relationship: newMember.relationship, role: "Member" as const }, ...MEMBERS]
    : MEMBERS;
  return (
    <ScreenShell>
      <TopBar title="The Hassan family" onMenu={false} bell={false} />
      <div className="px-5">
        <h2 className="font-serif text-[20px] leading-tight text-ink">members</h2>
        <p className="font-script text-[13px] text-ink-mocha">a quiet circle, just for you</p>
      </div>
      <div className="mt-3 flex flex-col gap-2 px-4 pb-24">
        {list.slice(0, 6).map((m) => (
          <motion.div
            key={m.id}
            initial={m.id === "new" ? { opacity: 0, y: -8 } : false}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
            className={`flex items-center gap-3 rounded-2xl border border-blush bg-polaroid px-3 py-2.5 shadow-soft ${
              m.id === "new" ? "ring-2 ring-terracotta/40" : ""
            }`}
          >
            <Avatar name={m.name} size={40} />
            <div className="flex-1">
              <p className="font-serif text-[14px] leading-tight text-ink">{m.name}</p>
              <p className="text-[10px] text-ink-soft">{m.relationship}</p>
            </div>
            <span
              className={`rounded-full border px-2 py-0.5 text-[9px] font-medium uppercase tracking-wider ${
                m.role === "Admin"
                  ? "border-terracotta/40 bg-terracotta/10 text-terracotta-deep"
                  : m.role === "Member"
                  ? "border-sage/60 bg-sage/15 text-ink-mocha"
                  : "border-blush bg-blush/30 text-ink-mocha"
              }`}
            >
              {m.role}
            </span>
          </motion.div>
        ))}
      </div>

      <motion.button
        type="button"
        animate={
          highlightInvite
            ? { scale: [1, 1.04, 1], boxShadow: "0 12px 30px -6px rgba(199, 107, 74, 0.55)" }
            : { scale: 1 }
        }
        transition={{ duration: 1, repeat: highlightInvite ? Infinity : 0 }}
        className="absolute bottom-20 left-1/2 z-10 -translate-x-1/2 rounded-full bg-terracotta-gradient px-5 py-2.5 text-sm font-semibold text-polaroid shadow-soft"
      >
        + invite someone
      </motion.button>

      <BottomTabs active="home" />
    </ScreenShell>
  );
}
