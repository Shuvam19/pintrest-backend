package com.pinterest.contentservice.service;

import com.pinterest.contentservice.dto.PinDto;
import com.pinterest.contentservice.dto.PinRequest;
import com.pinterest.contentservice.exception.ResourceNotFoundException;
import com.pinterest.contentservice.model.Pin;
import com.pinterest.contentservice.repository.PinRepository;
import com.pinterest.contentservice.service.impl.PinServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PinServiceImplTest {

    @Mock
    private PinRepository pinRepository;

    @InjectMocks
    private PinServiceImpl pinService;

    private Pin pin;
    private PinDto pinDto;
    private PinRequest pinRequest;

    @BeforeEach
    void setUp() {
        pin = Pin.builder()
                .id(1L)
                .title("Test Pin")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .userId(101L)
                .boardId(201L)
                .createdAt(LocalDateTime.now())
                .build();

        pinDto = PinDto.builder()
                .id(1L)
                .title("Test Pin")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .userId(101L)
                .boardId(201L)
                .createdAt(pin.getCreatedAt())
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
    void shouldCreatePin() {
        when(pinRepository.save(any(Pin.class)))
                .thenReturn(pin);

        PinDto result = pinService.createPin(pinRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Pin");
        assertThat(result.getUserId()).isEqualTo(101L);
        verify(pinRepository, times(1)).save(any(Pin.class));
    }

    @Test
    @DisplayName("Should get pin by ID")
    void shouldGetPinById() {
        when(pinRepository.findById(1L))
                .thenReturn(Optional.of(pin));

        PinDto result = pinService.getPinById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(pinRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when pin not found by ID")
    void shouldThrowExceptionWhenPinNotFoundById() {
        when(pinRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            pinService.getPinById(999L);
        });

        verify(pinRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update pin successfully")
    void shouldUpdatePin() {
        when(pinRepository.findById(1L))
                .thenReturn(Optional.of(pin));
        when(pinRepository.save(any(Pin.class)))
                .thenReturn(pin);

        pinRequest.setTitle("Updated Pin Title");
        PinDto result = pinService.updatePin(1L, pinRequest);

        assertThat(result).isNotNull();
        verify(pinRepository, times(1)).findById(1L);
        verify(pinRepository, times(1)).save(any(Pin.class));
    }

    @Test
    @DisplayName("Should delete pin successfully")
    void shouldDeletePin() {
        when(pinRepository.findById(1L))
                .thenReturn(Optional.of(pin));
        doNothing().when(pinRepository).delete(pin);

        pinService.deletePin(1L);

        verify(pinRepository, times(1)).findById(1L);
        verify(pinRepository, times(1)).delete(pin);
    }

    @Test
    @DisplayName("Should get pins by user ID")
    void shouldGetPinsByUserId() {
        when(pinRepository.findByUserId(101L))
                .thenReturn(List.of(pin));

        List<PinDto> result = pinService.getPinsByUserId(101L);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(101L);
        verify(pinRepository, times(1)).findByUserId(101L);
    }

    @Test
    @DisplayName("Should get pins by user ID with pagination")
    void shouldGetPinsByUserIdPaged() {
        Page<Pin> page = new PageImpl<>(List.of(pin));
        when(pinRepository.findByUserId(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        Page<PinDto> result = pinService.getPinsByUserId(101L, Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(101L);
        verify(pinRepository, times(1)).findByUserId(eq(101L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get pins by board ID")
    void shouldGetPinsByBoardId() {
        when(pinRepository.findByBoardId(201L))
                .thenReturn(List.of(pin));

        List<PinDto> result = pinService.getPinsByBoardId(201L);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getBoardId()).isEqualTo(201L);
        verify(pinRepository, times(1)).findByBoardId(201L);
    }

    @Test
    @DisplayName("Should get pins by board ID with pagination")
    void shouldGetPinsByBoardIdPaged() {
        Page<Pin> page = new PageImpl<>(List.of(pin));
        when(pinRepository.findByBoardId(eq(201L), any(Pageable.class)))
                .thenReturn(page);

        Page<PinDto> result = pinService.getPinsByBoardId(201L, Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBoardId()).isEqualTo(201L);
        verify(pinRepository, times(1)).findByBoardId(eq(201L), any(Pageable.class));
    }
}