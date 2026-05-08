package com.k3.examen.service.impl;

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

import java.sql.SQLException;
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

    public SessionServiceImpl(SessionRepository sessionRepository,
                              RoomRepository roomRepository,
                              SpeakerRepository speakerRepository,
                              QuestionRepository questionRepository,
                              EventRepository eventRepository,
                              SessionValidator sessionValidator) {
        this.sessionRepository  = sessionRepository;
        this.roomRepository     = roomRepository;
        this.speakerRepository  = speakerRepository;
        this.questionRepository = questionRepository;
        this.eventRepository    = eventRepository;
        this.sessionValidator = sessionValidator;
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public Session getSessionById(String id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }

    @Override
    public Session getSessionDetail(String id) {
        Session session = getSessionById(id);
        session.setRoom(roomRepository.findRoomById(session.getRoomId()).orElse(null));
        session.setSpeakers(speakerRepository.findBySessionId(id));
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
        return sessionRepository.save(session);
    }

    @Override
    public Session updateSession(String id, Session session) {
        getSessionById(id);
        sessionValidator.validate(session);
        session.setId(id);
        return sessionRepository.update(session);
    }

    @Override
    public void deleteSession(String id) {
        getSessionById(id);
        sessionRepository.delete(id);
    }

    @Override
    public void addSpeakerToSession(String sessionId, String speakerId) {
        getSessionById(sessionId);
        Speaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new ResourceNotFoundException("Speaker not found"));
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
