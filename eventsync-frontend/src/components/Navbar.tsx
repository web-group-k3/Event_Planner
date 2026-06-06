import Link from "next/link";

export default function Navbar() {
  return (
    // Correction : Utilisation de la couleur de fond globale ou de surface pour correspondre au reste du site
    <header className="sticky top-0 z-50 border-b border-white/5 backdrop-blur-xl bg-[#050816]/80">
      <div className="max-w-[1600px] mx-auto h-20 px-6 flex items-center justify-between">
        
        {/* Logo */}
        <Link href="/" className="flex items-center gap-3 group">
          {/* Correction : Remplacement du violet par ton dégradé officiel (Primary -> Secondary) */}
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-[var(--primary)] to-[var(--secondary)] flex items-center justify-center font-black text-lg text-black transition-transform group-hover:scale-105">
            E
          </div>

          <div>
            <h1 className="text-xl font-black tracking-tight">
              EventSync
            </h1>
            <p className="text-xs text-[var(--muted)]">
              Realtime Event Platform
            </p>
          </div>
        </Link>

        {/* Navigation */}
        <nav className="hidden md:flex items-center gap-8">
          <Link
            href="/events"
            className="text-[var(--primary)] font-semibold transition duration-300"
          >
            Events
          </Link>
          <Link
            href="/live"
            className="text-gray-300 hover:text-[var(--primary)] transition duration-300 flex items-center gap-1.5"
          >
            <span className="w-1.5 h-1.5 rounded-full bg-rose-500 animate-pulse" />
            Live
          </Link>
          <Link
            href="/speakers"
            className="text-gray-300 hover:text-white transition duration-300"
          >
            Speakers
          </Link>
          <Link
            href="/rooms"
            className="text-gray-300 hover:text-white transition duration-300"
          >
            Rooms
          </Link>
        </nav>

        {/* Button */}
        {/* Correction : Remplacement par button-secondary pour ne pas vampiriser l'attention visuelle */}
        <button className="button-secondary text-sm font-semibold cursor-pointer">
          Admin Login
        </button>

      </div>
    </header>
  );
}