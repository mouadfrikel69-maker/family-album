import type { Frame } from "../kin/FeatureMockup";
import MembersScreen from "../kin/screens/MembersScreen";
import InviteScreen from "../kin/screens/InviteScreen";
import HomeScreen from "../kin/screens/HomeScreen";
import { FeatureSection } from "./FeatureSection";

const frames: Frame[] = [
  {
    key: "members",
    caption: "open members",
    hold: 1800,
    render: () => <MembersScreen highlightInvite />,
  },
  {
    key: "invite-qr",
    caption: "share QR or 16-char code",
    hold: 2200,
    render: () => <InviteScreen />,
  },
  {
    key: "invite-copy",
    caption: "tap copy → link copied",
    hold: 1800,
    render: () => <InviteScreen copied />,
  },
  {
    key: "invite-share",
    caption: "share via your favourite app",
    hold: 2400,
    render: () => <InviteScreen showShare />,
  },
  {
    key: "joined",
    caption: "they're in. just kin.",
    hold: 2400,
    render: () => <HomeScreen />,
  },
];

export default function InviteFeature() {
  return (
    <FeatureSection
      id="invite"
      eyebrow="invite link"
      title={
        <>
          one private code,
          <br />
          <span className="italic text-terracotta">shared in a single tap</span>
        </>
      }
      bullets={[
        "16-character Crockford-alphabet invite codes — ~80 bits of entropy.",
        "Vector-clean QR rendered on-device, no third-party SDK.",
        "Copy https://kin.family/join/<code> or use the OS share sheet.",
        "Quick mail action for the relatives who still prefer email.",
      ]}
      frames={frames}
      reverse={false}
    />
  );
}
