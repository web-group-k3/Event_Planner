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
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final SessionRepository sessionRepository;
    public EventServiceImpl(EventRepository eventRepository,EventValidator eventValidator, SessionRepository sessionRepository) {
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
        this.eventValidator = eventValidator;
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
        eventValidator.validate(event);
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(String id, Event event) {
        getEventById(id);
        eventValidator.validate(event);
        event.setId(id);
        return eventRepository.update(event);
    }

    @Override
    public void deleteEvent(String id) {
        getEventById(id);
        eventRepository.delete(id);
    }

}
