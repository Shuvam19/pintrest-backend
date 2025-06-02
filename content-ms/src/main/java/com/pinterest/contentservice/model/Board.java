package com.pinterest.contentservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Board title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Cover image URL for the board
    private String coverImageUrl;

    // User ID who created this board (foreign key to User service)
    @Column(nullable = false)
    private Long userId;

    // Privacy setting
    @Column(nullable = false)
    private boolean isPrivate = false;

    // Category of the board
    private String category;

    // Display order for sorting
    private Integer displayOrder;

    // Relationship with Pins
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pin> pins = new ArrayList<>();

    // Collaborative board settings
    @Column(nullable = false)
    private boolean isCollaborative = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper method to add a pin to the board
    public void addPin(Pin pin) {
        pins.add(pin);
        pin.setBoard(this);
    }

    // Helper method to remove a pin from the board
    public void removePin(Pin pin) {
        pins.remove(pin);
        pin.setBoard(null);
    }
}