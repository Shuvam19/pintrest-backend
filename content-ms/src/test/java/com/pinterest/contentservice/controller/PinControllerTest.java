package com.pinterest.contentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinterest.contentservice.dto.PinDto;
import com.pinterest.contentservice.dto.PinRequest;
import com.pinterest.contentservice.service.PinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PinController.class)
public class PinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PinService pinService;

    @Autowired
    private ObjectMapper objectMapper;

    private PinDto pinDto;
    private PinRequest pinRequest;

    @BeforeEach
    void setUp() {
        pinDto = PinDto.builder()
                .id(1L)
                .title("Test Pin")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .userId(101L)
                .boardId(201L)
                .createdAt(LocalDateTime.now())
                .build();

        pinRequest = PinRequest.builder()
                .title("Test Pin")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .userId(101L)
                .boardId(201L)
                .build();
    }

    @Test
    @DisplayName("Should create pin successfully")
    void shouldCreatePin() throws Exception {
        when(pinService.createPin(any(PinRequest.class)))
                .thenReturn(pinDto);

        mockMvc.perform(post("/api/content/pins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pinRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Pin"));
    }

    @Test
    @DisplayName("Should get pin by ID")
    void shouldGetPinById() throws Exception {
        when(pinService.getPinById(1L))
                .thenReturn(pinDto);

        mockMvc.perform(get("/api/content/pins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Pin"));
    }

    @Test
    @DisplayName("Should update pin successfully")
    void shouldUpdatePin() throws Exception {
        when(pinService.updatePin(eq(1L), any(PinRequest.class)))
                .thenReturn(pinDto);

        mockMvc.perform(put("/api/content/pins/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("Should delete pin successfully")
    void shouldDeletePin() throws Exception {
        doNothing().when(pinService).deletePin(1L);

        mockMvc.perform(delete("/api/content/pins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should get pins by user ID")
    void shouldGetPinsByUserId() throws Exception {
        when(pinService.getPinsByUserId(101L))
                .thenReturn(List.of(pinDto));

        mockMvc.perform(get("/api/content/pins/user/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(101L));
    }

    @Test
    @DisplayName("Should get pins by user ID with pagination")
    void shouldGetPinsByUserIdPaged() throws Exception {
        Page<PinDto> page = new PageImpl<>(List.of(pinDto));
        when(pinService.getPinsByUserId(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/content/pins/user/101/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].userId").value(101L));
    }

    @Test
    @DisplayName("Should get pins by board ID")
    void shouldGetPinsByBoardId() throws Exception {
        when(pinService.getPinsByBoardId(201L))
                .thenReturn(List.of(pinDto));

        mockMvc.perform(get("/api/content/pins/board/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].boardId").value(201L));
    }

    @Test
    @DisplayName("Should get pins by board ID with pagination")
    void shouldGetPinsByBoardIdPaged() throws Exception {
        Page<PinDto> page = new PageImpl<>(List.of(pinDto));
        when(pinService.getPinsByBoardId(eq(201L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/content/pins/board/201/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].boardId").value(201L));
    }
}