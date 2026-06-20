package com.aigenerator.project_idea_generator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_logins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedLoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String ipAddress;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime attemptTime;
}
