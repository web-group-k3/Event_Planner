"use client";

import { useEffect, useState } from "react";
import { Session } from "@/types";
import { getSessions } from "@/services/session.service";
import { format } from "date-fns";
import { Wifi, Clock, MapPin, Users, Mic } from "lucide-react";
import SpeakerModal from "@/components/SpeakerModal";
import SessionModal from "@/components/SessionModal";

function SessionCard({
  session,
  onSessionClick,
  onSpeakerClick,
}: {
  session: Session;
  onSessionClick: (id: string) => void;
  onSpeakerClick: (id: string) => void;
}) {
  const start = session.startTime ? format(new Date(session.startTime), "HH:mm") : "--:--";
  const end   = session.endTime   ? format(new Date(session.endTime),   "HH:mm") : "--:--";

  return (
    <div
      onClick={() => onSessionClick(session.id)}
      className={`
        relative glass rounded-3xl p-6 border transition-all duration-300 cursor-pointer
        hover:-translate-y-1
        ${session.live
          ? "border-[#ff4d6d]/40 shadow-[0_0_40px_rgba(255,77,109,0.08)] hover:shadow-[0_0_50px_rgba(255,77,109,0.15)]"
          : "border-white/10 hover:border-[#a3ff12]/30 hover:shadow-[0_0_30px_rgba(163,255,18,0.08)]"
        }
      `}
    >
      {session.live && (
        <div className="absolute top-5 right-5 flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-[#ff4d6d]/20 border border-[#ff4d6d]/30">
          <Wifi className="w-3 h-3 text-[#ff4d6d] animate-pulse" />
          <span className="text-xs font-black text-[#ff4d6d]">LIVE</span>
        </div>
      )}

      <h3 className="text-lg font-black text-white pr-20 leading-tight">{session.title}</h3>

      {session.description && (
        <p className="text-sm text-gray-400 mt-2 line-clamp-2 leading-relaxed">
          {session.description}
        </p>
      )}

      <div className="mt-4 flex flex-wrap gap-4">
        <div className="flex items-center gap-1.5 text-xs text-gray-500">
          <Clock className="w-3.5 h-3.5 text-[#38bdf8]" />
          <span>{start} – {end}</span>
        </div>
        {session.room?.name && (
          <div className="flex items-center gap-1.5 text-xs text-gray-500">
            <MapPin className="w-3.5 h-3.5 text-[#a3ff12]" />
            <span>{session.room.name}</span>
          </div>
        )}
        {session.guestNumber != null && (
          <div className="flex items-center gap-1.5 text-xs text-gray-500">
            <Users className="w-3.5 h-3.5" />
            <span>{session.guestNumber} guests</span>
          </div>
        )}
      </div>

      {(session.speakers?.length ?? 0) > 0 && (
        <div className="mt-4 flex flex-wrap gap-2">
          {session.speakers!.map((spk) => (
            <button
              key={spk.id}
              onClick={(e) => { e.stopPropagation(); onSpeakerClick(spk.id); }}
              className="flex items-center gap-1.5 px-3 py-1 rounded-full glass border border-white/10
                         text-xs text-gray-300 hover:border-[#a3ff12]/40 hover:text-[#a3ff12] transition-all"
            >
              <Mic className="w-3 h-3" />
              {spk.fullName}
            </button>
          ))}
        </div>
      )}

      <p className="mt-4 text-xs text-gray-700">Click for details →</p>
    </div>
  );
}

export default function LiveSessionsPage() {
  const [live, setLive]         = useState<Session[]>([]);
  const [upcoming, setUpcoming] = useState<Session[]>([]);
  const [loading, setLoading]   = useState(true);
  const [selectedSpeakerId, setSelectedSpeakerId] = useState<string | null>(null);
  const [selectedSessionId, setSelectedSessionId] = useState<string | null>(null);

  useEffect(() => {
    getSessions().then((allData) => {
        setLive(allData.filter((s) => s.live));
        const now = new Date();
        setUpcoming(
          allData
            .filter((s) => !s.live && new Date(s.startTime) > now)
            .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime())
            .slice(0, 6)
        );
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="relative min-h-screen">
      <div className="fixed top-10 left-0 w-96 h-96 bg-[#ff4d6d]/05 blur-[140px] pointer-events-none" />
      <div className="fixed bottom-0 right-0 w-96 h-96 bg-sky-400/06 blur-[140px] pointer-events-none" />

      <div className="container-app py-16">

        {/* Header */}
        <div className="mb-14">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full glass border border-white/10 mb-6">
            <div className="w-2 h-2 rounded-full bg-[#ff4d6d] animate-pulse" />
            <span className="text-sm text-gray-300">Real-time</span>
          </div>
          <h1 className="text-5xl lg:text-6xl font-black leading-tight">
            Sessions
            <span className="gradient-text"> Live</span>
          </h1>
          <p className="text-gray-400 mt-4 text-lg max-w-xl leading-relaxed">
            Follow sessions happening right now and upcoming talks scheduled today.
          </p>
        </div>

        {loading && (
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="glass rounded-3xl p-6 border border-white/10 animate-pulse">
                <div className="h-4 bg-white/05 rounded w-3/4 mb-3" />
                <div className="h-3 bg-white/05 rounded w-full mb-2" />
                <div className="h-3 bg-white/05 rounded w-2/3" />
              </div>
            ))}
          </div>
        )}

        {!loading && (
          <>
            {/* Live now */}
            <div className="mb-12">
              <div className="flex items-center gap-3 mb-6">
                <div className="flex items-center gap-2">
                  <div className="w-2.5 h-2.5 rounded-full bg-[#ff4d6d] animate-pulse" />
                  <h2 className="text-xl font-black text-[#ff4d6d] uppercase tracking-wide">
                    Live Now
                  </h2>
                </div>
                <div className="px-3 py-0.5 rounded-full bg-[#ff4d6d]/15 border border-[#ff4d6d]/20">
                  <span className="text-sm font-bold text-[#ff4d6d]">{live.length}</span>
                </div>
                <div className="h-px flex-1 bg-[#ff4d6d]/15" />
              </div>

              {live.length > 0 ? (
                <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-5">
                  {live.map((s) => (
                    <SessionCard key={s.id} session={s}
                      onSessionClick={setSelectedSessionId}
                      onSpeakerClick={setSelectedSpeakerId} />
                  ))}
                </div>
              ) : (
                <div className="glass rounded-3xl p-10 border border-white/10 text-center">
                  <Wifi className="w-10 h-10 text-gray-600 mx-auto mb-4" />
                  <p className="text-gray-400 font-bold">No live sessions at the moment</p>
                  <p className="text-gray-600 text-sm mt-1">Check back soon or browse upcoming sessions below.</p>
                </div>
              )}
            </div>

            {/* Upcoming */}
            {upcoming.length > 0 && (
              <div>
                <div className="flex items-center gap-3 mb-6">
                  <h2 className="text-xl font-black text-gray-400 uppercase tracking-wide">
                    Coming Up
                  </h2>
                  <div className="h-px flex-1 bg-white/06" />
                </div>
                <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-5">
                  {upcoming.map((s) => (
                    <SessionCard key={s.id} session={s}
                      onSessionClick={setSelectedSessionId}
                      onSpeakerClick={setSelectedSpeakerId} />
                  ))}
                </div>
              </div>
            )}
          </>
        )}

      </div>

      <SpeakerModal
        speakerId={selectedSpeakerId}
        onClose={() => setSelectedSpeakerId(null)}
      />
      <SessionModal
        sessionId={selectedSessionId}
        onClose={() => setSelectedSessionId(null)}
      />
    </div>
  );
}
