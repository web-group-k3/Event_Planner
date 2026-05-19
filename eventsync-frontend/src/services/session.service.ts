import api from "@/lib/axios";
import { Session } from "@/types";

export const getSessions = async (): Promise<Session[]> => {

  const response = await api.get("/sessions");

  return response.data;
};
export const getSessionById = async (
  id: string
): Promise<Session> => {

  const response = await api.get(`/sessions/${id}`);

  return response.data;
};
export const getLiveSessions = async (): Promise<Session[]> => {

  const response = await api.get("/sessions");

  return response.data.filter(
    (session: Session) => session.live
  );
};