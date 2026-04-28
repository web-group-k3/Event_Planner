package com.k3.examen.model;

import java.time.LocalDateTime;

public class Session {
    private Long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long roomId;
    private Long eventId;

    public Session() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime v) { this.startTime = v; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime v) { this.endTime = v; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Session s = new Session();
        public Builder id(Long id) { s.id = id; return this; }
        public Builder title(String v) { s.title = v; return this; }
        public Builder startTime(LocalDateTime v) { s.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { s.endTime = v; return this; }
        public Builder roomId(Long v) { s.roomId = v; return this; }
        public Builder eventId(Long v) { s.eventId = v; return this; }
        public Session build() { return s; }
    }
}