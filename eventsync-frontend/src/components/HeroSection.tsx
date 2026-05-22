"use client";

import Image from "next/image";
import { useEffect, useState } from "react";
import Link from "next/link";

import hero1 from "@/assets/conference1.jpg";
import hero2 from "@/assets/experienceSharing1.jpg";
import hero3 from "@/assets/storytelling1.jpg";

import { Event, Session, Speaker } from "@/types";

import { getEvents } from "@/services/event.service";
import { getLiveSessions } from "@/services/session.service";
import { getSpeakers } from "@/services/speaker.service";

const images = [hero1, hero2, hero3];

function Counter({
  target,
  suffix = "",
}: {
  target: number;
  suffix?: string;
}) {
  const [count, setCount] = useState(0);

  useEffect(() => {
    if (target === 0) {
      setCount(0);
      return;
    }
    
    let start = 0;
    const duration = 2000;
    const increment = target / 100;

    const timer = setInterval(() => {
      start += increment;
      if (start >= target) {
        setCount(target);
        clearInterval(timer);
      } else {
        setCount(Math.floor(start));
      }
    }, duration / 100);

    return () => clearInterval(timer);
  }, [target]);

  return (
    <span>
      {count}
      {suffix}
    </span>
  );
}

export default function HeroSection() {
  const [currentImage, setCurrentImage] = useState(0);

  const [events, setEvents] = useState<Event[]>([]);
  const [allSessions, setAllSessions] = useState<Session[]>([]); 
  const [liveSessions, setLiveSessions] = useState<Session[]>([]); 
  const [speakers, setSpeakers] = useState<Speaker[]>([]);

  useEffect(() => {
    loadData();

    const interval = setInterval(() => {
      setCurrentImage((prev) =>
        prev === images.length - 1 ? 0 : prev + 1
      );
    }, 4000);

    return () => clearInterval(interval);
  }, []);

  const loadData = async () => {
    try {
      const eventsData = await getEvents();
      const speakersData = await getSpeakers();
      const sessionsData = await getLiveSessions(); 
      
      setAllSessions(sessionsData);

      const now = new Date();
      const currentLive = sessionsData.filter((session: Session) => {
        if (!session.startTime || !session.endTime) return false;
        const startTime = new Date(session.startTime);
        const endTime = new Date(session.endTime);
        return now >= startTime && now <= endTime;
      });

      setEvents(eventsData);
      setLiveSessions(currentLive);
      setSpeakers(speakersData);
    } catch (error) {
      console.error("Erreur lors du chargement des données du Hero:", error);
    }
  };

  const formatTime = (dateString: string) => {
    if (!dateString) return "--:--";
    return new Date(dateString).toLocaleTimeString("en-US", {
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const scrollToSchedule = () => {
    const element = document.getElementById("schedule-section");
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
    }
  };

  return (
    <section className="relative overflow-hidden">
      {/* Background Glow */}
      <div className="absolute top-20 left-20 w-72 h-72 bg-lime-400/10 blur-[120px]" />
      <div className="absolute bottom-10 right-10 w-72 h-72 bg-sky-400/10 blur-[120px]" />

      <div className="container-app py-20 lg:py-28">
        {/* TOP */}
        <div className="grid lg:grid-cols-2 gap-16 items-center">
          {/* LEFT */}
          <div>
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full glass mb-8">
              <div className="w-2 h-2 rounded-full bg-[#ff4d6d] animate-pulse" />
              <span className="text-sm text-gray-300">
                {liveSessions.length} {liveSessions.length > 1 ? "Sessions" : "Session"} LIVE
              </span>
            </div>

            <h1 className="text-5xl lg:text-7xl font-black leading-tight">
              Synchronize
              <span className="gradient-text"> Events</span>
              <br />
              In Real Time
            </h1>

            <p className="mt-8 text-lg text-gray-400 leading-relaxed max-w-xl">
              Manage conferences, explore sessions, interact with speakers and participate
              in live Q&A experiences seamlessly.
            </p>

            <div className="mt-10 flex flex-wrap gap-4">
              <button onClick={scrollToSchedule} className="button-primary">Explore Events</button>
              <button onClick={scrollToSchedule} className="button-secondary">
                View Live Sessions
              </button>
            </div>

            {/* Stats */}
            <div className="mt-16 flex flex-wrap gap-10">
              <div>
                <h2 className="text-4xl font-black">
                  <Counter target={events.length} suffix="+" />
                </h2>
                <p className="text-gray-400 mt-1">Events</p>
              </div>

              <div>
                <h2 className="text-4xl font-black">
                  <Counter target={speakers.length} suffix="+" />
                </h2>
                <p className="text-gray-400 mt-1">Speakers</p>
              </div>

              <div>
                <h2 className="text-4xl font-black">
                  <Counter target={liveSessions.length} />
                </h2>
                <p className="text-gray-400 mt-1">LIVE Now</p>
              </div>
            </div>
          </div>

          {/* RIGHT */}
          <div className="relative">
            <div className="relative h-[500px] rounded-[36px] overflow-hidden border border-white/10 shadow-2xl">
              {images.map((img, index) => (
                <Image
                  key={index}
                  src={img}
                  alt="Event"
                  fill
                  priority
                  sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                  className={`object-cover absolute transition-opacity duration-1000 ${
                    currentImage === index ? "opacity-100" : "opacity-0"
                  }`}
                />
              ))}

              <div className="absolute inset-0 bg-black/50" />

              <div className="absolute bottom-6 left-6 right-6">
                <div className="glass rounded-3xl p-6">
                  {liveSessions.length > 0 ? (
                    <Link href={`/rooms/${liveSessions[0].room || "room-alpha"}`} className="block group">
                      <div className="flex items-center justify-between">
                        <div>
                          <h3 className="text-2xl font-black group-hover:text-lime-400 transition">
                            {liveSessions[0].title}
                          </h3>
                          <p className="text-gray-300 mt-1">
                            {liveSessions[0].location || "Main Stage"} 
                          </p>
                        </div>
                        <div className="px-4 py-2 rounded-full bg-[#ff4d6d]/20 text-[#ff4d6d] text-sm font-bold animate-bounce">
                          JOIN LIVE
                        </div>
                      </div>
                      <div className="mt-6">
                        <span className="text-sm text-gray-300 line-clamp-2">
                          {liveSessions[0].description}
                        </span>
                      </div>
                    </Link>
                  ) : (
                    <div>
                      <h3 className="text-2xl font-black">No Live Session</h3>
                      <p className="text-gray-400 mt-2">
                        Waiting for upcoming sessions...
                      </p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* BOTTOM SECTION */}
        <div id="schedule-section" className="mt-24">
          <div className="glass rounded-[36px] p-8 lg:p-10">
            {/* Header */}
            <div className="flex items-center justify-between flex-wrap gap-4 mb-8">
              <div>
                <h2 className="text-3xl font-black">Event Schedule</h2>
                <p className="text-gray-400 mt-2">Multi-track planning experience</p>
              </div>
              <div className="px-4 py-2 rounded-full bg-[#ff4d6d]/20 text-[#ff4d6d] font-semibold text-sm">
                {liveSessions.length} {liveSessions.length > 1 ? "Sessions" : "Session"} LIVE
              </div>
            </div>

            <div className="grid md:grid-cols-3 gap-6">
              {allSessions.length > 0 ? (
                allSessions.slice(0, 3).map((session) => {
                  const now = new Date();
                  const isLive = session.startTime && session.endTime 
                    ? now >= new Date(session.startTime) && now <= new Date(session.endTime)
                    : false;

                  return (
                    <Link
                      href={`/sessions/${session.id}`}
                      key={session.id}
                      className={`block rounded-3xl p-6 hover:scale-[1.02] transition border cursor-pointer ${
                        isLive
                          ? "bg-lime-400/10 border-lime-400/25 shadow-[0_0_15px_rgba(163,230,53,0.05)]"
                          : "bg-white/5 border-white/5"
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span className={`text-sm ${isLive ? "text-lime-300" : "text-sky-300"}`}>
                          {formatTime(session.startTime)}
                        </span>
                        {isLive && (
                          <span className="px-2 py-1 rounded-full bg-[#ff4d6d] text-xs font-bold text-white">
                            LIVE
                          </span>
                        )}
                      </div>
                      <h3 className="text-xl font-black mt-6 truncate">{session.title}</h3>
                      <p className="text-gray-400 mt-2">{session.location || "Room Scheduled"}</p>
                    </Link>
                  );
                })
              ) : (
                <div className="col-span-3 text-center py-8 text-gray-500">
                  No sessions scheduled yet. Add data to your database!
                </div>
              )}
            </div>
          </div>
        </div>

      </div>
    </section>
  );
}