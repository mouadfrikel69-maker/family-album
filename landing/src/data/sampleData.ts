// Sample data used inside the phone-mockup screens.
// Names, captions, and avatar tints intentionally mirror the warm "Kin" tone.

export type SampleMember = {
  id: string
  name: string
  relationship: string
  initials: string
  color: string // one of the 8 warm accent tones from the spec
  role: 'Admin' | 'Member' | 'Viewer'
}

export const SAMPLE_MEMBERS: SampleMember[] = [
  { id: 'm1', name: 'Mom', relationship: 'Mom', initials: 'MO', color: '#C76B4A', role: 'Admin' },
  { id: 'm2', name: 'Dad', relationship: 'Dad', initials: 'DA', color: '#D4A574', role: 'Member' },
  { id: 'm3', name: 'Lina', relationship: 'Sister', initials: 'LI', color: '#E8B4A0', role: 'Member' },
  { id: 'm4', name: 'Yusuf', relationship: 'Brother', initials: 'YU', color: '#B8C4A8', role: 'Member' },
  { id: 'm5', name: 'Grandma', relationship: 'Grandma', initials: 'GR', color: '#A04E33', role: 'Viewer' },
  { id: 'm6', name: 'Grandpa', relationship: 'Grandpa', initials: 'GP', color: '#8B7560', role: 'Viewer' },
]

export type SamplePhoto = {
  id: string
  authorId: string
  caption: string
  tape: 'left' | 'right' | 'none'
  rotation: number
  scene: SceneKey
}

export type SceneKey =
  | 'sunset-beach'
  | 'birthday-cake'
  | 'mountain-hike'
  | 'kitchen-window'
  | 'flower-field'
  | 'grandma-tea'
  | 'kids-yard'
  | 'rainy-cafe'

export const SAMPLE_PHOTOS: SamplePhoto[] = [
  {
    id: 'p1',
    authorId: 'm1',
    caption: 'Sunday lunch — everyone showed up',
    tape: 'left',
    rotation: -2,
    scene: 'kitchen-window',
  },
  {
    id: 'p2',
    authorId: 'm3',
    caption: 'Yusuf turned eight today',
    tape: 'right',
    rotation: 1.5,
    scene: 'birthday-cake',
  },
  {
    id: 'p3',
    authorId: 'm2',
    caption: 'first hike of the season',
    tape: 'none',
    rotation: -1,
    scene: 'mountain-hike',
  },
]

export const SAMPLE_ALBUMS = [
  { id: 'a1', name: 'Summer 2026', accent: '#C76B4A', count: 42 },
  { id: 'a2', name: 'Yusuf turns 8', accent: '#D4A574', count: 23 },
  { id: 'a3', name: 'Cabin trip', accent: '#B8C4A8', count: 17 },
  { id: 'a4', name: 'Grandma visits', accent: '#E8B4A0', count: 9 },
] as const
