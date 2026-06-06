import api from "@/lib/axios";
import { Event } from "@/types";

export const getEvents = async (): Promise<Event[]> => {
  const response = await api.get("/events");
  return response.data;
};

export const getEventById = async (id: string): Promise<Event> => {
  const response = await api.get(`/events/${id}`);
  return response.data;
};

export const getEventsByRoom = async (roomId: string): Promise<Event[]> => {
  const res = await api.get(`/events/byRoom/${roomId}`);
  return res.data;
};

export const getEventsBySpeaker = async (speakerId: string): Promise<Event[]> => {
  const res = await api.get(`/events/bySpeaker/${speakerId}`);
  return res.data;
};