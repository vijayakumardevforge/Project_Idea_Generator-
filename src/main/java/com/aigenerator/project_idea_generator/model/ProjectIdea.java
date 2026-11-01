package com.aigenerator.project_idea_generator.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project_ideas")
public class ProjectIdea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillLevel;
    private String programmingLanguage;
    private String framework;
    private String projectDomain;

    private String projectName;

    @Column(columnDefinition = "TEXT")
    private String projectDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> keyFeatures;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> suggestedTables;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> recommendedEndpoints;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> learningRoadmap;

    @Column(columnDefinition = "TEXT")
    private String detailedRoadmap;

    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ProjectIdea() {}

    public ProjectIdea(Long id, String skillLevel, String programmingLanguage, String framework,
                       String projectDomain, String projectName, String projectDescription,
                       List<String> keyFeatures, List<String> suggestedTables,
                       List<String> recommendedEndpoints, List<String> learningRoadmap,
                       String detailedRoadmap, String ipAddress, String userAgent,
                       LocalDateTime createdAt, User user) {
        this.id = id;
        this.skillLevel = skillLevel;
        this.programmingLanguage = programmingLanguage;
        this.framework = framework;
        this.projectDomain = projectDomain;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.keyFeatures = keyFeatures;
        this.suggestedTables = suggestedTables;
        this.recommendedEndpoints = recommendedEndpoints;
        this.learningRoadmap = learningRoadmap;
        this.detailedRoadmap = detailedRoadmap;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public String getProgrammingLanguage() { return programmingLanguage; }
    public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }
    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }
    public String getProjectDomain() { return projectDomain; }
    public void setProjectDomain(String projectDomain) { this.projectDomain = projectDomain; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectDescription() { return projectDescription; }
    public void setProjectDescription(String projectDescription) { this.projectDescription = projectDescription; }
    public List<String> getKeyFeatures() { return keyFeatures; }
    public void setKeyFeatures(List<String> keyFeatures) { this.keyFeatures = keyFeatures; }
    public List<String> getSuggestedTables() { return suggestedTables; }
    public void setSuggestedTables(List<String> suggestedTables) { this.suggestedTables = suggestedTables; }
    public List<String> getRecommendedEndpoints() { return recommendedEndpoints; }
    public void setRecommendedEndpoints(List<String> recommendedEndpoints) { this.recommendedEndpoints = recommendedEndpoints; }
    public List<String> getLearningRoadmap() { return learningRoadmap; }
    public void setLearningRoadmap(List<String> learningRoadmap) { this.learningRoadmap = learningRoadmap; }
    public String getDetailedRoadmap() { return detailedRoadmap; }
    public void setDetailedRoadmap(String detailedRoadmap) { this.detailedRoadmap = detailedRoadmap; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Builder pattern
    public static ProjectIdeaBuilder builder() { return new ProjectIdeaBuilder(); }

    public static class ProjectIdeaBuilder {
        private Long id;
        private String skillLevel;
        private String programmingLanguage;
        private String framework;
        private String projectDomain;
        private String projectName;
        private String projectDescription;
        private List<String> keyFeatures;
        private List<String> suggestedTables;
        private List<String> recommendedEndpoints;
        private List<String> learningRoadmap;
        private String detailedRoadmap;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime createdAt;
        private User user;

        public ProjectIdeaBuilder id(Long id) { this.id = id; return this; }
        public ProjectIdeaBuilder skillLevel(String skillLevel) { this.skillLevel = skillLevel; return this; }
        public ProjectIdeaBuilder programmingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; return this; }
        public ProjectIdeaBuilder framework(String framework) { this.framework = framework; return this; }
        public ProjectIdeaBuilder projectDomain(String projectDomain) { this.projectDomain = projectDomain; return this; }
        public ProjectIdeaBuilder projectName(String projectName) { this.projectName = projectName; return this; }
        public ProjectIdeaBuilder projectDescription(String projectDescription) { this.projectDescription = projectDescription; return this; }
        public ProjectIdeaBuilder keyFeatures(List<String> keyFeatures) { this.keyFeatures = keyFeatures; return this; }
        public ProjectIdeaBuilder suggestedTables(List<String> suggestedTables) { this.suggestedTables = suggestedTables; return this; }
        public ProjectIdeaBuilder recommendedEndpoints(List<String> recommendedEndpoints) { this.recommendedEndpoints = recommendedEndpoints; return this; }
        public ProjectIdeaBuilder learningRoadmap(List<String> learningRoadmap) { this.learningRoadmap = learningRoadmap; return this; }
        public ProjectIdeaBuilder detailedRoadmap(String detailedRoadmap) { this.detailedRoadmap = detailedRoadmap; return this; }
        public ProjectIdeaBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public ProjectIdeaBuilder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public ProjectIdeaBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProjectIdeaBuilder user(User user) { this.user = user; return this; }

        public ProjectIdea build() {
            return new ProjectIdea(id, skillLevel, programmingLanguage, framework, projectDomain,
                    projectName, projectDescription, keyFeatures, suggestedTables,
                    recommendedEndpoints, learningRoadmap, detailedRoadmap, ipAddress,
                    userAgent, createdAt, user);
        }
    }
}
