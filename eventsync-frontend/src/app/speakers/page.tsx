"use client";

import { useEffect, useState } from "react";
import { Speaker } from "@/types";
import { getSpeakers } from "@/services/speaker.service";
import SpeakerCard from "@/components/speakers/SpeakerCard";
import SpeakerModal from "@/components/speakers/SpeakerModal";
import { Search, Mic, SlidersHorizontal, X } from "lucide-react";

export default function SpeakersPage() {

  const [speakers, setSpeakers] = useState<Speaker[]>([]);
  const [filtered, setFiltered] = useState<Speaker[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [selectedId, setSelectedId] = useState<string | null>(null);

  useEffect(() => {
    getSpeakers()
      .then((data) => {
        setSpeakers(data);
        setFiltered(data);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    const q = search.toLowerCase().trim();
    if (!q) {
      setFiltered(speakers);
      return;
    }
    setFiltered(
      speakers.filter(
        (s) =>
          s.fullName.toLowerCase().includes(q) ||
          s.bio?.toLowerCase().includes(q)
      )
    );
  }, [search, speakers]);

  return (
    <div className="relative min-h-screen">
      <div className="fixed top-10 left-0 w-96 h-96 bg-lime-400/6 blur-[140px] pointer-events-none" />
      <div className="fixed bottom-0 right-0 w-96 h-96 bg-sky-400/6 blur-[140px] pointer-events-none" />

      <div className="container-app py-16">
        <div className="mb-14">

          

          <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">

            <div>
              <h1 className="text-5xl lg:text-6xl font-black leading-tight">
                Meet the
                <span className="gradient-text"> Speakers</span>
              </h1>
              <p className="text-gray-400 mt-4 text-lg max-w-xl leading-relaxed">
                Experts, visionaries and practitioners sharing their knowledge
                across sessions and live talks.
              </p>
            </div>

            {!loading && (
              <div className="flex items-center gap-2 px-5 py-3 rounded-2xl glass border border-white/10">
                <span className="text-3xl font-black text-[#a3ff12]">{speakers.length}</span>
                <span className="text-sm text-gray-400">speakers</span>
              </div>
            )}

          </div>

        </div>
        <div className="relative mb-10 max-w-xl">

          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500" />

          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by name or keyword..."
            className="w-full pl-11 pr-11 py-3.5 rounded-2xl glass border border-white/10
                       text-sm text-white placeholder-gray-500 outline-none
                       focus:border-[#a3ff12]/40 focus:shadow-[0_0_20px_rgba(163,255,18,0.08)]
                       transition-all duration-200"
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

        {loading && (
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
            {Array.from({ length: 8 }).map((_, i) => (
              <div
                key={i}
                className="glass rounded-3xl p-6 border border-white/10 animate-pulse"
              >
                <div className="w-20 h-20 rounded-2xl bg-white/5 mb-5" />
                <div className="h-4 bg-white/5 rounded-lg w-3/4 mb-3" />
                <div className="h-3 bg-white/5 rounded-lg w-full mb-2" />
                <div className="h-3 bg-white/5 rounded-lg w-2/3" />
              </div>
            ))}
          </div>
        )}
        {!loading && filtered.length > 0 && (
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
            {filtered.map((speaker) => (
              <SpeakerCard
                key={speaker.id}
                speaker={speaker}
                onClick={(s) => setSelectedId(s.id)}
              />
            ))}
          </div>
        )}
        {!loading && filtered.length === 0 && (
          <div className="flex flex-col items-center justify-center py-32 text-center">
            <div className="w-20 h-20 rounded-3xl glass border border-white/10 flex items-center justify-center mb-6">
              <SlidersHorizontal className="w-8 h-8 text-gray-600" />
            </div>
            <h3 className="text-xl font-black text-gray-400 mb-2">No speakers found</h3>
            <p className="text-gray-600 text-sm">Try adjusting your search query.</p>
            {search && (
              <button
                onClick={() => setSearch("")}
                className="mt-6 button-secondary text-sm"
              >
                Clear search
              </button>
            )}
          </div>
        )}

      </div>
      <SpeakerModal
        speakerId={selectedId}
        onClose={() => setSelectedId(null)}
      />

    </div>
  );
}