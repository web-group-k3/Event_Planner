"use client";

import { useEffect, useState, useCallback } from "react";
import { createPortal } from "react-dom";
import { Speaker, Session } from "@/types";
import { getSpeakerById } from "@/services/speaker.service";
import { X, Mic, Clock, MapPin, Users, Calendar, Wifi } from "lucide-react";
import { format } from "date-fns";
import { parseLinks } from "@/lib/links";
import SessionModal from "@/components/SessionModal";

interface SpeakerModalProps {
  speakerId: string | null;
  onClose: () => void;
  /** z-index — increase when nested */
  zIndex?: number;
}

function SessionBadge({
  session,
  onClick,
}: {
  session: Session;
  onClick: (id: string) => void;
}) {
  const start = session.startTime ? format(new Date(session.startTime), "HH:mm") : "--:--";
  const end   = session.endTime   ? format(new Date(session.endTime),   "HH:mm") : "--:--";

  return (
    <button
      onClick={() => onClick(session.id)}
      className={`
        w-full text-left relative glass rounded-2xl p-5 border transition-all duration-300 hover:-translate-y-0.5
        ${session.live
          ? "border-[#ff4d6d]/40 hover:border-[#ff4d6d]/70 hover:shadow-[0_0_20px_rgba(255,77,109,0.1)]"
          : "border-white/10 hover:border-[#a3ff12]/40 hover:shadow-[0_0_20px_rgba(163,255,18,0.06)]"
        }
      `}
    >
      {session.live && (
        <div className="absolute top-4 right-4 flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#ff4d6d]/20 border border-[#ff4d6d]/30">
          <Wifi className="w-3 h-3 text-[#ff4d6d] animate-pulse" />
          <span className="text-xs font-bold text-[#ff4d6d]">LIVE</span>
        </div>
      )}
      <h4 className="font-bold text-white pr-16 leading-tight">{session.title}</h4>
      {session.description && (
        <p className="text-sm text-gray-400 mt-2 line-clamp-2 leading-relaxed">{session.description}</p>
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
            <Users className="w-3.5 h-3.5 text-gray-500" />
            <span>{session.guestNumber} guests</span>
          </div>
        )}
      </div>
      <p className="mt-3 text-xs text-gray-600">Click to see details →</p>
    </button>
  );
}

export default function SpeakerModal({ speakerId, onClose, zIndex = 100 }: SpeakerModalProps) {
  const [speaker, setSpeaker]     = useState<Speaker | null>(null);
  const [loading, setLoading]     = useState(false);
  const [visible, setVisible]     = useState(false);
  const [mounted, setMounted]     = useState(false);
  const [selectedSessionId, setSelectedSessionId] = useState<string | null>(null);

  useEffect(() => { setMounted(true); }, []);

  const handleClose = useCallback(() => {
    setVisible(false);
    setTimeout(onClose, 300);
  }, [onClose]);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape" && !selectedSessionId) handleClose();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [handleClose, selectedSessionId]);

  useEffect(() => {
    if (!speakerId) { setVisible(false); setSpeaker(null); return; }
    setLoading(true);
    setSpeaker(null);
    setVisible(false);

    getSpeakerById(speakerId)
      .then((data) => {
        setSpeaker(data);
        requestAnimationFrame(() => setVisible(true));
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [speakerId]);

  if (!speakerId || !mounted) return null;

  const initials    = speaker?.fullName.split(" ").map(n => n[0]).join("").toUpperCase().slice(0, 2) ?? "??";
  const links       = parseLinks(speaker?.links);
  const liveSessions  = speaker?.sessions?.filter(s => s.live)  ?? [];
  const otherSessions = speaker?.sessions?.filter(s => !s.live) ?? [];

  // SessionModal opens on top of this — use zIndex + 100
  const sessionZIndex = zIndex + 100;

  const modal = (
    <div
      className={`fixed inset-0 flex items-center justify-center transition-all duration-300 ${visible ? "opacity-100" : "opacity-0"}`}
      style={{ zIndex }}
    >
      {/* Backdrop */}
      <div className="absolute inset-0 bg-black/70 backdrop-blur-md" onClick={handleClose} />

      {/* Panel */}
      <div
        className={`
          relative w-full h-full md:h-auto md:max-h-[92vh]
          md:w-[90vw] md:max-w-5xl
          bg-[#050816] md:rounded-[40px]
          border border-white/10 overflow-hidden flex flex-col
          transition-all duration-300
          ${visible ? "scale-100 translate-y-0" : "scale-95 translate-y-8"}
        `}
        style={{
          background: "radial-gradient(circle at top right, rgba(56,189,248,0.06), transparent 50%), radial-gradient(circle at bottom left, rgba(163,255,18,0.06), transparent 50%), #050816"
        }}
      >
        {/* Close */}
        <button
          onClick={handleClose}
          className="absolute top-6 right-6 z-10 w-10 h-10 rounded-full glass border border-white/10
                     flex items-center justify-center hover:border-[#ff4d6d]/50 hover:text-[#ff4d6d]
                     transition-all duration-200 group"
        >
          <X className="w-5 h-5 group-hover:rotate-90 transition-transform duration-200" />
        </button>

        {/* Loading */}
        {loading && (
          <div className="flex-1 flex items-center justify-center min-h-[400px]">
            <div className="flex flex-col items-center gap-4">
              <div className="w-12 h-12 rounded-full border-2 border-[#a3ff12]/30 border-t-[#a3ff12] animate-spin" />
              <p className="text-gray-400 text-sm">Loading speaker...</p>
            </div>
          </div>
        )}

        {!loading && speaker && (
          <div className="flex-1 overflow-y-auto">

            {/* Hero */}
            <div className="relative px-8 pt-10 pb-8 border-b border-white/08">
              <div className="absolute top-0 right-0 w-80 h-80 bg-[#38bdf8]/05 blur-[100px] pointer-events-none" />
              <div className="flex flex-col md:flex-row gap-8 items-start md:items-center">

                {/* Avatar */}
                <div className="relative shrink-0">
                  <div className="w-28 h-28 rounded-3xl overflow-hidden bg-linear-to-br from-[#a3ff12]/20 to-[#38bdf8]/20 border border-white/10 flex items-center justify-center shadow-2xl">
                    {speaker.photoUrl ? (
                      <img src={speaker.photoUrl} alt={speaker.fullName} className="w-full h-full object-cover" />
                    ) : (
                      <span className="text-4xl font-black text-[#a3ff12]">{initials}</span>
                    )}
                  </div>
                  {liveSessions.length > 0 && (
                    <div className="absolute -bottom-2 -right-2 flex items-center gap-1 px-2.5 py-1 rounded-full bg-[#ff4d6d] shadow-lg">
                      <div className="w-1.5 h-1.5 rounded-full bg-white animate-pulse" />
                      <span className="text-xs font-black text-white">LIVE</span>
                    </div>
                  )}
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <h2 className="text-3xl md:text-4xl font-black text-white leading-tight">{speaker.fullName}</h2>

                  {links.length > 0 && (
                    <div className="mt-4 flex flex-wrap gap-2">
                      {links.map((l) => (
                        <a
                          key={l.platform}
                          href={l.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          onClick={(e) => e.stopPropagation()}
                          className="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl glass border border-white/10
                                     text-sm hover:border-white/20 transition-all duration-200"
                          style={{ color: l.color }}
                        >
                          {l.icon}
                          <span className="text-gray-300">{l.label}</span>
                        </a>
                      ))}
                    </div>
                  )}

                  {/* Stats */}
                  <div className="mt-5 flex flex-wrap gap-6">
                    <div className="flex items-center gap-2 text-sm">
                      <div className="w-8 h-8 rounded-xl bg-[#a3ff12]/10 flex items-center justify-center">
                        <Mic className="w-4 h-4 text-[#a3ff12]" />
                      </div>
                      <div>
                        <p className="font-bold text-white">{speaker.sessions?.length ?? 0}</p>
                        <p className="text-xs text-gray-500">Sessions</p>
                      </div>
                    </div>
                    {liveSessions.length > 0 && (
                      <div className="flex items-center gap-2 text-sm">
                        <div className="w-8 h-8 rounded-xl bg-[#ff4d6d]/10 flex items-center justify-center">
                          <Wifi className="w-4 h-4 text-[#ff4d6d]" />
                        </div>
                        <div>
                          <p className="font-bold text-white">{liveSessions.length}</p>
                          <p className="text-xs text-gray-500">Live now</p>
                        </div>
                      </div>
                    )}
                    <div className="flex items-center gap-2 text-sm">
                      <div className="w-8 h-8 rounded-xl bg-[#38bdf8]/10 flex items-center justify-center">
                        <Calendar className="w-4 h-4 text-[#38bdf8]" />
                      </div>
                      <div>
                        <p className="font-bold text-white">{otherSessions.length}</p>
                        <p className="text-xs text-gray-500">Upcoming</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Body */}
            <div className="px-8 py-8 grid md:grid-cols-5 gap-8">
              {/* Bio */}
              <div className="md:col-span-2">
                <h3 className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-4">About</h3>
                <p className="text-gray-300 leading-relaxed text-sm">
                  {speaker.bio || "No biography available for this speaker."}
                </p>
              </div>

              {/* Sessions */}
              <div className="md:col-span-3">
                {liveSessions.length > 0 && (
                  <div className="mb-8">
                    <div className="flex items-center gap-2 mb-4">
                      <div className="w-2 h-2 rounded-full bg-[#ff4d6d] animate-pulse" />
                      <h3 className="text-xs font-bold uppercase tracking-widest text-[#ff4d6d]">Live Now</h3>
                    </div>
                    <div className="flex flex-col gap-3">
                      {liveSessions.map((s) => (
                        <SessionBadge key={s.id} session={s} onClick={setSelectedSessionId} />
                      ))}
                    </div>
                  </div>
                )}

                {otherSessions.length > 0 ? (
                  <div>
                    <h3 className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-4">All Sessions</h3>
                    <div className="flex flex-col gap-3">
                      {otherSessions.map((s) => (
                        <SessionBadge key={s.id} session={s} onClick={setSelectedSessionId} />
                      ))}
                    </div>
                  </div>
                ) : (
                  <div className="glass rounded-2xl p-6 border border-white/10 text-center">
                    <Mic className="w-8 h-8 text-gray-600 mx-auto mb-3" />
                    <p className="text-gray-500 text-sm">No sessions scheduled yet.</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>

      {/* SessionModal via portal — above this modal */}
      <SessionModal
        sessionId={selectedSessionId}
        onClose={() => setSelectedSessionId(null)}
        zIndex={sessionZIndex}
        onSpeakerClick={(id) => {
          // Close session, open new speaker — for now just close session
          setSelectedSessionId(null);
        }}
      />
    </div>
  );

  return createPortal(modal, document.body);
}
