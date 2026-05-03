import Wordmark from "../kin/Wordmark";

export default function Nav() {
  return (
    <header className="sticky top-0 z-40 border-b border-blush/60 bg-cream/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-3">
        <a href="#" className="flex items-center" aria-label="Kin home">
          <Wordmark size={26} />
        </a>
        <nav className="hidden items-center gap-6 text-[13px] text-ink-mocha md:flex">
          <a className="transition hover:text-terracotta" href="#walkthrough">walkthrough</a>
          <a className="transition hover:text-terracotta" href="#invite">invite</a>
          <a className="transition hover:text-terracotta" href="#create-album">albums</a>
          <a className="transition hover:text-terracotta" href="#members">members</a>
          <a className="transition hover:text-terracotta" href="#why">why kin</a>
        </nav>
        <a href="#download" className="btn-primary !px-4 !py-2 text-[13px]">
          get the app
        </a>
      </div>
    </header>
  );
}
