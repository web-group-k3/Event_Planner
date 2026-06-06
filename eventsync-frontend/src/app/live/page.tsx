"use client";

import { useEffect, useState } from "react";
import { Session } from "@/types";
import { getSessions } from "@/services/session.service";
import SessionCard from "@/components/sessions/SessionCard";
import { isLive, sortByStartTime } from "@/utils/session";

export default function LivePage() {
  const [liveSessions, setLiveSessions] = useState<Session[]>([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdate, setLastUpdate] = useState<Date>(new Date());

  useEffect(() => {
    const fetchLive = () => {
      getSessions()
        .then((all) => {
          setLiveSessions(sortByStartTime(all.filter(isLive)));
          setLastUpdate(new Date());
        })
        .finally(() => setLoading(false));
    };

    fetchLive();

    // ✅ Rafraîchissement automatique toutes les 30 secondes
    const interval = setInterval(fetchLive, 30_000);
    return () => clearInterval(interval);
  }, []);

  // ── Loading ──────────────────────────────────────────────
  if (loading) {
    return (
      <main className="min-h-screen px-6 py-20 max-w-7xl mx-auto">
        <div className="h-12 w-48 bg-white/5 rounded-2xl animate-pulse mb-4" />
        <div className="h-5 w-72 bg-white/5 rounded-xl animate-pulse mb-16" />
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="h-64 rounded-[28px] bg-white/5 animate-pulse" />
          ))}
        </div>
      </main>
    );
  }

  // ── Content ──────────────────────────────────────────────
  return (
    <main className="min-h-screen px-6 py-20 max-w-7xl mx-auto">

      {/* Header */}
      <div className="mb-12 flex items-start justify-between flex-wrap gap-4">

        <div>
          {/* Titre avec point pulsant */}
          <div className="flex items-center gap-4 mb-4">
            <span className="relative flex h-4 w-4">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#ff4d6d] opacity-75" />
              <span className="relative inline-flex rounded-full h-4 w-4 bg-[#ff4d6d]" />
            </span>
            <h1 className="text-5xl font-black">En direct</h1>
          </div>

          <p className="text-gray-400 text-lg">
            {liveSessions.length === 0
              ? "Aucune session en cours actuellement."
              : `${liveSessions.length} session${liveSessions.length > 1 ? "s" : ""} en cours`}
          </p>
        </div>

        {/* Dernière mise à jour */}
        <div className="px-4 py-2 rounded-xl bg-white/5 border border-white/5 text-sm text-gray-500 self-start mt-2">
          Mis à jour à{" "}
          {lastUpdate.toLocaleTimeString("fr-FR", {
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit",
          })}
        </div>

      </div>

      {/* Empty state */}
      {liveSessions.length === 0 ? (
        <div className="glass rounded-[32px] p-16 text-center max-w-lg mx-auto mt-12 border border-white/5">
          <div className="text-6xl mb-6">📭</div>
          <h2 className="text-2xl font-black mb-3">Aucune session live</h2>
          <p className="text-gray-400 leading-relaxed">
            Il n'y a pas de session en cours pour le moment. Revenez pendant un événement pour interagir en direct.
          </p>
          <p className="text-gray-600 text-sm mt-4">
            Rafraîchissement automatique toutes les 30 secondes.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {liveSessions.map((session) => (
            <SessionCard key={session.id} session={session} />
          ))}
        </div>
      )}

    </main>
  );
}