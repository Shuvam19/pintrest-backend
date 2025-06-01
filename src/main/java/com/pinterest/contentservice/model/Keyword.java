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
@Table(name = "keywords")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Keyword name is required")
    @Size(max = 50, message = "Keyword name cannot exceed 50 characters")
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "keywords")
    private Set<Pin> pins = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}