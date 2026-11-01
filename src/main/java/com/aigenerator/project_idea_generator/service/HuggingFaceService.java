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
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HuggingFaceService {

    private static final Logger log = LoggerFactory.getLogger(HuggingFaceService.class);
    private static final List<String> MODELS = List.of(
            "openai/gpt-oss-120b",
            "openai/gpt-oss-20b"
    );
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final AdminFeatureService adminFeatureService;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.token.idea}")
    private String apiTokenIdea;

    @Value("${huggingface.api.token.roadmap}")
    private String apiTokenRoadmap;

    public HuggingFaceService(AdminFeatureService adminFeatureService) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(40000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
        this.objectMapper = new ObjectMapper();
        this.adminFeatureService = adminFeatureService;
    }

    public GeneratedIdea parseAndGenerateIdea(ProjectGenerationRequest request) {
        if (request.getCustomProjectTitle() != null) {
            String title = request.getCustomProjectTitle().trim();
            if (!title.isEmpty()) {
                String lower = title.toLowerCase();
                boolean hasVowels = lower.matches(".*[aeiouy].*");
                if (!hasVowels && lower.replaceAll("[^a-z]", "").length() > 4) {
                    log.warn("Instant gibberish block for title: {}", title);
                    throw new IllegalArgumentException("INVALID_INPUT");
                }
            }
        }
        
        String prompt = buildPrompt(request);

        for (String model : MODELS) {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1800);
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            try {
                log.info("Sending idea request to AI Router using model '{}'", model);
                adminFeatureService.recordApiCall("IDEA");
                byte[] responseBytes = restClient.post()
                        .uri(apiUrl)
                        .header("Authorization", "Bearer " + apiTokenIdea)
                        .header("x-use-cache", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(byte[].class);

                String rawResponse = new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8);
                log.info("Raw API response (idea, model {}): {}", model, rawResponse.length() > 500 ? rawResponse.substring(0, 500) : rawResponse);
                JsonNode rootNode = objectMapper.readTree(responseBytes);

                if (rootNode != null && rootNode.has("choices") && rootNode.get("choices").isArray() && !rootNode.get("choices").isEmpty()) {
                    String generatedText = rootNode.get("choices").get(0).path("message").path("content").asText();
                    log.info("Generated idea text (first 300 chars): {}", generatedText.length() > 300 ? generatedText.substring(0, 300) : generatedText);
                    return extractJsonFromResponse(generatedText, prompt);
                } else {
                    log.warn("Unexpected API response structure from model {}: {}", model, rawResponse);
                }
            } catch (IllegalArgumentException e) {
                log.warn("Invalid input detected: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.warn("Error with model '{}' for idea generation: {}. Trying fallback model if available.", model, e.getMessage());
            }
        }

        log.error("All AI models failed for idea generation. Using mock fallback.");
        return createMockIdea(request);
    }

    public String generateDetailedRoadmap(com.aigenerator.project_idea_generator.model.ProjectIdea idea) {
        String prompt = buildRoadmapPrompt(idea);

        for (String model : MODELS) {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1200);
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            try {
                log.info("Sending roadmap request to AI Router using model '{}'", model);
                adminFeatureService.recordApiCall("ROADMAP");
                byte[] responseBytes = restClient.post()
                        .uri(apiUrl)
                        .header("Authorization", "Bearer " + apiTokenRoadmap)
                        .header("x-use-cache", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(byte[].class);

                String rawResponse = new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8);
                log.info("Raw API response (roadmap, model {}): {}", model, rawResponse.length() > 500 ? rawResponse.substring(0, 500) : rawResponse);
                JsonNode rootNode = objectMapper.readTree(responseBytes);

                if (rootNode != null && rootNode.has("choices") && rootNode.get("choices").isArray() && !rootNode.get("choices").isEmpty()) {
                    String generatedText = rootNode.get("choices").get(0).path("message").path("content").asText();
                    if (generatedText != null && !generatedText.trim().isEmpty()) {
                        log.info("Generated roadmap text (first 300 chars): {}", generatedText.length() > 300 ? generatedText.substring(0, 300) : generatedText);
                        return cleanMarkdownResponse(generatedText, prompt);
                    }
                } else {
                    log.warn("Unexpected roadmap API response structure from model {}: {}", model, rawResponse);
                }
            } catch (Exception e) {
                log.warn("Error with model '{}' for roadmap generation: {}. Trying fallback model if available.", model, e.getMessage());
            }
        }

        log.error("All models exhausted for roadmap. Using mock fallback.");
        return "## Implementation Plan for " + idea.getProjectName() + "\n\n" +
               "*(Mock Roadmap generated due to API error)*\n\n" +
               "### Sprint 1: Setup\n- Initialize the " + idea.getFramework() + " project\n- Setup Database\n\n" +
               "### Sprint 2: Core Features\n- Implement core logic and APIs\n\n" +
               "### Sprint 3: UI & Polish\n- Build frontend\n- Final testing";
    }

    private String buildRoadmapPrompt(com.aigenerator.project_idea_generator.model.ProjectIdea idea) {
        String skillLevel = idea.getSkillLevel() != null ? idea.getSkillLevel() : "Beginner";
        StringBuilder prompt = new StringBuilder();

        // Concise system context — avoids wasting output tokens on preamble
        prompt.append("You are an expert Technical Project Manager. Write a concise, actionable 4-sprint implementation roadmap.\n\n");
        prompt.append("**Project:** ").append(idea.getProjectName()).append("\n");
        prompt.append("**Description:** ").append(idea.getProjectDescription()).append("\n");
        prompt.append("**Tech Stack:** ").append(idea.getProgrammingLanguage()).append(" / ").append(idea.getFramework()).append("\n");
        prompt.append("**Developer Level:** ").append(skillLevel).append("\n\n");

        prompt.append("STRICT RULES:\n");
        prompt.append("- Write EXACTLY 4 sprints (Sprint 1 through Sprint 4). Each sprint has a clear ## heading and 5-6 bullet points.\n");
        prompt.append("- Each bullet is one action sentence. No code blocks. No code snippets.\n");
        prompt.append("- Tailor complexity to '").append(skillLevel).append("' level:\n");

        if ("Beginner".equalsIgnoreCase(skillLevel)) {
            prompt.append("  Beginner: Use only built-in framework features — routing, ORM, templates, forms, built-in auth. ")
                  .append("NO WebSockets, NO async brokers, NO Docker, NO microservices.\n");
        } else if ("Intermediate".equalsIgnoreCase(skillLevel)) {
            prompt.append("  Intermediate: REST APIs, JWT/session auth, caching, database indexing, basic testing.\n");
        } else {
            prompt.append("  Advanced: Microservices, async messaging, containerization, CI/CD, scalability.\n");
        }

        prompt.append("- Sprint 4 MUST cover testing, final polish, and local/cloud deployment.\n");
        prompt.append("- Write in English only. Output clean Markdown. Do NOT truncate — complete all 4 sprints.\n");

        return prompt.toString();
    }

    private String cleanMarkdownResponse(String responseText, String prompt) {
        String cleanText = responseText;
        if (cleanText.startsWith(prompt)) {
            cleanText = cleanText.substring(prompt.length());
        }
        return cleanText.trim();
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
        String prompt;
        if (request.getCustomProjectTitle() != null && !request.getCustomProjectTitle().trim().isEmpty()) {
            prompt = "You are an expert software architect. " +
                   "A user has provided the following custom project title: '" + request.getCustomProjectTitle() + "'.\n\n" +
                   "STEP 1 (CRITICAL GIBBERISH CHECK): Evaluate if the title is valid. If it is random keyboard mashing, meaningless characters (like 'sdfuakfasfi' or 'dsgssdfrt'), or completely nonsensical, " +
                   "you MUST abort the generation and output ONLY this exact string: {\"projectName\": \"INVALID_INPUT\"}\n\n" +
                   "STEP 2: If the title is valid, generate a highly creative, real-world project idea based on it. " +
                   "The project should be specifically tailored for a " + request.getSkillLevel() + " developer using " + 
                   request.getProgrammingLanguage() + " with the " + request.getFramework() + " framework.\n\n";
        } else {
            prompt = "You are an expert software architect. " +
                   "Generate a highly creative, real-world project idea (e.g., Food Delivery App, AI SaaS, Fintech Dashboard) " +
                   "specifically tailored for a " + request.getSkillLevel() + " developer using " + 
                   request.getProgrammingLanguage() + " with the " + request.getFramework() + " framework " +
                   "in the " + request.getProjectDomain() + " domain.\n\n";
        }
               
        if (request.getPreviousIdeaName() != null && !request.getPreviousIdeaName().trim().isEmpty()) {
            prompt += "CRITICAL: Do NOT generate the idea named '" + request.getPreviousIdeaName() + "' or anything similar to it. You MUST give a completely new and different idea.\n\n";
        }
        
        prompt += "CRITICAL INSTRUCTION: You MUST write your ENTIRE response strictly in ENGLISH ONLY. Do not use Chinese, Spanish, or any other language under any circumstances.\n\n" +
               "Please provide a completely unique idea each time. (Request ID: " + java.util.UUID.randomUUID().toString() + ")\n" +
               "Keep lists focused: 4 to 6 keyFeatures, 4 to 6 suggestedTables, 4 to 6 recommendedEndpoints, and 5 to 6 learningRoadmap steps.\n\n" +
               "Make sure the architectural complexity strictly matches the developer's '" + request.getSkillLevel() + "' level:\n" +
               "- If 'Beginner': Suggest foundational, clean concepts (standard RESTful endpoints or MVC routes, clear relational tables). DO NOT suggest advanced or complex protocols like WebSockets, gRPC, or async distributed brokers.\n" +
               "- If 'Intermediate': Suggest modern industry-standard patterns (RESTful APIs, secure auth, caching, relational/document DBs, and optional WebSockets or GraphQL if genuinely needed).\n" +
               "- If 'Advanced': Suggest enterprise architectures (microservices or modular monolith, GraphQL, gRPC, WebSockets, event-driven streaming, caching, and CI/CD).\n\n" +
               "If you aborted in STEP 1, respond ONLY with {\"projectName\": \"INVALID_INPUT\"}.\n" +
               "Otherwise, respond ONLY with a valid JSON object matching exactly this schema. " +
               "Do NOT wrap the JSON in markdown code blocks or backticks. Just output the raw JSON string:\n" +
               "{\n" +
               "  \"projectName\": \"String (A catchy project title)\",\n" +
               "  \"projectDescription\": \"String (Detailed explanation of what the app does)\",\n" +
               "  \"keyFeatures\": [\"String\"],\n" +
               "  \"suggestedTables\": [\"String\"],\n" +
               "  \"recommendedEndpoints\": [\"String (e.g., REST, GraphQL, or WebSockets)\"],\n" +
               "  \"learningRoadmap\": [\"String (Step-by-step roadmap for this specific framework)\"]\n" +
               "}";
               
        return prompt;
    }

    private GeneratedIdea extractJsonFromResponse(String responseText, String prompt) {
        if (responseText == null || responseText.isBlank()) {
            throw new RuntimeException("Empty AI response.");
        }

        String cleanText = responseText.trim();
        if (cleanText.startsWith(prompt)) {
            cleanText = cleanText.substring(prompt.length()).trim();
        }

        // 1. Try standard Jackson JSON parsing
        int startIndex = cleanText.indexOf('{');
        int endIndex = cleanText.lastIndexOf('}');
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String jsonStr = cleanText.substring(startIndex, endIndex + 1);
            try {
                JsonNode rootNode = objectMapper.readTree(jsonStr);
                GeneratedIdea idea = buildIdeaFromNode(rootNode);
                if (idea != null) {
                    return idea;
                }
            } catch (IllegalArgumentException e) {
                throw e; // propagates INVALID_INPUT
            } catch (JsonProcessingException e) {
                log.warn("Jackson parse failed, attempting regex recovery: {}", e.getMessage());
            }
        }

        // 2. Robust Regex Fallback - extracts fields directly from raw model output
        GeneratedIdea regexIdea = extractIdeaWithRegex(cleanText);
        if (regexIdea != null) {
            log.info("Successfully recovered idea via regex parser: '{}'", regexIdea.getProjectName());
            return regexIdea;
        }

        throw new RuntimeException("Could not parse AI response into a valid project idea.");
    }

    private GeneratedIdea buildIdeaFromNode(JsonNode rootNode) {
        String pName = rootNode.path("projectName").asText("").trim();
        if ("INVALID_INPUT".equalsIgnoreCase(pName)) {
            throw new IllegalArgumentException("INVALID_INPUT");
        }
        if (pName.isEmpty() || "Unnamed Project".equals(pName)) {
            return null;
        }

        GeneratedIdea idea = new GeneratedIdea();
        idea.setProjectName(pName);
        idea.setProjectDescription(rootNode.path("projectDescription").asText("A modern web application."));
        idea.setKeyFeatures(extractList(rootNode, "keyFeatures"));
        idea.setSuggestedTables(extractList(rootNode, "suggestedTables"));
        idea.setRecommendedEndpoints(extractList(rootNode, "recommendedEndpoints"));
        idea.setLearningRoadmap(extractList(rootNode, "learningRoadmap"));
        return idea;
    }

    private GeneratedIdea extractIdeaWithRegex(String text) {
        String projectName = matchSingle(text, "\"projectName\"\\s*:\\s*\"([^\"]+)\"");
        if (projectName == null || projectName.isBlank()) {
            return null;
        }
        if ("INVALID_INPUT".equalsIgnoreCase(projectName)) {
            throw new IllegalArgumentException("INVALID_INPUT");
        }

        GeneratedIdea idea = new GeneratedIdea();
        idea.setProjectName(projectName.trim());

        String desc = matchSingle(text, "\"projectDescription\"\\s*:\\s*\"([^\"]+)\"");
        idea.setProjectDescription(desc != null ? desc.trim() : "A modern web application.");

        idea.setKeyFeatures(matchArray(text, "keyFeatures"));
        idea.setSuggestedTables(matchArray(text, "suggestedTables"));
        idea.setRecommendedEndpoints(matchArray(text, "recommendedEndpoints"));
        idea.setLearningRoadmap(matchArray(text, "learningRoadmap"));

        return idea;
    }

    private String matchSingle(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private List<String> matchArray(String text, String fieldName) {
        List<String> list = new ArrayList<>();
        Pattern arrayPattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\\[([\\s\\S]*?)\\]");
        Matcher arrayMatcher = arrayPattern.matcher(text);
        if (arrayMatcher.find()) {
            String itemsContent = arrayMatcher.group(1);
            Pattern itemPattern = Pattern.compile("\"([^\"]+)\"");
            Matcher itemMatcher = itemPattern.matcher(itemsContent);
            while (itemMatcher.find()) {
                String val = itemMatcher.group(1).trim();
                if (!val.isEmpty()) {
                    list.add(val);
                }
            }
        }
        return list;
    }
    
    private List<String> extractList(JsonNode rootNode, String fieldName) {
        List<String> list = new ArrayList<>();
        JsonNode node = rootNode.path(fieldName);
        if (node.isArray()) {
            for (JsonNode item : node) {
                list.add(item.asText());
            }
        } else if (node.isTextual()) {
            String text = node.asText();
            for (String line : text.split("\\r?\\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    list.add(trimmed);
                }
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
