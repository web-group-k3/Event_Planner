"use client";

import Image from "next/image";
import { useEffect, useState } from "react";

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
      const liveData = await getLiveSessions();
      const speakersData = await getSpeakers();

      setEvents(eventsData);
      setLiveSessions(liveData);
      setSpeakers(speakersData);

    } catch (error) {

      console.error(error);

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

                {liveSessions.length} Sessions LIVE

              </span>

            </div>

            <h1 className="text-5xl lg:text-7xl font-black leading-tight">

              Synchronize

              <span className="gradient-text">
                {" "}Events
              </span>

              <br />

              In Real Time

            </h1>

            <p className="mt-8 text-lg text-gray-400 leading-relaxed max-w-xl">

              Manage conferences, explore sessions,
              interact with speakers and participate
              in live Q&A experiences seamlessly.

            </p>

            {/* Buttons */}

            <div className="mt-10 flex flex-wrap gap-4">

              <button className="button-primary">
                Explore Events
              </button>

              <button className="button-secondary">
                View Live Sessions
              </button>

            </div>

            {/* Stats */}

            <div className="mt-16 flex flex-wrap gap-10">

              <div>

                <h2 className="text-4xl font-black">

                  <Counter
                    target={events.length}
                    suffix="+"
                  />

                </h2>

                <p className="text-gray-400 mt-1">
                  Events
                </p>

              </div>

              <div>

                <h2 className="text-4xl font-black">

                  <Counter
                    target={speakers.length}
                    suffix="+"
                  />

                </h2>

                <p className="text-gray-400 mt-1">
                  Speakers
                </p>

              </div>

              <div>

                <h2 className="text-4xl font-black">

                  <Counter
                    target={liveSessions.length}
                  />

                </h2>

                <p className="text-gray-400 mt-1">
                  LIVE Now
                </p>

              </div>

            </div>

          </div>

          {/* RIGHT */}

          <div className="relative">

            <div className="relative h-[500px] rounded-[36px] overflow-hidden border border-white/10 shadow-2xl">

              {/* Images */}

              {images.map((img, index) => (

                <Image
                  key={index}
                  src={img}
                  alt="Event"
                  fill
                  priority
                  className={`
                    object-cover
                    absolute
                    transition-opacity
                    duration-1000
                    ${currentImage === index
                      ? "opacity-100"
                      : "opacity-0"}
                  `}
                />

              ))}

              <div className="absolute inset-0 bg-black/50" />

              {/* Floating Live Card */}

              <div className="absolute bottom-6 left-6 right-6">

                <div className="glass rounded-3xl p-6">

                  {liveSessions.length > 0 ? (

                    <>
                      <div className="flex items-center justify-between">

                        <div>

                          <h3 className="text-2xl font-black">

                            {liveSessions[0].title}

                          </h3>

                          <p className="text-gray-300 mt-1">

                            {liveSessions[0].room?.name}

                          </p>

                        </div>

                        <div className="px-4 py-2 rounded-full bg-[#ff4d6d]/20 text-[#ff4d6d] text-sm font-bold">

                          LIVE

                        </div>

                      </div>

                      <div className="mt-6">

                        <span className="text-sm text-gray-300">

                          {liveSessions[0].description}

                        </span>

                      </div>
                    </>

                  ) : (

                    <div>

                      <h3 className="text-2xl font-black">
                        No Live Session
                      </h3>

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

      </div>

    </section>
  );
}