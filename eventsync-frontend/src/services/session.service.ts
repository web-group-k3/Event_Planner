import api from "@/lib/axios";
import { Session } from "@/types";
import { isLive } from "@/utils/session";

export const getSessions = async (): Promise<Session[]> => {
  const response = await api.get("/sessions");
  return response.data;
};

export const getSessionById = async (id: string): Promise<Session> => {
  const response = await api.get(`/sessions/${id}`);
  return response.data;
};

// ✅ isLive calculé côté client, pas via l'API
export const getLiveSessions = async (): Promise<Session[]> => {
  const response = await api.get("/sessions");
  return response.data.filter((session: Session) => isLive(session));
};

export const getSessionsByEvent = async (eventId: string): Promise<Session[]> => {
  const response = await api.get(`/sessions/byEvent/${eventId}`);
  return response.data;
};

export const getSessionsByRoom = async (roomId: string): Promise<Session[]> => {
  const res = await api.get(`/sessions/byRoom/${roomId}`);
  return res.data;
};

// ✅ route backend GET /api/sessions/:eventId/:roomId
export const getSessionsByEventAndRoom = async (
  eventId: string,
  roomId: string
): Promise<Session[]> => {
  const res = await api.get(`/sessions/${eventId}/${roomId}`);
  return res.data;
};