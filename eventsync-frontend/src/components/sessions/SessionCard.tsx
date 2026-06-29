import Link from "next/link";
import { Session } from "@/types";
import LiveBadge from "./LiveBadge";
import { FaArrowRight, FaClock, FaUsers } from "react-icons/fa";

interface Props {
  session: Session;
}

export default function SessionCard({ session }: Props) {
  const speakerCount = session.speakers?.length ?? 0;

  return (
    <div className="glass rounded-[28px] p-6 border border-white/5 hover:border-lime-400/20 transition-all duration-500 hover:-translate-y-1">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h3 className="text-2xl font-black">{session.title}</h3>
          <p className="text-gray-400 mt-3 line-clamp-3">{session.description}</p>
        </div>
        <LiveBadge startDate={session.startTime} endDate={session.endTime} />
      </div>

      {/* ✅ Nombre de speakers */}
      <div className="mt-4 flex items-center gap-2 text-sm text-gray-500">
        <FaUsers className="text-lime-400" />
        <span>
          <span className="font-bold text-white">{speakerCount}</span>{" "}
          Speaker{speakerCount !== 1 ? "s" : ""}
        </span>
      </div>

      <div className="mt-6 flex items-center justify-between">
        <div className="flex items-center gap-2 text-sm text-gray-500">
          <FaClock className="text-lime-400" />
          <p>
            {new Date(session.startTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
            {" - "}
            {new Date(session.endTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
          </p>
        </div>
        <Link
          href={`/sessions/${session.id}`}
          className="w-14 h-14 rounded-2xl bg-lime-400 text-black flex items-center justify-center text-xl font-black hover:scale-110 transition"
        >
          <FaArrowRight />
        </Link>
      </div>
    </div>
  );
}