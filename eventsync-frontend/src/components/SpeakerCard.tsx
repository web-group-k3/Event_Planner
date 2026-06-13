"use client";

import { Speaker } from "@/types";
import { Mic, ChevronRight } from "lucide-react";
import { parseLinks } from "@/lib/links";

interface SpeakerCardProps {
  speaker: Speaker;
  onClick: (speaker: Speaker) => void;
}

export default function SpeakerCard({ speaker, onClick }: SpeakerCardProps) {
  const initials = speaker.fullName
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);

  const sessionCount = speaker.sessionCount ?? 0;
  
  // Correction de sécurité pour le typage des liens
  const links = parseLinks(speaker.links as unknown as string);

  return (
    <div
      onClick={() => onClick(speaker)}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          onClick(speaker);
        }
      }}
      className="group w-full text-left glass rounded-3xl p-6 border border-white/10
                 hover:border-[#a3ff12]/30 transition-all duration-300 cursor-pointer select-none
                 hover:-translate-y-1 hover:shadow-[0_0_40px_rgba(163,255,18,0.08)] focus:outline-none focus:border-[#a3ff12]/50"
    >
      {/* Avatar + social icons */}
      <div className="relative mb-5 flex items-start justify-between">
        {/* Correction bg-linear en bg-gradient */}
        <div className="w-20 h-20 rounded-2xl overflow-hidden bg-gradient-to-br from-[#a3ff12]/20 to-[#38bdf8]/20 border border-white/10 flex items-center justify-center shrink-0">
          {speaker.photoUrl ? (
            <img
              src={speaker.photoUrl}
              alt={speaker.fullName}
              className="w-full h-full object-cover"
            />
          ) : (
            <span className="text-2xl font-black text-[#a3ff12]">{initials}</span>
          )}
        </div>

        {/* Social icons top-right */}
        {links.length > 0 && (
          <div className="flex gap-1.5 pt-1">
            {links.slice(0, 3).map((l) => (
              <a
                key={l.platform}
                href={l.url}
                target="_blank"
                rel="noopener noreferrer"
                onClick={(e) => e.stopPropagation()} // Évite de déclencher le onClick de la carte entière
                title={l.label}
                className="w-7 h-7 rounded-lg glass border border-white/10 flex items-center justify-center
                           text-gray-500 hover:border-white/20 transition-all duration-200"
                style={{ color: l.color }}
              >
                {l.icon}
              </a>
            ))}
          </div>
        )}
      </div>

      {/* Name & bio */}
      <h3 className="text-lg font-black text-white group-hover:text-[#a3ff12] transition-colors duration-200 leading-tight">
        {speaker.fullName}
      </h3>

      <p className="text-sm text-gray-400 mt-2 line-clamp-2 leading-relaxed">
        {speaker.bio || "No bio available."}
      </p>

      {/* Footer */}
      <div className="mt-5 flex items-center justify-between">
        <div className="flex items-center gap-2 text-xs text-gray-500">
          <Mic className="w-3.5 h-3.5 text-[#38bdf8]" />
          <span>{sessionCount} session{sessionCount !== 1 ? "s" : ""}</span>
        </div>

        <ChevronRight
          className="w-4 h-4 text-gray-600 group-hover:text-[#a3ff12]
                     group-hover:translate-x-1 transition-all duration-200"
        />
      </div>
    </div>
  );
}