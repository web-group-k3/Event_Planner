"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";

import { Session } from "@/types";
import { getSessionById } from "@/services/session.service";
import LiveBadge from "@/components/sessions/LiveBadge";
import { isLive, formatTime, formatDate } from "@/utils/session";

export default function SessionDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();

  const [session, setSession] = useState<Session | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isFavorite, setIsFavorite] = useState(false);

  // ── Chargement ───────────────────────────────────────────
  useEffect(() => {
    getSessionById(id)
      .then(setSession)
      .catch(() => setError("Impossible de charger la session."))
      .finally(() => setLoading(false));
  }, [id]);

  // ── Favoris (localStorage) ───────────────────────────────
  useEffect(() => {
    if (!id) return;
    const favorites: string[] = JSON.parse(
      localStorage.getItem("favorites") ?? "[]"
    );
    setIsFavorite(favorites.includes(id));
  }, [id]);

  const toggleFavorite = () => {
    const favorites: string[] = JSON.parse(
      localStorage.getItem("favorites") ?? "[]"
    );
    const updated = isFavorite
      ? favorites.filter((f) => f !== id)
      : [...favorites, id];
    localStorage.setItem("favorites", JSON.stringify(updated));
    setIsFavorite(!isFavorite);
  };

  // ── Loading ──────────────────────────────────────────────
  if (loading) {
    return (
      <main className="min-h-screen px-6 py-20 max-w-4xl mx-auto">
        <div className="h-8 w-32 bg-white/5 rounded-xl animate-pulse mb-12" />
        <div className="h-16 w-96 bg-white/5 rounded-2xl animate-pulse mb-4" />
        <div className="h-5 w-64 bg-white/5 rounded-xl animate-pulse mb-8" />
        <div className="h-48 bg-white/5 rounded-[32px] animate-pulse" />
      </main>
    );
  }

  // ── Error ────────────────────────────────────────────────
  if (error || !session) {
    return (
      <main className="min-h-screen flex items-center justify-center">
        <div className="glass rounded-[32px] p-12 text-center max-w-md">
          <p className="text-4xl mb-4">⚠️</p>
          <p className="text-gray-400">{error ?? "Session introuvable."}</p>
          <button
            onClick={() => router.back()}
            className="mt-8 px-6 py-3 rounded-2xl bg-lime-400 text-black font-black hover:scale-105 transition"
          >
            Retour
          </button>
        </div>
      </main>
    );
  }

  const live = isLive(session);

  // ── Content ──────────────────────────────────────────────
  return (
    <main className="min-h-screen px-6 py-20 max-w-4xl mx-auto">

      {/* Retour */}
      <button
        onClick={() => router.back()}
        className="inline-flex items-center gap-2 text-gray-400 hover:text-white transition mb-12 text-sm"
      >
        ← Retour
      </button>

      {/* Hero */}
      <div className="glass rounded-[40px] p-10 mb-8 border border-white/5 relative overflow-hidden">

        {/* Glow */}
        <div
          className={`absolute inset-0 pointer-events-none transition-opacity duration-500 ${
            live
              ? "bg-gradient-to-br from-[#ff4d6d]/5 via-transparent to-[#ff4d6d]/5 opacity-100"
              : "bg-gradient-to-br from-lime-400/5 via-transparent to-sky-400/5 opacity-100"
          }`}
        />

        {/* Header */}
        <div className="relative flex items-start justify-between gap-6">
          <div className="flex-1">

            {/* LiveBadge */}
            <div className="relative inline-block mb-6">
              <LiveBadge
                startDate={session.startTime}
                endDate={session.endTime}
              />
            </div>

            <h1 className="text-4xl font-black leading-tight mt-8">
              {session.title}
            </h1>

            {session.description && (
              <p className="text-gray-400 mt-4 text-lg leading-relaxed">
                {session.description}
              </p>
            )}

          </div>

          {/* Bouton favori */}
          <button
            onClick={toggleFavorite}
            title={isFavorite ? "Retirer des favoris" : "Ajouter aux favoris"}
            className={`
              flex-shrink-0 w-14 h-14 rounded-2xl border transition-all duration-300 
              flex items-center justify-center text-xl
              ${isFavorite
                ? "bg-lime-400/20 border-lime-400/40 text-lime-400 hover:bg-lime-400/30"
                : "bg-white/5 border-white/5 text-gray-400 hover:text-white hover:border-white/20"
              }
            `}
          >
            {isFavorite ? "★" : "☆"}
          </button>

        </div>

        {/* Infos */}
        <div className="relative mt-8 flex flex-wrap gap-4">

          <div className="px-4 py-2 rounded-xl bg-white/5 border border-white/5 text-sm text-gray-300">
            🕐 {formatTime(session.startTime)} → {formatTime(session.endTime)}
          </div>

          {session.room && (
            <div className="px-4 py-2 rounded-xl bg-white/5 border border-white/5 text-sm text-gray-300">
              📍 {session.room.name}
            </div>
          )}

          {session.capacity && (
            <div className="px-4 py-2 rounded-xl bg-white/5 border border-white/5 text-sm text-gray-300">
              👥 {session.capacity} places (informatif)
            </div>
          )}

        </div>

      </div>

      {/* Intervenants */}
      {session.speakers && session.speakers.length > 0 && (
        <div className="glass rounded-[32px] p-8 mb-8 border border-white/5">

          <h2 className="text-xl font-black mb-6">
            Intervenants
          </h2>

          <div className="flex flex-col gap-4">
            {session.speakers.map((speaker) => (
              <Link
                key={speaker.id}
                href={`/speakers/${speaker.id}`}
                className="flex items-center gap-4 p-4 rounded-2xl bg-white/5 border border-white/5 hover:border-lime-400/20 hover:-translate-y-1 transition-all duration-300"
              >
                {/* Avatar */}
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-lime-400/20 to-sky-400/20 flex items-center justify-center flex-shrink-0 overflow-hidden">
                  {speaker.profilePhoto ? (
                    <img
                      src={speaker.profilePhoto}
                      alt={speaker.fullName}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <span className="text-lg font-black text-lime-400">
                      {speaker.fullName.charAt(0)}
                    </span>
                  )}
                </div>

                {/* Nom */}
                <div className="flex-1">
                  <p className="font-bold">{speaker.fullName}</p>
                  {speaker.bio && (
                    <p className="text-sm text-gray-500 line-clamp-1 mt-0.5">
                      {speaker.bio}
                    </p>
                  )}
                </div>

                <span className="text-gray-500 text-sm">→</span>

              </Link>
            ))}
          </div>

        </div>
      )}

      {/* Section Q&A — visible uniquement si live */}
      {live ? (
        <div className="glass rounded-[32px] p-8 border border-[#ff4d6d]/20">

          <div className="flex items-center gap-3 mb-6">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#ff4d6d] opacity-75" />
              <span className="relative inline-flex rounded-full h-2 w-2 bg-[#ff4d6d]" />
            </span>
            <h2 className="text-xl font-black text-[#ff4d6d]">
              Questions en direct
            </h2>
          </div>

          {/* 
            ── Zone réservée pour DEV 3 ──
            Le composant QA sera branché ici.
            Il reçoit sessionId en prop.
          */}
          <div className="rounded-2xl border border-dashed border-[#ff4d6d]/20 p-8 text-center text-gray-500 text-sm">
            <p>Zone Q&A — à intégrer par DEV 3</p>
            <p className="mt-1 text-xs text-gray-600">sessionId : {session.id}</p>
          </div>

        </div>
      ) : (
        // ── Session non live : Q&A masquée ──
        <div className="glass rounded-[32px] p-8 border border-white/5 text-center">
          <p className="text-gray-500 text-sm">
            Les questions seront disponibles lorsque la session sera en direct.
          </p>
          <p className="text-gray-600 text-xs mt-2">
            Début : {formatTime(session.startTime)}
          </p>
        </div>
      )}

    </main>
  );
}