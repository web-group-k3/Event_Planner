package com.k3.examen.controller;

import com.k3.examen.dto.EventDto;
import com.k3.examen.model.Event;
import com.k3.examen.service.EventService;
import com.k3.examen.service.impl.EventServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final EventServiceImpl eventServiceImpl;
    public EventController(EventService eventService, EventServiceImpl eventServiceImpl) {
        this.eventService = eventService;
        this.eventServiceImpl = eventServiceImpl;
    }
    @GetMapping
    public ResponseEntity<List<Event>> findAll()  {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Event> findById(@PathVariable String id) {
        return ResponseEntity.ok(eventService.getEventWithSessions(id));
    }
    @GetMapping("/byRoom/{roomId}")
    public ResponseEntity<List<Event>> getByRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(eventService.getEventsByRoom(roomId));
    }
    @GetMapping("/bySpeaker/{speakerId}")
    public ResponseEntity<List<Event>> getBySpeaker(@PathVariable String speakerId) {
        return ResponseEntity.ok(eventService.getEventsBySpeaker(speakerId));
    }
    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return ResponseEntity.status(201).body(eventServiceImpl.createEvent(event));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable String id, @RequestBody Event event)  {
        return ResponseEntity.ok(eventServiceImpl.updateEvent(id, event));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id)  {
        eventServiceImpl.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
