const pillars = [
  {
    title: "private",
    body: "Photos and albums never leave your phone. The cloud only knows who is in which family — never what you uploaded.",
    glyph: <Lock />,
  },
  {
    title: "ad-free",
    body: "No feeds from strangers, no notifications from outside the family, no ads, no analytics, no third-party SDKs.",
    glyph: <Heart />,
  },
  {
    title: "just family",
    body: "One family per account. Members join with a private 16-character invite code, never a phone number.",
    glyph: <Circle />,
  },
];

export default function Pillars() {
  return (
    <section id="why" className="relative">
      <div className="mx-auto max-w-6xl px-6 py-20 lg:py-28">
        <div className="flex flex-col items-center text-center">
          <span className="pill">the kin promise</span>
          <h2 className="mt-4 font-serif text-[34px] leading-tight text-ink sm:text-[44px]">
            <span className="italic text-terracotta">private</span> · ad-free · just family
          </h2>
          <p className="mt-3 max-w-xl text-[15px] text-ink-mocha">
            Every choice — from on-device storage to encrypted preferences to FLAG_SECURE — points
            in one direction: keep the family album small, warm, and yours.
          </p>
        </div>
        <div className="mt-12 grid gap-5 md:grid-cols-3">
          {pillars.map((p) => (
            <div
              key={p.title}
              className="kin-card flex flex-col gap-3 p-6"
            >
              <span className="grid h-11 w-11 place-items-center rounded-2xl bg-terracotta/10 text-terracotta">
                {p.glyph}
              </span>
              <h3 className="font-serif text-[22px] italic text-ink">{p.title}</h3>
              <p className="text-[14px] leading-relaxed text-ink-mocha">{p.body}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

function Lock() {
  return (
    <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
      <rect x="4" y="9" width="14" height="10" rx="2" stroke="currentColor" strokeWidth="1.6" />
      <path d="M7 9V6a4 4 0 0 1 8 0v3" stroke="currentColor" strokeWidth="1.6" />
      <circle cx="11" cy="14" r="1.4" fill="currentColor" />
    </svg>
  );
}
function Heart() {
  return (
    <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
      <path
        d="M11 18.5s-7-4.5-7-9.5A4 4 0 0 1 11 6a4 4 0 0 1 7 3c0 5-7 9.5-7 9.5Z"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinejoin="round"
      />
    </svg>
  );
}
function Circle() {
  return (
    <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
      <circle cx="11" cy="11" r="7" stroke="currentColor" strokeWidth="1.6" />
      <circle cx="11" cy="11" r="3" fill="currentColor" />
    </svg>
  );
}
