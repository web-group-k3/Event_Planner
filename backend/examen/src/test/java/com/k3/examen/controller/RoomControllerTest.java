package com.k3.examen.controller;

import com.k3.examen.dto.CreateRoomRequest;
import com.k3.examen.dto.RoomDTO;
import com.k3.examen.service.RoomService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    void testGetRoomsByEventId() throws Exception {
        RoomDTO room1 = new RoomDTO();
        room1.setId(1);
        room1.setName("Room A");
        room1.setEventId(1);

        RoomDTO room2 = new RoomDTO();
        room2.setId(2);
        room2.setName("Room B");
        room2.setEventId(1);

        when(roomService.getRoomsByEventId(1)).thenReturn(Arrays.asList(room1, room2));

        mockMvc.perform(get("/api/events/1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Room A"))
                .andExpect(jsonPath("$[1].name").value("Room B"));
    }

    @Test
    void testGetRoomById() throws Exception {
        RoomDTO room = new RoomDTO();
        room.setId(1);
        room.setName("Test Room");
        room.setEventId(1);

        when(roomService.getRoomById(1)).thenReturn(room);

        mockMvc.perform(get("/api/events/1/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Room"))
                .andExpect(jsonPath("$.eventId").value(1));
    }

    @Test
    void testCreateRoom() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setName("New Room");

        RoomDTO response = new RoomDTO();
        response.setId(1);
        response.setName("New Room");
        response.setEventId(1);

        when(roomService.createRoom(any(RoomDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/events/1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Room"));
    }

    @Test
    void testCreateRoomValidationError() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        // Missing name - should fail validation

        mockMvc.perform(post("/api/events/1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRoom() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setName("Updated Room");

        RoomDTO existingRoom = new RoomDTO();
        existingRoom.setId(1);
        existingRoom.setName("Old Room");
        existingRoom.setEventId(1);

        RoomDTO response = new RoomDTO();
        response.setId(1);
        response.setName("Updated Room");
        response.setEventId(1);

        when(roomService.getRoomById(1)).thenReturn(existingRoom);
        when(roomService.updateRoom(eq(1), any(RoomDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/events/1/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Room"));
    }

    @Test
    void testDeleteRoom() throws Exception {
        RoomDTO existingRoom = new RoomDTO();
        existingRoom.setId(1);
        existingRoom.setEventId(1);

        when(roomService.getRoomById(1)).thenReturn(existingRoom);

        mockMvc.perform(delete("/api/events/1/rooms/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteRoomNotFound() throws Exception {
        when(roomService.getRoomById(999)).thenThrow(new RuntimeException("Room not found"));

        mockMvc.perform(delete("/api/events/1/rooms/999"))
                .andExpect(status().isNotFound());
    }
}
