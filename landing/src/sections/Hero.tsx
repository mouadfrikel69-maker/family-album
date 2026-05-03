import FeatureMockup, { type Frame } from "../kin/FeatureMockup";
import OnboardingScreen from "../kin/screens/OnboardingScreen";
import AuthScreen from "../kin/screens/AuthScreen";
import ProfileSetupScreen from "../kin/screens/ProfileSetupScreen";
import CreateFamilyScreen from "../kin/screens/CreateFamilyScreen";
import HomeScreen from "../kin/screens/HomeScreen";
import AlbumsScreen from "../kin/screens/AlbumsScreen";
import MemoriesScreen from "../kin/screens/MemoriesScreen";
import Wordmark from "../kin/Wordmark";

const heroFrames: Frame[] = [
  { key: "onboarding", render: () => <OnboardingScreen />, caption: "open the app", hold: 2200 },
  { key: "auth", render: () => <AuthScreen />, caption: "sign in, gently", hold: 2400 },
  { key: "profile", render: () => <ProfileSetupScreen />, caption: "tell us who you are", hold: 2400 },
  { key: "family", render: () => <CreateFamilyScreen />, caption: "make a family circle", hold: 2400 },
  { key: "home", render: () => <HomeScreen />, caption: "the home page", hold: 3000 },
  { key: "albums", render: () => <AlbumsScreen />, caption: "browse your albums", hold: 2600 },
  { key: "memories", render: () => <MemoriesScreen />, caption: "this week, in past years", hold: 2600 },
];

export default function Hero() {
  return (
    <section className="relative overflow-hidden">
      <div className="mx-auto flex max-w-6xl flex-col-reverse items-center gap-10 px-6 pb-16 pt-10 lg:flex-row lg:items-center lg:gap-16 lg:py-24">
        <div className="flex-1">
          <div className="flex items-center gap-3">
            <Wordmark size={30} />
            <span className="pill">private · ad-free · just family</span>
          </div>
          <h1 className="mt-7 font-serif text-[44px] leading-[1.05] tracking-tight text-ink sm:text-[56px] lg:text-[68px]">
            a private little
            <br />
            <span className="italic text-terracotta">family album</span>
            <br />
            for the people who feel like home.
          </h1>
          <p className="mt-5 max-w-xl text-[15px] leading-relaxed text-ink-mocha sm:text-base">
            Kin is a warm, paper-scrapbook photo app for Android. Photos stay on your phone.
            The cloud only knows who is in which family. No feeds from strangers, no ads, no
            tracking — just kin.
          </p>
          <div className="mt-8 flex flex-wrap items-center gap-3">
            <a className="btn-primary" href="#download">
              <PlayGlyph /> get it on Android
            </a>
            <a className="btn-secondary" href="#walkthrough">
              see how it works
            </a>
          </div>
          <div className="mt-8 grid grid-cols-3 gap-3 max-w-md">
            <Stat label="on-device only" value="0 photos in cloud" />
            <Stat label="ad / tracker count" value="zero" />
            <Stat label="invite entropy" value="80 bits" />
          </div>
        </div>
        <div className="flex-1 lg:flex lg:justify-end">
          <FeatureMockup frames={heroFrames} width={300} />
        </div>
      </div>
    </section>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl border border-blush bg-polaroid/80 p-3 text-center shadow-soft">
      <p className="font-serif text-[14px] italic text-terracotta">{value}</p>
      <p className="mt-0.5 text-[10px] uppercase tracking-wider text-ink-soft">{label}</p>
    </div>
  );
}

function PlayGlyph() {
  return (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <path d="M3.5 2v10l8-5-8-5Z" fill="currentColor" />
    </svg>
  );
}
