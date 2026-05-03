import Nav from "./sections/Nav";
import Hero from "./sections/Hero";
import MockupVideo from "./sections/MockupVideo";
import InviteFeature from "./sections/InviteFeature";
import CreateAlbumFeature from "./sections/CreateAlbumFeature";
import AddMembersFeature from "./sections/AddMembersFeature";
import UploadFeature from "./sections/UploadFeature";
import MemoriesFeature from "./sections/MemoriesFeature";
import Pillars from "./sections/Pillars";
import TechStrip from "./sections/TechStrip";
import CTA from "./sections/CTA";
import Footer from "./sections/Footer";

export default function App() {
  return (
    <div className="min-h-screen">
      <Nav />
      <main>
        <Hero />
        <MockupVideo />
        <InviteFeature />
        <CreateAlbumFeature />
        <AddMembersFeature />
        <UploadFeature />
        <MemoriesFeature />
        <Pillars />
        <TechStrip />
        <CTA />
      </main>
      <Footer />
    </div>
  );
}
