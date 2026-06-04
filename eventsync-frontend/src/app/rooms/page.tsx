"use client";

import { useEffect, useState } from "react";
import { Room, Session } from "@/types";
import { getRooms } from "@/services/room.service";
import { getSessionsByRoom } from "@/services/session.service";
import {
  Clock, ChevronDown, ChevronUp,
  Wifi, Building2, Mic, Calendar, Search, X
} from "lucide-react";
import { format } from "date-fns";
import SpeakerModal from "@/components/SpeakerModal";
import SessionModal from "@/components/SessionModal";

/* ── helpers ─────────────────────────────────────────── */

function formatTime(dt: string) {
  try { return format(new Date(dt), "HH:mm"); }
  catch { return "--:--"; }
}

function formatDate(dt: string) {
  try { return format(new Date(dt), "EEE d MMM"); }
  catch { return ""; }
}

/* ── Session row in planning ─────────────────────────── */

function SessionRow({
  session,
  onSessionClick,
  onSpeakerClick,
}: {
  session: Session;
  onSessionClick: (id: string) => void;
  onSpeakerClick: (id: string) => void;
}) {
  return (
    <div
      onClick={() => onSessionClick(session.id)}
      className={`
        relative flex gap-5 p-5 rounded-2xl border transition-all duration-300 cursor-pointer
        ${session.live
          ? "border-[#ff4d6d]/30 bg-[#ff4d6d]/04 hover:border-[#ff4d6d]/60 hover:shadow-[0_0_20px_rgba(255,77,109,0.08)]"
          : "border-white/08 glass hover:border-[#a3ff12]/30 hover:shadow-[0_0_20px_rgba(163,255,18,0.05)]"
        }
      `}
    >

      {/* Time column */}
      <div className="shrink-0 w-16 text-right">
        <p className="text-sm font-bold text-[#38bdf8]">{formatTime(session.startTime)}</p>
        <p className="text-xs text-gray-600 mt-1">{formatTime(session.endTime)}</p>
      </div>

      {/* Divider */}
      <div className="shrink-0 flex flex-col items-center gap-1 pt-1">
        <div className={`w-2 h-2 rounded-full ${session.live ? "bg-[#ff4d6d] animate-pulse" : "bg-[#a3ff12]/50"}`} />
        <div className="w-px flex-1 bg-white/08" />
      </div>

      {/* Content */}
      <div className="flex-1 min-w-0">

        <div className="flex items-start justify-between gap-3">
          <h4 className="font-bold text-white leading-tight">{session.title}</h4>
          {session.live && (
            <span className="shrink-0 flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-[#ff4d6d]/15 border border-[#ff4d6d]/30">
              <Wifi className="w-3 h-3 text-[#ff4d6d] animate-pulse" />
              <span className="text-xs font-bold text-[#ff4d6d]">LIVE</span>
            </span>
          )}
        </div>

        {session.description && (
          <p className="text-sm text-gray-400 mt-1.5 line-clamp-2 leading-relaxed">
            {session.description}
          </p>
        )}

        {/* Speakers chips */}
        {(session.speakers?.length ?? 0) > 0 && (
          <div className="mt-3 flex flex-wrap gap-2">
            {session.speakers!.map((spk) => (
              <button
                key={spk.id}
                onClick={(e) => { e.stopPropagation(); onSpeakerClick(spk.id); }}
                className="flex items-center gap-1.5 px-3 py-1 rounded-full glass border border-white/10
                           text-xs text-gray-300 hover:border-[#a3ff12]/40 hover:text-[#a3ff12]
                           transition-all duration-200"
              >
                <Mic className="w-3 h-3" />
                {spk.fullName}
              </button>
            ))}
          </div>
        )}

        {session.guestNumber != null && (
          <div className="mt-3 flex items-center gap-1.5 text-xs text-gray-600">
            <Users className="w-3.5 h-3.5" />
            <span>{session.guestNumber} guests expected</span>
          </div>
        )}

      </div>

    </div>
  );
}

/* ── Room card ───────────────────────────────────────── */

function RoomCard({
  room,
  onSessionClick,
  onSpeakerClick,
}: {
  room: Room;
  onSessionClick: (id: string) => void;
  onSpeakerClick: (id: string) => void;
}) {

  const [sessions, setSessions] = useState<Session[]>([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(true);

  useEffect(() => {
    getSessionsByRoom(room.id)
      .then(setSessions)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [room.id]);

  const liveSessions = sessions.filter((s) => s.live);
  const hasSessions = sessions.length > 0;

  /* Group sessions by date */
  const byDate = sessions.reduce<Record<string, Session[]>>((acc, s) => {
    const key = formatDate(s.startTime);
    if (!acc[key]) acc[key] = [];
    acc[key].push(s);
    return acc;
  }, {});

  return (
    <div
      className={`
        glass rounded-[28px] border overflow-hidden transition-all duration-300
        ${liveSessions.length > 0
          ? "border-[#ff4d6d]/25 shadow-[0_0_40px_rgba(255,77,109,0.06)]"
          : "border-white/10 hover:border-[#a3ff12]/20"
        }
      `}
    >

      {/* Room header */}
      <div
        className="flex items-start justify-between gap-4 p-6 cursor-pointer"
        onClick={() => setExpanded((v) => !v)}
      >

        <div className="flex items-start gap-4">

          {/* Icon */}
          <div className={`
            w-12 h-12 rounded-2xl flex items-center justify-center shrink-0
            ${liveSessions.length > 0
              ? "bg-[#ff4d6d]/10 border border-[#ff4d6d]/20"
              : "bg-[#a3ff12]/10 border border-[#a3ff12]/20"
            }
          `}>
            <Building2 className={`w-5 h-5 ${liveSessions.length > 0 ? "text-[#ff4d6d]" : "text-[#a3ff12]"}`} />
          </div>

          <div>

            <div className="flex items-center gap-3 flex-wrap">
              <h2 className="text-xl font-black text-white">{room.name}</h2>
              {liveSessions.length > 0 && (
                <span className="flex items-center gap-1.5 px-3 py-0.5 rounded-full bg-[#ff4d6d]/20 border border-[#ff4d6d]/30">
                  <div className="w-1.5 h-1.5 rounded-full bg-[#ff4d6d] animate-pulse" />
                  <span className="text-xs font-bold text-[#ff4d6d]">{liveSessions.length} LIVE</span>
                </span>
              )}
            </div>

            <div className="flex flex-wrap gap-4 mt-2">
              <div className="flex items-center gap-1.5 text-xs text-gray-500">
                <Calendar className="w-3.5 h-3.5 text-gray-600" />
                <span>{sessions.length} session{sessions.length !== 1 ? "s" : ""}</span>
              </div>
            </div>

          </div>

        </div>

        <button className="shrink-0 w-8 h-8 rounded-xl glass border border-white/10 flex items-center justify-center text-gray-400 hover:text-white transition">
          {expanded ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
        </button>

      </div>

      {/* Planning body */}
      {expanded && (
        <div className="px-6 pb-6">

          {/* Divider */}
          <div className="h-px bg-white/06 mb-6" />

          {loading && (
            <div className="space-y-3">
              {[1, 2, 3].map((i) => (
                <div key={i} className="h-20 rounded-2xl bg-white/03 animate-pulse" />
              ))}
            </div>
          )}

          {!loading && !hasSessions && (
            <div className="flex flex-col items-center py-10 text-center">
              <Clock className="w-8 h-8 text-gray-700 mb-3" />
              <p className="text-gray-500 text-sm">No sessions scheduled for this room.</p>
            </div>
          )}

          {!loading && hasSessions && (
            <div className="space-y-8">
              {Object.entries(byDate).map(([date, daySessions]) => (
                <div key={date}>

                  {/* Date label */}
                  <div className="flex items-center gap-3 mb-4">
                    <div className="px-3 py-1 rounded-full glass border border-white/10">
                      <span className="text-xs font-bold text-gray-400">{date}</span>
                    </div>
                    <div className="h-px flex-1 bg-white/06" />
                  </div>

                  <div className="space-y-3">
                    {daySessions
                      .sort((a, b) =>
                        new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
                      )
                      .map((s) => (
                        <SessionRow
                          key={s.id}
                          session={s}
                          onSessionClick={onSessionClick}
                          onSpeakerClick={onSpeakerClick}
                        />
                      ))}
                  </div>

                </div>
              ))}
            </div>
          )}

        </div>
      )}

    </div>
  );
}

/* ── Page ────────────────────────────────────────────── */

export default function RoomsPage() {

  const [rooms, setRooms] = useState<Room[]>([]);
  const [filtered, setFiltered] = useState<Room[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [selectedSpeakerId, setSelectedSpeakerId] = useState<string | null>(null);
  const [selectedSessionId, setSelectedSessionId] = useState<string | null>(null);

  useEffect(() => {
    getRooms()
      .then((data) => { setRooms(data); setFiltered(data); })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    const q = search.toLowerCase().trim();
    setFiltered(!q ? rooms : rooms.filter((r) =>
      r.name.toLowerCase().includes(q)
    ));
  }, [search, rooms]);

  return (
    <div className="relative min-h-screen">

      {/* Glows */}
      <div className="fixed top-20 right-0 w-96 h-96 bg-sky-400/06 blur-[140px] pointer-events-none" />
      <div className="fixed bottom-0 left-0 w-96 h-96 bg-lime-400/06 blur-[140px] pointer-events-none" />

      <div className="container-app py-16">

        {/* Header */}
        <div className="mb-14">

          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full glass border border-white/10 mb-6">
            <Building2 className="w-3.5 h-3.5 text-[#38bdf8]" />
            <span className="text-sm text-gray-300">Venues & Rooms</span>
          </div>

          <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">

            <div>
              <h1 className="text-5xl lg:text-6xl font-black leading-tight">
                Room
                <span className="gradient-text"> Planning</span>
              </h1>
              <p className="text-gray-400 mt-4 text-lg max-w-xl leading-relaxed">
                Browse all venues, explore their schedules and see which
                sessions are happening live right now.
              </p>
            </div>

            {!loading && (
              <div className="flex items-center gap-2 px-5 py-3 rounded-2xl glass border border-white/10">
                <span className="text-3xl font-black text-[#38bdf8]">{rooms.length}</span>
                <span className="text-sm text-gray-400">rooms</span>
              </div>
            )}

          </div>

        </div>

        {/* Search */}
        <div className="relative mb-10 max-w-xl">

          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500" />

          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by room name..."
            className="w-full pl-11 pr-11 py-3.5 rounded-2xl glass border border-white/10 text-sm text-white placeholder-gray-500 outline-none focus:border-[#38bdf8]/40 transition-all duration-200"
          />

          {search && (
            <button
              onClick={() => setSearch("")}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 hover:text-white transition"
            >
              <X className="w-4 h-4" />
            </button>
          )}

        </div>

        {/* Loading skeletons */}
        {loading && (
          <div className="space-y-5">
            {[1, 2, 3].map((i) => (
              <div key={i} className="glass rounded-[28px] border border-white/10 p-6 animate-pulse">
                <div className="flex items-center gap-4 mb-6">
                  <div className="w-12 h-12 rounded-2xl bg-white/05" />
                  <div className="flex-1 space-y-2">
                    <div className="h-5 bg-white/05 rounded-lg w-1/3" />
                    <div className="h-3 bg-white/05 rounded-lg w-1/4" />
                  </div>
                </div>
                <div className="space-y-3">
                  {[1, 2].map((j) => (
                    <div key={j} className="h-20 rounded-2xl bg-white/03" />
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Room list */}
        {!loading && filtered.length > 0 && (
          <div className="space-y-5">
            {filtered.map((room) => (
              <RoomCard
                key={room.id}
                room={room}
                onSessionClick={setSelectedSessionId}
                onSpeakerClick={setSelectedSpeakerId}
              />
            ))}
          </div>
        )}

        {/* Empty state */}
        {!loading && filtered.length === 0 && (
          <div className="flex flex-col items-center justify-center py-32 text-center">
            <div className="w-20 h-20 rounded-3xl glass border border-white/10 flex items-center justify-center mb-6">
              <Building2 className="w-8 h-8 text-gray-600" />
            </div>
            <h3 className="text-xl font-black text-gray-400 mb-2">No rooms found</h3>
            <p className="text-gray-600 text-sm">Try adjusting your search.</p>
            {search && (
              <button onClick={() => setSearch("")} className="mt-6 button-secondary text-sm">
                Clear search
              </button>
            )}
          </div>
        )}

      </div>

      {/* Speaker modal (triggered from session rows) */}
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
