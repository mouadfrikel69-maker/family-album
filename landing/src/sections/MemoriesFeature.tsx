import type { Frame } from "../kin/FeatureMockup";
import MemoriesScreen from "../kin/screens/MemoriesScreen";
import HomeScreen from "../kin/screens/HomeScreen";
import PhotoDetailScreen from "../kin/screens/PhotoDetailScreen";
import NotificationsScreen from "../kin/screens/NotificationsScreen";
import { FeatureSection } from "./FeatureSection";

const frames: Frame[] = [
  {
    key: "home",
    caption: "today's family page",
    hold: 1900,
    render: () => <HomeScreen />,
  },
  {
    key: "memories",
    caption: "this week, in past years",
    hold: 2400,
    render: () => <MemoriesScreen />,
  },
  {
    key: "detail",
    caption: "tap a memory, gently re-live it",
    hold: 2400,
    render: () => <PhotoDetailScreen />,
  },
  {
    key: "notif",
    caption: "calm, in-app reminders only",
    hold: 2200,
    render: () => <NotificationsScreen />,
  },
];

export default function MemoriesFeature() {
  return (
    <FeatureSection
      id="memories"
      eyebrow="memories & detail"
      title={
        <>
          remembering,
          <br />
          <span className="italic text-terracotta">on warm paper</span>
        </>
      }
      bullets={[
        "“This week, in previous years” surfaces photos within ±3 days of today's date from prior years.",
        "Photo detail: full-bleed image, italic-script caption, threaded comments, soft like.",
        "Notifications stay inside the app — no push spam, no third-party trackers.",
        "Press-spring touchables and gentle slide transitions keep everything calm.",
      ]}
      frames={frames}
      reverse={false}
    />
  );
}
