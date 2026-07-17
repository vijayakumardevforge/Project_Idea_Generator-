package com.aigenerator.project_idea_generator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectIdeaControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}
