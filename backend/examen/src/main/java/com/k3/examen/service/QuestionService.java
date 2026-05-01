package com.k3.examen.service;

import com.k3.examen.dto.QuestionDTO;
import com.k3.examen.model.Question;
import com.k3.examen.repository.QuestionRepository;
import com.k3.examen.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final SessionRepository sessionRepository;
    
    public QuestionService(QuestionRepository questionRepository, SessionRepository sessionRepository) {
        this.questionRepository = questionRepository;
        this.sessionRepository = sessionRepository;
    }
    
    public QuestionDTO createQuestion(Integer sessionId, QuestionDTO questionDTO) {
        // Check if session exists
        if (!sessionRepository.existsById(sessionId)) {
            throw new RuntimeException("Session not found with id: " + sessionId);
        }
        
        // Check if session is live (only allow questions during live session)
        // For now, we'll allow questions if session exists
        // In production, you'd check if session is currently live
        
        Question question = new Question(
                questionDTO.getContent(),
                questionDTO.getAuthorName(),
                sessionId
        );
        
        Question savedQuestion = questionRepository.save(question);
        return convertToDTO(savedQuestion);
    }
    
    public List<QuestionDTO> getQuestionsBySessionId(Integer sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new RuntimeException("Session not found with id: " + sessionId);
        }
        
        return questionRepository.findBySessionId(sessionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public QuestionDTO upvoteQuestion(Integer sessionId, Integer questionId) {
        // Verify question belongs to session
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
        
        if (!question.getSessionId().equals(sessionId)) {
            throw new RuntimeException("Question does not belong to session");
        }
        
        Question upvotedQuestion = questionRepository.upvote(questionId);
        return convertToDTO(upvotedQuestion);
    }
    
    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setAuthorName(question.getAuthorName());
        dto.setUpvotes(question.getUpvotes());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setSessionId(question.getSessionId());
        return dto;
    }
}
