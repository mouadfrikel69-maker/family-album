import type { Frame } from "../kin/FeatureMockup";
import HomeScreen from "../kin/screens/HomeScreen";
import UploadScreen from "../kin/screens/UploadScreen";
import { FeatureSection } from "./FeatureSection";

const CAPTION = "first bike ride 🚲";
// Split by code points so emoji surrogate pairs (e.g. 🚲) stay intact while typing.
const CAPTION_GLYPHS = [...CAPTION];
const typingFrames: Frame[] = CAPTION_GLYPHS.map((glyph, i) => {
  const visible = CAPTION_GLYPHS.slice(0, i + 1).join("");
  return {
    key: `caption-${i}`,
    hold: 90 + (glyph === " " ? 60 : 0),
    render: () => <UploadScreen caption={visible} progress={0} />,
  };
});

const progressFrames: Frame[] = [0.25, 0.55, 0.8, 1].map((p) => ({
  key: `progress-${p}`,
  hold: 500,
  render: () => <UploadScreen caption={CAPTION} progress={p} album="Milestones" />,
}));

const frames: Frame[] = [
  {
    key: "home-empty",
    caption: "tap + to add a memory",
    hold: 1700,
    render: () => <HomeScreen />,
  },
  {
    key: "picker",
    caption: "pick a photo, no permissions",
    hold: 1500,
    render: () => <UploadScreen />,
  },
  ...typingFrames,
  {
    key: "captioned",
    caption: "add a caption",
    hold: 800,
    render: () => <UploadScreen caption={CAPTION} />,
  },
  {
    key: "album",
    caption: "drop it into an album",
    hold: 1100,
    render: () => <UploadScreen caption={CAPTION} album="Milestones" />,
  },
  ...progressFrames,
  {
    key: "feed",
    caption: "polaroid lands on the feed",
    hold: 2400,
    render: () => <HomeScreen highlightLikeOnFirst />,
  },
];

export default function UploadFeature() {
  return (
    <FeatureSection
      id="upload"
      eyebrow="add a memory"
      title={
        <>
          drop a photo
          <br />
          <span className="italic text-terracotta">onto the family page</span>
        </>
      }
      bullets={[
        "Pick from gallery (Android Photo Picker — no storage permissions) or camera.",
        "Optional handwritten-style caption and album tag.",
        "Upload bar is just for the local copy — the photo never leaves your device.",
        "Lands as a polaroid on the family feed, with washi-tape and a soft rotation.",
      ]}
      frames={frames}
      reverse
    />
  );
}
