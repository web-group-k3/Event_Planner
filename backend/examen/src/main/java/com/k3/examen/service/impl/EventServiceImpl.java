package com.k3.examen.service.impl;

import com.k3.examen.dto.EventDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Event;
import com.k3.examen.repository.EventRepository;
import com.k3.examen.repository.SessionRepository;
import com.k3.examen.service.EventService;
import com.k3.examen.validator.EventValidator;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final SessionRepository sessionRepository;
    public EventServiceImpl(EventRepository eventRepository, SessionRepository sessionRepository) {
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
    }
    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No event found with id " + id));
    }

    @Override
    public Event getEventWithSessions(String id) {
        Event event = getEventById(id);
        event.setSessions(sessionRepository.findByEventId(id));
        return event;
    }

    @Override
    public Event createEvent(Event event) {
        validateEvent(event);
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(String id, Event event) {
        getEventById(id);
        validateEvent(event);
        event.setId(id);
        return eventRepository.update(event);
    }

    @Override
    public void deleteEvent(String id) {
        getEventById(id);
        eventRepository.delete(id);
    }
    private void validateEvent(Event event) {
        if (event.getTitle() == null || event.getTitle().isBlank())
            throw new IllegalArgumentException("title is blank");
        if (event.getStartDate() == null || event.getEndDate() == null)
            throw new IllegalArgumentException("date are blank");
        if (event.getEndDate().isBefore(event.getStartDate()))
            throw new IllegalArgumentException("end date is before start date");
    }
}
