import { notFound } from "next/navigation";

import { getEventById } from "@/services/event.service";

import ScheduleGrid from "@/components/sessions/ScheduleGrid";

interface Props {
  params: Promise<{
    id: string;
  }>;
}

export default async function EventSchedulePage({
  params,
}: Props) {

  const { id } = await params;

  try {

    const event =
      await getEventById(id);

    return (

      <main className="container-app py-20">

        {/* Header */}

        <div className="mb-16">

          <span className="text-lime-400 font-semibold tracking-widest">

            MULTI-TRACK PLANNING

          </span>

          <h1 className="text-5xl lg:text-7xl font-black mt-4">

            {event.title}

          </h1>

          <p className="text-gray-400 text-lg mt-6 max-w-3xl leading-relaxed">

            Explore the complete conference
            planning across all rooms and
            tracks.

          </p>

        </div>

        {/* Event Info */}

        <div
          className="
            glass
            rounded-[32px]
            p-8
            border border-white/5
            mb-14
          "
        >

          <div className="grid md:grid-cols-3 gap-8">

            {/* Start */}

            <div>

              <p className="text-sm text-gray-500">
                Start Date
              </p>

              <p className="text-xl font-bold mt-3">

                {new Date(
                  event.startDate
                ).toLocaleDateString()}

              </p>

            </div>

            {/* End */}

            <div>

              <p className="text-sm text-gray-500">
                End Date
              </p>

              <p className="text-xl font-bold mt-3">

                {new Date(
                  event.endDate
                ).toLocaleDateString()}

              </p>

            </div>

            {/* Sessions */}

            <div>

              <p className="text-sm text-gray-500">
                Sessions
              </p>

              <p className="text-xl font-bold mt-3">

                {event.sessions?.length || 0}

              </p>

            </div>

          </div>

        </div>

        {/* Schedule */}

        <ScheduleGrid
          sessions={event.sessions || []}
        />

      </main>

    );

  } catch {

    notFound();

  }
}