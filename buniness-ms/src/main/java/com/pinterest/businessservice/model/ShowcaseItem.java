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
@Table(name = "showcase_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowcaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Associated showcase ID
    @Column(name = "showcase_id", nullable = false)
    private Long showcaseId;
    
    // Pin ID
    @Column(name = "pin_id", nullable = false)
    private Long pinId;
    
    // Item description
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Item display order
    @Column(name = "display_order")
    private Integer displayOrder;
    
    // Is the item featured
    private boolean featured;
    
    // Is the item active
    @Column(nullable = false)
    private boolean active;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}