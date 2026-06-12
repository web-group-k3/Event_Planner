// src/hooks/useFavorite.ts

import { useState, useEffect } from "react";
import { isFavorite, toggleFavorite, FavoriteType } from "@/lib/favorites";

export function useFavorite(id: string, type: FavoriteType, label: string) {
  const [favorited, setFavorited] = useState(false);

  useEffect(() => {
    setFavorited(isFavorite(id, type));
  }, [id, type]);

  const toggle = () => {
    const result = toggleFavorite(id, type, label);
    setFavorited(result);
  };

  return { favorited, toggle };
}