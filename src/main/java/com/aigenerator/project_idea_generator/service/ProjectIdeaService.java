package com.aigenerator.project_idea_generator.service;

import com.aigenerator.project_idea_generator.dto.ProjectGenerationRequest;
import com.aigenerator.project_idea_generator.model.ProjectIdea;
import com.aigenerator.project_idea_generator.repository.ProjectIdeaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectIdeaService {

    private final ProjectIdeaRepository repository;
    private final HuggingFaceService huggingFaceService;

    public ProjectIdeaService(ProjectIdeaRepository repository, HuggingFaceService huggingFaceService) {
        this.repository = repository;
        this.huggingFaceService = huggingFaceService;
    }

    public ProjectIdea generateAndSaveProjectIdea(ProjectGenerationRequest request) {
        HuggingFaceService.GeneratedIdea generatedIdea = huggingFaceService.parseAndGenerateIdea(request);

        ProjectIdea idea = ProjectIdea.builder()
                .skillLevel(request.getSkillLevel())
                .programmingLanguage(request.getProgrammingLanguage())
                .framework(request.getFramework())
                .projectDomain(request.getProjectDomain())
                .projectName(generatedIdea.getProjectName())
                .projectDescription(generatedIdea.getProjectDescription())
                .keyFeatures(generatedIdea.getKeyFeatures())
                .suggestedTables(generatedIdea.getSuggestedTables())
                .recommendedEndpoints(generatedIdea.getRecommendedEndpoints())
                .learningRoadmap(generatedIdea.getLearningRoadmap())
                .build();

        return repository.save(idea);
    }

    public List<ProjectIdea> getHistory() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public ProjectIdea getProjectById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project idea not found with id: " + id));
    }
}
