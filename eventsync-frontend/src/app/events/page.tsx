import { Suspense } from "react";
import { getEvents } from "@/services/event.service";
import EventsFilter from "@/components/events/EventsFilter";

async function EventsList() {
  let events = [];

  try {
    events = await getEvents();
  } catch {
    return (
      <div className="glass rounded-[32px] p-16 text-center border border-white/5">
        <h2 className="text-2xl font-black">Failed to load events</h2>
        <p className="text-gray-400 mt-4">Please try again later.</p>
      </div>
    );
  }

  if (events.length === 0) {
    return (
      <div className="glass rounded-[32px] p-16 text-center border border-white/5">
        <h2 className="text-2xl font-black">No Events Yet</h2>
        <p className="text-gray-400 mt-4">Check back soon for upcoming events.</p>
      </div>
    );
  }
  return <EventsFilter events={events} />;
}
export default function EventsPage() {
  return (
    <main className="container-app py-20">
      <div className="mb-16">
        <span className="text-lime-400 lg:text-6xl font-semibold tracking-widest">Discover What's Next</span>
        <h1 className="text-5xl lg:text-4xl font-black mt-4">Your Ultimate Guide to Upcoming Experiences, Conferences, and Gatherings</h1>
        <p className="text-gray-400 mt-5 text-lg leading-relaxed">
          Join a vibrant community of thinkers, creators, and innovators. Whether you are looking to expand your professional network, learn a new skill,
          or simply connect with like-minded individuals, our curated lineup of events has something for everyone.
        </p>
      </div>

      <Suspense
        fallback={
          <div className="grid md:grid-cols-2 xl:grid-cols-3 gap-8">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="glass rounded-[32px] h-96 animate-pulse border border-white/5" />
            ))}
          </div>
        }
      >
        <EventsList />
      </Suspense>
    </main>
  );
}