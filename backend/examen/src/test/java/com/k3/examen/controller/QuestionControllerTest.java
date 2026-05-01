package com.k3.examen.controller;

import com.k3.examen.dto.CreateQuestionRequest;
import com.k3.examen.dto.QuestionDTO;
import com.k3.examen.service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For Java 8 date/time support
    }

    @Test
    void testGetQuestionsBySessionId() throws Exception {
        QuestionDTO question1 = new QuestionDTO();
        question1.setId(1);
        question1.setContent("Question 1");
        question1.setSessionId(1);

        QuestionDTO question2 = new QuestionDTO();
        question2.setId(2);
        question2.setContent("Question 2");
        question2.setSessionId(1);

        when(questionService.getQuestionsBySessionId(1)).thenReturn(Arrays.asList(question1, question2));

        mockMvc.perform(get("/api/events/1/sessions/1/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Question 1"))
                .andExpect(jsonPath("$[1].content").value("Question 2"));
    }

    @Test
    void testCreateQuestion() throws Exception {
        CreateQuestionRequest request = new CreateQuestionRequest();
        request.setContent("New question content");
        request.setAuthorName("John Doe");

        QuestionDTO response = new QuestionDTO();
        response.setId(1);
        response.setContent("New question content");
        response.setSessionId(1);
        response.setCreatedAt(LocalDateTime.now());

        when(questionService.createQuestion(eq(1), any(QuestionDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/events/1/sessions/1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("New question content"));
    }

    @Test
    void testCreateQuestionValidationError() throws Exception {
        CreateQuestionRequest request = new CreateQuestionRequest();
        // Missing content - should fail validation

        mockMvc.perform(post("/api/events/1/sessions/1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpvoteQuestion() throws Exception {
        QuestionDTO response = new QuestionDTO();
        response.setId(1);
        response.setContent("Question");
        response.setUpvotes(1);
        response.setSessionId(1);

        when(questionService.upvoteQuestion(1, 1)).thenReturn(response);

        mockMvc.perform(post("/api/events/1/sessions/1/questions/1/upvote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.upvotes").value(1));
    }
}
