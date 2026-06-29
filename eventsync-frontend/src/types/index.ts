export interface Speaker {
  sessions: any;
  sessionCount: number;
  photoUrl: any;
  links(links: any): unknown;
  id: string;
  fullName: string;
  bio: string;
  profilePicture?: string;
}

export interface Room {
  id: string;
  name: string;
  adress: string;
  capacity?: number;
}

export interface Session {
  guestNumber: null;
  location: string;
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
  hasVoted: boolean;
}