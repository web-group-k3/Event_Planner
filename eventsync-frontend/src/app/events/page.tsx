import { getEvents } from "@/services/event.service";

import EventCard from "@/components/events/EventCard";

export default async function EventsPage() {

  const events = await getEvents();

  return (
    <main className="container-app py-20">
      {/*hero in the events page*/}
      <div className="mb-16">

        <span className="text-lime-400 font-semibold tracking-widest">
          EVENTS
        </span>

        <h1 className="text-5xl lg:text-6xl font-black mt-4">

          Explore Events

        </h1>

        <p className="text-gray-400 mt-5 max-w-2xl text-lg leading-relaxed">

          Discover conferences, workshops,
          networking sessions and live
          experiences happening around you.

        </p>

      </div>

      {/* Grid for all exesting events */}

      <div className="grid md:grid-cols-2 xl:grid-cols-3 gap-8">

        {events.map((event) => (

          <EventCard
            key={event.id}
            event={event}
          />

        ))}

      </div>

    </main>
  );
}