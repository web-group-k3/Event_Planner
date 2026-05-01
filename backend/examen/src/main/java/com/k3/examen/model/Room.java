package com.k3.examen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private Integer id;
    private String name;
    private Integer eventId;
    
    // Constructor without ID (for creation)
    public Room(String name, Integer eventId) {
        this.name = name;
        this.eventId = eventId;
    }
}
