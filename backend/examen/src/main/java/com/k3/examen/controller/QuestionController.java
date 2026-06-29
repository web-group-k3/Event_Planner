
package com.k3.examen.controller;

import com.k3.examen.exception.ResourceNotFoundException;
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
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<List<Question>> getBySession(
            @RequestParam String sessionId,
            @RequestHeader(value="X-Anonymous-Id",required=false)
            String anonymousId,

            @RequestHeader(value="X-Fingerprint",required=false)
            String fingerprintId
    ) {

        return ResponseEntity.ok(

                questionService.getQuestionsBySession(
                        sessionId,
                        anonymousId,
                        fingerprintId
                )

        );
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
   /* @PostMapping("/{id}/upvote")
    public ResponseEntity<Map<String, String>> upvote(
            @PathVariable String id){
        questionService.upvote(id);
        return ResponseEntity.ok(Map.of("message", "Upvote enregistré"));
    }*/

   @PostMapping("/{id}/upvote")
   public ResponseEntity<?> upvote(
           @PathVariable String id,
           @RequestHeader(value = "X-Anonymous-Id", required = false) String anonymousId,
           @RequestHeader(value = "X-Fingerprint", required = false) String fingerprintId
   ) {

       if ((anonymousId == null || anonymousId.isBlank()) && (fingerprintId == null || fingerprintId.isBlank())) {
           return ResponseEntity.badRequest()
                   .body(Map.of("error", "Identifiant anonyme ou empreinte de l'appareil requis"));
       }

       try {
           questionService.upvote(id, anonymousId, fingerprintId);
           return ResponseEntity.ok().build();
       } catch (IllegalStateException e) {
           return ResponseEntity.status(409)
                   .body(Map.of("error", e.getMessage()));
       } catch (ResourceNotFoundException e) {
           return ResponseEntity.notFound().build();
       }
   }

    @PostMapping("/sessions/{sessionId}/questions")
    public ResponseEntity<?> createQuestion(
            @PathVariable String sessionId,
            @RequestBody Question question,
            @RequestHeader(value = "X-Anonymous-Id", required = false) String anonymousId
    ) {
        if (anonymousId == null || anonymousId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Identifiant anonyme requis"));
        }
        try {
            return ResponseEntity.ok(
                    questionService.createQuestion(sessionId, question)
            );
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}