package com.aigenerator.project_idea_generator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_ips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedIp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ipAddress;

    private String reason;
    private String userAgent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime blockedAt;

    @PrePersist
    protected void onCreate() {
        this.blockedAt = LocalDateTime.now();
    }
}
