package com.k3.examen.repository;

import com.k3.examen.model.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {
    List<Question> findBySessionId(String sessionId);
    Optional<Question> findById(String id);
    Question save(Question question);
    void upvote(String id);
    void delete(String id);
    void updateContent(String id, String newContent);
    boolean hasVoted(String questionId, String anonymousId, String fingerprintId);
    void addVote(String questionId, String anonymousId, String fingerprintId);
}