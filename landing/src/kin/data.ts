export type Member = {
  id: string;
  name: string;
  relationship: string;
  role: "Admin" | "Member" | "Viewer";
};

export type Photo = {
  id: string;
  authorId: string;
  caption: string;
  album?: string;
  ago: string;
  likes: number;
  comments: number;
  liked?: boolean;
  seed: string;
};

export type Album = {
  id: string;
  name: string;
  count: number;
  seed: string;
};

export const MEMBERS: Member[] = [
  { id: "u1", name: "Mom", relationship: "Mom", role: "Admin" },
  { id: "u2", name: "Dad", relationship: "Dad", role: "Admin" },
  { id: "u3", name: "Yara", relationship: "Sister", role: "Member" },
  { id: "u4", name: "Adam", relationship: "Brother", role: "Member" },
  { id: "u5", name: "Teta", relationship: "Grandma", role: "Viewer" },
  { id: "u6", name: "Jido", relationship: "Grandpa", role: "Viewer" },
];

export const PHOTOS: Photo[] = [
  {
    id: "p1",
    authorId: "u1",
    caption: "sunday picnic at the orchard 🍑",
    album: "Summer",
    ago: "2h",
    likes: 4,
    comments: 2,
    liked: true,
    seed: "orchard-sunday",
  },
  {
    id: "p2",
    authorId: "u3",
    caption: "Teta's cardamom cake — gone in ten minutes",
    album: "Kitchen",
    ago: "yesterday",
    likes: 6,
    comments: 3,
    seed: "cardamom-cake",
  },
  {
    id: "p3",
    authorId: "u4",
    caption: "first bike ride without training wheels 🚲",
    album: "Milestones",
    ago: "2d",
    likes: 8,
    comments: 5,
    seed: "first-bike",
  },
  {
    id: "p4",
    authorId: "u2",
    caption: "morning sage at the cabin",
    ago: "3d",
    likes: 3,
    comments: 1,
    seed: "morning-sage",
  },
];

export const ALBUMS: Album[] = [
  { id: "a1", name: "Summer", count: 24, seed: "summer-orchard" },
  { id: "a2", name: "Birthdays", count: 18, seed: "birthday-warm" },
  { id: "a3", name: "Kitchen", count: 32, seed: "kitchen-cake" },
  { id: "a4", name: "Cabin", count: 11, seed: "cabin-pines" },
  { id: "a5", name: "Milestones", count: 9, seed: "first-steps" },
  { id: "a6", name: "Holidays", count: 27, seed: "holiday-fire" },
];

export const INVITE_CODE = "K7M3-X9PQ-RT4N-Z2H8";
export const INVITE_LINK = `https://kin.family/join/${INVITE_CODE}`;
