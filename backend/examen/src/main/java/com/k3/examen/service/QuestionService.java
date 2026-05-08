package com.k3.examen.service;

import com.k3.examen.model.Question;

import java.util.List;

public interface QuestionService {
    List<Question> getQuestionsBySession(String sessionId);
    Question createQuestion(String sessionId, Question question);
    void upvote(String questionId);
    void deleteQuestion(String id);
}
