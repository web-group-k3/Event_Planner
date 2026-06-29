import { notFound } from "next/navigation";
import { getEventById } from "@/services/event.service";
import AgendaView from "@/components/sessions/AgendaView";
import { FaCalendarAlt, FaLayerGroup, FaDoorOpen } from "react-icons/fa";

interface Props {
  params: Promise<{ id: string }>;
}

export default async function EventSchedulePage({ params }: Props) {
  const { id } = await params;

  try {
    const event = await getEventById(id);

    return (
      <main className="container-app py-20">
        <div className="mb-10">
          <span className="text-lime-400 font-semibold tracking-widest uppercase">
            Agenda for the event
          </span>
          <h1 className="text-5xl lg:text-4xl font-black mt-4">{event.title}</h1>
          <p className="text-gray-400 text-lg mt-6 max-w-3xl leading-relaxed">
          Complete programme of the event — all sessions listed in chronological order.
          </p>
        </div>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-14 mb-14">
          {[
            { label: "Start", value: new Date(event.startDate).toLocaleDateString("fr-FR", { day: "numeric", month: "short", year: "numeric" }), icon: <FaCalendarAlt className="text-lime-400" /> },
            { label: "End", value: new Date(event.endDate).toLocaleDateString("fr-FR", { day: "numeric", month: "short", year: "numeric" }), icon: <FaCalendarAlt className="text-lime-400" /> },
            { label: "Sessions", value: event.sessions?.length || 0, icon: <FaLayerGroup className="text-lime-400" /> },
          ].map((stat, i) => (
            <div key={i} className="glass rounded-[24px] p-6 border border-white/5">
              <div className="flex items-center gap-2 text-sm text-gray-500 mb-3">
                {stat.icon}
                {stat.label}
              </div>
              <p className="text-2xl font-black">{stat.value}</p>
            </div>
          ))}
        </div>
        <AgendaView sessions={event.sessions || []} />

      </main>
    );
  } catch {
    notFound();
  }
}