import Link from "next/link";
import { Session } from "@/types";
import LiveBadge from "@/components/sessions/LiveBadge";

interface Props {
  session: Session;
}

export default function SessionCard({ session }: Props) {
  return (
    <div
      className="
        glass rounded-[28px] p-6
        border border-white/5 hover:border-lime-400/20
        transition-all duration-500 hover:-translate-y-1
      "
    >
      {/* Top */}
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 min-w-0">
          <h3 className="text-2xl font-black">
            {session.title}
          </h3>
          <p className="text-gray-400 mt-3 line-clamp-3">
            {session.description}
          </p>
        </div>

        {/* ✅ pas de absolute ici, inline dans le flex */}
        <LiveBadge
          startDate={session.startTime}
          endDate={session.endTime}
          className="shrink-0"
        />
      </div>

      {/* Speakers */}
      {session.speakers && session.speakers.length > 0 && (
        <div className="mt-4 flex flex-wrap gap-2">
          {session.speakers.map((speaker) => (
            <span
              key={speaker.id}
              className="px-3 py-1 rounded-full bg-white/5 border border-white/5 text-xs text-gray-300"
            >
              🎤 {speaker.fullName}
            </span>
          ))}
        </div>
      )}

      {/* Bottom */}
      <div className="mt-8 flex items-center justify-between">
        <div className="space-y-1 text-sm">
          <p className="text-gray-500">
            {new Date(session.startTime).toLocaleTimeString("fr-FR", {
              hour: "2-digit",
              minute: "2-digit",
            })}
            {" — "}
            {new Date(session.endTime).toLocaleTimeString("fr-FR", {
              hour: "2-digit",
              minute: "2-digit",
            })}
          </p>
          <p className="text-gray-400">
            📍 {session.room?.name || "Salle non définie"}
          </p>
        </div>

        <Link
          href={`/sessions/${session.id}`}
          className="
            w-12 h-12 rounded-2xl bg-lime-400 text-black
            flex items-center justify-center
            font-black hover:scale-110 transition
          "
        >
          →
        </Link>
      </div>
    </div>
  );
}