import api from "@/lib/axios";

export interface Question {
  id: string;
  content: string;
  authorName: string;
  upvotes: number;
  createdAt: string;
  sessionId: string;
}

// Récupérer les questions d'une session
export async function getQuestionsBySession(sessionId: string): Promise<Question[]> {
  const res = await api.get<Question[]>(`/questions?sessionId=${sessionId}`);
  return res.data;
}

// Créer une nouvelle question (génère un ID côté client)
export async function createQuestion(
  sessionId: string,
  content: string,
  authorName: string
): Promise<Question> {
  // Génération d'un ID de 15 caractères commençant par 'q_' pour respecter VARCHAR(20)
  const generatedId = "q_" + Math.random().toString(36).substring(2, 15);
  
  const res = await api.post<Question>(`/questions?sessionId=${sessionId}`, {
    id: generatedId,
    content,
    authorName: authorName || "Anonyme",
  });
  return res.data;
}

// Upvoter une question
export async function upvoteQuestion(id: string): Promise<void> {
  await api.post(`/questions/${id}/upvote`);
}

// Modifier le contenu d'une question (réservé admin)
export async function updateQuestionContent(id: string, content: string): Promise<void> {
  await api.patch(`/questions/${id}/content`, { content });
}

// Supprimer une question (réservé admin)
export async function deleteQuestion(id: string): Promise<void> {
  await api.delete(`/questions/${id}`);
}