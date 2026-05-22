import Link from "next/link";

export default function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="border-t border-white/10 bg-[#070a14] text-gray-400 py-10 mt-auto">
      <div className="container mx-auto px-6">
        
        <div className="flex flex-col md:flex-row justify-between items-center gap-6">
          
          <div className="flex items-center gap-3">
            <div className="w-7 h-4 rounded-xl bg-gradient-to-br from-violet-500 to-cyan-500 flex items-center justify-center font-bold text-white text-xs">
              E
            </div>
            <span className="text-lg font-semibold text-white tracking-tight">
              EventSync
            </span>
          </div>

          {/* Liens rapides */}
          {/* <div className="flex gap-6 text-sm">
            <Link href="/about" className="hover:text-white transition-colors">
              À propos
            </Link>
            <Link href="/contact" className="hover:text-white transition-colors">
              Contact
            </Link>
            <Link href="/privacy" className="hover:text-white transition-colors">
              Confidentialité
            </Link>
            <Link href="/terms" className="hover:text-white transition-colors">
              Conditions
            </Link>
          </div> */}

          <div className="text-xs text-gray-500">
            © {currentYear} EventSync. Tous droits réservés.
          </div>

        </div>
      </div>
    </footer>
  );
}