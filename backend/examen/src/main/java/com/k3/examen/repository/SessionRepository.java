package com.k3.examen.repository;

import com.k3.examen.model.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    List<Session> findAll();
    void deleteSpeaker(String sessionId, String speakerId);
    Optional<Session> findById(String id);
    List<Session> findByEventId(String EventId);
    List<Session> findByRoomId(String RoomId);
    List<Session> findByRoomIdAndEventId(String roomId, String eventId);
    Session save(Session session);
    Session update(Session session);
    void delete(String id);
    void addSpeaker(String sessionId, String speakerId);
}