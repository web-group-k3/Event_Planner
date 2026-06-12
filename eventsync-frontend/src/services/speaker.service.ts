import api from "@/lib/axios";
import { Speaker } from "@/types";

export const getSpeakers = async (speakerId?: string): Promise<Speaker[]> => {

  const response = await api.get("/speakers");

  return response.data;
};