package com.k3.examen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Integer id;
    private String content;
    private String authorName;
    private Integer upvotes;
    private LocalDateTime createdAt;
    private Integer sessionId;
}
