import Wordmark from "../kin/Wordmark";

export default function Footer() {
  return (
    <footer className="border-t border-blush bg-warm/60">
      <div className="mx-auto flex max-w-6xl flex-col items-center gap-4 px-6 py-10 text-center text-[12px] text-ink-mocha sm:flex-row sm:justify-between sm:text-left">
        <div className="flex flex-col items-center gap-1 sm:items-start">
          <Wordmark size={22} />
          <span>private · ad-free · just family</span>
        </div>
        <nav className="flex items-center gap-5">
          <a className="hover:text-terracotta" href="#walkthrough">walkthrough</a>
          <a className="hover:text-terracotta" href="#invite">invite</a>
          <a className="hover:text-terracotta" href="#members">members</a>
          <a
            className="hover:text-terracotta"
            href="https://github.com/mouadfrikel69-maker/family-album"
          >
            source
          </a>
        </nav>
        <div className="font-script text-ink-soft">© the people who feel like home</div>
      </div>
    </footer>
  );
}
