"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";

import { Event, Session } from "@/types";
import { getEventById } from "@/services/event.service";
import { getSessionsByEvent } from "@/services/session.service";
import SessionCard from "@/components/sessions/SessionCard";
import LiveBadge from "@/components/sessions/LiveBadge";
import { isLive, sortByStartTime, groupByRoom, formatDate } from "@/utils/session";

export default function EventDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();

  const [event, setEvent] = useState<Event | null>(null);
  const [sessions, setSessions] = useState<Session[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<"list" | "planning">("list");
  const [activeRoom, setActiveRoom] = useState<string>("all");

  useEffect(() => {
    Promise.all([getEventById(id), getSessionsByEvent(id)])
    .then(([eventData, sessionsData]) => {
      console.log("sessions reçues →", JSON.stringify(sessionsData, null, 2)); // 👈
      setEvent(eventData);
      setSessions(sortByStartTime(sessionsData));
    })
      .catch(() => setError("Impossible de charger l'événement."))
      .finally(() => setLoading(false));
  }, [id]);

  // ── Loading ──────────────────────────────────────────────
  if (loading) {
    return (
      <main className="min-h-screen px-6 py-20 max-w-7xl mx-auto">
        <div className="h-8 w-32 bg-white/5 rounded-xl animate-pulse mb-12" />
        <div className="h-16 w-96 bg-white/5 rounded-2xl animate-pulse mb-4" />
        <div className="h-5 w-64 bg-white/5 rounded-xl animate-pulse mb-16" />
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="h-64 rounded-[28px] bg-white/5 animate-pulse" />
          ))}
        </div>
      </main>
    );
  }

  // ── Error ────────────────────────────────────────────────
  if (error || !event) {
    return (
      <main className="min-h-screen flex items-center justify-center">
        <div className="glass rounded-[32px] p-12 text-center max-w-md">
          <p className="text-4xl mb-4">⚠️</p>
          <p className="text-gray-400">{error ?? "Événement introuvable."}</p>
          <button
            onClick={() => router.push("/events")}
            className="mt-8 px-6 py-3 rounded-2xl bg-lime-400 text-black font-black hover:scale-105 transition"
          >
            Retour aux événements
          </button>
        </div>
      </main>
    );
  }

  // ── Données dérivées ─────────────────────────────────────
  const liveSessions = sessions.filter(isLive);
  const rooms = Object.keys(groupByRoom(sessions));
  const byRoom = groupByRoom(sessions);

  const filteredSessions =
    activeRoom === "all"
      ? sessions
      : sessions.filter((s) => s.room?.name === activeRoom);

  // ── Content ──────────────────────────────────────────────
  return (
    <main className="min-h-screen px-6 py-20 max-w-7xl mx-auto">

      {/* Retour */}
      <Link
        href="/events"
        className="inline-flex items-center gap-2 text-gray-400 hover:text-white transition mb-12 text-sm"
      >
        ← Tous les événements
      </Link>

      {/* Hero */}
      <div className="glass rounded-[40px] p-10 mb-12 border border-white/5 relative overflow-hidden">

        {/* Glow background */}
        <div className="absolute inset-0 bg-gradient-to-br from-lime-400/5 via-transparent to-sky-400/5 pointer-events-none" />

        <div className="relative flex flex-col md:flex-row md:items-start md:justify-between gap-6">

          <div className="flex-1">
            <h1 className="text-5xl font-black mb-4 leading-tight">
              {event.title}
            </h1>
            <p className="text-gray-400 text-lg leading-relaxed max-w-2xl">
              {event.description}
            </p>
          </div>

          {/* Live badge si l'event est en cours */}
          {liveSessions.length > 0 && (
            <div className="flex-shrink-0">
              <div className="px-4 py-2 rounded-full bg-[#ff4d6d]/20 text-[#ff4d6d] border border-[#ff4d6d]/30 text-sm font-bold tracking-widest inline-flex items-center gap-2">
                <span className="relative flex h-2 w-2">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#ff4d6d] opacity-75" />
                  <span className="relative inline-flex rounded-full h-2 w-2 bg-[#ff4d6d]" />
                </span>
                {liveSessions.length} session{liveSessions.length > 1 ? "s" : ""} en direct
              </div>
            </div>
          )}

        </div>

        {/* Infos */}
        <div className="relative mt-8 flex flex-wrap gap-4">
          <div className="px-4 py-2 rounded-xl bg-white/5 border border-white/5 text-sm text-gray-300">
            📅 {formatDate(event.startDate)} → {formatDate(event.endDate)}
          </div>
          {event.location && (
            <div className="px-4 py-2 rounded-xl bg-white/5 border border-white/5 text-sm text-gray-300">
              📍 {event.location}
            </div>
          )}
          <div className="px-4 py-2 rounded-xl bg-white/5 border border-white/5 text-sm text-gray-300">
            🎤 {sessions.length} session{sessions.length > 1 ? "s" : ""}
          </div>
        </div>

      </div>

      {/* Sessions live en avant */}
      {liveSessions.length > 0 && (
        <div className="mb-12">
          <h2 className="text-2xl font-black mb-6 text-[#ff4d6d]">
            🔴 En ce moment
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {liveSessions.map((session) => (
              <SessionCard key={session.id} session={session} />
            ))}
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="flex items-center gap-4 mb-8">
        <button
          onClick={() => setActiveTab("list")}
          className={`px-6 py-3 rounded-2xl font-bold text-sm transition ${
            activeTab === "list"
              ? "bg-lime-400 text-black"
              : "bg-white/5 text-gray-400 hover:text-white"
          }`}
        >
          Liste
        </button>
        <button
          onClick={() => setActiveTab("planning")}
          className={`px-6 py-3 rounded-2xl font-bold text-sm transition ${
            activeTab === "planning"
              ? "bg-lime-400 text-black"
              : "bg-white/5 text-gray-400 hover:text-white"
          }`}
        >
          Planning multi-track
        </button>
      </div>

      {/* ── Vue Liste ── */}
      {activeTab === "list" && (
        <div>

          {/* Filtre par salle */}
          {rooms.length > 1 && (
            <div className="flex items-center gap-3 flex-wrap mb-8">
              <button
                onClick={() => setActiveRoom("all")}
                className={`px-4 py-2 rounded-xl text-sm font-bold transition ${
                  activeRoom === "all"
                    ? "bg-lime-400/20 text-lime-400 border border-lime-400/30"
                    : "bg-white/5 text-gray-400 hover:text-white border border-white/5"
                }`}
              >
                Toutes les salles
              </button>
              {rooms.map((room) => (
                <button
                  key={room}
                  onClick={() => setActiveRoom(room)}
                  className={`px-4 py-2 rounded-xl text-sm font-bold transition ${
                    activeRoom === room
                      ? "bg-lime-400/20 text-lime-400 border border-lime-400/30"
                      : "bg-white/5 text-gray-400 hover:text-white border border-white/5"
                  }`}
                >
                  {room}
                </button>
              ))}
            </div>
          )}

          {filteredSessions.length === 0 ? (
            <div className="glass rounded-[32px] p-12 text-center">
              <p className="text-gray-400">Aucune session pour cette salle.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredSessions.map((session) => (
                <SessionCard key={session.id} session={session} />
              ))}
            </div>
          )}

        </div>
      )}

      {/* ── Vue Planning Multi-Track ── */}
      {activeTab === "planning" && (
        <div className="overflow-x-auto">

          {rooms.length === 0 ? (
            <div className="glass rounded-[32px] p-12 text-center">
              <p className="text-gray-400">Aucune session disponible.</p>
            </div>
          ) : (
            <div className="min-w-[700px]">

              {/* Header salles */}
              <div
                className="grid gap-4 mb-4"
                style={{ gridTemplateColumns: `120px repeat(${rooms.length}, 1fr)` }}
              >
                <div />
                {rooms.map((room) => (
                  <div
                    key={room}
                    className="glass rounded-2xl px-4 py-3 text-center text-sm font-black text-lime-400 border border-lime-400/20"
                  >
                    {room}
                  </div>
                ))}
              </div>

              {/* Lignes horaires */}
              {sortByStartTime(sessions)
                .reduce<string[]>((slots, s) => {
                  const slot = new Date(s.startTime).toLocaleTimeString("fr-FR", {
                    hour: "2-digit",
                    minute: "2-digit",
                  });
                  return slots.includes(slot) ? slots : [...slots, slot];
                }, [])
                .map((slot) => (
                  <div
                    key={slot}
                    className="grid gap-4 mb-4"
                    style={{ gridTemplateColumns: `120px repeat(${rooms.length}, 1fr)` }}
                  >
                    {/* Heure */}
                    <div className="flex items-center justify-center">
                      <span className="text-sm font-black text-gray-400">
                        {slot}
                      </span>
                    </div>

                    {/* Cellules par salle */}
                    {rooms.map((room) => {
                      const session = byRoom[room]?.find(
                        (s) =>
                          new Date(s.startTime).toLocaleTimeString("fr-FR", {
                            hour: "2-digit",
                            minute: "2-digit",
                          }) === slot
                      );

                      return (
                        <div key={room}>
                          {session ? (
                            <Link href={`/sessions/${session.id}`}>
                              <div
                                className={`
                                  glass rounded-2xl p-4 border transition hover:-translate-y-1 duration-300 cursor-pointer
                                  ${isLive(session)
                                    ? "border-[#ff4d6d]/30 hover:border-[#ff4d6d]/50"
                                    : "border-white/5 hover:border-lime-400/20"
                                  }
                                `}
                              >
                                {isLive(session) && (
                                  <div className="flex items-center gap-1.5 mb-2">
                                    <span className="relative flex h-1.5 w-1.5">
                                      <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#ff4d6d] opacity-75" />
                                      <span className="relative inline-flex rounded-full h-1.5 w-1.5 bg-[#ff4d6d]" />
                                    </span>
                                    <span className="text-[10px] font-bold text-[#ff4d6d] tracking-widest">
                                      LIVE
                                    </span>
                                  </div>
                                )}
                                <p className="font-bold text-sm leading-snug line-clamp-2">
                                  {session.title}
                                </p>
                                {session.speakers && session.speakers.length > 0 && (
                                  <p className="text-xs text-gray-500 mt-2 line-clamp-1">
                                    {session.speakers.map((sp) => sp.fullName).join(", ")}
                                  </p>
                                )}
                                <p className="text-xs text-gray-500 mt-1">
                                  {new Date(session.startTime).toLocaleTimeString("fr-FR", {
                                    hour: "2-digit",
                                    minute: "2-digit",
                                  })}
                                  {" → "}
                                  {new Date(session.endTime).toLocaleTimeString("fr-FR", {
                                    hour: "2-digit",
                                    minute: "2-digit",
                                  })}
                                </p>
                              </div>
                            </Link>
                          ) : (
                            <div className="rounded-2xl h-full min-h-[80px] border border-white/3 bg-white/2" />
                          )}
                        </div>
                      );
                    })}
                  </div>
                ))}
            </div>
          )}
        </div>
      )}

    </main>
  );
}