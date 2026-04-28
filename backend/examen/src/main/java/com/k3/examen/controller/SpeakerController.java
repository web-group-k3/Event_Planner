package com.k3.examen.controller;

import com.k3.examen.model.Speaker;
import com.k3.examen.service.SpeakerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/speakers")
public class SpeakerController {

    private final SpeakerService speakerService;

    public SpeakerController(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }

    @GetMapping
    public ResponseEntity<List<Speaker>> getAll() throws SQLException {
        return ResponseEntity.ok(speakerService.getAll());
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Speaker>> getByEvent(@PathVariable Long eventId) throws SQLException {
        return ResponseEntity.ok(speakerService.getByEvent(eventId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Speaker> getDetail(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(speakerService.getDetail(id));
    }

    @PostMapping
    public ResponseEntity<Speaker> create(@RequestBody Speaker speaker) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(speakerService.create(speaker));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Speaker> update(@PathVariable Long id, @RequestBody Speaker speaker) throws SQLException {
        return ResponseEntity.ok(speakerService.update(id, speaker));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws SQLException {
        speakerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}