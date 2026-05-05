package com.k3.examen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private String id;
    private String name;
    private String address;
    private Integer capacity;

    public Room(Object o, String name) {
    }
}
