package com.k3.examen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Integer eventId;
    private Integer roomId;
    private String roomName;
    private Boolean live;
    private java.util.List<SpeakerDTO> speakers;
    private Integer questionCount;
}
