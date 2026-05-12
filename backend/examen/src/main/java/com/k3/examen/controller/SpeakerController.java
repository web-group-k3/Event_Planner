package com.k3.examen.controller;

import com.k3.examen.model.Speaker;
import com.k3.examen.service.SpeakerService;
import com.k3.examen.service.impl.SpeakerServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/speakers")
@CrossOrigin(origins = "http://localhost:3000")
public class SpeakerController {

    private final SpeakerService speakerService;

    public SpeakerController(SpeakerServiceImpl speakerService) {
        this.speakerService = speakerService;
    }

    @GetMapping
    public ResponseEntity<List<Speaker>> getAll()  {
        return ResponseEntity.ok(speakerService.getAllSpeakers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Speaker> getById(@PathVariable String id) {
        return ResponseEntity.ok(speakerService.getSpeakerWithSessions(id));
    }
    @GetMapping("/byRoom/{roomId}")
    public ResponseEntity<List<Speaker>> getByRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(speakerService.getSpeakersByRoom(roomId));
    }
    @GetMapping("/byEvent/{eventId}")
    public ResponseEntity<List<Speaker>> getByEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(speakerService.getSpeakersByEvent(eventId));
    }
    @PostMapping
    public ResponseEntity<Speaker> create(@RequestBody Speaker speaker) {
        return ResponseEntity.status(201).body(speakerService.createSpeaker(speaker));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Speaker> update(@PathVariable String id, @RequestBody Speaker speaker) {
        return ResponseEntity.ok(speakerService.updateSpeaker(id, speaker));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        speakerService.deleteSpeaker(id);
        return ResponseEntity.noContent().build();
    }

}