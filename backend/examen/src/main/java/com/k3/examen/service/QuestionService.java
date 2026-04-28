package com.k3.examen.service;

import com.k3.examen.model.Question;
import com.k3.examen.repository.QuestionRepository;
import com.k3.examen.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SessionRepository sessionRepository;

    public QuestionService(QuestionRepository questionRepository, SessionRepository sessionRepository) {
        this.questionRepository = questionRepository;
        this.sessionRepository = sessionRepository;
    }

    public List<Question> getBySession(Long sessionId) throws SQLException {
        return questionRepository.findBySessionId(sessionId);
    }

    public Question create(Question question) throws SQLException {
        if (!sessionRepository.isLive(question.getSessionId())) {
            throw new IllegalStateException("Questions acceptées uniquement pendant une session live.");
        }
        return questionRepository.save(question);
    }

    public void upvote(Long id) throws SQLException {
        questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question non trouvée : " + id));
        questionRepository.upvote(id);
    }

    public void delete(Long id) throws SQLException {
        int rows = questionRepository.delete(id);
        if (rows == 0) throw new RuntimeException("Question non trouvée : " + id);
    }
}