"use client";

import { useState, useEffect } from "react";
import { useQuestions } from "@/hooks/useQuestions";
import { MessageSquare, Send, Trash2, Edit2, ThumbsUp, X, Check } from "lucide-react";

interface QuestionSectionProps {
  sessionId: string;
}

export default function QuestionSection({ sessionId }: QuestionSectionProps) {
  const { questions, loading, error, addQuestion, upvote, remove, updateContent } =
    useQuestions(sessionId);

  const [content, setContent] = useState("");
  const [authorName, setAuthorName] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  // Gestion de l'édition d'une question par l'admin
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editingContent, setEditingContent] = useState("");

  // Vérifier si l'utilisateur est admin
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    // Dans Next.js, on vérifie localStorage uniquement côté client
    const token = localStorage.getItem("token");
    if (token) {
      setIsAdmin(true);
    }
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!content.trim()) {
      setFormError("La question ne peut pas être vide.");
      return;
    }
    try {
      setSubmitting(true);
      setFormError(null);
      await addQuestion(content.trim(), authorName.trim() || "Anonyme");
      setContent("");
      setAuthorName("");
    } catch {
      setFormError("Erreur lors de l'envoi de la question. Veuillez réessayer.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleStartEdit = (id: string, currentText: string) => {
    setEditingId(id);
    setEditingContent(currentText);
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditingContent("");
  };

  const handleSaveEdit = async (id: string) => {
    if (!editingContent.trim()) return;
    try {
      await updateContent(id, editingContent.trim());
      setEditingId(null);
      setEditingContent("");
    } catch {
      alert("Erreur lors de la modification de la question.");
    }
  };

  return (
    <section className="mt-12 glass rounded-3xl p-6 lg:p-8 relative overflow-hidden">
      {/* Background glow decorator */}
      <div className="absolute top-0 right-0 w-48 h-48 bg-sky-400/5 blur-[80px] pointer-events-none" />

      <div className="flex items-center gap-3 mb-8">
        <div className="w-10 h-10 rounded-xl bg-sky-500/10 flex items-center justify-center text-sky-400">
          <MessageSquare className="w-5 h-5" />
        </div>
        <div>
          <h2 className="text-xl font-bold text-white">Questions & Réponses</h2>
          <p className="text-xs text-gray-400 mt-0.5">Posez vos questions en direct à l'intervenant</p>
        </div>
      </div>

      {/* Formulaire de soumission */}
      <form onSubmit={handleSubmit} className="bg-white/[0.02] border border-white/5 rounded-2xl p-5 mb-8 space-y-4">
        <div className="grid md:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-gray-400 mb-1.5 uppercase tracking-wider">
              Votre Nom
            </label>
            <input
              type="text"
              placeholder="Ex: Sophie Martin (ou Anonyme)"
              value={authorName}
              onChange={(e) => setAuthorName(e.target.value)}
              className="w-full bg-black/20 text-white placeholder-gray-500 rounded-xl border border-white/10 px-4 py-3 text-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 transition-all"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-400 mb-1.5 uppercase tracking-wider">
            Votre Question
          </label>
          <textarea
            placeholder="Posez votre question de manière claire et concise..."
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={3}
            className="w-full bg-black/20 text-white placeholder-gray-500 rounded-xl border border-white/10 px-4 py-3 text-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 transition-all resize-none"
          />
        </div>

        {formError && <p className="text-rose-400 text-xs font-medium">{formError}</p>}

        <div className="flex justify-end">
          <button
            type="submit"
            disabled={submitting}
            className="inline-flex items-center gap-2 bg-gradient-to-r from-sky-500 to-blue-600 hover:from-sky-400 hover:to-blue-500 disabled:opacity-50 text-white font-semibold px-6 py-3 rounded-xl text-sm transition-all duration-300 shadow-lg shadow-sky-500/10 cursor-pointer"
          >
            {submitting ? (
              "Envoi en cours..."
            ) : (
              <>
                <span>Envoyer la question</span>
                <Send className="w-4 h-4" />
              </>
            )}
          </button>
        </div>
      </form>

      {/* Liste des questions */}
      <div className="space-y-4">
        {loading && (
          <div className="flex flex-col items-center justify-center py-10 space-y-3">
            <div className="w-8 h-8 rounded-full border-2 border-sky-500/20 border-t-sky-400 animate-spin" />
            <p className="text-gray-400 text-sm">Chargement des questions...</p>
          </div>
        )}

        {error && (
          <div className="bg-rose-500/10 border border-rose-500/20 rounded-2xl p-4 text-center">
            <p className="text-rose-400 text-sm">{error}</p>
          </div>
        )}

        {!loading && questions.length === 0 && (
          <div className="border border-dashed border-white/10 rounded-2xl p-10 text-center">
            <p className="text-gray-500 text-sm italic">
              Aucune question pour le moment. Lancez la discussion !
            </p>
          </div>
        )}

        {questions.map((q) => (
          <div
            key={q.id}
            className="bg-white/[0.01] hover:bg-white/[0.02] border border-white/5 rounded-2xl p-5 flex items-start justify-between gap-5 transition-all duration-300"
          >
            <div className="flex-1">
              {editingId === q.id ? (
                <div className="space-y-3">
                  <textarea
                    value={editingContent}
                    onChange={(e) => setEditingContent(e.target.value)}
                    rows={2}
                    className="w-full bg-black/40 text-white rounded-xl border border-white/10 p-3 text-sm focus:outline-none focus:border-sky-500 transition-all resize-none"
                  />
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleSaveEdit(q.id)}
                      className="inline-flex items-center gap-1 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-colors cursor-pointer"
                    >
                      <Check className="w-3.5 h-3.5" />
                      Enregistrer
                    </button>
                    <button
                      onClick={handleCancelEdit}
                      className="inline-flex items-center gap-1 bg-white/10 hover:bg-white/15 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-colors cursor-pointer"
                    >
                      <X className="w-3.5 h-3.5" />
                      Annuler
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <p className="text-white text-sm leading-relaxed whitespace-pre-wrap">{q.content}</p>
                  <div className="flex items-center gap-2 mt-2">
                    <span className="text-gray-400 text-xs font-medium">{q.authorName}</span>
                    <span className="text-white/10 text-xs">•</span>
                    <span className="text-gray-500 text-xs">
                      {new Date(q.createdAt).toLocaleTimeString("fr-FR", {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </span>
                  </div>
                </>
              )}
            </div>

            <div className="flex items-center gap-3">
              {/* Upvote Button */}
              <button
                onClick={() => upvote(q.id)}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-gray-400 hover:text-[#a3ff12] hover:bg-[#a3ff12]/5 border border-transparent hover:border-[#a3ff12]/10 transition-all duration-300 cursor-pointer group"
              >
                <ThumbsUp className="w-4 h-4 transition-transform group-hover:-translate-y-0.5 group-active:scale-95" />
                <span className="text-xs font-bold text-gray-300 group-hover:text-[#a3ff12]">
                  {q.upvotes}
                </span>
              </button>

              {/* Admin Actions */}
              {isAdmin && (
                <div className="flex gap-1 border-l border-white/10 pl-2">
                  <button
                    onClick={() => handleStartEdit(q.id, q.content)}
                    className="p-2 text-gray-500 hover:text-white transition-colors cursor-pointer"
                    title="Modifier"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => {
                      if (confirm("Supprimer cette question ?")) {
                        remove(q.id);
                      }
                    }}
                    className="p-2 text-gray-500 hover:text-rose-400 transition-colors cursor-pointer"
                    title="Supprimer"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
