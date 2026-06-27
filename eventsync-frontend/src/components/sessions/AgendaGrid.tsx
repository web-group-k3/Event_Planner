"use client";

import { useState } from "react";
import SessionModal from "./SessionModal";
import { Session, Speaker } from "@/types";

interface AgendaGridProps {
  sessions: Session[];
}

export default function AgendaGrid({ sessions }: AgendaGridProps) {
  const [selectedSessionId, setSelectedSessionId] = useState<string | null>(null);

  return (
    <>
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {sessions.map((session) => (
          <div
            key={session.id}
            onClick={() => setSelectedSessionId(session.id)}
            className="flex flex-col bg-slate-900/60 border border-slate-800 rounded-xl shadow-sm hover:shadow-md hover:border-lime-400/30 transition-all duration-200 overflow-hidden cursor-pointer group"
          >
            <div className="bg-slate-900/90 px-4 py-3 border-b border-slate-800 flex justify-between items-center gap-2">
              <span className="text-xs font-semibold text-indigo-400 uppercase tracking-wider">
                Session
              </span>
              {session.live && (
                <span className="inline-flex items-center gap-1.5 py-0.5 px-2 rounded-full text-xs font-medium bg-red-500/10 text-red-400 animate-pulse shrink-0">
                  <span className="w-1.5 h-1.5 rounded-full bg-red-500"></span>
                  En direct
                </span>
              )}
            </div>
            <div className="p-5 flex-1 flex flex-col justify-between">
              <div>
                <h2 className="text-xl font-bold text-white mb-2 line-clamp-2 group-hover:text-lime-400 transition-colors" title={session.title}>
                  {session.title}
                </h2>
                <p className="text-sm text-slate-400 line-clamp-3 mb-4">
                  {session.description || "Aucune description fournie."}
                </p>
              </div>
              <div className="space-y-2.5 pt-4 border-t border-slate-800/60 text-sm text-slate-400">
                <div className="flex items-center gap-2">
                  <svg className="w-4 h-4 text-lime-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span>
                    {new Date(session.startTime).toLocaleDateString("fr-FR", {
                      weekday: "long",
                      day: "numeric",
                      month: "long",
                    })}{" "}
                    • {new Date(session.startTime).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" })} - {new Date(session.endTime).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" })}
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <svg className="w-4 h-4 text-lime-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <span className="font-medium text-slate-300">
                    {session.room?.name ? (
                      <span title={session.room.adress}>{session.room.name}</span>
                    ) : (
                      <span className="text-slate-500 italic">{session.location || "Salle non définie"}</span>
                    )}
                  </span>
                </div>
              </div>
            </div>
            <div className="bg-slate-900/40 px-5 py-4 border-t border-slate-800/60">
              <span className="text-xs font-semibold text-slate-500 block mb-2 uppercase tracking-wider">
                Speakers
              </span>
              {session.speakers && session.speakers.length > 0 ? (
                <div className="flex flex-wrap gap-2">
                  {session.speakers.map((speaker: Speaker) => (
                    <div
                      key={speaker.id}
                      className="inline-flex items-center gap-1.5 py-1 px-2.5 rounded-lg text-xs font-medium bg-slate-900 border border-slate-800 text-slate-200 shadow-sm"
                    >
                      {(speaker.profilePicture || speaker.photoUrl) && (
                        <img
                          src={speaker.profilePicture || speaker.photoUrl}
                          alt={speaker.fullName}
                          className="w-4 h-4 rounded-full object-cover"
                        />
                      )}
                      <span>{speaker.fullName}</span>
                    </div>
                  ))}
                </div>
              ) : (
                <span className="text-xs text-slate-500 italic">Aucun intervenant</span>
              )}
            </div>
          </div>
        ))}
      </div>
      <SessionModal
        sessionId={selectedSessionId}
        onClose={() => setSelectedSessionId(null)}
        onSpeakerClick={(speakerId) => {
          console.log("Clic speaker ID depuis l'agenda:", speakerId);
        }}
      />
    </>
  );
}