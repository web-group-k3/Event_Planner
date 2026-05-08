package com.k3.examen.service.impl;

import com.k3.examen.dto.RoomDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Room;
import com.k3.examen.repository.RoomRepository;
import com.k3.examen.service.RoomService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }
    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoomById(String id) {
        return roomRepository.findRoomById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @Override
    public Room createRoom(Room room) {
        if (roomRepository.findRoomById(room.getId()).isPresent()){
            throw new ResourceNotFoundException("Room already exists");
        }
        if (room.getName() == null || room.getName().isBlank()) {
            throw new IllegalArgumentException("room name cannot be empty");
        }
        if (room.getAddress() == null || room.getAddress().isBlank()) {
            throw new IllegalArgumentException("room address cannot be empty");
        }
        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(String id, Room room) {
        getRoomById(id);
        room.setId(id);
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(String id) {
        getRoomById(id);
        roomRepository.delete(id);
    }
}
