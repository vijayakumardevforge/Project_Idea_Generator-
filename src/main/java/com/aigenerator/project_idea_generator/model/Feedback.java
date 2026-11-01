package com.aigenerator.project_idea_generator.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer stars;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 2000)
    private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Feedback() {}

    public Feedback(Long id, Integer stars, String email, String message, LocalDateTime createdAt) {
        this.id = id;
        this.stars = stars;
        this.email = email;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
