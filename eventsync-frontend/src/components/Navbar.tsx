"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Wifi, Menu, X } from "lucide-react";
import { useState } from "react";

const NAV_LINKS = [
  { href: "/events",        label: "Events" },
  { href: "/sessions/live", label: "Live",    live: true },
  { href: "/speakers",      label: "Speakers" },
  { href: "/rooms",         label: "Rooms" },
];

export default function Navbar() {

  const pathname = usePathname();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-white/08 backdrop-blur-xl bg-[#050816]/80">

      <div className="container-app h-20 flex items-center justify-between">

        {/* Logo */}
        <Link href="/" className="flex items-center gap-3 group">

          <div className="w-10 h-10 rounded-2xl bg-linear-to-br from-[#a3ff12] to-[#38bdf8] flex items-center justify-center font-black text-lg text-black shadow-[0_0_20px_rgba(163,255,18,0.3)] group-hover:shadow-[0_0_30px_rgba(163,255,18,0.5)] transition-all">
            E
          </div>

          <div>
            <h1 className="text-xl font-black">EventSync</h1>
            <p className="text-xs text-gray-500">Realtime Event Platform</p>
          </div>

        </Link>

        {/* Desktop nav */}
        <nav className="hidden md:flex items-center gap-1">
          {NAV_LINKS.map(({ href, label, live }) => {
            const active = pathname === href || pathname.startsWith(href + "/");
            return (
              <Link
                key={href}
                href={href}
                className={`
                  relative flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200
                  ${active
                    ? "text-white bg-white/08"
                    : "text-gray-400 hover:text-white hover:bg-white/05"
                  }
                `}
              >
                {live && (
                  <Wifi className="w-3.5 h-3.5 text-[#ff4d6d]" />
                )}
                {label}
                {active && (
                  <span className="absolute bottom-0 left-1/2 -translate-x-1/2 w-4 h-0.5 rounded-full bg-[#a3ff12]" />
                )}
              </Link>
            );
          })}
        </nav>

        {/* Right */}
        <div className="flex items-center gap-3">

          <Link href="/admin/login" className="hidden md:block button-primary text-sm px-5 py-2.5">
            Admin Login
          </Link>

          {/* Mobile menu toggle */}
          <button
            className="md:hidden w-9 h-9 rounded-xl glass border border-white/10 flex items-center justify-center"
            onClick={() => setMenuOpen((v) => !v)}
          >
            {menuOpen ? <X className="w-4 h-4" /> : <Menu className="w-4 h-4" />}
          </button>

        </div>

      </div>

      {/* Mobile menu */}
      {menuOpen && (
        <div className="md:hidden border-t border-white/08 bg-[#050816]/95 backdrop-blur-xl px-6 py-4 flex flex-col gap-1">
          {NAV_LINKS.map(({ href, label, live }) => {
            const active = pathname === href;
            return (
              <Link
                key={href}
                href={href}
                onClick={() => setMenuOpen(false)}
                className={`
                  flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all
                  ${active ? "text-white bg-white/08" : "text-gray-400 hover:text-white hover:bg-white/05"}
                `}
              >
                {live && <Wifi className="w-3.5 h-3.5 text-[#ff4d6d]" />}
                {label}
              </Link>
            );
          })}
          <div className="mt-2 pt-2 border-t border-white/08">
            <Link href="/admin/login" onClick={() => setMenuOpen(false)} className="button-primary w-full text-center text-sm block">
              Admin Login
            </Link>
          </div>
        </div>
      )}

    </header>
  );
}