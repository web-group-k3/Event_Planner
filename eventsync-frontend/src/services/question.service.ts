import api from "@/lib/axios";

export interface Question {
  id: string;
  content: string;
  authorName: string;
  upvotes: number;
  createdAt: string;
  sessionId: string;
  hasVoted: boolean;
}
export async function getQuestionsBySession(
  sessionId: string
): Promise<Question[]> {
  const res = await api.get<Question[]>(
    `/questions?sessionId=${sessionId}`
  );

  return res.data;
}
export async function createQuestion(
  sessionId: string,
  content: string,
  authorName: string
): Promise<Question> {
  const generatedId = "q_" + Math.random().toString(36).substring(2, 15);
  const res = await api.post<Question>(`/questions?sessionId=${sessionId}`, {
    id: generatedId,
    content,
    authorName: authorName || "Anonyme",
  });
  return res.data;
}
export async function upvoteQuestion(id: string): Promise<void> {
  // 1. Récupérer l'identifiant stocké (ou génère-le s'il n'existe pas encore)
  let anonymousId = localStorage.getItem("anonymous_id");
  if (!anonymousId) {
    anonymousId = "anon_" + Math.random().toString(36).substring(2, 15);
    localStorage.setItem("anonymous_id", anonymousId);
  }

  // 2. Utiliser une valeur par défaut pour le fingerprint pour tes tests locaux
  // (En production, tu pourras utiliser une vraie lib comme FingerprintJS)
  const fingerprintId = "fp_local_device"; 

  // 3. Envoyer la requête avec les en-têtes personnalisés demandés par ton Spring Boot
  await api.post(`/questions/${id}/upvote`, {}, {
    headers: {
      "X-Anonymous-Id": anonymousId,
      "X-Fingerprint": fingerprintId
    }
  });
}
export async function updateQuestionContent(id: string, content: string): Promise<void> {
  await api.patch(`/questions/${id}/content`, { content });
}
export async function deleteQuestion(id: string): Promise<void> {
  await api.delete(`/questions/${id}`);
}