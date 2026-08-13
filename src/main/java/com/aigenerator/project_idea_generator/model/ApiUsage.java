package com.aigenerator.project_idea_generator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "api_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String endpointType; // "IDEA" or "ROADMAP"

    @Column(nullable = false)
    private LocalDate callDate;

    @Column(nullable = false)
    private long callCount;
}
