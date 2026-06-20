package com.aigenerator.project_idea_generator.service;

import com.aigenerator.project_idea_generator.dto.HuggingFaceRequest;
import com.aigenerator.project_idea_generator.dto.HuggingFaceResponse;
import com.aigenerator.project_idea_generator.dto.ProjectGenerationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceService {

    private static final Logger log = LoggerFactory.getLogger(HuggingFaceService.class);
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.token}")
    private String apiToken;

    public HuggingFaceService() {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    public GeneratedIdea parseAndGenerateIdea(ProjectGenerationRequest request) {
        String prompt = buildPrompt(request);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "Qwen/Qwen2.5-7B-Instruct");
        requestBody.put("temperature", 0.9);
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        try {
            log.info("Sending request to Hugging Face API via router");
            byte[] responseBytes = restClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiToken)
                    .header("x-use-cache", "0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(byte[].class);

            JsonNode rootNode = objectMapper.readTree(responseBytes);

            if (rootNode != null && rootNode.has("choices") && rootNode.get("choices").isArray() && !rootNode.get("choices").isEmpty()) {
                String generatedText = rootNode.get("choices").get(0).path("message").path("content").asText();
                // Extract just the JSON part from the response if the model added extra text
                return extractJsonFromResponse(generatedText, prompt);
            }
        } catch (Exception e) {
            log.error("Error communicating with Hugging Face API. Using mock fallback.", e);
            return createMockIdea(request);
        }

        throw new RuntimeException("Empty response from AI.");
    }

    private GeneratedIdea createMockIdea(ProjectGenerationRequest request) {
        GeneratedIdea idea = new GeneratedIdea();
        idea.setProjectName("Mock " + request.getProjectDomain() + " Platform");
        idea.setProjectDescription("A comprehensive " + request.getProjectDomain() + " application built for a " + request.getSkillLevel() + " developer using " + request.getProgrammingLanguage() + " and " + request.getFramework() + ". (Note: This is a fallback mock idea since the AI API could not be reached).");
        idea.setKeyFeatures(List.of("User Authentication & Authorization", "Interactive Dashboard & Analytics", "Real-time Notifications", "Data Export & Reporting"));
        idea.setSuggestedTables(List.of("users (id, username, email, role)", "activities (id, user_id, type, timestamp)", "settings (id, user_id, preferences)"));
        idea.setRecommendedEndpoints(List.of("POST /api/auth/login", "GET /api/dashboard", "POST /api/activities", "PUT /api/settings"));
        idea.setLearningRoadmap(List.of("1. Initialize " + request.getFramework() + " project and configure DB", "2. Design database schema and entities", "3. Implement core REST endpoints and security", "4. Build responsive frontend UI", "5. Test and deploy application"));
        return idea;
    }

    private String buildPrompt(ProjectGenerationRequest request) {
        return "<s>[INST] You are an expert software architect. " +
               "Generate a highly creative, real-world project idea (e.g., Food Delivery App, AI SaaS, Fintech Dashboard) " +
               "specifically tailored for a " + request.getSkillLevel() + " developer using " + 
               request.getProgrammingLanguage() + " with the " + request.getFramework() + " framework " +
               "in the " + request.getProjectDomain() + " domain.\n\n" +
               "CRITICAL: You MUST write your ENTIRE response strictly in ENGLISH ONLY. Do not use Chinese or any other language.\n\n" +
               "Please provide a completely unique idea each time. (Request ID: " + java.util.UUID.randomUUID().toString() + ")\n" +
               "Make sure the complexity perfectly matches the '" + request.getSkillLevel() + "' level. " +
               "For the API endpoints, do not just limit it to REST! Depending on what is best for the project and framework, " +
               "you can suggest REST, GraphQL, gRPC, or WebSockets.\n\n" +
               "Respond ONLY with a valid JSON object matching exactly this schema. " +
               "Do NOT wrap the JSON in markdown code blocks or backticks. Just output the raw JSON string:\n" +
               "{\n" +
               "  \"projectName\": \"String (A catchy project title)\",\n" +
               "  \"projectDescription\": \"String (Detailed explanation of what the app does)\",\n" +
               "  \"keyFeatures\": [\"String\"],\n" +
               "  \"suggestedTables\": [\"String\"],\n" +
               "  \"recommendedEndpoints\": [\"String (e.g., REST, GraphQL, or WebSockets)\"],\n" +
               "  \"learningRoadmap\": [\"String (Step-by-step roadmap for this specific framework)\"]\n" +
               "} [/INST]";
    }

    private GeneratedIdea extractJsonFromResponse(String responseText, String prompt) {
        try {
            // Models often include the prompt in the response, we should remove it
            String cleanText = responseText;
            if (cleanText.startsWith(prompt)) {
                cleanText = cleanText.substring(prompt.length());
            }

            // Find the first '{' and last '}'
            int startIndex = cleanText.indexOf('{');
            int endIndex = cleanText.lastIndexOf('}');
            
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                String jsonStr = cleanText.substring(startIndex, endIndex + 1);
                JsonNode rootNode = objectMapper.readTree(jsonStr);
                
                GeneratedIdea idea = new GeneratedIdea();
                idea.setProjectName(rootNode.path("projectName").asText("Unnamed Project"));
                idea.setProjectDescription(rootNode.path("projectDescription").asText("No description provided."));
                
                idea.setKeyFeatures(extractList(rootNode, "keyFeatures"));
                idea.setSuggestedTables(extractList(rootNode, "suggestedTables"));
                idea.setRecommendedEndpoints(extractList(rootNode, "recommendedEndpoints"));
                idea.setLearningRoadmap(extractList(rootNode, "learningRoadmap"));
                
                return idea;
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI JSON response", e);
            throw new RuntimeException("AI generated an invalid format.");
        }
        
        throw new RuntimeException("Could not find JSON in AI response.");
    }
    
    private List<String> extractList(JsonNode rootNode, String fieldName) {
        List<String> list = new ArrayList<>();
        JsonNode arrayNode = rootNode.path(fieldName);
        if (arrayNode.isArray()) {
            for (JsonNode node : arrayNode) {
                list.add(node.asText());
            }
        }
        return list;
    }

    public static class GeneratedIdea {
        private String projectName;
        private String projectDescription;
        private List<String> keyFeatures;
        private List<String> suggestedTables;
        private List<String> recommendedEndpoints;
        private List<String> learningRoadmap;

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        public String getProjectDescription() { return projectDescription; }
        public void setProjectDescription(String projectDescription) { this.projectDescription = projectDescription; }
        public List<String> getKeyFeatures() { return keyFeatures; }
        public void setKeyFeatures(List<String> keyFeatures) { this.keyFeatures = keyFeatures; }
        public List<String> getSuggestedTables() { return suggestedTables; }
        public void setSuggestedTables(List<String> suggestedTables) { this.suggestedTables = suggestedTables; }
        public List<String> getRecommendedEndpoints() { return recommendedEndpoints; }
        public void setRecommendedEndpoints(List<String> recommendedEndpoints) { this.recommendedEndpoints = recommendedEndpoints; }
        public List<String> getLearningRoadmap() { return learningRoadmap; }
        public void setLearningRoadmap(List<String> learningRoadmap) { this.learningRoadmap = learningRoadmap; }
    }
}
