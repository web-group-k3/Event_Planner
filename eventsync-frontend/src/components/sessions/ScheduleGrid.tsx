"use client";

import { useState } from "react";
import Link from "next/link";
import { FaClock, FaDoorOpen, FaUsers, FaCalendarAlt } from "react-icons/fa";
import { Session, Room } from "@/types";
import LiveBadge from "./LiveBadge";

interface Props {
  sessions: Session[];
}

export default function ScheduleGrid({ sessions }: Props) {

  // Rooms uniques
  const roomsMap = new Map<string, Room>();
  sessions.forEach((session) => {
    if (session.room) roomsMap.set(session.room.id, session.room);
  });
  const rooms = Array.from(roomsMap.values());

  // Filtre par salle
  const [selectedRoom, setSelectedRoom] = useState<string>("all");

  const filteredSessions =
    selectedRoom === "all"
      ? sessions
      : sessions.filter((s) => s.room?.id === selectedRoom);

  // Heures uniques triées
  const uniqueHours = Array.from(
    new Set(
      sessions.map((s) =>
        new Date(s.startTime).toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
        })
      )
    )
  ).sort();

  const filteredRooms =
    selectedRoom === "all"
      ? rooms
      : rooms.filter((r) => r.id === selectedRoom);

  return (
    <div>

      {/* Filtres salles */}
      <div className="flex flex-wrap gap-4 mb-10">
        <button
          onClick={() => setSelectedRoom("all")}
          className={`px-5 py-3 rounded-2xl font-semibold transition-all ${
            selectedRoom === "all" ? "bg-lime-400 text-black" : "bg-white/5 text-white hover:bg-white/10"
          }`}
        >
          All Rooms
        </button>
        {rooms.map((room) => (
          <button
            key={room.id}
            onClick={() => setSelectedRoom(room.id)}
            className={`px-5 py-3 rounded-2xl font-semibold transition-all ${
              selectedRoom === room.id ? "bg-lime-400 text-black" : "bg-white/5 text-white hover:bg-white/10"
            }`}
          >
            {room.name}
          </button>
        ))}
      </div>

      {/* Grille */}
      <div className="overflow-x-auto">
        <div
          className="min-w-[1000px] border border-white/10 rounded-[32px] overflow-hidden bg-white/[0.02] backdrop-blur-xl"
        >

          {/* Header */}
          <div
            className="grid bg-white/5 border-b border-white/10"
            style={{ gridTemplateColumns: `180px repeat(${filteredRooms.length}, 1fr)` }}
          >
            <div className="p-6 font-black border-r border-white/10 flex items-center gap-2">
              <FaClock className="text-lime-400" />
              Time
            </div>
            {filteredRooms.map((room) => (
              <div key={room.id} className="p-6 font-black border-r border-white/10">
                <div className="flex items-center gap-2 text-lime-400 mb-1">
                  <FaDoorOpen />
                  <span className="text-white">{room.name}</span>
                </div>
                <p className="text-xs text-gray-500 font-normal">{room.adress}</p>
              </div>
            ))}
          </div>

          {/* Rows */}
          {uniqueHours.map((hour) => (
            <div
              key={hour}
              className="grid border-b border-white/5"
              style={{ gridTemplateColumns: `180px repeat(${filteredRooms.length}, 1fr)` }}
            >
              {/* Heure */}
              <div className="p-6 border-r border-white/10 text-gray-400 font-semibold flex items-start gap-2 pt-7">
                <FaClock className="text-gray-600 mt-0.5 shrink-0" />
                {hour}
              </div>

              {/* Cellules */}
              {filteredRooms.map((room) => {
                const session = filteredSessions.find((s) => {
                  const sessionHour = new Date(s.startTime).toLocaleTimeString([], {
                    hour: "2-digit",
                    minute: "2-digit",
                  });
                  return sessionHour === hour && s.room?.id === room.id;
                });

                return (
                  <div
                    key={`${hour}-${room.id}`}
                    className="p-4 border-r border-white/10 min-h-[260px]"
                  >
                    {session ? (
                      <Link
                        href={`/sessions/${session.id}`}
                        className="block rounded-[24px] p-5 h-full border border-white/5 bg-white/[0.03] hover:border-lime-400/20 transition-all duration-300 hover:-translate-y-1"
                      >
                        {/* Badge live */}
                        <LiveBadge startDate={session.startTime} endDate={session.endTime} />

                        {/* Titre session */}
                        <h3 className="font-black text-xl mt-4 leading-tight">
                          {session.title}
                        </h3>

                        {/* Description */}
                        <p className="text-sm text-gray-400 mt-3 line-clamp-2">
                          {session.description}
                        </p>

                        {/* Horaire */}
                        <div className="flex items-center gap-2 text-xs text-gray-500 mt-4">
                          <FaClock className="text-lime-400 shrink-0" />
                          <span>
                            {new Date(session.startTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                            {" - "}
                            {new Date(session.endTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                          </span>
                        </div>

                        {/* Date */}
                        <div className="flex items-center gap-2 text-xs text-gray-500 mt-2">
                          <FaCalendarAlt className="text-lime-400 shrink-0" />
                          <span>
                            {new Date(session.startTime).toLocaleDateString("fr-FR", {
                              weekday: "short",
                              day: "numeric",
                              month: "short",
                            })}
                          </span>
                        </div>

                        {/* Salle */}
                        <div className="flex items-center gap-2 text-xs text-gray-500 mt-2">
                          <FaDoorOpen className="text-lime-400 shrink-0" />
                          <span>{session.room?.name || "Room"}</span>
                        </div>

                        {/* Capacité */}
                        {session.guestNumber && (
                          <div className="flex items-center gap-2 text-xs text-gray-500 mt-2">
                            <FaUsers className="text-lime-400 shrink-0" />
                            <span>{session.guestNumber} guests</span>
                          </div>
                        )}

                        {/* Speakers */}
                        {session.speakers && session.speakers.length > 0 && (
                          <div className="mt-5 pt-4 border-t border-white/5">
                            <p className="text-xs text-gray-500 mb-2 uppercase tracking-wider">Speakers</p>
                            <div className="flex flex-wrap gap-2">
                              {session.speakers.map((speaker) => (
                                <span
                                  key={speaker.id}
                                  className="px-3 py-1.5 rounded-full bg-lime-400/10 text-lime-300 text-xs font-medium border border-lime-400/10"
                                >
                                  {speaker.fullName}
                                </span>
                              ))}
                            </div>
                          </div>
                        )}
                      </Link>
                    ) : (
                      <div className="h-full rounded-[24px] border border-dashed border-white/5" />
                    )}
                  </div>
                );
              })}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}