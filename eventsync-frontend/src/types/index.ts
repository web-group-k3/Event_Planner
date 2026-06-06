export interface Room {
  id: string;
  name: string;
  adress: string;
}

export interface Speaker {
  id: string;
  fullName: string;
  profilePhoto?: string;
  bio?: string;
  externalLinks?: string[];
  sessions?: Session[];
}

export interface Question {
  id: string;
  content: string;
  name?: string;
  upvotes: number;
  sessionId: string;
  createdAt: string;
}

export interface Session {
  id: string;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  room?: Room;
  capacity?: number;
  speakers?: Speaker[];
  questions?: Question[];
  eventId?: string;
}

export interface Event {
  id: string;
  title: string;
  description?: string;
  startDate: string;
  endDate: string;
  location?: string;
  sessions?: Session[];
}