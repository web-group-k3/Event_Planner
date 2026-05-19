import { getSessions } from "@/services/session.service";

import SessionCard from "@/components/sessions/SessionCard";

export default async function LiveSessionsPage() {

  const sessions =
    await getSessions();

  // Filter LIVE sessions

  const liveSessions =
    sessions.filter((session) => {

      const now = new Date();

      const start = new Date(
        session.startTime
      );

      const end = new Date(
        session.endTime
      );

      return (
        now >= start &&
        now <= end
      );

    });

  return (

    <main className="container-app py-20">

      {/* Header */}

      <div className="mb-16">

        <span className="text-[#ff4d6d] font-semibold tracking-widest">

          LIVE NOW

        </span>

        <h1 className="text-5xl lg:text-7xl font-black mt-4">

          Live Sessions

        </h1>

        <p className="text-gray-400 text-lg mt-6 max-w-3xl leading-relaxed">

          Join ongoing sessions happening
          right now across all conference
          rooms.

        </p>

      </div>

      {/* Empty */}

      {liveSessions.length === 0 && (

        <div
          className="
            glass
            rounded-[32px]
            p-16
            border border-white/5
            text-center
          "
        >

          <h2 className="text-3xl font-black">

            No Live Sessions

          </h2>

          <p className="text-gray-400 mt-6">

            There are currently no sessions
            running at the moment.

          </p>

        </div>

      )}

      {/* Sessions */}

      {liveSessions.length > 0 && (

        <div className="grid lg:grid-cols-2 gap-8">

          {liveSessions.map((session) => (

            <SessionCard
              key={session.id}
              session={session}
            />

          ))}

        </div>

      )}

    </main>

  );
}