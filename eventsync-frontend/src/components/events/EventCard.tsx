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
        group
        glass
        rounded-[32px]
        overflow-hidden
        border border-white/5
        hover:border-lime-400/20
        transition-all
        duration-500
        hover:-translate-y-2
      "
    >

      {/* Cover */}

      <div className="relative h-56 overflow-hidden">

        <div className="absolute inset-0 bg-gradient-to-br from-lime-400/20 via-sky-400/10 to-[#ff4d6d]/20" />

        <div className="absolute inset-0 bg-black/30" />

        <LiveBadge
          startDate={event.startDate}
          endDate={event.endDate}
        />

      </div>

      {/* Content */}

      <div className="p-7">

        {/* Title */}

        <h2
          className="
            text-2xl
            font-black
            transition
            group-hover:text-lime-400
          "
        >

          {event.title}

        </h2>

        {/* Description */}

        <p className="text-gray-400 mt-4 line-clamp-3 leading-relaxed">

          {event.description}

        </p>

        {/* Dates */}

        <div className="mt-8 space-y-2">

          <div className="flex items-center justify-between text-sm">

            <span className="text-gray-500">
              Start
            </span>

            <span className="font-medium">

              {new Date(
                event.startDate
              ).toLocaleDateString()}

            </span>

          </div>

          <div className="flex items-center justify-between text-sm">

            <span className="text-gray-500">
              End
            </span>

            <span className="font-medium">

              {new Date(
                event.endDate
              ).toLocaleDateString()}

            </span>

          </div>

        </div>

        {/* Footer */}

        <div className="mt-10 flex items-center justify-between">

          <div className="text-sm text-gray-500">
            View details
          </div>

          <Link
            href={`/events/${event.id}`}
            className="
              w-14 h-14
              rounded-2xl
              bg-lime-400
              text-black
              flex
              items-center
              justify-center
              text-xl
              font-black
              hover:scale-110
              transition
            "
          >

            →

          </Link>

        </div>

      </div>

    </div>

  );
}