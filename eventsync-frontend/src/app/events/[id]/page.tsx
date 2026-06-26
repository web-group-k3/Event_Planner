import { notFound } from "next/navigation";
import Link from "next/link";
import { getEventById } from "@/services/event.service";

import LiveBadge from "@/components/sessions/LiveBadge";
import SessionCard from "@/components/sessions/SessionCard";
import {  FaCalendarAlt, } from "react-icons/fa";

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

            <h1 className="text-5xl lg:text-5xl font-black mt-8 max-w-4xl leading-tight">

              {event.title}

            </h1>

            <p className="text-lg text-gray-300 mt-8 max-w-3xl leading-relaxed">

              {event.description}

            </p>

            <div className="mt-10 flex flex-wrap gap-8">

              <div>
                <FaCalendarAlt className="text-lime-400" />
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
                <FaCalendarAlt className="text-lime-400" />
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
        <div className="mb-16">
        <span className="text-lime-400 lg:text-4xl font-semibold tracking-widest">Explore the Sessions</span>
        <h1 className="text-5xl lg:text-2xl font-black mt-4">Craft Your Perfect Schedule and Dive into Expert-Led Discussions</h1>
        <p className="text-gray-400 mt-5 text-lg leading-relaxed">Dive deep into our comprehensive program designed to inspire and empower. 
          From keynote speeches and interactive workshops to panel debates led by industry experts, explore the full lineup of sessions below. Filter by track, speaker, 
          or time to customize your agenda and make the most out of your event experience.
        </p>
      </div>
          <div className="flex items-center justify-between mb-10">
            <div>
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
                  ml-9
                "
              >
                View Schedule
              </Link>
             
            </div>

            <div className=" inline-flex
                  items-center
                  justify-center
                  px-6 py-4
                  rounded-2xl
                  bg-lime-400
                  text-black
                  font-bold
                  mt-8
                  ml-9">

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