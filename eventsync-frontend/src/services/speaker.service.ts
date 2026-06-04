import api from "@/lib/axios";
import { Speaker } from "@/types";

export const getSpeakers = async (): Promise<Speaker[]> => {
  const response = await api.get("/speakers");
  return response.data;
};

export const getSpeakerById = async (id: string): Promise<Speaker> => {
  const response = await api.get(`/speakers/${id}`);
  return response.data;
};

export const getSpeakersByEvent = async (eventId: string): Promise<Speaker[]> => {
  const response = await api.get(`/speakers/byEvent/${eventId}`);
  return response.data;
};