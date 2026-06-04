import api from "@/lib/axios";
import { Room } from "@/types";

export const getRooms = async (): Promise<Room[]> => {
  const response = await api.get("/rooms");
  return response.data;
};

export const getRoomById = async (id: string): Promise<Room> => {
  const response = await api.get(`/rooms/${id}`);
  return response.data;
};

export const getRoomsByEvent = async (eventId: string): Promise<Room[]> => {
  const response = await api.get(`/rooms/byEvent/${eventId}`);
  return response.data;
};
