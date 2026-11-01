package com.aigenerator.project_idea_generator.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "api_usage")
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

    public ApiUsage() {}

    public ApiUsage(Long id, String endpointType, LocalDate callDate, long callCount) {
        this.id = id;
        this.endpointType = endpointType;
        this.callDate = callDate;
        this.callCount = callCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEndpointType() { return endpointType; }
    public void setEndpointType(String endpointType) { this.endpointType = endpointType; }
    public LocalDate getCallDate() { return callDate; }
    public void setCallDate(LocalDate callDate) { this.callDate = callDate; }
    public long getCallCount() { return callCount; }
    public void setCallCount(long callCount) { this.callCount = callCount; }

    // Builder pattern
    public static ApiUsageBuilder builder() { return new ApiUsageBuilder(); }

    public static class ApiUsageBuilder {
        private Long id;
        private String endpointType;
        private LocalDate callDate;
        private long callCount;

        public ApiUsageBuilder id(Long id) { this.id = id; return this; }
        public ApiUsageBuilder endpointType(String endpointType) { this.endpointType = endpointType; return this; }
        public ApiUsageBuilder callDate(LocalDate callDate) { this.callDate = callDate; return this; }
        public ApiUsageBuilder callCount(long callCount) { this.callCount = callCount; return this; }

        public ApiUsage build() {
            return new ApiUsage(id, endpointType, callDate, callCount);
        }
    }
}
