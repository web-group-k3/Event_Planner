import HeroSection from "@/components/HeroSection";
import QuestionSection from "@/components/QuestionSection";
import FavoriteButton from "@/components/FavoriteButton";

export default function HomePage() {
  return (
    <main className="bg-[#050816] min-h-screen text-white pb-24">
      <HeroSection />
      
      <div className="container-app mt-12 max-w-4xl mx-auto px-6">
        <div className="border border-lime-500/30 bg-lime-500/5 rounded-3xl p-6 mb-8 flex items-center justify-between">
          <div>
            <h3 className="text-lg font-bold text-lime-400">Zone de Test (Question & Favoris)</h3>
            <p className="text-sm text-gray-400 mt-1">
              Cette zone vous permet de tester vos composants avec l'identifiant <code className="text-lime-300">session-1</code>.
            </p>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-xs text-gray-400 font-bold uppercase tracking-wider">Favori :</span>
            <FavoriteButton id="session-1" type="session" label="Introduction à Next.js 16" />
          </div>
        </div>

        <QuestionSection sessionId="session-1" />
      </div>
    </main>
  );
}
