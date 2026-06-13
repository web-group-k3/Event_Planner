"use client";

import { useEffect, useState, useCallback } from "react";
import { createPortal } from "react-dom";
import { Session, Question } from "@/types";
import { getSessionById } from "@/services/session.service";
import {
  X, Clock, Users, Mic, Wifi,
  MessageSquare, ThumbsUp, Building2
} from "lucide-react";
import { format } from "date-fns";
import { useQuestions } from "@/hooks/useQuestions"; // Gardé si tu prévois de l'utiliser plus tard
import { parseLinks } from "@/lib/links";

interface SessionModalProps {
  sessionId: string | null;
  onClose: () => void;
  zIndex?: number;
  onSpeakerClick?: (id: string) => void;
}

function QuestionRow({ q }: { q: Question }) {
  return (
    <div className="flex gap-4 p-4 glass rounded-2xl border border-white/10 hover:border-white/20 transition-all duration-200">
      <div className="flex flex-col items-center gap-1 shrink-0">
        <ThumbsUp className="w-3.5 h-3.5 text-[#a3ff12]" />
        <span className="text-xs font-black text-[#a3ff12]">{q.upvotes}</span>
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm text-white leading-relaxed">{q.content}</p>
        <p className="text-xs text-gray-600 mt-1.5">{q.authorName || "Anonymous"}</p>
      </div>
    </div>
  );
}

export default function SessionModal({
  sessionId,
  onClose,
  zIndex = 200,
  onSpeakerClick,
}: SessionModalProps) {
  // Correction ici : On intercepte le type Session pour lui ajouter localement la propriété questions
  const [session, setSession] = useState<(Session & { questions?: Question[] }) | null>(null);
  const [loading, setLoading]   = useState(false);
  const [visible, setVisible]   = useState(false);
  const [mounted, setMounted]   = useState(false);

  useEffect(() => { setMounted(true); }, []);

  const handleClose = useCallback(() => {
    setVisible(false);
    setTimeout(onClose, 300);
  }, [onClose]);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => { if (e.key === "Escape") handleClose(); };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [handleClose]);

  useEffect(() => {
    if (!sessionId) { setVisible(false); setSession(null); return; }
    setLoading(true);
    setSession(null);
    setVisible(false);

    getSessionById(sessionId)
      .then((data) => {
        // Le cast ici permet d'adapter la réponse de l'API à notre state étendu
        setSession(data as Session & { questions?: Question[] });
        requestAnimationFrame(() => setVisible(true));
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [sessionId]);

  if (!sessionId || !mounted) return null;

  const start     = session?.startTime ? format(new Date(session.startTime), "HH:mm") : "--:--";
  const end       = session?.endTime   ? format(new Date(session.endTime),   "HH:mm") : "--:--";
  const date      = session?.startTime ? format(new Date(session.startTime), "EEEE d MMMM yyyy") : "";
  
  // Plus d'erreur TypeScript ici car session sait désormais qu'il peut contenir 'questions'
  const questions = (session?.questions ?? []).sort((a, b) => b.upvotes - a.upvotes);

  const modal = (
    <div
      className={`fixed inset-0 flex items-center justify-center transition-all duration-300 ${visible ? "opacity-100" : "opacity-0"}`}
      style={{ zIndex }}
    >
      {/* Backdrop */}
      <div className="absolute inset-0 bg-black/75 backdrop-blur-md" onClick={handleClose} />

      {/* Panel */}
      <div
        className={`
          relative w-full h-full md:h-auto md:max-h-[92vh]
          md:w-[90vw] md:max-w-4xl
          bg-[#050816] md:rounded-[40px] border border-white/10
          overflow-hidden flex flex-col
          transition-all duration-300
          ${visible ? "scale-100 translate-y-0" : "scale-95 translate-y-8"}
        `}
        style={{
          background: "radial-gradient(circle at top left, rgba(163,255,18,0.05), transparent 50%), radial-gradient(circle at bottom right, rgba(56,189,248,0.05), transparent 50%), #050816"
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
              <p className="text-gray-400 text-sm">Loading session...</p>
            </div>
          </div>
        )}

        {!loading && session && (
          <div className="flex-1 overflow-y-auto">

            {/* Header */}
            <div className="relative px-8 pt-10 pb-8 border-b border-white/10">
              <div className="absolute top-0 left-0 w-96 h-64 bg-[#a3ff12]/5 blur-[120px] pointer-events-none" />

              {session.live && (
                <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-[#ff4d6d]/20 border border-[#ff4d6d]/30 mb-5">
                  <Wifi className="w-3.5 h-3.5 text-[#ff4d6d] animate-pulse" />
                  <span className="text-sm font-black text-[#ff4d6d]">LIVE NOW</span>
                </div>
              )}

              <h2 className="text-2xl md:text-3xl font-black text-white leading-tight max-w-2xl">
                {session.title}
              </h2>

              {session.description && (
                <p className="text-gray-400 mt-3 leading-relaxed max-w-2xl text-sm">
                  {session.description}
                </p>
              )}

              {/* Meta */}
              <div className="mt-6 flex flex-wrap gap-5">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-xl bg-[#38bdf8]/10 flex items-center justify-center">
                    <Clock className="w-4 h-4 text-[#38bdf8]" />
                  </div>
                  <div>
                    <p className="text-sm font-bold text-white">{start} – {end}</p>
                    <p className="text-xs text-gray-500">{date}</p>
                  </div>
                </div>

                {session.room?.name && (
                  <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-xl bg-[#a3ff12]/10 flex items-center justify-center">
                      <Building2 className="w-4 h-4 text-[#a3ff12]" />
                    </div>
                    <div>
                      <p className="text-sm font-bold text-white">{session.room.name}</p>
                      <p className="text-xs text-gray-500">Room</p>
                    </div>
                  </div>
                )}

                {session.guestNumber != null && (
                  <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-xl bg-white/5 flex items-center justify-center">
                      <Users className="w-4 h-4 text-gray-400" />
                    </div>
                    <div>
                      <p className="text-sm font-bold text-white">{session.guestNumber}</p>
                      <p className="text-xs text-gray-500">Capacity</p>
                    </div>
                  </div>
                )}

                {(session.speakers?.length ?? 0) > 0 && (
                  <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-xl bg-white/5 flex items-center justify-center">
                      <Mic className="w-4 h-4 text-gray-400" />
                    </div>
                    <div>
                      <p className="text-sm font-bold text-white">{session.speakers!.length}</p>
                      <p className="text-xs text-gray-500">Speaker{session.speakers!.length > 1 ? "s" : ""}</p>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Body */}
            <div className="px-8 py-8 grid md:grid-cols-2 gap-8">

              {/* Speakers */}
              <div>
                <h3 className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-5">Speakers</h3>
                {(session.speakers?.length ?? 0) > 0 ? (
                  <div className="flex flex-col gap-3">
                    {session.speakers!.map((spk) => {
                      const links = parseLinks((spk.links as unknown as string) ?? null);
                      const initials = spk.fullName ? spk.fullName.split(" ").map(n => n[0]).join("").toUpperCase().slice(0, 2) : "??";
                      return (
                        <button
                          key={spk.id}
                          onClick={() => onSpeakerClick?.(spk.id)}
                          disabled={!onSpeakerClick}
                          className="w-full flex items-center gap-4 p-4 glass rounded-2xl border border-white/10
                                     hover:border-[#a3ff12]/40 transition-all duration-200 text-left group
                                     disabled:cursor-default"
                        >
                          <div className="w-12 h-12 rounded-xl overflow-hidden bg-gradient-to-br from-[#a3ff12]/20 to-[#38bdf8]/20 border border-white/10 flex items-center justify-center shrink-0">
                            {spk.photoUrl ? (
                              <img src={spk.photoUrl} alt={spk.fullName} className="w-full h-full object-cover" />
                            ) : (
                              <span className="text-sm font-black text-[#a3ff12]">{initials}</span>
                            )}
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className="font-bold text-white group-hover:text-[#a3ff12] transition-colors text-sm leading-tight">
                              {spk.fullName}
                            </p>
                            {links.length > 0 && (
                              <div className="flex gap-2 mt-1">
                                {links.slice(0, 3).map((l) => (
                                  <span key={l.platform} className="text-xs" style={{ color: l.color }}>
                                    {l.label}
                                  </span>
                                ))}
                              </div>
                            )}
                          </div>
                          {onSpeakerClick && (
                            <Mic className="w-4 h-4 text-gray-600 group-hover:text-[#a3ff12] transition-colors shrink-0" />
                          )}
                        </button>
                      );
                    })}
                  </div>
                ) : (
                  <div className="glass rounded-2xl p-6 border border-white/10 text-center">
                    <Mic className="w-6 h-6 text-gray-700 mx-auto mb-2" />
                    <p className="text-gray-600 text-sm">No speakers assigned.</p>
                  </div>
                )}
              </div>

              {/* Q&A */}
              <div>
                <div className="flex items-center justify-between mb-5">
                  <h3 className="text-xs font-bold uppercase tracking-widest text-gray-500">Q&A</h3>
                  {questions.length > 0 && (
                    <span className="px-2.5 py-0.5 rounded-full bg-[#a3ff12]/10 text-[#a3ff12] text-xs font-bold">
                      {questions.length}
                    </span>
                  )}
                </div>
                {questions.length > 0 ? (
                  <div className="flex flex-col gap-2.5 max-h-72 overflow-y-auto pr-1">
                    {questions.map((q: Question) => <QuestionRow key={q.id} q={q} />)}
                  </div>
                ) : (
                  <div className="glass rounded-2xl p-6 border border-white/10 text-center">
                    <MessageSquare className="w-6 h-6 text-gray-700 mx-auto mb-2" />
                    <p className="text-gray-600 text-sm">No questions yet.</p>
                  </div>
                )}
              </div>

            </div>
          </div>
        )}
      </div>
    </div>
  );

  return createPortal(modal, document.body);
}