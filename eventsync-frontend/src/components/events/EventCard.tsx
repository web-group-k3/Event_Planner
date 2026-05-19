import Link from "next/link";

import { Event } from "@/types";

import LiveBadge from "../sessions/LiveBadge";

interface Props {
  event: Event;
}

export default function EventCard({
  event,
}: Props) {

  return (

    <div
      className="
        glass
        rounded-4xl
        overflow-hidden
        border border-white/5
        hover:border-lime-400/20
        transition-all duration-500
        hover:-translate-y-2
      "
    >

      <div className="h-52 bg-linear-to-br from-lime-400/20 via-sky-400/10 to-[#ff4d6d]/20" />

      <div className="p-7">

        <LiveBadge
          startDate={event.startDate}
          endDate={event.endDate}
        />

        <h2 className="text-3xl font-black mt-6">

          {event.title}

        </h2>

        <p className="text-gray-400 mt-4 line-clamp-3">

          {event.description}

        </p>

        <div className="mt-8">

          <Link
            href={`/events/${event.id}`}
            className="
              inline-flex
              items-center
              justify-center
              w-14 h-14
              rounded-2xl
              bg-lime-400
              text-black
              font-black
            "
          >

            →

          </Link>

        </div>

      </div>

    </div>

  );
}