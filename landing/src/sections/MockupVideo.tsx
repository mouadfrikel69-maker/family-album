import FeatureMockup, { type Frame } from "../kin/FeatureMockup";
import OnboardingScreen from "../kin/screens/OnboardingScreen";
import AuthScreen from "../kin/screens/AuthScreen";
import ProfileSetupScreen from "../kin/screens/ProfileSetupScreen";
import CreateFamilyScreen from "../kin/screens/CreateFamilyScreen";
import HomeScreen from "../kin/screens/HomeScreen";
import AlbumsScreen from "../kin/screens/AlbumsScreen";
import MemoriesScreen from "../kin/screens/MemoriesScreen";
import MembersScreen from "../kin/screens/MembersScreen";
import NotificationsScreen from "../kin/screens/NotificationsScreen";
import InviteScreen from "../kin/screens/InviteScreen";
import PhotoDetailScreen from "../kin/screens/PhotoDetailScreen";

const frames: Frame[] = [
  { key: "v-onboarding", caption: "01 · open kin", hold: 2200, render: () => <OnboardingScreen /> },
  { key: "v-auth", caption: "02 · sign in", hold: 2200, render: () => <AuthScreen /> },
  { key: "v-profile", caption: "03 · profile setup", hold: 2200, render: () => <ProfileSetupScreen /> },
  { key: "v-family", caption: "04 · create the family", hold: 2200, render: () => <CreateFamilyScreen /> },
  { key: "v-home", caption: "05 · home page · the family feed", hold: 2800, render: () => <HomeScreen /> },
  { key: "v-photo", caption: "06 · open a polaroid", hold: 2400, render: () => <PhotoDetailScreen /> },
  { key: "v-albums", caption: "07 · browse albums", hold: 2400, render: () => <AlbumsScreen /> },
  { key: "v-memories", caption: "08 · remember together", hold: 2400, render: () => <MemoriesScreen /> },
  { key: "v-members", caption: "09 · the family circle", hold: 2400, render: () => <MembersScreen /> },
  { key: "v-invite", caption: "10 · invite someone in", hold: 2400, render: () => <InviteScreen /> },
  { key: "v-notif", caption: "11 · calm, in-app only", hold: 2400, render: () => <NotificationsScreen /> },
];

export default function MockupVideo() {
  return (
    <section
      id="walkthrough"
      className="relative overflow-hidden border-y border-blush/60 bg-warm/70"
    >
      <div className="mx-auto flex max-w-6xl flex-col items-center gap-10 px-6 py-24 lg:py-32">
        <div className="text-center">
          <span className="pill">a quiet little tour</span>
          <h2 className="mt-4 font-serif text-[36px] leading-tight text-ink sm:text-[48px]">
            the whole app,
            <br />
            <span className="italic text-terracotta">page by page</span>
          </h2>
          <p className="mx-auto mt-3 max-w-xl text-[15px] text-ink-mocha">
            A continuous walk through Kin: from opening the app, signing in, building a family,
            and landing on the home page — all the way to invites, memories, and notifications.
          </p>
        </div>
        <div className="relative">
          <span
            className="absolute -inset-10 -z-10 rounded-[60px] opacity-60 blur-3xl"
            style={{
              background:
                "radial-gradient(closest-side, rgba(199, 107, 74, 0.35), transparent 70%)",
            }}
            aria-hidden
          />
          <FeatureMockup frames={frames} width={320} />
        </div>
      </div>
    </section>
  );
}
