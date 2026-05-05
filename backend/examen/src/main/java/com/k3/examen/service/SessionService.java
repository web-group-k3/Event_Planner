package com.k3.examen.service;

import com.k3.examen.dto.RoomDto;
import com.k3.examen.dto.SessionDto;
import com.k3.examen.dto.SpeakerDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Event;
import com.k3.examen.model.Room;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.repository.RoomRepository;
import com.k3.examen.repository.SessionRepository;
import com.k3.examen.repository.SpeakerRepository;
import com.k3.examen.validator.SessionValidator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionService {
    private final SessionRepository sessionRepository;
    private final RoomRepository roomRepository;
    private final SpeakerRepository speakerRepository;
    public SessionService(SessionRepository sessionRepository, RoomRepository roomRepository, SpeakerRepository speakerRepository) {
        this.sessionRepository = sessionRepository;
        this.roomRepository = roomRepository;
        this.speakerRepository = speakerRepository;
    }
    public List<SessionDto> findByEventId(String eventId,String roomId) throws SQLException {
        List<Session> sessions = roomId !=null
            ? sessionRepository.findByEventIdAndRoomId(eventId, roomId)
            : sessionRepository.findByEventId(eventId);

        return sessions.stream()
                .map(s -> toDto(s,true))
                .collect(Collectors.toList());

    }
    public SessionDto findById(String eventId,Long sessionId)   {
        Session session = sessionRepository.findById(sessionId);
        if (session != null || !session.getEventId().equals(eventId)) {
            throw new ResourceNotFoundException("Session not found");
            return toDto(session,true);
        }
        throw new ResourceNotFoundException("Session not found");
    }

    public SessionDto create(String eventId,SessionDto dto,List<String> SpeakerIds)  {
        SessionValidator.validate(dto);
        Session session = new Session();
        session.setTitle(dto.getTitle());
        session.setDescription(dto.getDescription());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setGuestNumber(dto.getGuestNumber());
        session.setEventId(eventId);
        session.setRoomId(dto.getRoomId());
        Session saved = sessionRepository.create(session,SpeakerIds);
        return toDto(saved, true);
    }
    public SessionDto update(String eventId,String sessionId,SessionDto dto,List<String> SpeakerIds) throws SQLException {
        SessionValidator.validate(dto);
        Session existing= sessionRepository.findById(sessionId);
        if(existing != null || !existing.getEventId().equals(eventId) ) {
            throw new ResourceNotFoundException("Session not found");
        }
        Session session = new Session();
        session.setTitle(dto.getTitle());
        session.setDescription(dto.getDescription());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setGuestNumber(dto.getGuestNumber());
        session.setRoomId(dto.getRoomId());
        Session updated= sessionRepository.update(sessionId,session,SpeakerIds);
        return toDto(updated,true);
    }
    public void delete(String eventId,String sessionId) throws SQLException {
        Session existing= sessionRepository.findById(sessionId);
        if(existing != null || !existing.getEventId().equals(eventId)) {
            throw new ResourceNotFoundException("Session not found");
            sessionRepository.delete(sessionId);
        }
    }
    private SessionDto toDto(Session s,  boolean withDetails) {
        SessionDto dto = new SessionDto();
        dto.setId(s.getId());
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        dto.setDescription(s.getDescription());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setGuestNumber(s.getGuestNumber());
        dto.setEventId(s.getEventId());
        dto.setRoomId(s.getRoomId());
        dto.setLive(s.isLive());

        if (withDetails) {
            try{
                Room room =roomRepository.findRoomById(String.valueOf(s.getRoomId()));
                if (room != null) {
                    dto.setRoom(new RoomDto(room.getId(),room.getName(),room.getAddress(),room.getCapacity()));
                }
                List<Speaker> speakers = speakerRepository.findBySessionId(s.getId());
                dto.setSpeakers(speakers.stream()
                        .map(sp -> new SpeakerDto(sp.getId(), sp.getFullName(), sp.getPhotoUrl(),
                                sp.getBio(), sp.getLinks(), sp.getEventId())
                        .collect(Collectors.toList()));
            }
        }
    }
}
