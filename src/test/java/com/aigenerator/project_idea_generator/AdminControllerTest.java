package com.aigenerator.project_idea_generator;

import com.aigenerator.project_idea_generator.model.BlockedIp;
import com.aigenerator.project_idea_generator.model.FailedLoginAttempt;
import com.aigenerator.project_idea_generator.model.Feedback;
import com.aigenerator.project_idea_generator.repository.FailedLoginAttemptRepository;
import com.aigenerator.project_idea_generator.repository.ProjectIdeaRepository;
import com.aigenerator.project_idea_generator.service.AdminFeatureService;
import com.aigenerator.project_idea_generator.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedbackService feedbackService;

    @MockitoBean
    private FailedLoginAttemptRepository failedLoginAttemptRepository;

    @MockitoBean
    private ProjectIdeaRepository projectIdeaRepository;

    @MockitoBean
    private AdminFeatureService adminFeatureService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetStats() throws Exception {
        Mockito.when(projectIdeaRepository.countDistinctUsersToday(any(LocalDateTime.class))).thenReturn(5L);
        Mockito.when(projectIdeaRepository.countByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(10L);
        Mockito.when(projectIdeaRepository.countDistinctUsers()).thenReturn(50L);
        Mockito.when(projectIdeaRepository.count()).thenReturn(100L);

        mockMvc.perform(get("/api/admin/stats").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsersToday").value(5))
                .andExpect(jsonPath("$.totalIdeasToday").value(10))
                .andExpect(jsonPath("$.totalUsers").value(50))
                .andExpect(jsonPath("$.totalIdeas").value(100));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllFeedback() throws Exception {
        Feedback feedback = new Feedback();
        feedback.setMessage("Great app");
        Mockito.when(feedbackService.getAllFeedback()).thenReturn(List.of(feedback));

        mockMvc.perform(get("/api/admin/feedback").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Great app"));
    }

    @Test
    void testGetFailedLogins() throws Exception {
        FailedLoginAttempt attempt = new FailedLoginAttempt();
        attempt.setUsername("testuser");
        Mockito.when(failedLoginAttemptRepository.findAllByOrderByAttemptTimeDesc()).thenReturn(List.of(attempt));

        mockMvc.perform(get("/api/admin/failed-logins").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void testGetApiUsage() throws Exception {
        Mockito.when(adminFeatureService.getApiUsageStats()).thenReturn(Map.of("totalCalls", 100));

        mockMvc.perform(get("/api/admin/api-usage").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalls").value(100));
    }

    @Test
    void testGetRecentUsers() throws Exception {
        Mockito.when(adminFeatureService.getRecentUniqueUsers()).thenReturn(List.of(Map.of("ip", "127.0.0.1")));

        mockMvc.perform(get("/api/admin/recent-users").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ip").value("127.0.0.1"));
    }

    @Test
    void testGetRateLimitedUsers() throws Exception {
        Mockito.when(adminFeatureService.getRateLimitedUsers(anyInt())).thenReturn(List.of(Map.of("ip", "192.168.1.1")));

        mockMvc.perform(get("/api/admin/rate-limited-users").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ip").value("192.168.1.1"));
    }

    @Test
    void testGetRecent24HourUsers() throws Exception {
        Mockito.when(adminFeatureService.getRecent24HourUsers()).thenReturn(List.of(Map.of("ip", "10.0.0.1")));

        mockMvc.perform(get("/api/admin/recent-24h").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ip").value("10.0.0.1"));
    }

    @Test
    void testGetBlockedIps() throws Exception {
        BlockedIp blockedIp = new BlockedIp();
        blockedIp.setIpAddress("1.1.1.1");
        Mockito.when(adminFeatureService.getBlockedIps()).thenReturn(List.of(blockedIp));

        mockMvc.perform(get("/api/admin/blocked-ips").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ipAddress").value("1.1.1.1"));
    }

    @Test
    void testBlockIp() throws Exception {
        Map<String, String> request = Map.of("ipAddress", "1.1.1.1", "reason", "spam");

        mockMvc.perform(post("/api/admin/block-ip").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("IP Blocked successfully"));
        
        Mockito.verify(adminFeatureService).blockIp("1.1.1.1", "spam");
    }

    @Test
    void testUnblockIp() throws Exception {
        Map<String, String> request = Map.of("ipAddress", "1.1.1.1");

        mockMvc.perform(post("/api/admin/unblock-ip").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("IP Unblocked successfully"));
        
        Mockito.verify(adminFeatureService).unblockIp("1.1.1.1");
    }
}
