package com.k3.examen.service.impl;

import com.k3.examen.dto.EventDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Event;
import com.k3.examen.repository.EventRepository;
import com.k3.examen.repository.RoomRepository;
import com.k3.examen.repository.SessionRepository;
import com.k3.examen.repository.SpeakerRepository;
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
    private final RoomRepository roomRepository;
    private final SpeakerRepository speakerRepository;
    public EventServiceImpl(EventRepository eventRepository,
                            EventValidator eventValidator,
                            SessionRepository sessionRepository,
                            RoomRepository roomRepository,
                            SpeakerRepository speakerRepository) {
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
        this.eventValidator = eventValidator;
        this.roomRepository     = roomRepository;
        this.speakerRepository  = speakerRepository;
    }
    @Override
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        events.forEach(event ->
                event.setSessions(sessionRepository.findByEventIdWithSpeakers(event.getId()))
        );
        return events;
    }
    @Override
    public Event getEventById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No event found with id " + id));
    }

    @Override
    public Event getEventWithSessions(String id) {
        Event event = getEventById(id);
        event.setSessions(sessionRepository.findByEventIdWithSpeakers(id)); // même méthode
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

    @Override
    public List<Event> getEventsByRoom(String roomId) {
        roomRepository.findRoomById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("No room found with id " + roomId));
        return eventRepository.findByRoomId(roomId);
    }

    @Override
    public List<Event> getEventsBySpeaker(String speakerId) {
        speakerRepository.findById(speakerId)
                .orElseThrow(()-> new ResourceNotFoundException("No speaker found with id " + speakerId));
        return eventRepository.findBySpeakerId(speakerId);
    }

}
