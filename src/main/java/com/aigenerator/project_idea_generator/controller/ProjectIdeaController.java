package com.aigenerator.project_idea_generator.controller;

import com.aigenerator.project_idea_generator.dto.ProjectGenerationRequest;
import com.aigenerator.project_idea_generator.model.ProjectIdea;
import com.aigenerator.project_idea_generator.service.ProjectIdeaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*") // Allow requests from frontend
public class ProjectIdeaController {

    private final ProjectIdeaService service;

    public ProjectIdeaController(ProjectIdeaService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ResponseEntity<ProjectIdea> generateIdea(@Valid @RequestBody ProjectGenerationRequest request) {
        ProjectIdea generatedIdea = service.generateAndSaveProjectIdea(request);
        return ResponseEntity.ok(generatedIdea);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ProjectIdea>> getHistory() {
        return ResponseEntity.ok(service.getHistory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectIdea> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProjectById(id));
    }
}
