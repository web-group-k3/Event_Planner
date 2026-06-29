package com.k3.examen.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Admin {
    private Long id;
    private String username;
    private String passwordHash;
}
