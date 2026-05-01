package com.k3.examen.service;

import com.k3.examen.dto.*;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.repository.SessionRepository;
import com.k3.examen.repository.SpeakerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
    
    private final SessionRepository sessionRepository;
    private final SpeakerRepository speakerRepository;
    
    public SessionService(SessionRepository sessionRepository, SpeakerRepository speakerRepository) {
        this.sessionRepository = sessionRepository;
        this.speakerRepository = speakerRepository;
    }
    
    public SessionDTO createSession(Integer eventId, CreateSessionRequest request) {
        Session session = new Session(
                request.getTitle(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCapacity(),
                eventId,
                request.getRoomId()
        );
        
        Session savedSession = sessionRepository.save(session);
        
        // Add speakers if provided
        if (request.getSpeakerIds() != null && !request.getSpeakerIds().isEmpty()) {
            sessionRepository.updateSpeakers(savedSession.getId(), request.getSpeakerIds());
            savedSession.setSpeakers(speakerRepository.findBySessionId(savedSession.getId()));
        }
        
        return convertToDTO(savedSession);
    }
    
    public SessionDTO getSessionById(Integer id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        return convertToDTO(session);
    }
    
    public List<SessionDTO> getSessionsByEventId(Integer eventId) {
        return sessionRepository.findByEventId(eventId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SessionDTO> getSessionsByRoomId(Integer roomId) {
        return sessionRepository.findByRoomId(roomId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SessionDTO> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public SessionDTO updateSession(Integer id, CreateSessionRequest request) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        
        session.setTitle(request.getTitle());
        session.setDescription(request.getDescription());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setCapacity(request.getCapacity());
        session.setRoomId(request.getRoomId());
        
        Session updatedSession = sessionRepository.update(session);
        
        // Update speakers if provided
        if (request.getSpeakerIds() != null) {
            sessionRepository.updateSpeakers(id, request.getSpeakerIds());
            updatedSession.setSpeakers(speakerRepository.findBySessionId(id));
        }
        
        return convertToDTO(updatedSession);
    }
    
    public void deleteSession(Integer id) {
        if (!sessionRepository.existsById(id)) {
            throw new RuntimeException("Session not found with id: " + id);
        }
        sessionRepository.deleteById(id);
    }
    
    public SessionDTO addSpeakerToSession(Integer sessionId, Integer speakerId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new RuntimeException("Session not found with id: " + sessionId);
        }
        if (!speakerRepository.existsById(speakerId)) {
            throw new RuntimeException("Speaker not found with id: " + speakerId);
        }
        
        sessionRepository.addSpeakerToSession(sessionId, speakerId);
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return convertToDTO(session);
    }
    
    public SessionDTO removeSpeakerFromSession(Integer sessionId, Integer speakerId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new RuntimeException("Session not found with id: " + sessionId);
        }
        
        sessionRepository.removeSpeakerFromSession(sessionId, speakerId);
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return convertToDTO(session);
    }
    
    private SessionDTO convertToDTO(Session session) {
        SessionDTO dto = new SessionDTO();
        dto.setId(session.getId());
        dto.setTitle(session.getTitle());
        dto.setDescription(session.getDescription());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setCapacity(session.getCapacity());
        dto.setEventId(session.getEventId());
        dto.setRoomId(session.getRoomId());
        dto.setLive(session.isLive());
        
        // Convert speakers to DTOs
        if (session.getSpeakers() != null) {
            List<SpeakerDTO> speakerDTOs = session.getSpeakers().stream()
                    .map(this::convertSpeakerToDTO)
                    .collect(Collectors.toList());
            dto.setSpeakers(speakerDTOs);
        }
        
        // Get room name if roomId is set
        if (session.getRoomId() != null) {
            // This would need RoomRepository - for now just set the ID
            dto.setRoomId(session.getRoomId());
        }
        
        return dto;
    }
    
    private SpeakerDTO convertSpeakerToDTO(Speaker speaker) {
        SpeakerDTO dto = new SpeakerDTO();
        dto.setId(speaker.getId());
        dto.setFullName(speaker.getFullName());
        dto.setPhotoUrl(speaker.getPhotoUrl());
        dto.setBio(speaker.getBio());
        dto.setLinks(speaker.getLinks());
        dto.setEventId(speaker.getEventId());
        return dto;
    }
}
