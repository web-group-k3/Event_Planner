package com.k3.examen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Integer eventId;
    private Integer roomId;
    private List<Speaker> speakers;
    private List<Question> questions;
    
    // Constructor without ID (for creation)
    public Session(String title, String description, LocalDateTime startTime, 
                  LocalDateTime endTime, Integer capacity, Integer eventId, Integer roomId) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.eventId = eventId;
        this.roomId = roomId;
    }
    
    // Helper method to check if session is live
    public boolean isLive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
}
