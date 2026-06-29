package com.k3.examen.service.impl;

import com.k3.examen.dto.RoomDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Room;
import com.k3.examen.model.RoomUpdateRequest;
import com.k3.examen.repository.EventRepository;
import com.k3.examen.repository.RoomRepository;
import com.k3.examen.repository.SpeakerRepository;
import com.k3.examen.repository.impl.EventRepositoryImpl;
import com.k3.examen.service.RoomService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private final SpeakerRepository speakerRepository;
    public RoomServiceImpl(RoomRepository roomRepository,
                           EventRepositoryImpl eventRepository,
                           SpeakerRepository speakerRepository) {
        this.roomRepository = roomRepository;
        this.eventRepository = eventRepository;
        this.speakerRepository = speakerRepository;
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

        if (room.getName() == null || room.getName().isBlank()) {
            throw new IllegalArgumentException("room name cannot be empty");
        }
        if (room.getAdress() == null || room.getAdress().isBlank()) {
            throw new IllegalArgumentException("room address cannot be empty");
        }
        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(String id, RoomUpdateRequest request) {
        getRoomById(id);
        return roomRepository.update(id, request);
    }

    @Override
    public void deleteRoom(String id) {
        getRoomById(id);
        roomRepository.delete(id);
    }

    @Override
    public List<Room> getRoomsByEvent(String eventId) {
        eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return roomRepository.findByEventId(eventId);
    }

    @Override
    public List<Room> getRoomsBySpeaker(String speakerId) {
        speakerRepository.findById(speakerId)
                .orElseThrow(() -> new ResourceNotFoundException("Speaker not found"));
        return roomRepository.findBySpeakerId(speakerId);
    }

    @Override
    public List<Room> getRoomsByAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address cannot be empty");

        }
        return roomRepository.findByAddress(address);
    }
}
