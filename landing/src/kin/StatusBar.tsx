type Props = {
  time?: string;
};

export default function StatusBar({ time = "9:41" }: Props) {
  return (
    <div className="relative z-10 flex items-center justify-between px-6 pt-3 pb-1 text-[11px] font-semibold text-ink">
      <span>{time}</span>
      <div className="flex items-center gap-1.5">
        {/* signal */}
        <svg width="14" height="10" viewBox="0 0 14 10" aria-hidden>
          <rect x="0" y="6" width="2" height="4" rx="0.5" fill="currentColor" />
          <rect x="3" y="4" width="2" height="6" rx="0.5" fill="currentColor" />
          <rect x="6" y="2" width="2" height="8" rx="0.5" fill="currentColor" />
          <rect x="9" y="0" width="2" height="10" rx="0.5" fill="currentColor" />
        </svg>
        {/* wifi */}
        <svg width="13" height="10" viewBox="0 0 13 10" aria-hidden fill="currentColor">
          <path d="M6.5 1c-2.5 0-4.7 1-6.4 2.7l1.4 1.4C2.9 3.7 4.6 3 6.5 3s3.6.7 4.9 2L12.9 3.7C11.2 2 9 1 6.5 1Z" />
          <path d="M6.5 5c-1.4 0-2.6.5-3.5 1.4L4.4 7.8c.6-.5 1.3-.8 2.1-.8s1.5.3 2.1.8l1.4-1.4C9.1 5.5 7.9 5 6.5 5Z" />
          <circle cx="6.5" cy="9" r="1" />
        </svg>
        {/* battery */}
        <svg width="22" height="10" viewBox="0 0 22 10" aria-hidden>
          <rect x="0.5" y="0.5" width="18" height="9" rx="2" ry="2" fill="none" stroke="currentColor" strokeOpacity="0.8" />
          <rect x="2" y="2" width="14" height="6" rx="1" fill="currentColor" />
          <rect x="19.5" y="3" width="2" height="4" rx="1" fill="currentColor" opacity="0.7" />
        </svg>
      </div>
    </div>
  );
}
