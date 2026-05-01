package com.k3.examen.controller;

import com.k3.examen.dto.CreateQuestionRequest;
import com.k3.examen.dto.QuestionDTO;
import com.k3.examen.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/sessions/{sessionId}/questions")
public class QuestionController {
    
    private final QuestionService questionService;
    
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
    
    // GET /api/events/{eventId}/sessions/{sessionId}/questions - Get all questions for a session
    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getQuestionsBySessionId(
            @PathVariable Integer eventId,
            @PathVariable Integer sessionId) {
        List<QuestionDTO> questions = questionService.getQuestionsBySessionId(sessionId);
        return ResponseEntity.ok(questions);
    }
    
    // POST /api/events/{eventId}/sessions/{sessionId}/questions - Create a question
    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(
            @PathVariable Integer eventId,
            @PathVariable Integer sessionId,
            @Valid @RequestBody CreateQuestionRequest request) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setContent(request.getContent());
        questionDTO.setAuthorName(request.getAuthorName());
        
        QuestionDTO createdQuestion = questionService.createQuestion(sessionId, questionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }
    
    // POST /api/events/{eventId}/sessions/{sessionId}/questions/{questionId}/upvote - Upvote a question
    @PostMapping("/{questionId}/upvote")
    public ResponseEntity<QuestionDTO> upvoteQuestion(
            @PathVariable Integer eventId,
            @PathVariable Integer sessionId,
            @PathVariable Integer questionId) {
        QuestionDTO upvotedQuestion = questionService.upvoteQuestion(sessionId, questionId);
        return ResponseEntity.ok(upvotedQuestion);
    }
}
