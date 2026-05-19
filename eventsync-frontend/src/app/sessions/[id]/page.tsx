import { notFound } from "next/navigation";

import { getSessionById } from "@/services/session.service";

import LiveBadge from "@/components/sessions/LiveBadge";

interface Props {
  params: Promise<{
    id: string;
  }>;
}

export default async function SessionDetailPage({
  params,
}: Props) {

  const { id } = await params;

  try {

    const session =
      await getSessionById(id);

    return (
      <main className="container-app py-20">

        <div className="glass rounded-[40px] p-10">

          <LiveBadge
            startDate={session.startTime}
            endDate={session.endTime}
          />

          <h1 className="text-6xl font-black mt-8">

            {session.title}

          </h1>

          <p className="text-gray-400 mt-8 text-lg">

            {session.description}

          </p>

        </div>

      </main>
    );

  } catch {

    notFound();

  }
}