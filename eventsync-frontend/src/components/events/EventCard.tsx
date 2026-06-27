import Link from "next/link";
import { Event } from "@/types";
import LiveBadge from "../sessions/LiveBadge";
import { FaMapMarkerAlt, FaCalendarAlt, FaUsers, FaArrowRight, FaLayerGroup } from "react-icons/fa";

interface Props {
  event: Event;
}

export default function EventCard({ event }: Props) {
  const sessionCount = event.sessions?.length ?? 0;
  const speakerCount = new Set(
    event.sessions?.flatMap(s => s.speakers?.map(sp => sp.id) ?? []) ?? []
  ).size;
  return (
    <div className="group glass rounded-[32px] overflow-hidden border border-white/5 hover:border-lime-400/20 transition-all duration-500 hover:-translate-y-2">
<div className="relative flex items-center justify-center h-16 overflow-hidden">
  <div className="absolute inset-0 bg-gradient-to-br from-lime-400/20 via-sky-400/10 to-[#ff4d6d]/20" />
  <div className="absolute inset-0 bg-black/30" />
  <div className="relative z-10">
    <LiveBadge startDate={event.startDate} endDate={event.endDate} />
  </div>
</div>
      <div className="p-7">
        <h2 className="text-2xl font-black transition group-hover:text-lime-400">
          {event.title}
        </h2>
        <p className="text-gray-400 mt-4 line-clamp-3 leading-relaxed">
          {event.description}
        </p>
        <div className="mt-4 flex items-center gap-2 text-sm text-gray-500">
          <FaMapMarkerAlt className="text-white" />
          <span className="font-bold text-lime-400">{event.location}</span>
        </div>
        <div className="mt-4 flex items-center gap-6 text-sm">
          <div className="flex items-center gap-2 text-gray-400">
            <FaLayerGroup className="text-lime-400" />
            <span>
              <span className="font-bold text-white">{sessionCount}</span> Session{sessionCount !== 1 ? "s" : ""}
            </span>
          </div>
          <div className="flex items-center gap-2 text-gray-400">
            <FaUsers className="text-lime-400" />
            <span>
              <span className="font-bold text-white">{speakerCount}</span> Speaker{speakerCount !== 1 ? "s" : ""}
            </span>
          </div>
        </div>
        <div className="mt-6 space-y-2">
          <div className="flex items-center justify-between text-sm">
            <FaCalendarAlt className="text-gray-500" />
            <span className="text-gray-500">Start</span>
            <span className="font-medium">{new Date(event.startDate).toLocaleDateString()}</span>
          </div>
          <div className="flex items-center justify-between text-sm">
            <FaCalendarAlt className="text-gray-500" />
            <span className="text-gray-500">End</span>
            <span className="font-medium">{new Date(event.endDate).toLocaleDateString()}</span>
          </div>
        </div>
        <div className="mt-10 flex items-center justify-between">
          <div className="text-sm text-gray-500">View details</div>
          <Link
            href={`/events/${event.id}`}
            className="w-14 h-14 rounded-2xl bg-lime-400 text-black flex items-center justify-center text-xl font-black hover:scale-110 transition"
          >
            <FaArrowRight />
          </Link>
        </div>
      </div>
    </div>
  );
}