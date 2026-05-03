import type { Frame } from "../kin/FeatureMockup";
import AlbumsScreen from "../kin/screens/AlbumsScreen";
import { FeatureSection } from "./FeatureSection";

const NAME = "Cabin";
const typingFrames: Frame[] = NAME.split("").map((_, i) => ({
  key: `typing-${i}`,
  hold: 280,
  render: () => <AlbumsScreen showDialog newAlbumName={NAME.slice(0, i + 1)} />,
}));

const frames: Frame[] = [
  {
    key: "albums",
    caption: "tap + new",
    hold: 1800,
    render: () => <AlbumsScreen />,
  },
  {
    key: "open-dialog",
    caption: "name a new chapter",
    hold: 1000,
    render: () => <AlbumsScreen showDialog newAlbumName="" />,
  },
  ...typingFrames.map((f, i) => ({
    ...f,
    caption: i === typingFrames.length - 1 ? "tap create" : undefined,
    hold: i === typingFrames.length - 1 ? 1200 : 240,
  })),
  {
    key: "created",
    caption: "the album is yours",
    hold: 2600,
    render: () => <AlbumsScreen extraAlbum={{ name: "Cabin" }} highlightAlbumIndex={0} />,
  },
];

export default function CreateAlbumFeature() {
  return (
    <FeatureSection
      id="create-album"
      eyebrow="create an album"
      title={
        <>
          gather the moments
          <br />
          <span className="italic text-terracotta">into little chapters</span>
        </>
      }
      bullets={[
        "Open the new-album dialog from anywhere in the main scaffold.",
        "Each album gets its own deterministic warm accent — same name, same colour, forever.",
        "Tile grid with album cover and photo count, all rendered on cream paper.",
        "Open an album to see only the photos that belong to that chapter.",
      ]}
      frames={frames}
      reverse
    />
  );
}
