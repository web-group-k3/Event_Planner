import Link from "next/link";
import { Event } from "@/types";
import LiveBadge from "@/components/sessions/LiveBadge";

interface Props {
  event: Event;
}

export default function EventCard({ event }: Props) {
  // ✅ calculé depuis sessions, pas depuis un champ inexistant
  const sessionCount = event.sessions?.length ?? 0;

  return (
    <div
      className="
        group relative glass rounded-[32px] overflow-hidden
        border border-white/5 hover:border-lime-400/20
        transition-all duration-500
        hover:-translate-y-3
        hover:shadow-[0_0_40px_rgba(163,230,53,0.12)]
      "
    >
      {/* Glow */}
      <div
        className="
          absolute inset-0 opacity-0 group-hover:opacity-100
          transition duration-500
          bg-gradient-to-br from-lime-400/5 via-transparent to-sky-400/5
        "
      />

      {/* Cover */}
      <div className="relative h-56 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-lime-400/20 via-sky-400/10 to-[#ff4d6d]/20" />
        <div className="absolute inset-0 bg-black/30" />

        {/* ✅ positionnement géré ici, pas dans LiveBadge */}
        <LiveBadge
          startDate={event.startDate}
          endDate={event.endDate}
          className="absolute top-4 left-4"
        />
      </div>

      {/* Content */}
      <div className="relative p-7">

        <h2 className="text-3xl font-black transition group-hover:text-lime-400">
          {event.title}
        </h2>

        <p className="text-gray-400 mt-4 line-clamp-3 leading-relaxed">
          {event.description}
        </p>

        

        {/* Dates */}
        <div className="mt-8 space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span className="text-gray-500">Start</span>
            <span className="font-medium">
              {new Date(event.startDate).toLocaleDateString("fr-FR")}
            </span>
          </div>
          <div className="flex items-center justify-between text-sm">
            <span className="text-gray-500">End</span>
            <span className="font-medium">
              {new Date(event.endDate).toLocaleDateString("fr-FR")}
            </span>
          </div>
        </div>

        {/* Footer */}
        <div className="mt-10 flex items-center justify-between">
          <span className="text-sm text-gray-400 group-hover:text-white transition">
            Explore event
          </span>
          <Link
            href={`/events/${event.id}`}
            className="
              w-14 h-14 rounded-2xl bg-lime-400 text-black
              flex items-center justify-center
              text-xl font-black hover:scale-110 transition
            "
          >
            →
          </Link>
        </div>

      </div>
    </div>
  );
}