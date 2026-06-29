interface Props {
    startDate: string;
    endDate: string;
  }
  
  export default function LiveBadge({
    startDate,
    endDate,
  }: Props) {
  
    const now = new Date();
  
    const start = new Date(startDate);
    const end = new Date(endDate);
  
    let label = "";
    let className = "";
  
    if (now < start) {
  
      label = "UPCOMING";
  
      className =
        "bg-sky-400/20 text-sky-300";
  
    } else if (
      now >= start &&
      now <= end
    ) {
  
      label = "LIVE";
  
      className =
        "bg-[#ff4d6d]/20 text-[#ff4d6d]";
  
    } else {
  
      label = "FINISHED";
  
      className =
        "bg-gray-400/20 text-gray-300";
  
    }
  
    return (
  
      <div
        className={`
          px-4 py-2 rounded-full
          text-xs font-bold
          backdrop-blur-md
          inline-flex items-center gap-2
          ${className}
        `}
      >
  
        {label === "LIVE" && (
          <div className="w-2 h-2 rounded-full bg-[#ff4d6d] animate-pulse" />
        )}
  
        {label}
  
      </div>
  
    );
  }