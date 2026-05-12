
package com.k3.examen.controller;

import com.k3.examen.model.Question;
import com.k3.examen.service.QuestionService;
import com.k3.examen.service.impl.QuestionServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:3000")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<List<Question>> getBySession(@RequestParam String sessionId) {
        return ResponseEntity.ok(questionService.getQuestionsBySession(sessionId));
    }

    @PostMapping
    public ResponseEntity<Question> create(
            @RequestParam String sessionId,
            @RequestBody Question question) {
        return ResponseEntity.status(201).body(questionService.createQuestion(sessionId, question));
    }
    @PatchMapping("/{id}/content")
    public ResponseEntity<Void> updateContent(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        questionService.updateContent(id, body.get("content"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/upvote")
    public ResponseEntity<Map<String, String>> upvote(
            @PathVariable String id){
        questionService.upvote(id);
        return ResponseEntity.ok(Map.of("message", "Upvote enregistré"));
    }
}