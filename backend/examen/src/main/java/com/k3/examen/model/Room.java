package com.k3.examen.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    private String id;
    private String name;
    private String address;
    private Integer capacity;


}
