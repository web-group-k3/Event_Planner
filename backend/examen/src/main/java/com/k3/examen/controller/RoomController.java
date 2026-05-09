package com.k3.examen.controller;

import com.k3.examen.model.Room;
import com.k3.examen.model.RoomUpdateRequest;
import com.k3.examen.service.RoomService;
import com.k3.examen.service.impl.RoomServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private  final RoomService roomService;
    public RoomController(RoomServiceImpl roomService) {
        this.roomService = roomService;
    }
    @GetMapping
    public ResponseEntity<List<Room>> getAll() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Room> getById(@PathVariable String id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }
    @GetMapping("/byEvent/{eventId}")
    public ResponseEntity<List<Room>> getByEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(roomService.getRoomsByEvent(eventId));
    }
    @GetMapping("/bySpeaker/{speakerId}")
    public ResponseEntity<List<Room>> getBySpeaker(@PathVariable String speakerId) {
        return ResponseEntity.ok(roomService.getRoomsBySpeaker(speakerId));
    }
    @GetMapping("/byAdress")
    public ResponseEntity<List<Room>> getByAddress(@RequestParam String address) {
        return ResponseEntity.ok(roomService.getRoomsByAddress(address));
    }
    @PostMapping
    public ResponseEntity<Room> create(@RequestBody Room room) {
        return ResponseEntity.status(201).body(roomService.createRoom(room));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable String id, @RequestBody RoomUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
