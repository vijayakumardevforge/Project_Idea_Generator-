package com.aigenerator.project_idea_generator.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_ips")
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

    public BlockedIp() {}

    public BlockedIp(Long id, String ipAddress, String reason, String userAgent, LocalDateTime blockedAt) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.reason = reason;
        this.userAgent = userAgent;
        this.blockedAt = blockedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }

    @PrePersist
    protected void onCreate() {
        this.blockedAt = LocalDateTime.now();
    }

    // Builder pattern
    public static BlockedIpBuilder builder() { return new BlockedIpBuilder(); }

    public static class BlockedIpBuilder {
        private Long id;
        private String ipAddress;
        private String reason;
        private String userAgent;
        private LocalDateTime blockedAt;

        public BlockedIpBuilder id(Long id) { this.id = id; return this; }
        public BlockedIpBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public BlockedIpBuilder reason(String reason) { this.reason = reason; return this; }
        public BlockedIpBuilder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public BlockedIpBuilder blockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; return this; }

        public BlockedIp build() {
            return new BlockedIp(id, ipAddress, reason, userAgent, blockedAt);
        }
    }
}
