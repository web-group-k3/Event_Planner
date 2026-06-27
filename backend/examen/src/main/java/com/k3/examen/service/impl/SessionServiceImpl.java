package com.k3.examen.service.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.repository.*;
import com.k3.examen.service.SessionService;
import org.springframework.stereotype.Service;


import com.k3.examen.dto.RoomDto;
import com.k3.examen.dto.SessionDto;
import com.k3.examen.dto.SpeakerDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Room;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.validator.SessionValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final RoomRepository roomRepository;
    private final SpeakerRepository speakerRepository;
    private final QuestionRepository questionRepository;
    private final EventRepository eventRepository;
    private final SessionValidator sessionValidator;
    private final DatabaseConnection databaseConnection;

    public SessionServiceImpl(SessionRepository sessionRepository,
                              RoomRepository roomRepository,
                              SpeakerRepository speakerRepository,
                              QuestionRepository questionRepository,
                              EventRepository eventRepository,
                              SessionValidator sessionValidator, DatabaseConnection databaseConnection) {
        this.sessionRepository  = sessionRepository;
        this.roomRepository     = roomRepository;
        this.speakerRepository  = speakerRepository;
        this.questionRepository = questionRepository;
        this.eventRepository    = eventRepository;
        this.sessionValidator = sessionValidator;
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAllWithSpeakers();
    }
    @Override
    public Session getSessionById(String id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }

    @Override
    public Session getSessionDetail(String id) {
        Session session = getSessionById(id);
        session.setQuestions(questionRepository.findBySessionId(id));
        return session;
    }

    @Override
    public List<Session> getSessionsByEvent(String eventId) {
        return sessionRepository.findByEventId(eventId);
    }

    @Override
    public List<Session> getSessionsByRoom(String roomId) {
        return sessionRepository.findByRoomId(roomId);
    }

    @Override
    public Session createSession(Session session) {
        sessionValidator.validate(session);
        sessionValidator.validateSessionAgainstEvent(session);
        sessionValidator.validateRoomAvailability(session);
        sessionValidator.validateSpeakersAvailability(session);
        return sessionRepository.save(session);
    }

    @Override
    public Session updateSession(String id, Session session) {
        getSessionById(id);
        session.setId(id);
        sessionValidator.validate(session);
        sessionValidator.validateSessionAgainstEvent(session);
        sessionValidator.validateRoomAvailability(session);
        sessionValidator.validateSpeakersAvailability(session);
        return sessionRepository.update(session);
    }

    @Override
    public void deleteSession(String id) {
        getSessionById(id);
        sessionRepository.delete(id);
    }

    @Override
    public void addSpeakerToSession(String sessionId, String speakerId) {
        Session session=getSessionById(sessionId);
        Speaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new ResourceNotFoundException("Speaker not found"));
        boolean conflict= sessionRepository.existsConflictForSpeaker(
                speakerId,
                session.getStartTime(),
                session.getEndTime(),
                sessionId
        );
        if(conflict){
            throw new IllegalArgumentException("Speaker already taked");
        }
        sessionRepository.addSpeaker(sessionId,speakerId);
    }

    @Override
    public void removeSpeakerFromSession(String sessionId, String speakerId) {
        sessionRepository.deleteSpeaker(sessionId,speakerId);
    }

    @Override
    public List<Session> getSessionsByEventAndRoom(String eventId, String roomId) {
        eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        roomRepository.findRoomById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        return sessionRepository.findByRoomIdAndEventId(roomId,eventId);
    }




}
