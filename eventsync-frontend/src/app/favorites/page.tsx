"use client";

import { useEffect, useState } from "react";
import { getFavorites, Favorite, removeFavorite } from "@/lib/favorites";
import { FaClock, FaTrashAlt, FaCalendar } from "react-icons/fa";
import Link from "next/link";

export default function FavoritesPage() {
  const [favorites, setFavorites] = useState<Favorite[]>([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    setFavorites(getFavorites());
    setLoading(false);
  }, []);
  const sessionFavorites = favorites.filter((f) => f.type === "session");
  const handleDelete = (id: string) => {
    removeFavorite(id, "session");
    setFavorites(getFavorites()); 
  };

  if (loading) {
    return (
      <main className="container-app py-20 text-center">
        <p className="text-gray-400 text-lg animate-pulse">Chargement de vos favoris...</p>
      </main>
    );
  }

  return (
    <main className="container-app py-20 min-h-[70vh]">
      <header className="mb-14">
        <span className="text-lime-400 font-semibold tracking-widest uppercase text-2xl">
        Your Saved Sessions
        </span>
        <h1 className="text-4xl font-black mt-3 bg-gradient-to-r from-white to-gray-400 bg-clip-text text-transparent">
        Your Personal Selection of Must-Watch Event Highlights
        </h1>
        <p className="text-gray-400 mt-3 text-lg">
        Keep track of the moments you don't want to miss. This is your dedicated space where all your bookmarked and favorite sessions are gathered. Review your top choices, refine your interests, 
        and get ready to dive into the topics and speakers that matter most to you as the event unfolds.
        </p>
      </header>

      {sessionFavorites.length === 0 ? (
        <div className="glass rounded-[40px] p-16 text-center border border-white/5 flex flex-col items-center justify-center max-w-2xl mx-auto mt-10">
          <FaCalendar className="text-gray-600 text-6xl mb-6" />
          <h2 className="text-2xl font-bold text-white mb-3">Aucun favori pour le moment</h2>
          <p className="text-gray-400 max-w-sm mb-8">
            Parcourez l'agenda global et cliquez sur l'étoile pour ajouter des sessions à votre liste.
          </p>
          <Link
            href="/agenda"
            className="px-6 py-3 rounded-xl bg-lime-400 text-black font-black hover:bg-[#a3ff12] hover:scale-105 active:scale-95 transition-all duration-300 shadow-[0_0_20px_rgba(163,255,18,0.2)]"
          >
            Découvrir l'agenda
          </Link>
        </div>
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {sessionFavorites.map((fav) => (
            <div
              key={fav.id}
              className="glass rounded-3xl p-6 border border-white/5 flex flex-col justify-between hover:border-white/10 transition-all duration-300 relative group"
            >
              <div>
                <div className="flex items-center justify-between gap-4 mb-4">
                  <span className="text-xs font-semibold px-2.5 py-1 rounded-md bg-white/5 text-gray-400 uppercase tracking-wider">
                    Session
                  </span>
                  <button
                    onClick={() => handleDelete(fav.id)}
                    className="text-gray-500 hover:text-red-400 p-1.5 rounded-lg hover:bg-white/5 transition-all duration-200"
                    title="Supprimer des favoris"
                  >
                    <FaTrashAlt className="w-4 h-4" />
                  </button>
                </div>
                <h3 className="text-2xl font-black text-white line-clamp-2 leading-tight group-hover:text-lime-400 transition-colors duration-200">
                  {fav.label}
                </h3>
              </div>
              <div className="mt-8 pt-4 border-t border-white/5 flex flex-col gap-4">
                <div className="flex items-center gap-2 text-xs text-gray-500">
                  <FaClock className="text-lime-400" />
                  <span>
                    add on {new Date(fav.savedAt).toLocaleDateString("fr-FR", {
                      day: "numeric",
                      month: "short",
                      hour: "2-digit",
                      minute: "2-digit"
                    })}
                  </span>
                </div>

                <Link
                  href={`/sessions/${fav.id}`}
                  className="w-full text-center py-3 rounded-xl bg-white/5 hover:bg-white/10 text-white font-bold text-sm border border-white/10 hover:border-white/20 transition-all duration-200 block"
                >
                  Vew details
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </main>
  );
}