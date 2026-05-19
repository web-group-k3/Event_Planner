"use client";

import { useState } from "react";

import Link from "next/link";

import { Session } from "@/types";

import LiveBadge from "./LiveBadge";

interface Props {
  sessions: Session[];
}

export default function ScheduleGrid({
  sessions,
}: Props) {

  // -----------------------------
  // Rooms
  // -----------------------------

  const roomsMap = new Map();

  sessions.forEach((session) => {

    if (session.room) {

      roomsMap.set(
        session.room.id,
        session.room
      );

    }

  });

  const rooms = Array.from(
    roomsMap.values()
  );

  // -----------------------------
  // Filter room
  // -----------------------------

  const [selectedRoom, setSelectedRoom] =
    useState<string>("all");

  const filteredRooms =
    selectedRoom === "all"
      ? rooms
      : rooms.filter(
          (room: any) =>
            room.id === selectedRoom
        );

  // -----------------------------
  // Unique Hours
  // -----------------------------

  const uniqueHours = Array.from(
    new Set(

      sessions.map((session) => {

        return new Date(
          session.startTime
        ).toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
        });

      })

    )
  ).sort();

  return (

    <div>

      {/* ========================================= */}
      {/* ROOM FILTERS */}
      {/* ========================================= */}

      <div className="flex flex-wrap gap-4 mb-10">

        {/* ALL */}

        <button
          onClick={() =>
            setSelectedRoom("all")
          }
          className={`
            px-5 py-3 rounded-2xl
            font-semibold transition-all
            ${
              selectedRoom === "all"
                ? "bg-lime-400 text-black"
                : "bg-white/5 text-white"
            }
          `}
        >

          All Rooms

        </button>

        {/* ROOMS */}

        {rooms.map((room: any) => (

          <button
            key={room.id}
            onClick={() =>
              setSelectedRoom(room.id)
            }
            className={`
              px-5 py-3 rounded-2xl
              font-semibold transition-all
              ${
                selectedRoom === room.id
                  ? "bg-lime-400 text-black"
                  : "bg-white/5 text-white"
              }
            `}
          >

            {room.name}

          </button>

        ))}

      </div>

      {/* ========================================= */}
      {/* GRID */}
      {/* ========================================= */}

      <div className="overflow-x-auto">

        <div
          className="
            min-w-[1000px]
            border border-white/10
            rounded-[32px]
            overflow-hidden
            bg-white/[0.02]
            backdrop-blur-xl
          "
        >

          {/* ========================================= */}
          {/* HEADER */}
          {/* ========================================= */}

          <div
            className="
              grid
              bg-white/5
              border-b border-white/10
            "
            style={{
              gridTemplateColumns:
                `180px repeat(${filteredRooms.length}, 1fr)`,
            }}
          >

            {/* TIME */}

            <div
              className="
                p-6
                font-black
                border-r border-white/10
              "
            >

              Time

            </div>

            {/* ROOMS */}

            {filteredRooms.map((room: any) => (

              <div
                key={room.id}
                className="
                  p-6
                  font-black
                  border-r border-white/10
                "
              >

                {room.name}

              </div>

            ))}

          </div>

          {/* ========================================= */}
          {/* ROWS */}
          {/* ========================================= */}

          {uniqueHours.map((hour) => (

            <div
              key={hour}
              className="
                grid
                border-b border-white/5
              "
              style={{
                gridTemplateColumns:
                  `180px repeat(${filteredRooms.length}, 1fr)`,
              }}
            >

              {/* HOUR */}

              <div
                className="
                  p-6
                  border-r border-white/10
                  text-gray-400
                  font-semibold
                "
              >

                {hour}

              </div>

              {/* CELLS */}

              {filteredRooms.map((room: any) => {

                const session =
                  sessions.find((s) => {

                    const sessionHour =
                      new Date(
                        s.startTime
                      ).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      });

                    return (
                      sessionHour === hour &&
                      s.room?.id === room.id
                    );

                  });

                return (

                  <div
                    key={`${hour}-${room.id}`}
                    className="
                      p-4
                      border-r border-white/10
                      min-h-[220px]
                    "
                  >

                    {session ? (

                      <Link
                        href={`/sessions/${session.id}`}
                        className="
                          block
                          rounded-[24px]
                          p-5
                          h-full
                          border border-white/5
                          bg-white/[0.03]
                          hover:border-lime-400/20
                          transition-all
                          duration-300
                          hover:-translate-y-1
                        "
                      >

                        {/* BADGE */}

                        <LiveBadge
                          startDate={
                            session.startTime
                          }
                          endDate={
                            session.endTime
                          }
                        />

                        {/* TITLE */}

                        <h3 className="font-black text-xl mt-5">

                          {session.title}

                        </h3>

                        {/* DESCRIPTION */}

                        <p className="text-sm text-gray-400 mt-4 line-clamp-3">

                          {session.description}

                        </p>

                        {/* SPEAKERS */}

                        {session.speakers &&
                          session.speakers.length > 0 && (

                            <div className="mt-6 flex flex-wrap gap-2">

                              {session.speakers.map((speaker) => (

                                <span
                                  key={speaker.id}
                                  className="
                                    px-3 py-2
                                    rounded-full
                                    bg-white/5
                                    text-xs
                                  "
                                >

                                  {speaker.fullName}

                                </span>

                              ))}

                            </div>

                          )}

                        {/* TIME */}

                        <p className="text-xs text-gray-500 mt-6">

                          {new Date(
                            session.startTime
                          ).toLocaleTimeString([], {
                            hour: "2-digit",
                            minute: "2-digit",
                          })}

                          {" - "}

                          {new Date(
                            session.endTime
                          ).toLocaleTimeString([], {
                            hour: "2-digit",
                            minute: "2-digit",
                          })}

                        </p>

                      </Link>

                    ) : (

                      <div
                        className="
                          h-full
                          rounded-[24px]
                          border border-dashed
                          border-white/5
                        "
                      />

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