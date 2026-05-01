package com.k3.examen.controller;

import com.k3.examen.dto.CreateRoomRequest;
import com.k3.examen.dto.RoomDTO;
import com.k3.examen.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/rooms")
public class RoomController {
    
    private final RoomService roomService;
    
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    
    // GET /api/events/{eventId}/rooms - Get all rooms for an event
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getRoomsByEventId(@PathVariable Integer eventId) {
        List<RoomDTO> rooms = roomService.getRoomsByEventId(eventId);
        return ResponseEntity.ok(rooms);
    }
    
    // GET /api/events/{eventId}/rooms/{roomId} - Get a specific room
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(
            @PathVariable Integer eventId,
            @PathVariable Integer roomId) {
        RoomDTO room = roomService.getRoomById(roomId);
        // Verify room belongs to the event
        if (!room.getEventId().equals(eventId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }
    
    // POST /api/events/{eventId}/rooms - Create a new room
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(
            @PathVariable Integer eventId,
            @Valid @RequestBody CreateRoomRequest request) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName(request.getName());
        roomDTO.setEventId(eventId);
        
        RoomDTO createdRoom = roomService.createRoom(roomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }
    
    // PUT /api/events/{eventId}/rooms/{roomId} - Update a room
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(
            @PathVariable Integer eventId,
            @PathVariable Integer roomId,
            @Valid @RequestBody CreateRoomRequest request) {
        // Verify room exists and belongs to event
        RoomDTO existingRoom = roomService.getRoomById(roomId);
        if (!existingRoom.getEventId().equals(eventId)) {
            return ResponseEntity.notFound().build();
        }
        
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(roomId);
        roomDTO.setName(request.getName());
        roomDTO.setEventId(eventId);
        
        RoomDTO updatedRoom = roomService.updateRoom(roomId, roomDTO);
        return ResponseEntity.ok(updatedRoom);
    }
    
    // DELETE /api/events/{eventId}/rooms/{roomId} - Delete a room
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Integer eventId,
            @PathVariable Integer roomId) {
        // Verify room exists and belongs to event
        try {
            RoomDTO existingRoom = roomService.getRoomById(roomId);
            if (!existingRoom.getEventId().equals(eventId)) {
                return ResponseEntity.notFound().build();
            }
            roomService.deleteRoom(roomId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
