
package com.k3.examen.controller;

import com.k3.examen.model.Question;
import com.k3.examen.service.impl.QuestionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

//@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionServiceImpl questionServiceImpl;

    public QuestionController(QuestionServiceImpl questionServiceImpl) {
        this.questionServiceImpl = questionServiceImpl;
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<Question>> getBySession(@PathVariable Long sessionId) throws SQLException {
        return ResponseEntity.ok(questionServiceImpl.getBySession(sessionId));
    }

    @PostMapping
    public ResponseEntity<Question> create(@RequestBody Question question) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionServiceImpl.create(question));
    }

    @PostMapping("/{id}/upvote")
    public ResponseEntity<Void> upvote(@PathVariable Long id) throws SQLException {
        questionServiceImpl.upvote(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws SQLException {
        questionServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}