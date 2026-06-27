import Link from "next/link";

export default function Navbar() {
  return (
    <header className="sticky top-0 z-50 border-b border-white/10 backdrop-blur-xl bg-[#0b1020]/80">
      <div className="container-app h-20 flex items-center justify-between px-6 lg:px-8">
        
        <Link href="/" className="flex items-center gap-3 group">
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-violet-500 via-fuchsia-500 to-cyan-400 flex items-center justify-center font-black text-2xl shadow-lg shadow-violet-500/30 transition-all group-hover:scale-110 group-hover:rotate-6">
            Es
          </div>
          
          <div>
            <h1 className="text-2xl font-black tracking-tighter text-white">
              EventSync
            </h1>
          </div>
        </Link>

        <nav className="hidden md:flex items-center gap-8 text-sm font-medium">
          <Link
            href="/events"
            className="text-gray-300 hover:text-white transition-colors duration-200"
          >
            Events
          </Link>
          <Link
            href="/sessions/live"
            className="text-gray-300 hover:text-white transition-colors duration-200"
          >
            Live
          </Link>
          <Link
            href="/speakers"
            className="text-gray-300 hover:text-white transition-colors duration-200"
          >
            Speakers
          </Link>
          <Link
            href="/rooms"
            className="text-gray-300 hover:text-white transition-colors duration-200"
          >
            Rooms
          </Link>
          <Link
            href="/agenda"
            className="text-gray-300 hover:text-white transition-colors duration-200"
          >
          Planning
          </Link>
          <Link
            href="/favorites"
            className="text-gray-300 hover:text-white transition-colors duration-200"
          >
          My favorites
          </Link>
        </nav>

        <div className="flex items-center gap-4">
        <a            href="http://localhost:5173"
            target="_blank"
            rel="noopener noreferrer"
            className="hidden sm:block px-5 py-2.5 text-sm font-semibold rounded-2xl bg-white/10 hover:bg-white/20 text-white border border-white/10 transition-all active:scale-95"
          >
            Create event
          </a>

          
           <a href="http://localhost:5173"
            target="_blank"
            rel="noopener noreferrer"
            className="button-primary px-6 py-2.5 text-sm font-semibold rounded-2xl"
          >
            Admin Login
          </a>
        </div>

      </div>
    </header>
  );
}