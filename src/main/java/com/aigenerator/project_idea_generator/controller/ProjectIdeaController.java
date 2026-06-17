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
    private final com.aigenerator.project_idea_generator.service.HistoryService historyService;

    public ProjectIdeaController(ProjectIdeaService service, com.aigenerator.project_idea_generator.service.HistoryService historyService) {
        this.service = service;
        this.historyService = historyService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ProjectIdea> generateIdea(
            @Valid @RequestBody ProjectGenerationRequest request,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
            
        ProjectIdea generatedIdea = service.generateAndSaveProjectIdea(request);
        
        // Save to Redis cache if sessionId is provided
        if (sessionId != null && !sessionId.isEmpty()) {
            historyService.saveIdeaToHistory(sessionId, generatedIdea);
        }
        
        return ResponseEntity.ok(generatedIdea);
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getHistory(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
            
        // If session ID is provided, fetch from Redis.
        if (sessionId != null && !sessionId.isEmpty()) {
            List<ProjectIdea> history = historyService.getUserHistory(sessionId);
            if (!history.isEmpty()) {
                return ResponseEntity.ok(history);
            }
        }
        
        // Return a clear message object when there is no history, exactly as requested
        return ResponseEntity.ok(java.util.Map.of("message", "no history"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectIdea> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProjectById(id));
    }
}
