package com.k3.examen.model;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private String id;
    private String content;
    private String authorName;
    private Integer upvotes;
    private LocalDateTime createdAt;
    private String sessionId;


}