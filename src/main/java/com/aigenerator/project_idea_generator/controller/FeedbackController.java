package com.aigenerator.project_idea_generator.controller;

import com.aigenerator.project_idea_generator.model.Feedback;
import com.aigenerator.project_idea_generator.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback) {
        if (feedback.getStars() == null || feedback.getStars() < 1 || feedback.getStars() > 5) {
            return ResponseEntity.badRequest().body("Invalid star rating");
        }
        if (feedback.getEmail() == null || feedback.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (feedback.getMessage() == null || feedback.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }

        Feedback saved = feedbackService.saveFeedback(feedback);
        return ResponseEntity.ok(saved);
    }
}
