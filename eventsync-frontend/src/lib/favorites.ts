// src/lib/favorites.ts

const FAVORITES_KEY = "eventsync_favorites";

export type FavoriteType = "event" | "session" | "speaker";

export interface Favorite {
  id: string;
  type: FavoriteType;
  label: string;
  savedAt: string;
}

// Récupérer tous les favoris
export function getFavorites(): Favorite[] {
  if (typeof window === "undefined") return [];
  const data = localStorage.getItem(FAVORITES_KEY);
  return data ? JSON.parse(data) : [];
}

// Vérifier si un élément est en favori
export function isFavorite(id: string, type: FavoriteType): boolean {
  return getFavorites().some((f) => f.id === id && f.type === type);
}

// Ajouter un favori
export function addFavorite(id: string, type: FavoriteType, label: string): void {
  const favorites = getFavorites();
  if (!isFavorite(id, type)) {
    favorites.push({ id, type, label, savedAt: new Date().toISOString() });
    localStorage.setItem(FAVORITES_KEY, JSON.stringify(favorites));
  }
}

// Supprimer un favori
export function removeFavorite(id: string, type: FavoriteType): void {
  const updated = getFavorites().filter((f) => !(f.id === id && f.type === type));
  localStorage.setItem(FAVORITES_KEY, JSON.stringify(updated));
}

// Basculer favori (toggle)
export function toggleFavorite(id: string, type: FavoriteType, label: string): boolean {
  if (isFavorite(id, type)) {
    removeFavorite(id, type);
    return false;
  } else {
    addFavorite(id, type, label);
    return true;
  }
}

// Récupérer favoris filtrés par type
export function getFavoritesByType(type: FavoriteType): Favorite[] {
  return getFavorites().filter((f) => f.type === type);
}