package com.aigenerator.project_idea_generator;

import com.aigenerator.project_idea_generator.dto.ProjectGenerationRequest;
import com.aigenerator.project_idea_generator.model.ProjectIdea;
import com.aigenerator.project_idea_generator.repository.ProjectIdeaRepository;
import com.aigenerator.project_idea_generator.repository.UserRepository;
import com.aigenerator.project_idea_generator.service.AdminFeatureService;
import com.aigenerator.project_idea_generator.service.HistoryService;
import com.aigenerator.project_idea_generator.service.ProjectIdeaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectIdeaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectIdeaService service;

    @MockitoBean
    private HistoryService historyService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProjectIdeaRepository ideaRepository;

    @MockitoBean
    private AdminFeatureService adminFeatureService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetHistoryWithoutSessionIdReturnsNoHistoryMessage() throws Exception {
        mockMvc.perform(get("/api/projects/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("no history"));
    }

    @Test
    void testGetHistoryWithEmptySessionReturnsNoHistoryMessage() throws Exception {
        mockMvc.perform(get("/api/projects/history").header("X-Session-Id", "test_session_empty_12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("no history"));
    }

    @Test
    void testGetSavedIdeasUnauthenticatedReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects/saved"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void testSaveIdeaUnauthenticatedReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/projects/1/save"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void testGenerateIdeaSuccess() throws Exception {
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        request.setSkillLevel("Beginner");
        request.setProgrammingLanguage("Java");
        request.setFramework("Spring");
        request.setProjectDomain("Web");
        
        ProjectIdea idea = new ProjectIdea();
        idea.setProjectName("Test Idea");
        
        Mockito.when(adminFeatureService.isIpBlocked(anyString())).thenReturn(false);
        Mockito.when(ideaRepository.countByIpAddressAndCreatedAtAfter(anyString(), any(LocalDateTime.class))).thenReturn(5L);
        Mockito.when(service.generateAndSaveProjectIdea(any(ProjectGenerationRequest.class), anyString(), any())).thenReturn(idea);

        mockMvc.perform(post("/api/projects/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Test Idea"));
    }

    @Test
    void testGenerateIdeaRateLimited() throws Exception {
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        request.setSkillLevel("Beginner");
        request.setProgrammingLanguage("Java");
        request.setFramework("Spring");
        request.setProjectDomain("Web");
        
        Mockito.when(adminFeatureService.isIpBlocked(anyString())).thenReturn(false);
        Mockito.when(ideaRepository.countByIpAddressAndCreatedAtAfter(anyString(), any(LocalDateTime.class))).thenReturn(1001L); // above 1000 limit

        mockMvc.perform(post("/api/projects/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void testGenerateIdeaBlockedIp() throws Exception {
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        request.setSkillLevel("Beginner");
        request.setProgrammingLanguage("Java");
        request.setFramework("Spring");
        request.setProjectDomain("Web");
        
        Mockito.when(adminFeatureService.isIpBlocked(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/projects/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetProjectById() throws Exception {
        ProjectIdea idea = new ProjectIdea();
        idea.setProjectName("Find Me");
        
        Mockito.when(service.getProjectById(1L)).thenReturn(idea);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Find Me"));
    }

    @Test
    void testGenerateRoadmapSuccess() throws Exception {
        ProjectIdea idea = new ProjectIdea();
        idea.setDetailedRoadmap("Step 1: Do something");
        
        Mockito.when(adminFeatureService.isIpBlocked(anyString())).thenReturn(false);
        Mockito.when(ideaRepository.countByIpAddressAndCreatedAtAfter(anyString(), any(LocalDateTime.class))).thenReturn(5L);
        Mockito.when(service.generateAndSaveRoadmap(1L)).thenReturn(idea);

        mockMvc.perform(post("/api/projects/1/roadmap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailedRoadmap").value("Step 1: Do something"));
    }
}
