package com.aigenerator.project_idea_generator.controller;

import com.aigenerator.project_idea_generator.dto.ProjectGenerationRequest;
import com.aigenerator.project_idea_generator.model.ProjectIdea;
import com.aigenerator.project_idea_generator.service.ProjectIdeaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*") // Allow requests from frontend
public class ProjectIdeaController {

    private final ProjectIdeaService service;
    private final com.aigenerator.project_idea_generator.service.HistoryService historyService;
    private final com.aigenerator.project_idea_generator.repository.UserRepository userRepository;
    private final com.aigenerator.project_idea_generator.repository.ProjectIdeaRepository ideaRepository;

    public ProjectIdeaController(ProjectIdeaService service, com.aigenerator.project_idea_generator.service.HistoryService historyService, com.aigenerator.project_idea_generator.repository.UserRepository userRepository, com.aigenerator.project_idea_generator.repository.ProjectIdeaRepository ideaRepository) {
        this.service = service;
        this.historyService = historyService;
        this.userRepository = userRepository;
        this.ideaRepository = ideaRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<ProjectIdea> generateIdea(
            @Valid @RequestBody ProjectGenerationRequest request,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            HttpServletRequest httpRequest) {
            
        // Render passes the real IP in the X-Forwarded-For header. Fallback to direct remote address.
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        } else {
            // X-Forwarded-For can contain multiple IPs, the first one is the client.
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        String userAgent = httpRequest.getHeader("X-Client-User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            userAgent = httpRequest.getHeader("User-Agent");
        }
            
        ProjectIdea generatedIdea = service.generateAndSaveProjectIdea(request, ipAddress, userAgent);
        
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

    @PostMapping("/{id}/roadmap")
    public ResponseEntity<ProjectIdea> generateRoadmap(@PathVariable Long id) {
        return ResponseEntity.ok(service.generateAndSaveRoadmap(id));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<?> saveIdeaForUser(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "Unauthorized"));
        }
        
        java.util.Optional<com.aigenerator.project_idea_generator.model.User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "User not found"));
        }

        ProjectIdea idea = service.getProjectById(id);
        idea.setUser(userOpt.get());
        ideaRepository.save(idea);

        return ResponseEntity.ok(java.util.Map.of("message", "Idea saved successfully"));
    }

    @GetMapping("/saved")
    public ResponseEntity<?> getSavedIdeas(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "Unauthorized"));
        }

        java.util.Optional<com.aigenerator.project_idea_generator.model.User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "User not found"));
        }

        List<ProjectIdea> savedIdeas = ideaRepository.findByUserOrderByCreatedAtDesc(userOpt.get());
        if (savedIdeas.isEmpty()) {
            return ResponseEntity.ok(java.util.Map.of("message", "no history"));
        }
        return ResponseEntity.ok(savedIdeas);
    }
}
