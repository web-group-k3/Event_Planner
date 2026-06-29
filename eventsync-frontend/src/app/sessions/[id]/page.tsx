"use client";

import { useEffect, useState } from "react";
import { notFound, useParams } from "next/navigation";
import { getSessionById } from "@/services/session.service";
import LiveBadge from "@/components/sessions/LiveBadge";
import QuestionSection from "@/components/sessions/QuestionSection";
import FavoriteButton from "@/components/sessions/FavoriteButton";
import SpeakerModal from "@/components/speakers/SpeakerModal";
import { FaClock, FaDoorOpen, FaUsers } from "react-icons/fa";
import { Session } from "@/types";
import Link from "next/link";

export default function SessionDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [session, setSession] = useState<Session | null>(null);
  const [notFoundError, setNotFoundError] = useState(false);
  const [selectedSpeakerId, setSelectedSpeakerId] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    getSessionById(id)
      .then(setSession)
      .catch(() => setNotFoundError(true));
  }, [id]);
  useEffect(() => {
    if (!id) return;
    getSessionById(id)
      .then((data) => {
        setSession(data);
        window.scrollTo({ top: 0, behavior: "smooth" }); 
      })
      .catch(() => setNotFoundError(true));
  }, [id]);
  if (notFoundError) return notFound();
  if (!session) return (
    <main className="container-app py-20 flex items-center justify-center min-h-[60vh]">
      <div className="w-12 h-12 rounded-full border-2 border-lime-400/30 border-t-lime-400 animate-spin" />
    </main>
  );

  const now = new Date();
  const isLive = now >= new Date(session.startTime) && now <= new Date(session.endTime);

  return (
    <main className="container-app py-20">
      <section className="glass rounded-[40px] p-10 border border-white/10">
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
          <Link
            href={`/rooms#${session.room?.id}`}
            className="glass rounded-3xl p-6 border border-white/5 hover:border-lime-400/30 transition-all duration-300 hover:-translate-y-1 group"
          >
            <FaDoorOpen className="text-lime-400 mb-2" />
            <p className="text-sm text-gray-400">Room</p>
            <p className="font-bold text-xl mt-3 group-hover:text-lime-400 transition-colors">
              {session.room?.name ?? "—"}
            </p>
            {session.room?.adress && (
              <p className="text-xs text-gray-500 mt-1">{session.room.adress}</p>
            )}
          </Link>
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
          <span className="text-lime-400 text-4xl font-black">Meet the Speakers</span>
          <p className="text-gray-400 mt-5 text-lg leading-relaxed max-w-4xl">
            Get ready to learn from the brightest minds and forward-thinking pioneers who are shaping
            the future of the industry.
          </p>
        </div>

        {session.speakers && session.speakers.length > 0 ? (
          <div className="grid md:grid-cols-2 xl:grid-cols-3 gap-8">
            {session.speakers.map((speaker) => (
              <button
                key={speaker.id}
                onClick={() => setSelectedSpeakerId(speaker.id)}
                className="glass rounded-[32px] p-8 border border-white/5 text-left
                           hover:border-lime-400/30 transition-all duration-300 hover:-translate-y-1 group"
              >
                <div className="w-20 h-20 rounded-2xl overflow-hidden bg-gradient-to-br from-lime-400/20 to-sky-400/20 border border-white/10 flex items-center justify-center">
                  {speaker.photoUrl ? (
                    <img src={speaker.photoUrl} alt={speaker.fullName} className="w-full h-full object-cover" />
                  ) : (
                    <span className="text-2xl font-black text-lime-400">
                      {speaker.fullName.split(" ").map(n => n[0]).join("").toUpperCase().slice(0, 2)}
                    </span>
                  )}
                </div>
                <h3 className="text-2xl font-black mt-6 group-hover:text-lime-400 transition-colors">
                  {speaker.fullName}
                </h3>
                <p className="text-gray-400 mt-4 line-clamp-4">{speaker.bio}</p>
                <p className="text-xs text-lime-400/60 mt-4">View profile →</p>
              </button>
            ))}
          </div>
        ) : (
          <div className="glass rounded-[32px] p-16 text-center border border-white/5">
            <p className="text-gray-400">No speakers assigned to this session.</p>
          </div>
        )}
      </section>

      <QuestionSection sessionId={session.id} isLive={isLive} />

      <SpeakerModal
        speakerId={selectedSpeakerId}
        onClose={() => setSelectedSpeakerId(null)}
      />
    </main>
  );
}