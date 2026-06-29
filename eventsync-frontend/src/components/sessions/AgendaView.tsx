"use client";

import { useState } from "react";
import Link from "next/link";
import { FaClock, FaDoorOpen, FaUsers, FaMicrophone } from "react-icons/fa";
import { Session } from "@/types";
import LiveBadge from "./LiveBadge";

interface Props {
  sessions: Session[];
}

export default function AgendaView({ sessions }: Props) {

  const grouped = sessions.reduce((acc, session) => {
    const day = new Date(session.startTime).toLocaleDateString("fr-FR", {
      weekday: "long", day: "numeric", month: "long", year: "numeric",
    });
    if (!acc[day]) acc[day] = [];
    acc[day].push(session);
    return acc;
  }, {} as Record<string, Session[]>);

  const days = Object.keys(grouped).sort((a, b) =>
    new Date(grouped[a][0].startTime).getTime() - new Date(grouped[b][0].startTime).getTime()
  );

  const [selectedDay, setSelectedDay] = useState<string>(days[0] ?? "all");

  const displayedDays = selectedDay === "all" ? days : days.filter(d => d === selectedDay);

  if (sessions.length === 0) {
    return (
      <div className="glass rounded-[32px] p-16 text-center border border-white/5">
        <p className="text-2xl font-black">Aucune session</p>
        <p className="text-gray-400 mt-4">Aucune session n'a encore été planifiée.</p>
      </div>
    );
  }

  return (
    <div>

      {/* Filtre par jour */}
      <div className="flex flex-wrap gap-3 mb-10">
        <button
          onClick={() => setSelectedDay("all")}
          className={`px-5 py-3 rounded-2xl font-semibold transition-all ${
            selectedDay === "all" ? "bg-lime-400 text-black" : "bg-white/5 text-white hover:bg-white/10"
          }`}
        >
          All day 
        </button>
        {days.map((day) => (
          <button
            key={day}
            onClick={() => setSelectedDay(day)}
            className={`px-5 py-3 rounded-2xl font-semibold transition-all capitalize ${
              selectedDay === day ? "bg-lime-400 text-black" : "bg-white/5 text-white hover:bg-white/10"
            }`}
          >
            {day}
          </button>
        ))}
      </div>

      <div className="space-y-14">
        {displayedDays.map((day) => (
          <div key={day}>

            <div className="flex items-center gap-4 mb-6">
              <div className="h-px flex-1 bg-white/10" />
              <span className="text-lime-400 font-bold uppercase tracking-widest text-sm capitalize">
                {day}
              </span>
              <div className="h-px flex-1 bg-white/10" />
            </div>
            <div className="relative pl-8 border-l border-white/10 space-y-6">
              {grouped[day]
                .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime())
                .map((session) => (
                  <div key={session.id} className="relative">
                    <div className="absolute -left-[37px] w-3 h-3 rounded-full bg-lime-400 border-2 border-black mt-6" />

                    <Link
                      href={`/sessions/${session.id}`}
                      className="
                        block glass rounded-[24px] p-6
                        border border-white/5
                        hover:border-lime-400/20
                        transition-all duration-300
                        hover:-translate-y-1
                        group
                      "
                    >
                      <div className="flex flex-col md:flex-row md:items-start gap-4">
                        <div className="shrink-0 w-28">
                          <p className="text-2xl font-black text-lime-400">
                            {new Date(session.startTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                          </p>
                          <p className="text-xs text-gray-500 mt-1">
                            → {new Date(session.endTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                          </p>
                        </div>
                        <div className="flex-1">
                          <div className="flex items-start justify-between gap-4 flex-wrap">
                            <h3 className="text-xl font-black group-hover:text-lime-400 transition-colors">
                              {session.title}
                            </h3>
                            <LiveBadge startDate={session.startTime} endDate={session.endTime} />
                          </div>

                          {session.description && (
                            <p className="text-gray-400 text-sm mt-2 line-clamp-2">
                              {session.description}
                            </p>
                          )}
                          <div className="mt-4 flex flex-wrap gap-4 text-xs text-gray-500">
                            {session.room?.name && (
                              <div className="flex items-center gap-1.5">
                                <FaDoorOpen className="text-lime-400" />
                                <span>{session.room.name}</span>
                              </div>
                            )}
                            {session.guestNumber != null && (
                              <div className="flex items-center gap-1.5">
                                <FaUsers className="text-lime-400" />
                                <span>{session.guestNumber} guests</span>
                              </div>
                            )}
                            {(session.speakers?.length ?? 0) > 0 && (
                              <div className="flex items-center gap-1.5">
                                <FaMicrophone className="text-lime-400" />
                                <span>{session.speakers!.length} speaker{session.speakers!.length > 1 ? "s" : ""}</span>
                              </div>
                            )}
                          </div>
                          {(session.speakers?.length ?? 0) > 0 && (
                            <div className="mt-4 flex flex-wrap gap-2">
                              {session.speakers!.map((sp) => (
                                <span
                                  key={sp.id}
                                  className="px-3 py-1 rounded-full bg-lime-400/10 text-lime-300 text-xs font-medium border border-lime-400/10"
                                >
                                  {sp.fullName}
                                </span>
                              ))}
                            </div>
                          )}
                        </div>
                      </div>
                    </Link>
                  </div>
                ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}