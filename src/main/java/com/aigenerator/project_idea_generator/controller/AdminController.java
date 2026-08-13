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
import java.util.Map;
import com.aigenerator.project_idea_generator.model.BlockedIp;
import com.aigenerator.project_idea_generator.service.AdminFeatureService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final FeedbackService feedbackService;
    private final FailedLoginAttemptRepository failedLoginAttemptRepository;
    private final ProjectIdeaRepository projectIdeaRepository;
    private final AdminFeatureService adminFeatureService;

    public AdminController(FeedbackService feedbackService, 
                           FailedLoginAttemptRepository failedLoginAttemptRepository,
                           ProjectIdeaRepository projectIdeaRepository,
                           AdminFeatureService adminFeatureService) {
        this.feedbackService = feedbackService;
        this.failedLoginAttemptRepository = failedLoginAttemptRepository;
        this.projectIdeaRepository = projectIdeaRepository;
        this.adminFeatureService = adminFeatureService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long totalUsersToday = projectIdeaRepository.countDistinctUsersToday(startOfDay);
        long totalIdeasToday = projectIdeaRepository.countByCreatedAtAfter(startOfDay);
        
        long totalUsers = projectIdeaRepository.countDistinctUsers();
        long totalIdeas = projectIdeaRepository.count();

        return ResponseEntity.ok(DashboardStats.builder()
                .totalUsersToday(totalUsersToday)
                .totalIdeasToday(totalIdeasToday)
                .totalUsers(totalUsers)
                .totalIdeas(totalIdeas)
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
    
    @GetMapping("/api-usage")
    public ResponseEntity<Map<String, Object>> getApiUsage() {
        return ResponseEntity.ok(adminFeatureService.getApiUsageStats());
    }

    @GetMapping("/recent-users")
    public ResponseEntity<List<Map<String, String>>> getRecentUsers() {
        return ResponseEntity.ok(adminFeatureService.getRecentUniqueUsers());
    }

    @GetMapping("/rate-limited-users")
    public ResponseEntity<List<Map<String, String>>> getRateLimitedUsers() {
        // Enforcing a limit of 15
        return ResponseEntity.ok(adminFeatureService.getRateLimitedUsers(15));
    }

    @GetMapping("/recent-24h")
    public ResponseEntity<List<Map<String, String>>> getRecent24HourUsers() {
        return ResponseEntity.ok(adminFeatureService.getRecent24HourUsers());
    }

    @GetMapping("/blocked-ips")
    public ResponseEntity<List<BlockedIp>> getBlockedIps() {
        return ResponseEntity.ok(adminFeatureService.getBlockedIps());
    }

    @PostMapping("/block-ip")
    public ResponseEntity<Map<String, String>> blockIp(@RequestBody Map<String, String> payload) {
        String ipAddress = payload.get("ipAddress");
        String reason = payload.get("reason");
        adminFeatureService.blockIp(ipAddress, reason);
        return ResponseEntity.ok(Map.of("message", "IP Blocked successfully"));
    }

    @PostMapping("/unblock-ip")
    public ResponseEntity<Map<String, String>> unblockIp(@RequestBody Map<String, String> payload) {
        String ipAddress = payload.get("ipAddress");
        adminFeatureService.unblockIp(ipAddress);
        return ResponseEntity.ok(Map.of("message", "IP Unblocked successfully"));
    }
}
