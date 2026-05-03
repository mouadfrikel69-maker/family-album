import { Hero } from './sections/Hero'
import { FeatureSection } from './sections/FeatureSection'
import { PromisesSection } from './sections/PromisesSection'
import { DesignShowcase } from './sections/DesignShowcase'
import { Footer } from './sections/Footer'
import { InviteMock } from './screens/InviteMock'
import { AlbumsMock } from './screens/AlbumsMock'
import { MembersMock } from './screens/MembersMock'
import { UploadMock } from './screens/UploadMock'
import { TopNav } from './components/TopNav'

function App() {
  return (
    <div className="min-h-screen">
      <TopNav />
      <Hero />

      <PromisesSection />

      <FeatureSection
        id="invite"
        eyebrow="invite the family"
        title={
          <>
            One code.
            <br />
            <span className="font-display text-terracotta-deep">
              Everyone you love, in.
            </span>
          </>
        }
        description={
          <>
            A 16-character invite code, drawn from the Crockford alphabet so it's hard to
            mistype. Share it as a QR, a copy-pasted link, or through your phone's share
            sheet — straight to Mom or Grandpa, no public profile required.
          </>
        }
        bullets={[
          {
            title: 'QR + copy + share',
            body: 'Drawn live on a Compose Canvas — sharp on every screen and Wallet-friendly.',
          },
          {
            title: '~1.2 × 10²⁴ search space',
            body: 'Generated with SecureRandom. Server-checked for uniqueness. Truly invite-only.',
          },
          {
            title: 'No public usernames',
            body: "There's nothing to discover. The only way in is a code from someone who's already family.",
          },
        ]}
        caption="copy · share · welcome"
        screen={<InviteMock animated />}
        tint="cream"
      />

      <FeatureSection
        id="create-album"
        eyebrow="albums by hand"
        title={
          <>
            Bound by hand.
            <br />
            <span className="font-display text-terracotta-deep">Filled together.</span>
          </>
        }
        description={
          <>
            Make an album for the trip, the season, the milestone. Each album gets a
            deterministic warm accent so "Eid 2026" and "Cabin trip" never blur into the
            same beige tile.
          </>
        }
        bullets={[
          {
            title: 'One tap to create',
            body: 'Type a name. The new tile slides in with a strip of washi tape on top.',
          },
          {
            title: '8 warm accent tones',
            body: 'Hashed from the album name so the same album always wears the same colour.',
          },
          {
            title: 'Photos move freely',
            body: 'A photo can live in many albums at once — no awkward "duplicate" copies.',
          },
        ]}
        caption="type · tap · the album lands"
        screen={<AlbumsMock animated />}
        reverse
        tint="washi"
      />

      <FeatureSection
        id="add-members"
        eyebrow="just family"
        title={
          <>
            Roles, not followers.
            <br />
            <span className="font-display text-terracotta-deep">
              Admin · Member · Viewer.
            </span>
          </>
        }
        description={
          <>
            Every face in the family gets an avatar with a deterministic warm tint and a
            tiny role badge. When Aunt Salma joins, she lands at the top of the list with a
            soft sage glow, and the whole family sees a quiet "joined" toast.
          </>
        }
        bullets={[
          {
            title: 'Three gentle roles',
            body: 'Admin holds the keys, members upload, viewers stop by — no permission spaghetti.',
          },
          {
            title: 'Avatars that stay yours',
            body: "Initials, relationship label, and a colour that never shifts because it's hashed.",
          },
          {
            title: 'Quiet notifications',
            body: '"NewMember", "Like", "Comment", "Memory" — only ever from inside the family.',
          },
        ]}
        caption="someone just joined"
        screen={<MembersMock animated />}
        tint="sage"
      />

      <FeatureSection
        id="upload"
        eyebrow="add a memory"
        title={
          <>
            Pick. Caption. Save.
            <br />
            <span className="font-display text-terracotta-deep">
              Stays on your device.
            </span>
          </>
        }
        description={
          <>
            Pick up to ten photos at once via the Android Photo Picker — no permissions
            required — or fire up the camera. Add an optional script-style caption, drop it
            in an album, and watch it land on every family phone as a polaroid card.
          </>
        }
        bullets={[
          {
            title: 'Photo Picker friendly',
            body: 'No "Allow access to all photos" prompt. Pick exactly what you mean to share.',
          },
          {
            title: 'Camera that respects you',
            body: 'Permission requested only at capture time, with a clear rationale and Settings deep-link.',
          },
          {
            title: 'Stored locally',
            body: 'In an app-private folder, encrypted at rest, never synced to a cloud bucket you didn\'t pick.',
          },
        ]}
        caption="pick · caption · save"
        screen={<UploadMock animated />}
        reverse
        tint="blush"
      />

      <DesignShowcase />

      <Footer />
    </div>
  )
}

export default App
