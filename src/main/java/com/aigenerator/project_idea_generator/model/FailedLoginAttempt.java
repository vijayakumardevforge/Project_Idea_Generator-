package com.aigenerator.project_idea_generator.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_logins")
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

    public FailedLoginAttempt() {}

    public FailedLoginAttempt(Long id, String username, String ipAddress, LocalDateTime attemptTime) {
        this.id = id;
        this.username = username;
        this.ipAddress = ipAddress;
        this.attemptTime = attemptTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getAttemptTime() { return attemptTime; }
    public void setAttemptTime(LocalDateTime attemptTime) { this.attemptTime = attemptTime; }

    // Builder pattern
    public static FailedLoginAttemptBuilder builder() { return new FailedLoginAttemptBuilder(); }

    public static class FailedLoginAttemptBuilder {
        private Long id;
        private String username;
        private String ipAddress;
        private LocalDateTime attemptTime;

        public FailedLoginAttemptBuilder id(Long id) { this.id = id; return this; }
        public FailedLoginAttemptBuilder username(String username) { this.username = username; return this; }
        public FailedLoginAttemptBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public FailedLoginAttemptBuilder attemptTime(LocalDateTime attemptTime) { this.attemptTime = attemptTime; return this; }

        public FailedLoginAttempt build() {
            return new FailedLoginAttempt(id, username, ipAddress, attemptTime);
        }
    }
}
