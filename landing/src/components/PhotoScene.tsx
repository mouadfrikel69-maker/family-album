import type { ReactElement } from 'react'
import type { SceneKey } from '../data/sampleData'

type Props = {
  scene: SceneKey
  className?: string
}

// Lightweight SVG illustrations of family memories.
// Warm, painterly tones matching the Kin palette so each polaroid feels
// hand-developed rather than a stock photo.
export function PhotoScene({ scene, className = '' }: Props) {
  return (
    <svg
      viewBox="0 0 200 200"
      preserveAspectRatio="xMidYMid slice"
      className={`block w-full h-full ${className}`}
      aria-hidden="true"
    >
      {SCENES[scene]}
    </svg>
  )
}

const SCENES: Record<SceneKey, ReactElement> = {
  'sunset-beach': (
    <g>
      <defs>
        <linearGradient id="sb-sky" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#F4DBCF" />
          <stop offset="50%" stopColor="#E8B4A0" />
          <stop offset="100%" stopColor="#C76B4A" />
        </linearGradient>
        <linearGradient id="sb-sand" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#D4A574" />
          <stop offset="100%" stopColor="#A04E33" />
        </linearGradient>
      </defs>
      <rect width="200" height="200" fill="url(#sb-sky)" />
      <circle cx="100" cy="120" r="22" fill="#FFFBF5" opacity="0.85" />
      <rect y="140" width="200" height="60" fill="url(#sb-sand)" />
      <path d="M0 152 Q50 146 100 152 T200 152 L200 200 L0 200 Z" fill="#A04E33" opacity="0.6" />
      <circle cx="40" cy="60" r="3" fill="#FFFBF5" opacity="0.6" />
      <circle cx="170" cy="40" r="2" fill="#FFFBF5" opacity="0.5" />
    </g>
  ),
  'birthday-cake': (
    <g>
      <rect width="200" height="200" fill="#FFFCF6" />
      <rect width="200" height="120" fill="#F4DBCF" opacity="0.5" />
      <rect x="60" y="120" width="80" height="50" rx="6" fill="#FFFCF6" stroke="#C76B4A" strokeWidth="2" />
      <rect x="68" y="138" width="64" height="24" rx="3" fill="#E8B4A0" />
      <rect x="95" y="92" width="10" height="32" rx="2" fill="#FFFBF5" stroke="#8B7560" strokeWidth="1" />
      <ellipse cx="100" cy="86" rx="3.5" ry="6" fill="#C76B4A" />
      <ellipse cx="100" cy="86" rx="1.8" ry="3" fill="#F4DBCF" />
      <circle cx="40" cy="40" r="3" fill="#C76B4A" />
      <circle cx="160" cy="50" r="3" fill="#D4A574" />
      <circle cx="170" cy="100" r="2.5" fill="#B8C4A8" />
      <circle cx="30" cy="100" r="2.5" fill="#E8B4A0" />
    </g>
  ),
  'mountain-hike': (
    <g>
      <defs>
        <linearGradient id="mh-sky" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#FFFBF5" />
          <stop offset="100%" stopColor="#F4DBCF" />
        </linearGradient>
      </defs>
      <rect width="200" height="200" fill="url(#mh-sky)" />
      <circle cx="155" cy="50" r="14" fill="#FAF4EC" opacity="0.9" />
      <polygon points="0,150 50,80 100,140 150,70 200,150 200,200 0,200" fill="#8B7560" />
      <polygon points="50,80 70,110 30,110" fill="#FFFBF5" opacity="0.6" />
      <polygon points="150,70 170,100 130,100" fill="#FFFBF5" opacity="0.6" />
      <path d="M0 170 Q50 165 100 170 T200 170 L200 200 L0 200 Z" fill="#B8C4A8" />
      <path d="M0 180 Q60 175 120 180 T200 180 L200 200 L0 200 Z" fill="#A04E33" opacity="0.5" />
    </g>
  ),
  'kitchen-window': (
    <g>
      <rect width="200" height="200" fill="#F4DBCF" />
      <rect x="20" y="20" width="160" height="120" fill="#FFFBF5" stroke="#8B7560" strokeWidth="2" />
      <line x1="100" y1="20" x2="100" y2="140" stroke="#8B7560" strokeWidth="1.5" />
      <line x1="20" y1="80" x2="180" y2="80" stroke="#8B7560" strokeWidth="1.5" />
      <circle cx="60" cy="50" r="8" fill="#E8B4A0" />
      <circle cx="140" cy="50" r="6" fill="#B8C4A8" />
      <rect x="50" y="100" width="20" height="30" fill="#C76B4A" />
      <rect x="130" y="100" width="20" height="30" fill="#D4A574" />
      <rect y="140" width="200" height="60" fill="#D4A574" />
      <rect x="40" y="155" width="120" height="14" rx="2" fill="#8B7560" />
      <rect x="50" y="172" width="100" height="22" rx="2" fill="#A04E33" />
    </g>
  ),
  'flower-field': (
    <g>
      <defs>
        <linearGradient id="ff-sky" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#FFFBF5" />
          <stop offset="100%" stopColor="#F4DBCF" />
        </linearGradient>
      </defs>
      <rect width="200" height="200" fill="url(#ff-sky)" />
      <rect y="120" width="200" height="80" fill="#B8C4A8" />
      {Array.from({ length: 18 }).map((_, i) => {
        const cx = 8 + (i * 12) % 200
        const cy = 130 + ((i * 7) % 60)
        const colors = ['#C76B4A', '#E8B4A0', '#D4A574', '#A04E33']
        return <circle key={i} cx={cx} cy={cy} r="3" fill={colors[i % colors.length]} />
      })}
      <circle cx="170" cy="40" r="14" fill="#FAF4EC" opacity="0.9" />
    </g>
  ),
  'grandma-tea': (
    <g>
      <rect width="200" height="200" fill="#FFFCF6" />
      <rect width="200" height="200" fill="#F4DBCF" opacity="0.4" />
      <ellipse cx="100" cy="170" rx="80" ry="14" fill="#D4A574" opacity="0.6" />
      <path d="M70 130 Q70 100 100 100 Q130 100 130 130 L128 160 Q128 168 100 168 Q72 168 72 160 Z" fill="#FFFBF5" stroke="#8B7560" strokeWidth="1.5" />
      <path d="M130 110 Q150 110 150 130 Q150 150 130 150" fill="none" stroke="#8B7560" strokeWidth="1.5" />
      <ellipse cx="100" cy="105" rx="22" ry="3" fill="#A04E33" opacity="0.7" />
      <path d="M92 95 Q94 80 90 70" fill="none" stroke="#B5A696" strokeWidth="1.4" />
      <path d="M100 95 Q102 78 98 65" fill="none" stroke="#B5A696" strokeWidth="1.4" />
      <path d="M108 95 Q110 80 106 72" fill="none" stroke="#B5A696" strokeWidth="1.4" />
    </g>
  ),
  'kids-yard': (
    <g>
      <defs>
        <linearGradient id="ky-sky" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#FFFBF5" />
          <stop offset="100%" stopColor="#F4DBCF" />
        </linearGradient>
      </defs>
      <rect width="200" height="200" fill="url(#ky-sky)" />
      <rect y="130" width="200" height="70" fill="#B8C4A8" />
      <circle cx="80" cy="115" r="8" fill="#C76B4A" />
      <rect x="78" y="123" width="4" height="14" fill="#A04E33" />
      <line x1="80" y1="130" x2="72" y2="142" stroke="#A04E33" strokeWidth="2" />
      <line x1="80" y1="130" x2="88" y2="142" stroke="#A04E33" strokeWidth="2" />
      <circle cx="125" cy="118" r="7" fill="#D4A574" />
      <rect x="123" y="125" width="4" height="12" fill="#8B7560" />
      <line x1="125" y1="131" x2="118" y2="140" stroke="#8B7560" strokeWidth="2" />
      <line x1="125" y1="131" x2="132" y2="140" stroke="#8B7560" strokeWidth="2" />
      <circle cx="100" cy="100" r="6" fill="#E8B4A0" />
      <circle cx="40" cy="50" r="10" fill="#FFFBF5" opacity="0.7" />
      <circle cx="160" cy="40" r="6" fill="#FFFBF5" opacity="0.7" />
    </g>
  ),
  'rainy-cafe': (
    <g>
      <rect width="200" height="200" fill="#3D2E26" />
      <rect width="200" height="200" fill="#A04E33" opacity="0.3" />
      <rect x="20" y="40" width="160" height="100" rx="6" fill="#FFFBF5" opacity="0.92" />
      <rect x="30" y="50" width="60" height="80" fill="#F4DBCF" opacity="0.6" />
      <rect x="110" y="50" width="60" height="80" fill="#F4DBCF" opacity="0.6" />
      <line x1="100" y1="40" x2="100" y2="140" stroke="#8B7560" strokeWidth="1.2" />
      {Array.from({ length: 14 }).map((_, i) => {
        const x = 12 + i * 14
        return (
          <line
            key={i}
            x1={x}
            y1="0"
            x2={x - 20}
            y2="200"
            stroke="#FFFBF5"
            strokeOpacity="0.18"
            strokeWidth="1"
          />
        )
      })}
      <circle cx="55" cy="110" r="5" fill="#C76B4A" opacity="0.9" />
    </g>
  ),
}
