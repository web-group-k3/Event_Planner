import Link from "next/link";

export default function Navbar() {

  return (
    <header className="sticky top-0 z-50 border-b border-white/10 backdrop-blur-xl bg-[#0b1020]/80">

      <div className="container-app h-20 flex items-center justify-between">

        {/* Logo */}

        <Link
          href="/"
          className="flex items-center gap-3"
        >

          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-violet-600 to-cyan-500 flex items-center justify-center font-black text-lg">
            E
          </div>

          <div>
            <h1 className="text-xl font-black">
              EventSync
            </h1>

            <p className="text-xs text-gray-400">
              Realtime Event Platform
            </p>
          </div>

        </Link>

        {/* Navigation */}

        <nav className="hidden md:flex items-center gap-8">

          <Link
            href="/events"
            className="text-gray-300 hover:text-white transition"
          >
            Events
          </Link>

          <Link
            href="/sessions/live"
            className="text-gray-300 hover:text-white transition"
          >
            Live
          </Link>

          <Link
            href="/speakers"
            className="text-gray-300 hover:text-white transition"
          >
            Speakers
          </Link>

          <Link
            href="/rooms"
            className="text-gray-300 hover:text-white transition"
          >
            Rooms
          </Link>

        </nav>

        {/* Button */}

        <button className="button-primary">
          Admin Login
        </button>

      </div>

    </header>
  );
}