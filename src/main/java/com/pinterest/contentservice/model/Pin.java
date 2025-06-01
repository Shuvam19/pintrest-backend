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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    // For video content
    private String videoUrl;

    // Source URL for attribution
    private String sourceUrl;

    // Original content creator attribution
    private String attribution;

    // Keywords/tags for the pin
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "pin_keywords",
        joinColumns = @JoinColumn(name = "pin_id"),
        inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords = new HashSet<>();
    
    // Legacy keywords field as string (for backward compatibility)
    @Column(name = "keywords_text", columnDefinition = "TEXT")
    private String keywordsText;

    // Privacy setting
    @Column(nullable = false)
    private boolean isPrivate = false;

    // Draft status
    @Column(nullable = false)
    private boolean isDraft = false;

    // User ID who created this pin (foreign key to User service)
    @Column(nullable = false)
    private Long userId;

    // Relationship with Board
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}