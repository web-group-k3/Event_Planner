import { Session } from "@/types";

/**
 * Vérifie si une session est en cours (live)
 */
export function isLive(session: Session): boolean {
  const now = new Date();
  const start = new Date(session.startTime);
  const end = new Date(session.endTime);
  return now >= start && now <= end;
}

/**
 * Trie les sessions par heure de début
 */
export function sortByStartTime(sessions: Session[]): Session[] {
  return [...sessions].sort(
    (a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
  );
}

/**
 * Regroupe les sessions par salle
 * Retourne un objet { [roomName]: Session[] }
 */
export function groupByRoom(sessions: Session[]): Record<string, Session[]> {
  return sessions.reduce((acc, session) => {
    const roomName = session.room?.name ?? "Sans salle";
    if (!acc[roomName]) {
      acc[roomName] = [];
    }
    acc[roomName].push(session);
    return acc;
  }, {} as Record<string, Session[]>);
}

/**
 * Regroupe les sessions par créneau horaire (heure de début)
 * Retourne un objet { ["HH:mm"]: Session[] }
 */
export function groupByTimeSlot(sessions: Session[]): Record<string, Session[]> {
  return sessions.reduce((acc, session) => {
    const slot = formatTime(session.startTime);
    if (!acc[slot]) {
      acc[slot] = [];
    }
    acc[slot].push(session);
    return acc;
  }, {} as Record<string, Session[]>);
}

/**
 * Formate une date ISO en "HH:mm"
 */
export function formatTime(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleTimeString("fr-FR", {
    hour: "2-digit",
    minute: "2-digit",
  });
}

/**
 * Formate une date ISO en "dd/MM/yyyy"
 */
export function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString("fr-FR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}