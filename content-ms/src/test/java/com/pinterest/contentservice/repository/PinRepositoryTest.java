package com.pinterest.contentservice.repository;

import com.pinterest.contentservice.model.Pin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PinRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PinRepository pinRepository;

    private Pin pin1;
    private Pin pin2;
    private Pin pin3;

    @BeforeEach
    void setUp() {
        pin1 = Pin.builder()
                .userId(101L)
                .boardId(201L)
                .title("Beautiful Sunset")
                .description("Amazing sunset at the beach")
                .imageUrl("https://example.com/images/sunset.jpg")
                .link("https://example.com/sunset")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pin2 = Pin.builder()
                .userId(101L)
                .boardId(202L)
                .title("Delicious Pasta")
                .description("Homemade pasta recipe")
                .imageUrl("https://example.com/images/pasta.jpg")
                .link("https://example.com/pasta")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pin3 = Pin.builder()
                .userId(102L)
                .boardId(201L)
                .title("Mountain View")
                .description("Beautiful mountain landscape")
                .imageUrl("https://example.com/images/mountain.jpg")
                .link("https://example.com/mountain")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persist(pin1);
        entityManager.persist(pin2);
        entityManager.persist(pin3);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find pins by user ID")
    void shouldFindPinsByUserId() {
        List<Pin> pins = pinRepository.findByUserId(101L);

        assertThat(pins).isNotEmpty();
        assertThat(pins).hasSize(2);
        assertThat(pins.get(0).getUserId()).isEqualTo(101L);
        assertThat(pins.get(1).getUserId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find pins by user ID with pagination")
    void shouldFindPinsByUserIdWithPagination() {
        Page<Pin> pinsPage = pinRepository.findByUserId(101L, PageRequest.of(0, 10));

        assertThat(pinsPage).isNotEmpty();
        assertThat(pinsPage.getContent()).hasSize(2);
        assertThat(pinsPage.getContent().get(0).getUserId()).isEqualTo(101L);
        assertThat(pinsPage.getContent().get(1).getUserId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find pins by board ID")
    void shouldFindPinsByBoardId() {
        List<Pin> pins = pinRepository.findByBoardId(201L);

        assertThat(pins).isNotEmpty();
        assertThat(pins).hasSize(2);
        assertThat(pins.get(0).getBoardId()).isEqualTo(201L);
        assertThat(pins.get(1).getBoardId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find pins by board ID with pagination")
    void shouldFindPinsByBoardIdWithPagination() {
        Page<Pin> pinsPage = pinRepository.findByBoardId(201L, PageRequest.of(0, 10));

        assertThat(pinsPage).isNotEmpty();
        assertThat(pinsPage.getContent()).hasSize(2);
        assertThat(pinsPage.getContent().get(0).getBoardId()).isEqualTo(201L);
        assertThat(pinsPage.getContent().get(1).getBoardId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find pins by user ID and board ID")
    void shouldFindPinsByUserIdAndBoardId() {
        List<Pin> pins = pinRepository.findByUserIdAndBoardId(101L, 201L);

        assertThat(pins).isNotEmpty();
        assertThat(pins).hasSize(1);
        assertThat(pins.get(0).getUserId()).isEqualTo(101L);
        assertThat(pins.get(0).getBoardId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find pins by title containing keyword")
    void shouldFindPinsByTitleContaining() {
        List<Pin> pins = pinRepository.findByTitleContainingIgnoreCase("sunset");

        assertThat(pins).isNotEmpty();
        assertThat(pins).hasSize(1);
        assertThat(pins.get(0).getTitle()).contains("Sunset");
    }

    @Test
    @DisplayName("Should find pins by description containing keyword")
    void shouldFindPinsByDescriptionContaining() {
        List<Pin> pins = pinRepository.findByDescriptionContainingIgnoreCase("mountain");

        assertThat(pins).isNotEmpty();
        assertThat(pins).hasSize(1);
        assertThat(pins.get(0).getDescription()).contains("mountain");
    }

    @Test
    @DisplayName("Should find pins by title or description containing keyword")
    void shouldFindPinsByTitleOrDescriptionContaining() {
        List<Pin> pins = pinRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("pasta", "pasta");

        assertThat(pins).isNotEmpty();
        assertThat(pins).hasSize(1);
        assertThat(pins.get(0).getTitle()).contains("Pasta");
    }

    @Test
    @DisplayName("Should count pins by board ID")
    void shouldCountPinsByBoardId() {
        long count = pinRepository.countByBoardId(201L);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count pins by user ID")
    void shouldCountPinsByUserId() {
        long count = pinRepository.countByUserId(101L);

        assertThat(count).isEqualTo(2);
    }
}