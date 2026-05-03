import type { Frame } from "../kin/FeatureMockup";
import HomeScreen from "../kin/screens/HomeScreen";
import MembersScreen from "../kin/screens/MembersScreen";
import InviteScreen from "../kin/screens/InviteScreen";
import { FeatureSection } from "./FeatureSection";

const frames: Frame[] = [
  {
    key: "home",
    caption: "tap an avatar in the family strip",
    hold: 2000,
    render: () => <HomeScreen />,
  },
  {
    key: "members",
    caption: "see your circle",
    hold: 2000,
    render: () => <MembersScreen highlightInvite />,
  },
  {
    key: "invite",
    caption: "send the invite",
    hold: 2200,
    render: () => <InviteScreen showShare />,
  },
  {
    key: "joined",
    caption: "Yara just joined",
    hold: 2600,
    render: () => (
      <MembersScreen newMember={{ name: "Yara", relationship: "Sister" }} />
    ),
  },
];

export default function AddMembersFeature() {
  return (
    <FeatureSection
      id="members"
      eyebrow="add members"
      title={
        <>
          a quiet circle,
          <br />
          <span className="italic text-terracotta">just for you</span>
        </>
      }
      bullets={[
        "Each family member has a deterministic warm tint — Mom is always Mom-coloured.",
        "Roles are clear: Admin · Member · Viewer, enforced by row-level security on the server.",
        "New members get an invite via QR, link, or mail — no phone numbers required.",
        "When someone joins, a calm in-app notification appears. No push spam.",
      ]}
      frames={frames}
      reverse={false}
    />
  );
}
