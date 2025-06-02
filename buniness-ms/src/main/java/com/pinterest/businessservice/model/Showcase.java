package com.pinterest.businessservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "showcases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Showcase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Associated business profile ID
    @Column(name = "business_profile_id", nullable = false)
    private Long businessProfileId;
    
    // Showcase title
    @Column(nullable = false)
    private String title;
    
    // Showcase description
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Showcase cover image URL
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    
    // Showcase theme/category
    private String theme;
    
    // Is the showcase featured
    private boolean featured;
    
    // Is the showcase active
    @Column(nullable = false)
    private boolean active;
    
    // Showcase display order
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}