package com.k3.examen.dto;

import com.k3.examen.model.Event;
import com.k3.examen.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {
    private String id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer guestNumber;
    private Event eventId;
    private Room roomId;
    private boolean isLive;
    private RoomDto room;
    private List<SpeakerDto> speakers;
}
