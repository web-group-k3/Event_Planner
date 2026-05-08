package com.k3.examen.service.impl;

import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Question;
import com.k3.examen.model.Session;
import com.k3.examen.repository.QuestionRepository;
import com.k3.examen.repository.SessionRepository;
import com.k3.examen.service.QuestionService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

//@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final SessionRepository sessionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, SessionRepository sessionRepository) {
        this.questionRepository = questionRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<Question> getQuestionsBySession(String sessionId) {
        return questionRepository.findBySessionId(sessionId);
    }

    @Override
    public Question createQuestion(String sessionId, Question question) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("session not found: " + sessionId));
        if (!session.isLive()){
            throw new ResourceNotFoundException("session is not live wait the begining of the session");
        }
        if (question.getContent() == null || question.getContent().isBlank()){
            throw new ResourceNotFoundException("question is empty");
        }
        question.setSessionId(sessionId);
        return questionRepository.save(question);
    }

    @Override
    public void upvote(String questionId) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("question not found: " + questionId));
        questionRepository.upvote(questionId);
    }

    @Override
    public void deleteQuestion(String id) {
        questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("question not found: " + id));
        questionRepository.delete(id);

    }
}