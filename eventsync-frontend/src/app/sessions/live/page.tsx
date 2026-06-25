"use client";

import { useEffect, useState } from "react";
import { getSessions } from "@/services/session.service";
import { Session } from "@/types";
import SessionCard from "@/components/sessions/SessionCard";

export default function LiveSessionsPage() {
  const [liveSessions, setLiveSessions] = useState<Session[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchLive = async () => {
    try {
      const sessions = await getSessions();
      const now = new Date();
      setLiveSessions(
        sessions.filter((s) => {
          return now >= new Date(s.startTime) && now <= new Date(s.endTime);
        })
      );
    } catch (err) {
      console.error("Failed to fetch sessions", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLive();
    const interval = setInterval(fetchLive, 30_000); // refresh toutes les 30s
    return () => clearInterval(interval);
  }, []);

  return (
    <main className="container-app py-20">
      <div className="mb-16">
        <span className="text-[#ff4d6d] font-semibold tracking-widest">
          LIVE NOW
        </span>
        <h1 className="text-5xl lg:text-7xl font-black mt-4">Live Sessions</h1>
        <p className="text-gray-400 text-lg mt-6 max-w-3xl leading-relaxed">
          Join ongoing sessions happening right now across all conference rooms.
        </p>
      </div>

      {loading && (
        <div className="glass rounded-[32px] p-16 border border-white/5 text-center">
          <p className="text-gray-400 animate-pulse">Loading live sessions...</p>
        </div>
      )}

      {!loading && liveSessions.length === 0 && (
        <div className="glass rounded-[32px] p-16 border border-white/5 text-center">
          <h2 className="text-3xl font-black">No Live Sessions</h2>
          <p className="text-gray-400 mt-6">
            There are currently no sessions running at the moment.
          </p>
        </div>
      )}

      {!loading && liveSessions.length > 0 && (
        <div className="grid lg:grid-cols-2 gap-8">
          {liveSessions.map((session) => (
            <SessionCard key={session.id} session={session} />
          ))}
        </div>
      )}
    </main>
  );
}