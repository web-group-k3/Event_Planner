"use client";

import { useState, useMemo } from "react";
import EventCard from "@/components/events/EventCard";
import { Event } from "@/types";
import { FaSearch } from "react-icons/fa";

type Status = "all" | "live" | "upcoming" | "past";

interface Props {
  events: Event[];
}

function getStatus(event: Event): "live" | "upcoming" | "past" {
  const now = new Date();
  const start = new Date(event.startDate);
  const end = new Date(event.endDate);
  if (now >= start && now <= end) return "live";
  if (now < start) return "upcoming";
  return "past";
}

export default function EventsFilter({ events }: Props) {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState<Status>("all");

  const filtered = useMemo(() => {
    return events.filter((event) => {
      const matchesQuery =
        query.trim() === "" ||
        event.title.toLowerCase().includes(query.toLowerCase()) ||
        event.description?.toLowerCase().includes(query.toLowerCase());

      const matchesStatus =
        status === "all" || getStatus(event) === status;

      return matchesQuery && matchesStatus;
    });
  }, [events, query, status]);

  const filters: { label: string; value: Status }[] = [
    { label: "All", value: "all" },
    { label: "🔴 Live", value: "live" },
    { label: "Upcoming", value: "upcoming" },
    { label: "Past", value: "past" },
  ];

  return (
    <div>
      <div className="flex flex-col sm:flex-row gap-4 mb-10">
        <div className="relative flex-1">
          <FaSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 text-sm" />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search events..."
            className="
              w-full pl-10 pr-4 py-3
              bg-white/5 border border-white/10
              rounded-2xl text-white placeholder-gray-500
              focus:outline-none focus:border-lime-400/50
              transition
            "
          />
        </div>
        <div className="flex gap-2">
          {filters.map((f) => (
            <button
              key={f.value}
              onClick={() => setStatus(f.value)}
              className={`
                px-4 py-3 rounded-2xl text-sm font-semibold transition
                ${status === f.value
                  ? "bg-lime-400 text-black"
                  : "bg-white/5 border border-white/10 text-gray-400 hover:border-lime-400/30 hover:text-white"
                }
              `}
            >
              {f.label}
            </button>
          ))}
        </div>
      </div>
      {filtered.length === 0 ? (
        <div className="glass rounded-[32px] p-16 text-center border border-white/5">
          <h2 className="text-2xl font-black">No results found</h2>
          <p className="text-gray-400 mt-4">Try a different keyword or filter.</p>
        </div>
      ) : (
        <div className="grid md:grid-cols-2 xl:grid-cols-3 gap-8">
          {filtered.map((event) => (
            <EventCard key={event.id} event={event} />
          ))}
        </div>
      )}
    </div>
  );
}