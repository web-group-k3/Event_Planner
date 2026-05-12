package com.k3.examen.controller;

import com.k3.examen.dto.SessionDto;
import com.k3.examen.model.Session;
import com.k3.examen.service.SessionService;
import com.k3.examen.service.impl.SessionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "http://localhost:3000")
public class SessionController {
    private final SessionService sessionService;
    public SessionController(SessionService sessionService) {
        this.sessionService= sessionService;
    }
    @GetMapping
    public ResponseEntity<List<Session>> getAll() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }
    @GetMapping("/{sessionId}")
    public ResponseEntity<Session> getDetail(@PathVariable String sessionId) {
        return ResponseEntity.ok(sessionService.getSessionDetail(sessionId));
    }
    @GetMapping("/byEvent/{eventId}")
    public ResponseEntity<List<Session>> getByEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(sessionService.getSessionsByEvent(eventId));
    }
    @GetMapping("/byRoom/{roomId}")
    public ResponseEntity<List<Session>> getByRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(sessionService.getSessionsByRoom(roomId));
    }
    @GetMapping("/{eventId}/{roomId}")
    public ResponseEntity<List<Session>> getByEventAndRoom(
            @PathVariable String eventId,
            @PathVariable String roomId) {
        return ResponseEntity.ok(sessionService.getSessionsByEventAndRoom(eventId, roomId));
    }
    @PostMapping
    public ResponseEntity<Session> create(@RequestBody Session session) {
        return ResponseEntity.status(201).body(sessionService.createSession(session));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Session> update(@PathVariable String id, @RequestBody Session session) {
        return ResponseEntity.ok(sessionService.updateSession(id, session));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/speakers/{speakerId}")
    public ResponseEntity<Void> addSpeaker(
            @PathVariable String id,
            @PathVariable String speakerId) {
        sessionService.addSpeakerToSession(id, speakerId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}/speakers/{speakerId}")
    public ResponseEntity<Void> removeSpeaker(
            @PathVariable String id,
            @PathVariable String speakerId) {
        sessionService.removeSpeakerFromSession(id, speakerId);
        return ResponseEntity.noContent().build();
    }
}
