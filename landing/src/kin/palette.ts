// Deterministic 8-tone warm palette matching the Kotlin app.
// Index is stable per name/id so a person always has the same colour.
export const ACCENTS = [
  "#E8B4A0", // dusty
  "#D4A574", // washi
  "#C76B4A", // terracotta
  "#B8C4A8", // sage
  "#F4DBCF", // blush
  "#D17A5C", // light terracotta
  "#A04E33", // terracotta deep
  "#8B7560", // soft brown
] as const;

function hashString(s: string): number {
  let h = 0;
  for (let i = 0; i < s.length; i++) {
    h = (h * 31 + s.charCodeAt(i)) | 0;
  }
  return Math.abs(h);
}

export function accentFor(seed: string): string {
  return ACCENTS[hashString(seed) % ACCENTS.length];
}

export function initialsFor(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return "?";
  if (parts.length === 1) return parts[0]!.slice(0, 2).toUpperCase();
  return ((parts[0]![0] ?? "") + (parts[parts.length - 1]![0] ?? "")).toUpperCase();
}
