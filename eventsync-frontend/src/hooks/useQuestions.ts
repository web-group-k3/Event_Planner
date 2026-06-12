// src/hooks/useQuestions.ts

import { useState, useEffect, useCallback, useRef } from "react";
import {
  getQuestionsBySession,
  createQuestion,
  upvoteQuestion,
  deleteQuestion,
  updateQuestionContent,
  Question,
} from "@/services/question.service";

export function useQuestions(sessionId: string) {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Réf pour éviter d'exécuter des requêtes sur un composant démonté
  const isMounted = useRef(true);

  const fetchQuestions = useCallback(async (silent = false) => {
    if (!sessionId) return;
    try {
      if (!silent) setLoading(true);
      const data = await getQuestionsBySession(sessionId);
      
      if (isMounted.current) {
        // Trier par upvotes décroissant, puis par date de création décroissante (les plus récentes en premier si upvotes égaux)
        const sorted = data.sort((a, b) => {
          if (b.upvotes !== a.upvotes) {
            return b.upvotes - a.upvotes;
          }
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        });
        setQuestions(sorted);
        setError(null);
      }
    } catch (err) {
      if (isMounted.current && !silent) {
        setError("Impossible de charger les questions");
      }
    } finally {
      if (isMounted.current && !silent) {
        setLoading(false);
      }
    }
  }, [sessionId]);

  useEffect(() => {
    isMounted.current = true;
    fetchQuestions(false);

    // Polling toutes les 5 secondes pour les questions live
    const interval = setInterval(() => {
      fetchQuestions(true);
    }, 5000);

    return () => {
      isMounted.current = false;
      clearInterval(interval);
    };
  }, [sessionId, fetchQuestions]);

  const addQuestion = async (content: string, authorName: string) => {
    try {
      const newQuestion = await createQuestion(sessionId, content, authorName);
      if (isMounted.current) {
        setQuestions((prev) => [newQuestion, ...prev].sort((a, b) => {
          if (b.upvotes !== a.upvotes) return b.upvotes - a.upvotes;
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        }));
      }
    } catch (err) {
      throw new Error("Erreur lors de la création de la question");
    }
  };

  const upvote = async (id: string) => {
    try {
      await upvoteQuestion(id);
      if (isMounted.current) {
        setQuestions((prev) =>
          prev
            .map((q) => (q.id === id ? { ...q, upvotes: q.upvotes + 1 } : q))
            .sort((a, b) => {
              if (b.upvotes !== a.upvotes) return b.upvotes - a.upvotes;
              return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
            })
        );
      }
    } catch (err) {
      console.error("Erreur lors du upvote:", err);
    }
  };

  const remove = async (id: string) => {
    try {
      await deleteQuestion(id);
      if (isMounted.current) {
        setQuestions((prev) => prev.filter((q) => q.id !== id));
      }
    } catch (err) {
      console.error("Erreur lors de la suppression:", err);
      throw err;
    }
  };

  const updateContent = async (id: string, content: string) => {
    try {
      await updateQuestionContent(id, content);
      if (isMounted.current) {
        setQuestions((prev) =>
          prev.map((q) => (q.id === id ? { ...q, content } : q))
        );
      }
    } catch (err) {
      console.error("Erreur lors de la modification:", err);
      throw err;
    }
  };

  return {
    questions,
    loading,
    error,
    addQuestion,
    upvote,
    remove,
    updateContent,
    refresh: () => fetchQuestions(false),
  };
}