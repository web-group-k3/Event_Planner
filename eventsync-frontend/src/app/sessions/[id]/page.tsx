import { notFound } from "next/navigation";

import { getSessionById } from "@/services/session.service";
import { getQuestionsBySession } from "@/services/question.service";

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

    const questions =
      await getQuestionsBySession(id);

    return (
      <main className="container-app py-20">

        {/* HERO */}

        <section className="glass rounded-[40px] p-10 lg:p-16 border border-white/10">

          {/* Top */}

          <div className="flex flex-wrap items-start justify-between gap-6">

            <div>

              <LiveBadge
                startDate={session.startTime}
                endDate={session.endTime}
              />

              <h1 className="text-5xl lg:text-6xl font-black mt-8 max-w-4xl leading-tight">

                {session.title}

              </h1>

            </div>

            {/* Favorite */}

            <button
              className="
                px-6 py-4
                rounded-2xl
                bg-lime-400
                text-black
                font-bold
                hover:scale-105
                transition
              "
            >

              ☆ Favorite

            </button>

          </div>

          {/* Description */}

          <p className="text-gray-300 text-lg leading-relaxed mt-10 max-w-4xl">

            {session.description}

          </p>

          {/* Infos */}

          <div className="grid md:grid-cols-3 gap-8 mt-14">

            {/* Time */}

            <div className="glass rounded-3xl p-6">

              <p className="text-sm text-gray-400">
                Schedule
              </p>

              <p className="font-bold text-xl mt-3">

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

            </div>

            {/* Room */}

            <div className="glass rounded-3xl p-6">

              <p className="text-sm text-gray-400">
                Room
              </p>

              <p className="font-bold text-xl mt-3">

                {session.room?.name || "Main Room"}

              </p>

            </div>

            {/* Capacity */}

            <div className="glass rounded-3xl p-6">

              <p className="text-sm text-gray-400">
                Capacity
              </p>

              <p className="font-bold text-xl mt-3">

                {session.guestNumber || 0}

              </p>

            </div>

          </div>

        </section>

        {/* SPEAKERS */}

        <section className="mt-20">

          <div className="mb-10">

            <span className="text-lime-400 font-semibold tracking-widest">
              SPEAKERS
            </span>

            <h2 className="text-4xl font-black mt-3">

              Session Speakers

            </h2>

          </div>

          <div className="grid md:grid-cols-2 xl:grid-cols-3 gap-8">

            {session.speakers?.map((speaker) => (

              <div
                key={speaker.id}
                className="
                  glass
                  rounded-[32px]
                  p-8
                  border border-white/5
                "
              >

                {/* Avatar */}

                <div
                  className="
                    w-20 h-20
                    rounded-full
                    bg-gradient-to-br
                    from-lime-400
                    to-sky-400
                  "
                />

                <h3 className="text-2xl font-black mt-6">

                  {speaker.fullName}

                </h3>

                <p className="text-gray-400 mt-4 line-clamp-4">

                  {speaker.bio}

                </p>

              </div>

            ))}

          </div>

        </section>

        {/* QUESTIONS */}

        <section className="mt-20">

          <div className="mb-10">

            <span className="text-lime-400 font-semibold tracking-widest">
              Q&A
            </span>

            <h2 className="text-4xl font-black mt-3">

              Live Questions

            </h2>

          </div>

          {/* Questions */}

          <div className="space-y-6">

            {questions.map((question) => (

              <div
                key={question.id}
                className="
                  glass
                  rounded-[28px]
                  p-8
                  border border-white/5
                  flex items-start justify-between gap-6
                "
              >

                <div>

                  <p className="text-lg leading-relaxed">

                    {question.content}

                  </p>

                  <p className="text-sm text-gray-500 mt-4">

                    {question.authorName || "Anonymous"}

                  </p>

                </div>

                {/* Votes */}

                <button
                  className="
                    min-w-[90px]
                    h-14
                    rounded-2xl
                    bg-lime-400
                    text-black
                    font-black
                    hover:scale-105
                    transition
                  "
                >

                  ▲ {question.upvotes}

                </button>

              </div>

            ))}

          </div>

        </section>

      </main>
    );

  } catch {

    notFound();

  }
}