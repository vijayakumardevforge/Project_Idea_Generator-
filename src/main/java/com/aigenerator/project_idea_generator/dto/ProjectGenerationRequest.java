package com.aigenerator.project_idea_generator.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectGenerationRequest {

    @NotBlank(message = "Skill level is required")
    private String skillLevel;

    @NotBlank(message = "Programming language is required")
    private String programmingLanguage;

    @NotBlank(message = "Framework is required")
    private String framework;

    @NotBlank(message = "Project domain is required")
    private String projectDomain;
    
    private String customProjectTitle;
    
    private String previousIdeaName;

    // Getters and Setters
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public String getProgrammingLanguage() { return programmingLanguage; }
    public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }
    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }
    public String getProjectDomain() { return projectDomain; }
    public void setProjectDomain(String projectDomain) { this.projectDomain = projectDomain; }
    public String getCustomProjectTitle() { return customProjectTitle; }
    public void setCustomProjectTitle(String customProjectTitle) { this.customProjectTitle = customProjectTitle; }
    public String getPreviousIdeaName() { return previousIdeaName; }
    public void setPreviousIdeaName(String previousIdeaName) { this.previousIdeaName = previousIdeaName; }
}
