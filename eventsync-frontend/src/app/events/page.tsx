"use client";

import { useEffect, useState, useMemo } from "react";
import Image from "next/image";
import { Event } from "@/types";
import { getEvents } from "@/services/event.service";
import EventCard from "@/components/events/EventCard";

//import icon for the filter
import { 
  Calendar, 
  History, 
  Hourglass, 
  Mic, 
  Search, 
  X, 
  AlertTriangle 
} from "lucide-react";

import HeaderBg from "@/assets/conference1.jpg";
type SortOption = "date-asc" | "date-desc" | "duration-asc" | "duration-desc" | "sessions-desc";

export default function EventsPage() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [search, setSearch] = useState("");
  const [sort, setSort] = useState<SortOption>("date-asc");

  useEffect(() => {
    getEvents()
      .then(setEvents)
      .catch(() => setError("We couldn't load the upcoming events. Please try again."))
      .finally(() => setLoading(false));
  }, []);

  // Filtering + Sorting Logic 
  const filtered = useMemo(() => {
    let result = [...events];

    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(
        (e) =>
          e.title.toLowerCase().includes(q) ||
          e.description?.toLowerCase().includes(q)
      );
    }

    result.sort((a, b) => {
      switch (sort) {
        case "date-asc":
          return new Date(a.startDate).getTime() - new Date(b.startDate).getTime();
        case "date-desc":
          return new Date(b.startDate).getTime() - new Date(b.startDate).getTime();
        case "duration-asc": {
          const dA = new Date(a.endDate).getTime() - new Date(a.startDate).getTime();
          const dB = new Date(b.endDate).getTime() - new Date(b.startDate).getTime();
          return dA - dB;
        }
        case "duration-desc": {
          const dA = new Date(a.endDate).getTime() - new Date(a.startDate).getTime();
          const dB = new Date(b.endDate).getTime() - new Date(b.startDate).getTime();
          return dB - dA;
        }
        case "sessions-desc":
          return (b.sessions?.length ?? 0) - (a.sessions?.length ?? 0);
        default:
          return 0;
      }
    });

    return result;
  }, [events, search, sort]);

  //using the icon for the table
  const sortOptions = [
    { value: "date-asc" as SortOption, label: "Earliest", icon: Calendar },
    { value: "date-desc" as SortOption, label: "Latest", icon: History },
    { value: "duration-asc" as SortOption, label: "Shortest", icon: Hourglass },
    { value: "duration-desc" as SortOption, label: "Longest", icon: Hourglass },
    { value: "sessions-desc" as SortOption, label: "Most Sessions", icon: Mic },
  ];

  if (loading) {
    return (
      <main className="min-h-screen max-w-[1600px] mx-auto px-6 py-16 animate-fade-in">
        <div className="space-y-4 mb-12">
          <div className="h-14 w-80 bg-white/5 rounded-2xl animate-pulse" />
          <div className="h-6 w-48 bg-white/5 rounded-xl animate-pulse" />
        </div>
        <div className="h-28 w-full bg-white/5 rounded-[28px] animate-pulse mb-12" />
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6">
          {[...Array(8)].map((_, i) => (
            <div key={i} className="h-[420px] rounded-[32px] bg-white/5 border border-white/5 animate-pulse" />
          ))}
        </div>
      </main>
    );
  }

  if (error) {
    return (
      <main className="min-h-screen flex items-center justify-center p-6">
        <div className="glass rounded-[32px] p-10 text-center max-w-md border-rose-500/20 shadow-2xl shadow-rose-500/5">
          <div className="w-16 h-16 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-full flex items-center justify-center mx-auto mb-6">
            <AlertTriangle className="w-8 h-8" />
          </div>
          <h2 className="text-xl font-bold text-white mb-2">Something went wrong</h2>
          <p className="text-sm text-[var(--muted)] mb-6">{error}</p>
          <button onClick={() => window.location.reload()} className="button-secondary text-sm w-full font-semibold">
            Try Again
          </button>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen max-w-[1600px] mx-auto px-6 pb-20 pt-6">
      
      {/* Hero Banner */}
      <div className="relative rounded-[40px] overflow-hidden mb-12 border border-white/5 bg-gradient-to-b from-white/[0.02] to-transparent shadow-2xl shadow-black/50">
        <div className="absolute inset-0 z-0">
          <Image 
            src={HeaderBg} 
            alt="Events Banner Background" 
            placeholder="blur"
            fill
            className="object-cover object-center opacity-25 mix-blend-luminosity scale-105"
          />
          <div className="absolute top-[-20%] left-[-10%] w-[50%] h-[80%] bg-[var(--primary)]/10 rounded-full blur-[120px]" />
          <div className="absolute inset-0 bg-gradient-to-t from-[#050816] via-[#050816]/70 to-[#050816]/40" />
        </div>

        <div className="relative z-10 px-8 py-20 md:p-16 lg:p-20 flex flex-col lg:flex-row lg:items-center justify-between gap-12">
          <div className="max-w-2xl space-y-6">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/5 border border-white/10 text-xs font-medium text-[var(--secondary)]">
              <span className="w-1.5 h-1.5 rounded-full bg-[var(--secondary)] animate-pulse" />
              Live Platform Active
            </div>
            <h1 className="text-4xl md:text-6xl font-black tracking-tight leading-[1.1] text-white">
              <span className="block animate-slide-up" style={{ animationFillMode: 'forwards' }}>
                Explore World-Class
              </span>
              <span className="block animate-slide-up" style={{ animationDelay: '0.15s', animationFillMode: 'forwards' }}>
                <span className="gradient-text drop-shadow-[0_0_30px_rgba(163,255,18,0.2)]">
                  Live Experiences
                </span>
              </span>
            </h1>
            <p className="text-gray-400 text-base md:text-lg font-medium leading-relaxed animate-slide-up" style={{ animationDelay: '0.3s', animationFillMode: 'forwards' }}>
              Connect, learn, and grow in real-time. Join exclusive interactive keynotes and tech sessions led by industry pioneers.
            </p>
          </div>
          
          <div className="animate-scale-up lg:self-center" style={{ animationDelay: '0.4s', animationFillMode: 'forwards' }}>
            <div className="relative group">
              <div className="absolute inset-0 bg-gradient-to-br from-[var(--primary)] to-[var(--secondary)] opacity-20 blur-xl group-hover:opacity-30 transition-opacity duration-500 rounded-3xl" />
              <div className="relative min-w-[240px] glass backdrop-blur-xl bg-black/40 border border-white/10 p-6 rounded-3xl flex flex-col items-center justify-center text-center shadow-xl">
                <p className="text-xs font-bold uppercase tracking-widest text-[var(--muted)] mb-1">Active Events</p>
                <div className="flex items-baseline gap-1 my-2">
                  <span className="text-6xl font-black tracking-tighter text-white bg-gradient-to-b from-white to-gray-400 bg-clip-text text-transparent">
                    {events.length}
                  </span>
                  <span className="w-3 h-3 rounded-full bg-[var(--primary)] animate-pulse" />
                </div>
                <div className="w-full h-[1px] bg-white/5 my-3" />
                {search ? (
                  <p className="text-xs font-semibold text-[var(--primary)] flex items-center gap-1.5">
                    <Search className="w-3.5 h-3.5" /> {filtered.length} match{filtered.length > 1 ? 'es' : ''} found
                  </p>
                ) : (
                  <p className="text-xs text-gray-400 font-medium flex items-center gap-1.5">
                    <span className="w-2 h-2 rounded-full bg-[var(--primary)] animate-pulse" />
                    Ready to stream
                  </p>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/*  STICKY SEARCH & FILTER BAR */}
      <div className="sticky top-20 z-40 pt-4 pb-6 bg-[#050816]/60 backdrop-blur-xl -mx-4 px-4 transition-all duration-200">
        <div className="glass rounded-[28px] p-5 md:p-6 shadow-2xl shadow-black/40">
          
          <div className="relative flex items-center">
            <Search className="absolute left-4 text-gray-400 w-5 h-5 pointer-events-none" />
            <input
              type="text"
              placeholder="Search by topic, speaker, keyphrase..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-12 pr-12 py-4 rounded-2xl bg-transparent border border-white/10 text-white placeholder-gray-500 focus:outline-none focus:border-[var(--primary)]/40 focus:bg-white/[0.02] transition-all text-sm"
            />
            {search && (
              <button
                onClick={() => setSearch("")}
                className="absolute right-4 w-7 h-7 bg-white/5 hover:bg-white/10 rounded-full flex items-center justify-center text-gray-400 hover:text-white transition-all"
                title="Clear search"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            )}
          </div>

          <div className="space-y-2 mt-4">
            <p className="text-xs font-bold uppercase tracking-wider text-[var(--muted)] px-1">Sort by</p>
            <div className="flex flex-wrap gap-2">
              {sortOptions.map((option) => {
                const isActive = sort === option.value;
                const IconComponent = option.icon; 
                
                return (
                  <button
                    key={option.value}
                    onClick={() => setSort(option.value)}
                    
                    className={`px-4 py-2.5 rounded-xl text-xs font-semibold transition-all duration-200 flex items-center gap-2 cursor-pointer border ${
                      isActive 
                        ? "bg-[var(--primary)]/10 text-[var(--primary)] border-[var(--primary)]/40 shadow-[0_0_15px_rgba(163,255,18,0.12)]" 
                        : "bg-white/5 text-[var(--muted)] hover:bg-white/10 hover:text-white border-white/5"
                    }`}
                  >
                    <IconComponent className="w-4 h-4" />
                    {option.label}
                  </button>
                );
              })}
            </div>
          </div>

        </div>
      </div>

      {search && (
        <div className="flex items-center gap-2 text-sm text-[var(--muted)] mb-8 bg-white/[0.02] border border-white/5 w-fit px-4 py-2 rounded-full">
          <span>Active Filter:</span> <span className="text-white">"{search}"</span> <span className="text-[var(--muted)]">•</span>
          <span className="text-[var(--primary)] font-semibold">{filtered.length} result{filtered.length > 1 ? "s" : ""}</span>
        </div>
      )}

      {/* Grid principal / Empty State */}
      {filtered.length === 0 ? (
        <div className="glass rounded-[32px] p-16 text-center max-w-md mx-auto mt-12 border-dashed border-white/10">
          <div className="w-20 h-20 bg-white/5 rounded-full flex items-center justify-center mx-auto mb-6">
            <Search className="w-8 h-8 text-[var(--muted)]" />
          </div>
          <h3 className="text-lg font-bold text-white mb-2">No results found</h3>
          <p className="text-sm text-[var(--muted)] max-w-xs mx-auto leading-relaxed">We couldn't find any events matching your current search criteria.</p>
          <button onClick={() => { setSearch(""); setSort("date-asc"); }} className="button-primary mt-8 text-xs px-6 py-3 cursor-pointer">
            Reset Filters
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6 transition-all duration-300">
          {filtered.map((event) => (
            <div key={event.id} className="hover:translate-y-[-6px] transition-all duration-300 ease-out">
              <EventCard event={event} />
            </div>
          ))}
        </div>
      )}
    </main>
  );
}