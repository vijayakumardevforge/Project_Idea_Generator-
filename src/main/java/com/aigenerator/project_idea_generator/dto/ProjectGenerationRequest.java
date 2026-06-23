package com.aigenerator.project_idea_generator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectGenerationRequest {

    @NotBlank(message = "Skill level is required")
    private String skillLevel;

    @NotBlank(message = "Programming language is required")
    private String programmingLanguage;

    @NotBlank(message = "Framework is required")
    private String framework;

    @NotBlank(message = "Project domain is required")
    private String projectDomain;
    
    private String previousIdeaName;
}
