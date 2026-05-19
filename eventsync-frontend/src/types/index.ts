export interface Speaker {
  id: string;
  fullName: string;
  bio: string;
  profilePicture?: string;
}

export interface Room {
  id: string;
  name: string;
  adress: string;
}

export interface Session {
  id: string;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  live: boolean;
  room?: Room;
  speakers?: Speaker[];
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
  authorName?: string;
  upvotes: number;
  createdAt: string;
}