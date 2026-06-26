import { notFound } from "next/navigation";
import { getSessionById } from "@/services/session.service";
import LiveBadge from "@/components/sessions/LiveBadge";
import QuestionSection from "@/components/sessions/QuestionSection";
import FavoriteButton from "@/components/sessions/FavoriteButton";
import { FaClock, FaDoorOpen, FaUsers } from "react-icons/fa";

interface Props {
  params: Promise<{ id: string }>;
}

export default async function SessionDetailPage({ params }: Props) {
  const { id } = await params;

  try {
    const session = await getSessionById(id);

    const now = new Date();
    const isLive =
      now >= new Date(session.startTime) && now <= new Date(session.endTime);

    return (
      <main className="container-app py-20">
        <section className="glass rounded-[40px] p-10 lg:p-10 border border-white/10">
          <div className="flex flex-wrap items-start justify-between gap-6">
            <div>
              <LiveBadge startDate={session.startTime} endDate={session.endTime} />
              <h1 className="text-5xl text-lime-400 lg:text-4xl font-black mt-8 max-w-4xl leading-tight">
                {session.title}
              </h1>
            </div>
            <FavoriteButton id={session.id} type="session" label={session.title} className="mt-2" />
          </div>

          <p className="text-gray-300 text-lg leading-relaxed mt-5 max-w-4xl">
            {session.description}
          </p>

          <div className="grid md:grid-cols-3 gap-8 mt-14">
            <div className="glass rounded-3xl p-6">
              <FaClock className="text-lime-400 mb-2" />
              <p className="text-sm text-gray-400">Schedule</p>
              <p className="font-bold text-xl mt-3">
                {new Date(session.startTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                {" - "}
                {new Date(session.endTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
              </p>
            </div>

            <div className="glass rounded-3xl p-6">
              <FaDoorOpen className="text-lime-400 mb-2" />
              <p className="text-sm text-gray-400">Room</p>
              <p className="font-bold text-xl mt-3">
                {session.room?.name || "Main Room"}
              </p>
            </div>

            <div className="glass rounded-3xl p-6">
              <FaUsers className="text-lime-400 mb-2" />
              <p className="text-sm text-gray-400">Capacity</p>
              <p className="font-bold text-xl mt-3">
                {session.guestNumber || 0}
              </p>
            </div>
          </div>
        </section>

        <section className="mt-20">
          <div className="mb-10">
            <span className="text-lime-400 text-4xl font-black ">Meet the Speakers</span>
            <h2 className=" text-gray-400 mt-5 text-lg leading-relaxed mt-3">Get ready to learn from the brightest minds and forward-thinking pioneers who are shaping the future of the industry. 
              Our carefully selected speakers bring a wealth of firsthand experience, ground-breaking ideas, and practical knowledge straight to the stage.
               Explore their profiles below to discover who will be leading your sessions and driving the conversations that matter.</h2>
          </div>

          {session.speakers && session.speakers.length > 0 ? (
            <div className="grid md:grid-cols-2 xl:grid-cols-3 gap-8">
              {session.speakers.map((speaker) => (
                <div key={speaker.id} className="glass rounded-[32px] p-8 border border-white/5">
                  <div className="w-20 h-20 rounded-full bg-gradient-to-br from-lime-400 to-sky-400" />
                  <h3 className="text-2xl font-black mt-6">{speaker.fullName}</h3>
                  <p className="text-gray-400 mt-4 line-clamp-4">{speaker.bio}</p>
                </div>
              ))}
            </div>
          ) : (
            <div className="glass rounded-[32px] p-16 text-center border border-white/5">
              <p className="text-gray-400">No speakers assigned to this session.</p>
            </div>
          )}
        </section>

        <QuestionSection sessionId={session.id} isLive={isLive} />
      </main>
    );
  } catch {
    notFound();
  }
}