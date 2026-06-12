import Link from "next/link";

export default function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="border-t border-white/10 bg-[#070a14] text-gray-400 pt-14 pb-8 mt-auto w-full">
      <div className="max-w-7xl mx-auto px-6 sm:px-8 lg:px-12">
        
        <div className="flex flex-col md:flex-row justify-between items-start gap-8 pb-10">
          
          <div className="flex flex-col gap-3">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-violet-500 to-cyan-500 flex items-center justify-center font-bold text-white text-sm shadow-md shadow-violet-500/25">
                E
              </div>
              <span className="text-xl font-semibold text-white tracking-tight">
                EventSync
              </span>
            </div>
            <p className="text-s text-gray-400 max-w-xs leading-relaxed">
              Realtime event platform to seamlessly manage, explore, and participate in live conference experiences.
            </p>
          </div>

          <div className="grid grid-cols-2 gap-12 sm:gap-20 text-sm">
            <div className="flex flex-col gap-3.5">
              <span className="text-xs font-bold text-[#b4ff39] tracking-wider uppercase">
                Platform
              </span>
              <Link href="/events" className="hover:text-white transition-colors duration-200 text-gray-400">
                Events
              </Link>
              <Link href="/live" className="hover:text-white transition-colors duration-200 text-gray-400 flex items-center gap-2">
                Live Sessions
                <span className="w-1.5 h-1.5 rounded-full bg-red-500 animate-pulse"></span>
              </Link>
            </div>

            <div className="flex flex-col gap-3.5">
              <span className="text-xs font-bold text-[#b4ff39] tracking-wider uppercase">
                Discover
              </span>
              <Link href="/speakers" className="hover:text-white transition-colors duration-200 text-gray-400">
                Speakers
              </Link>
              <Link href="/rooms" className="hover:text-white transition-colors duration-200 text-gray-400">
                Rooms
              </Link>
            </div>
          </div>

        </div>

        <div className="border-t border-white/5 pt-6 flex flex-col sm:flex-row justify-between items-center gap-4 text-xs text-gray-500">
          <div className="tracking-wide">
            © {currentYear} EventSync. All rights reserved.
          </div>
          <div className="flex gap-6">
            <Link href="/privacy" className="hover:text-white transition-colors duration-200">
              Privacy Policy
            </Link>
            <Link href="/terms" className="hover:text-white transition-colors duration-200">
              Terms of Service
            </Link>
          </div>
        </div>

      </div>
    </footer>
  );
}