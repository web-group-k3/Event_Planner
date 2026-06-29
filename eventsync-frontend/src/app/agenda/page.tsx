import { getSessions } from "@/services/session.service";
import AgendaGrid from "@/components/sessions/AgendaGrid";
import { Session } from "@/types";

export default async function AgendaPage() {
  const sessions: Session[] = await getSessions();
  const sortedSessions = sessions.sort(
    (a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
  );

  return (
    <main className="max-w-7xl mx-auto p-6 text-slate-100">
      <header className="mb-8">
        <h1 className="text-3xl font-bold tracking-tight text-lime-400">Your Complete Roadmap to Every Session, Track, and Keynote</h1>
        <p className="text-slate-400 text-xl text-white mt-2">
        Plan your entire event journey right here. This comprehensive timeline displays every single session, workshop, and panel discussion across all tracks.
         Use the filters to sort by date, topic, or speaker, and easily build your schedule so you can navigate the event seamlessly from start to finish.
        </p>
      </header>

      {sortedSessions.length === 0 ? (
        <div className="text-center py-16 bg-slate-900/40 rounded-xl border border-dashed border-slate-800">
          <p className="text-slate-400 text-lg">Aucune session trouvée dans l'agenda.</p>
        </div>
      ) : (
        <AgendaGrid sessions={sortedSessions} />
      )}
    </main>
  );
}