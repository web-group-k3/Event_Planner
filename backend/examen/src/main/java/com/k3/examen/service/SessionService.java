package com.k3.examen.service;

import com.k3.examen.model.Session;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionService {
    List<Session> getAllSessions();

    Session getSessionById(String id);

    Session getSessionDetail(String id);

    List<Session> getSessionsByEvent(String eventId);

    List<Session> getSessionsByRoom(String roomId);

    Session createSession(Session session);

    Session updateSession(String id, Session session);

    void deleteSession(String id);

    void addSpeakerToSession(String sessionId, String speakerId);

    void removeSpeakerFromSession(String sessionId, String speakerId);

    List<Session> getSessionsByEventAndRoom(String eventId, String roomId);
}
