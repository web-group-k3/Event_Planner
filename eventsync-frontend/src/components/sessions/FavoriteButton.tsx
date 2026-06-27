"use client";

import { useFavorite } from "@/hooks/useFavorite";
import { FavoriteType } from "@/lib/favorites";
import { Star } from "lucide-react";

interface FavoriteButtonProps {
  id: string;
  type: FavoriteType;
  label: string;
  className?: string;
}

export default function FavoriteButton({
  id,
  type,
  label,
  className = "",
}: FavoriteButtonProps) {
  const { favorited, toggle } = useFavorite(id, type, label);

  return (
    <button
      onClick={(e) => {
        e.preventDefault();
        e.stopPropagation();
        toggle();
      }}
      className={`p-2.5 rounded-xl transition-all duration-300 hover:scale-105 active:scale-95 glass ${className} ${
        favorited
          ? "text-[#a3ff12] bg-[#a3ff12]/10 border-[#a3ff12]/20"
          : "text-gray-400 hover:text-white border-transparent"
      }`}
      title={favorited ? "Retirer des favoris" : "Ajouter aux favoris"}
    >
      <Star
        className={`w-5 h-5 transition-transform duration-300 ${
          favorited ? "fill-current scale-110" : "scale-100"
        }`}
      />
    </button>
  );
}
