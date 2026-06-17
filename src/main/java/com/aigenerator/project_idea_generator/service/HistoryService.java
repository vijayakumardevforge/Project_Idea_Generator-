package com.aigenerator.project_idea_generator.service;

import com.aigenerator.project_idea_generator.model.ProjectIdea;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class HistoryService {

    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper mapper;
    
    // Key prefix to organize our Redis keys
    private static final String HISTORY_PREFIX = "history:session:";
    
    public HistoryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        
        // Initialize the ObjectMapper once
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Saves a generated idea into the user's specific history cache in Redis.
     */
    public void saveIdeaToHistory(String sessionId, ProjectIdea idea) {
        if (sessionId == null || sessionId.isEmpty()) {
            return;
        }
        
        String key = HISTORY_PREFIX + sessionId;
        
        try {
            String jsonIdea = mapper.writeValueAsString(idea);
            
            // Append the new idea (as a JSON string) to the list stored at this key
            redisTemplate.opsForList().leftPush(key, jsonIdea);
            
            // Set the entire list to expire after 24 hours
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
            
            logger.info("Successfully saved idea to history for session {}", sessionId);
        } catch (Exception e) {
            logger.error("Failed to save history to Redis for session {}: {}", sessionId, e.getMessage());
        }
    }

    /**
     * Retrieves the user's history from the Redis cache.
     */
    public List<ProjectIdea> getUserHistory(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return new ArrayList<>();
        }
        
        String key = HISTORY_PREFIX + sessionId;
        
        try {
            // Retrieve all items in the list (0 to -1 means all elements)
            List<String> cachedData = redisTemplate.opsForList().range(key, 0, -1);
            
            List<ProjectIdea> history = new ArrayList<>();
            if (cachedData != null) {
                for (String jsonStr : cachedData) {
                    try {
                        ProjectIdea idea = mapper.readValue(jsonStr, ProjectIdea.class);
                        history.add(idea);
                    } catch (Exception e) {
                        logger.warn("Skipping unparseable history item: {}", e.getMessage());
                    }
                }
            }
            return history;
        } catch (Exception e) {
            logger.error("Failed to fetch history from Redis for session {}: {}", sessionId, e.getMessage());
            // If the old format is completely corrupt, reset the key
            try {
                redisTemplate.delete(key);
            } catch (Exception ex) {
                // Ignore delete failures
            }
            return new ArrayList<>();
        }
    }
}
