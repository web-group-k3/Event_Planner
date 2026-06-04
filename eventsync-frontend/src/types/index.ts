export interface Speaker {
  id: string;
  fullName: string;
  bio: string;
  photoUrl?: string;
  links?: string;
  sessions?: Session[];
  sessionCount: number;
}

export interface Room {
  id: string;
  name: string;
}

export interface Session {
  id: string;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  live: boolean;
  guestNumber?: number;
  room?: Room;
  roomId?: string;
  eventId?: string;
  speakers?: Speaker[];
  questions?: Question[];
}

export interface Event {
  id: string;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  location?: string;
  sessions?: Session[];
}

export interface Question {
  id: string;
  content: string;
  authorName: string;
  upvotes: number;
  createdAt: string;
  sessionId: string;
}