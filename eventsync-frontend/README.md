# EventSync Frontend

> Next.js 16 + React 19 frontend for the EventSync event management platform.

[![Next.js](https://img.shields.io/badge/Next.js-16.2.6-000?logo=next.js)](https://nextjs.org)
[![React](https://img.shields.io/badge/React-19.2.4-61DAFB?logo=react)](https://react.dev)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript)](https://www.typescriptlang.org)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4-06B6D4?logo=tailwindcss)](https://tailwindcss.com)

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Dev Server](#running-the-dev-server)
- [Backend Dependency](#backend-dependency)
- [Project Structure](#project-structure)
- [Pages & Routes](#pages--routes)
- [Key Components](#key-components)
- [API Services](#api-services)
- [TypeScript Interfaces](#typescript-interfaces)
- [Styling](#styling)
- [Important Notes](#important-notes)

---

## Prerequisites

- **Node.js** 20+ (the project uses the built-in fetch API and other modern features)
- **npm** or **pnpm** (npm ships with Node.js)

---

## Installation

```bash
cd eventsync-frontend
npm install
```

This installs all dependencies listed in `package.json`, including:

| Package | Version | Purpose |
|---|---|---|
| `next` | 16.2.6 | App Router framework with Turbopack |
| `react` / `react-dom` | 19.2.4 | UI library |
| `axios` | ^1.16.0 | HTTP client for API calls |
| `date-fns` | ^4.1.0 | Date formatting utilities |
| `lucide-react` | ^1.14.0 | Icon library |
| `react-hot-toast` | ^2.6.0 | Toast notifications |
| `zustand` | ^5.0.13 | Lightweight state management |
| `tailwindcss` | ^4 | Utility-first CSS framework |

---

## Running the Dev Server

```bash
npm run dev
```

The development server starts at **http://localhost:3000** with Turbopack enabled for fast HMR.

Other available scripts:

| Command | Description |
|---|---|
| `npm run build` | Production build |
| `npm run start` | Start production server |
| `npm run lint` | Run ESLint |

---

## Backend Dependency

The frontend requires the **EventSync Spring Boot backend** to be running at:

```
http://localhost:8080/api
```

This base URL is configured in [`src/lib/axios.ts`](src/lib/axios.ts):

```typescript
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
```

If your backend runs on a different port or host, update the `baseURL` in this file. The backend must also be seeded with data for pages to display content.

---

## Project Structure

```
eventsync-frontend/
├── public/                         # Static assets (favicon, images, etc.)
├── src/
│   ├── app/                        # Next.js App Router pages
│   │   ├── globals.css             # Global styles & CSS custom properties
│   │   ├── layout.tsx              # Root layout (Navbar, metadata)
│   │   ├── page.tsx                # Home page (hero + live preview)
│   │   ├── sessions/
│   │   │   └── live/
│   │   │       └── page.tsx        # Live sessions + upcoming sessions
│   │   ├── speakers/
│   │   │   └── page.tsx            # Speaker grid with search & detail modal
│   │   └── rooms/
│   │       └── page.tsx            # Room planning with session schedules
│   ├── components/                 # Reusable UI components
│   │   ├── Navbar.tsx              # Sticky navigation bar
│   │   ├── HeroSection.tsx         # Landing hero (carousel, stats, live card)
│   │   ├── SpeakerCard.tsx         # Speaker grid card
│   │   ├── SpeakerModal.tsx        # Full-screen speaker detail modal
│   │   └── SessionModal.tsx        # Full-screen session detail modal
│   ├── services/                   # Axios API service layer
│   │   ├── event.service.ts        # GET /events, /events/:id, /events/byEvent/:id
│   │   ├── session.service.ts      # GET /sessions, /sessions/:id, etc.
│   │   ├── speaker.service.ts      # GET /speakers, /speakers/:id, etc.
│   │   └── room.service.ts         # GET /rooms, /rooms/:id, etc.
│   ├── types/
│   │   └── index.ts                # TypeScript interfaces (Speaker, Session, Room, Event, Question)
│   └── lib/
│       ├── axios.ts                # Axios instance with base URL config
│       ├── links.tsx               # Speaker link parser (JSON/plain URL → icons)
│       └── utils.ts                # Shared utility helpers
├── tailwind.config.ts              # Tailwind theme customization
├── next.config.ts                  # Next.js configuration (React Compiler, Turbopack)
├── AGENTS.md                       # AI coding assistant notes / breaking changes
├── package.json
├── tsconfig.json
└── README.md                       # This file
```

---

## Pages & Routes

### `/` — Home Page

The landing page featuring:
- **HeroSection** — Image carousel, event stats, and a "Get Started" call-to-action
- **Live Session Preview** — A card highlighting the currently live session (if any)
- Quick navigation links to speakers, sessions, and rooms

### `/sessions/live` — Live Sessions

Displays two groups:
- **Live Now** — Sessions currently in progress (`live: true`)
- **Upcoming Sessions** — Future sessions sorted by start time

Each session card shows title, time, room, speakers, and a clickable detail modal.

### `/speakers` — Speakers

A responsive grid of speaker cards with:
- **Search** — Filter speakers by name or bio in real time
- **SpeakerCard** — Photo, full name, bio preview, session count
- **SpeakerModal** — Full-screen detail with complete bio, social/professional links (LinkedIn, GitHub, Twitter, etc.), and associated sessions

### `/rooms` — Room Planning

A room-by-room view showing:
- All rooms with their scheduled sessions
- Each session displayed as an expandable card showing time, speakers, and description
- Click any session to open the **SessionModal** with full details

---

## Key Components

| Component | File | Description |
|---|---|---|
| **Navbar** | `src/components/Navbar.tsx` | Sticky top navigation with links to Home, Sessions, Speakers, and Rooms. Stays fixed on scroll with a glass backdrop. |
| **HeroSection** | `src/components/HeroSection.tsx` | Landing page hero with a background image carousel, event statistics counters, and a live-session card overlay. |
| **SpeakerCard** | `src/components/SpeakerCard.tsx` | Card in the speakers grid showing the speaker's photo, full name, truncated bio, session count badge, and social link icons. |
| **SpeakerModal** | `src/components/SpeakerModal.tsx` | Full-screen overlay modal displaying the speaker's complete bio, parsed social/professional links (with platform-specific icons and colors), and a list of their sessions. |
| **SessionModal** | `src/components/SessionModal.tsx` | Full-screen overlay modal with session title, description, time range, room name, speaker list, and Q&A section showing submitted questions with upvote counts. |

---

## API Services

All API communication goes through Axios via the shared instance in `src/lib/axios.ts`. Every endpoint is a **public GET** request — no authentication is required.

| Service | Function | Endpoint | Description |
|---|---|---|---|
| `event.service.ts` | `getEvents()` | `GET /api/events` | List all events |
| | `getEventById(id)` | `GET /api/events/:id` | Single event detail |
| `session.service.ts` | `getSessions()` | `GET /api/sessions` | List all sessions |
| | `getLiveSessions()` | `GET /api/sessions` (client-filtered) | Filter sessions where `live: true` |
| | `getSessionById(id)` | `GET /api/sessions/:id` | Single session detail |
| | `getSessionsByRoom(roomId)` | `GET /api/sessions/byRoom/:roomId` | Sessions in a specific room |
| | `getSessionsByEvent(eventId)` | `GET /api/sessions/byEvent/:eventId` | Sessions for a specific event |
| `speaker.service.ts` | `getSpeakers()` | `GET /api/speakers` | List all speakers |
| | `getSpeakerById(id)` | `GET /api/speakers/:id` | Single speaker detail |
| | `getSpeakersByEvent(eventId)` | `GET /api/speakers/byEvent/:eventId` | Speakers for a specific event |
| `room.service.ts` | `getRooms()` | `GET /api/rooms` | List all rooms |
| | `getRoomById(id)` | `GET /api/rooms/:id` | Single room detail |
| | `getRoomsByEvent(eventId)` | `GET /api/rooms/byEvent/:eventId` | Rooms for a specific event |

All service functions are `async` and return typed promises. Example usage:

```typescript
import { getSpeakers } from "@/services/speaker.service";

const speakers = await getSpeakers();
// speakers is typed as Speaker[]
```

---

## TypeScript Interfaces

The shared types live in [`src/types/index.ts`](src/types/index.ts) and must match the JSON shape returned by the Spring Boot backend.

```typescript
export interface Speaker {
  id: string;
  fullName: string;
  bio: string;
  photoUrl?: string;
  links?: string;           // JSON object or plain URL
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
  startTime: string;        // ISO-8601 datetime
  endTime: string;          // ISO-8601 datetime
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
```

> **Important:** If the backend JSON response shape changes, these interfaces **must** be updated accordingly to keep TypeScript type safety intact.

---

## Styling

### Tailwind CSS v4

The project uses **Tailwind CSS v4** with the `@tailwindcss/postcss` plugin. The custom theme is defined in [`tailwind.config.ts`](tailwind.config.ts) and global styles in [`src/app/globals.css`](src/app/globals.css).

### Dark Theme Palette

| Role | Color | Hex |
|---|---|---|
| Background | Deep navy | `#050816` |
| Surface | Dark slate | `#0f172a` |
| Primary accent | Neon green | `#a3ff12` |
| Secondary accent | Cyan | `#38bdf8` |
| Danger / highlight | Red | `#ff4d6d` |
| Text | White | `#f8fafc` |
| Muted text | Slate | `#94a3b8` |
| Border | White (8% opacity) | `rgba(255,255,255,0.08)` |

The body has subtle radial gradient overlays (neon green top-left, cyan bottom-right) for depth.

### Custom Utility Classes

These classes are defined in `globals.css` and can be used anywhere in JSX:

| Class | What it does |
|---|---|
| `.glass` | Frosted glass effect: semi-transparent background + `backdrop-filter: blur(14px)` + subtle border |
| `.button-primary` | Neon green filled button with hover lift and glow shadow |
| `.button-secondary` | Outlined button with hover highlight |
| `.gradient-text` | Text gradient from neon green to cyan (`#a3ff12` → `#38bdf8`) |
| `.container-app` | Max-width 1280px centered container with 24px padding |

---

## Important Notes

### React Compiler

The project uses **React 19** with the new **React Compiler** enabled in `next.config.ts`:

```typescript
const nextConfig: NextConfig = {
  reactCompiler: true,
  turbopack: {
    root: path.resolve(__dirname),
  },
};
```

This means React automatically memoizes components and hooks — you don't need to manually add `useMemo`, `useCallback`, or `React.memo`. Ensure your code follows React's rules of hooks and doesn't produce compiler warnings.

### Next.js 16 Breaking Changes

This project uses **Next.js 16.2.6** with the App Router. Check [`AGENTS.md`](AGENTS.md) for a note about potential breaking changes. If you encounter unexpected behavior, consult the guides in `node_modules/next/dist/docs/`.

### Backend Must Be Running

The frontend fetches all data from the Spring Boot backend at `http://localhost:8080/api`. Without the backend running and seeded, pages will show loading states and eventually error out. Start the backend first, then run the frontend.

### State Management

State management is handled with **Zustand** (`^5.0.13`). Check `src/store/` (if it exists) or individual page/component files for store usage.

---

## License

This project is part of the EventSync platform. All rights reserved.
