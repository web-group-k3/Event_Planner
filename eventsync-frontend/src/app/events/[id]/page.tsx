import { notFound } from "next/navigation";
import Link from "next/link";
import { getEventById } from "@/services/event.service";

import LiveBadge from "@/components/sessions/LiveBadge";
import SessionCard from "@/components/sessions/SessionCard";

interface Props {
  params: Promise<{
    id: string;
  }>;
}

export default async function EventDetailPage({
  params,
}: Props) {

  const { id } = await params;

  try {

    const event = await getEventById(id);

    return (
      <main className="container-app py-20">

        {/* HERO */}

        <section className="relative overflow-hidden rounded-[40px] border border-white/10">

          <div className="absolute inset-0 bg-gradient-to-br from-lime-400/20 via-sky-400/10 to-[#ff4d6d]/20" />

          <div className="absolute inset-0 bg-black/50" />

          <div className="relative z-10 p-10 lg:p-16">

            <LiveBadge
              startDate={event.startDate}
              endDate={event.endDate}
            />

            <h1 className="text-5xl lg:text-7xl font-black mt-8 max-w-4xl leading-tight">

              {event.title}

            </h1>

            <p className="text-lg text-gray-300 mt-8 max-w-3xl leading-relaxed">

              {event.description}

            </p>

            <div className="mt-10 flex flex-wrap gap-8">

              <div>

                <p className="text-sm text-gray-400">
                  Start Date
                </p>

                <p className="font-semibold mt-2">

                  {new Date(
                    event.startDate
                  ).toLocaleDateString()}

                </p>

              </div>

              <div>

                <p className="text-sm text-gray-400">
                  End Date
                </p>

                <p className="font-semibold mt-2">

                  {new Date(
                    event.endDate
                  ).toLocaleDateString()}

                </p>

              </div>

            </div>

          </div>

        </section>

        {/* SESSIONS */}

        <section className="mt-20">

          <div className="flex items-center justify-between mb-10">

            <div>

              <span className="text-lime-400 font-semibold tracking-widest">
                SESSIONS
              </span>
              <Link
  href={`/events/${event.id}/schedule`}
  className="
    inline-flex
    items-center
    justify-center
    px-6 py-4
    rounded-2xl
    bg-lime-400
    text-black
    font-bold
    mt-8
  "
>

  View Schedule

</Link>
              <h2 className="text-4xl font-black mt-3">

                Event Sessions

              </h2>

            </div>

            <div className="text-sm text-gray-400">

              {event.sessions?.length || 0} Sessions

            </div>

          </div>
          
          <div className="grid lg:grid-cols-2 gap-8">

            {event.sessions?.map((session) => (

              <SessionCard
                key={session.id}
                session={session}
              />

            ))}

          </div>

        </section>

      </main>
    );

  } catch {

    notFound();

  }
}