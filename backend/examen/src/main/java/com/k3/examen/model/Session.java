package com.k3.examen.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Session {
    private String id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer guestNumber;
    private String roomId;
    private String eventId;
    private Room room;
    private List<Speaker> speakers;
    private List<Question> questions;
    public boolean isLive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }



    /*public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Session s = new Session();
        public Builder id(Long id) { s.id = id; return this; }
        public Builder title(String v) { s.title = v; return this; }
        public Builder startTime(LocalDateTime v) { s.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { s.endTime = v; return this; }
        public Builder roomId(Long v) { s.roomId = v; return this; }
        public Builder eventId(Long v) { s.eventId = v; return this; }
        public Session build() { return s; }
    }*/
}