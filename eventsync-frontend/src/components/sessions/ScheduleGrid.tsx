import Link from "next/link";

import { Session } from "@/types";

import LiveBadge from "./LiveBadge";

interface Props {
  sessions: Session[];
}

export default function ScheduleGrid({
  sessions,
}: Props) {

  // Unique rooms

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

  // Unique hours

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

    <div className="overflow-x-auto">

      <div
        className="
          min-w-[1000px]
          border border-white/10
          rounded-[32px]
          overflow-hidden
        "
      >

        {/* Header */}

        <div
          className="
            grid
            bg-white/5
            border-b border-white/10
          "
          style={{
            gridTemplateColumns:
              `180px repeat(${rooms.length}, 1fr)`,
          }}
        >

          {/* Time */}

          <div
            className="
              p-6
              font-black
              border-r border-white/10
            "
          >

            Time

          </div>

          {/* Rooms */}

          {rooms.map((room: any) => (

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

        {/* Rows */}

        {uniqueHours.map((hour) => (

          <div
            key={hour}
            className="
              grid
              border-b border-white/5
            "
            style={{
              gridTemplateColumns:
                `180px repeat(${rooms.length}, 1fr)`,
            }}
          >

            {/* Hour */}

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

            {/* Cells */}

            {rooms.map((room: any) => {

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
                    min-h-[180px]
                  "
                >

                  {session ? (

                    <Link
                      href={`/sessions/${session.id}`}
                      className="
                        block
                        glass
                        rounded-[24px]
                        p-5
                        h-full
                        hover:border-lime-400/20
                        border border-white/5
                        transition-all
                        duration-300
                        hover:-translate-y-1
                      "
                    >

                      {/* Badge */}

                      <LiveBadge
                        startDate={
                          session.startTime
                        }
                        endDate={
                          session.endTime
                        }
                      />

                      {/* Title */}

                      <h3 className="font-black text-xl mt-5">

                        {session.title}

                      </h3>

                      {/* Description */}

                      <p className="text-sm text-gray-400 mt-4 line-clamp-3">

                        {session.description}

                      </p>

                      {/* Time */}

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

  );
}