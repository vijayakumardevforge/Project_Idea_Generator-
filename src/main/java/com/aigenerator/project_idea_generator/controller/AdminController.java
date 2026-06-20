package com.aigenerator.project_idea_generator.controller;

import com.aigenerator.project_idea_generator.model.Feedback;
import com.aigenerator.project_idea_generator.model.FailedLoginAttempt;
import com.aigenerator.project_idea_generator.dto.DashboardStats;
import com.aigenerator.project_idea_generator.service.FeedbackService;
import com.aigenerator.project_idea_generator.repository.FailedLoginAttemptRepository;
import com.aigenerator.project_idea_generator.repository.ProjectIdeaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final FeedbackService feedbackService;
    private final FailedLoginAttemptRepository failedLoginAttemptRepository;
    private final ProjectIdeaRepository projectIdeaRepository;

    public AdminController(FeedbackService feedbackService, 
                           FailedLoginAttemptRepository failedLoginAttemptRepository,
                           ProjectIdeaRepository projectIdeaRepository) {
        this.feedbackService = feedbackService;
        this.failedLoginAttemptRepository = failedLoginAttemptRepository;
        this.projectIdeaRepository = projectIdeaRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long totalUsersToday = projectIdeaRepository.countDistinctUsersToday(startOfDay);
        long totalIdeasToday = projectIdeaRepository.countByCreatedAtAfter(startOfDay);
        
        return ResponseEntity.ok(DashboardStats.builder()
                .totalUsersToday(totalUsersToday)
                .totalIdeasToday(totalIdeasToday)
                .build());
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }
    
    @GetMapping("/failed-logins")
    public ResponseEntity<List<FailedLoginAttempt>> getFailedLogins() {
        return ResponseEntity.ok(failedLoginAttemptRepository.findAllByOrderByAttemptTimeDesc());
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAdmin() {
        // This endpoint is just used by the frontend to verify credentials are correct
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }
}
