const items = [
  { name: "Kotlin", note: "100% Kotlin, JDK 11" },
  { name: "Jetpack Compose", note: "Material 3 + Navigation" },
  { name: "Supabase", note: "Auth + 2 tables, nothing else" },
  { name: "Ktor", note: "GoTrue client" },
  { name: "ZXing", note: "QR rendered on Compose Canvas" },
  { name: "EncryptedSharedPreferences", note: "AES-256-GCM" },
  { name: "FLAG_SECURE", note: "screenshots blocked" },
  { name: "Photo Picker", note: "no storage permission" },
];

export default function TechStrip() {
  return (
    <section className="relative">
      <div className="mx-auto max-w-6xl px-6 py-16">
        <div className="flex flex-col items-center text-center">
          <span className="pill">built calmly</span>
          <h3 className="mt-3 font-serif text-[24px] italic text-ink sm:text-[28px]">
            a small stack, on purpose
          </h3>
        </div>
        <div className="mt-8 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          {items.map((it) => (
            <div
              key={it.name}
              className="rounded-2xl border border-blush bg-polaroid/80 px-4 py-3 shadow-soft"
            >
              <p className="font-serif text-[15px] text-ink">{it.name}</p>
              <p className="mt-0.5 text-[11px] text-ink-mocha">{it.note}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
