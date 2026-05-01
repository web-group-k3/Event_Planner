package com.k3.examen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Integer id;
    private String content;
    private String authorName; // nullable for anonymous
    private Integer upvotes;
    private LocalDateTime createdAt;
    private Integer sessionId;
    
    // Constructor without ID (for creation)
    public Question(String content, String authorName, Integer sessionId) {
        this.content = content;
        this.authorName = authorName;
        this.upvotes = 0;
        this.sessionId = sessionId;
        this.createdAt = LocalDateTime.now();
    }
}
