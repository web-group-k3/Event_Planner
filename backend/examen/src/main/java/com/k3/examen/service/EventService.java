package com.k3.examen.service;

import com.k3.examen.model.Event;

import java.util.List;

public interface EventService {
    List<Event> getAllEvents();
    Event getEventById(String id);
    Event getEventWithSessions(String id);
    Event createEvent(Event event);
    Event updateEvent(String id, Event event);
    void deleteEvent(String id);
    List<Event> getEventsByRoom(String roomId);
    List<Event> getEventsBySpeaker(String speakerId);
}
