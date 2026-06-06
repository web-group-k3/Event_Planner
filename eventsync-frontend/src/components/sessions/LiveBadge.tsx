interface Props {
  startDate: string;
  endDate: string;
  className?: string;
}

export default function LiveBadge({ startDate, endDate, className = "" }: Props) {
  const now = new Date();
  const start = new Date(startDate);
  const end = new Date(endDate);

  let label = "";
  let badgeClass = "";

  if (now < start) {
    label = "UPCOMING";
    badgeClass = "bg-lime-400/20 text-lime-300 border border-lime-400/30";
  } else if (now >= start && now <= end) {
    label = "LIVE";
    badgeClass = "bg-[#ff4d6d]/20 text-[#ff4d6d] border border-[#ff4d6d]/30";
  } else {
    label = "FINISHED";
    badgeClass = "bg-gray-400/15 text-gray-400 border border-gray-500/20";
  }

  return (
    <div
      className={`
        px-3 py-1.5 rounded-full
        text-xs font-bold tracking-widest
        backdrop-blur-md
        inline-flex items-center gap-2
        ${badgeClass}
        ${className}
      `}
    >
      {label === "LIVE" && (
        <span className="relative flex h-2 w-2">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#ff4d6d] opacity-75" />
          <span className="relative inline-flex rounded-full h-2 w-2 bg-[#ff4d6d]" />
        </span>
      )}
      {label}
    </div>
  );
}