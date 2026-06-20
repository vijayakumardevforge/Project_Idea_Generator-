package com.aigenerator.project_idea_generator.service;

import com.aigenerator.project_idea_generator.model.Feedback;
import com.aigenerator.project_idea_generator.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository repository;

    public FeedbackService(FeedbackRepository repository) {
        this.repository = repository;
    }

    public Feedback saveFeedback(Feedback feedback) {
        return repository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
}
