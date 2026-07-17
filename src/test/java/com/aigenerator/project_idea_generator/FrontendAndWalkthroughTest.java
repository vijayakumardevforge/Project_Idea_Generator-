package com.aigenerator.project_idea_generator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FrontendAndWalkthroughTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testIndexHtmlStructureAndRequirements() throws Exception {
        MvcResult result = mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        // 1. Verify Portal structure exists
        assertTrue(content.contains("id=\"intro-portal\""), "index.html should contain #intro-portal");
        assertTrue(content.contains("id=\"enter-portal-btn\""), "index.html should contain #enter-portal-btn");
        assertTrue(content.contains("Enter Innovation Workspace"), "Enter portal button should feature clean text without arrow icon");
        assertFalse(content.contains("fa-arrow-right-long"), "Enter portal button arrow icon should be removed");

        // 2. Verify Skip button is removed from entrance portal
        assertFalse(content.contains("id=\"skip-intro-btn\""), "Skip button (#skip-intro-btn) must be removed from entrance portal");

        // 3. Verify Replay Intro link is removed from top navigation bar
        assertFalse(content.contains("id=\"replay-intro-link\""), "Replay Intro link (#replay-intro-link) must be removed from top navbar");

        // 4. Verify Guide link (#start-tour-link) is present on navigation bar
        assertTrue(content.contains("id=\"start-tour-link\""), "Guide link (#start-tour-link) must be present in navbar");
        assertTrue(content.contains("Guide"), "Navbar link should say 'Guide'");

        // 5. Verify Interactive Step-by-Step Onboarding Walkthrough Tour structure
        assertTrue(content.contains("id=\"onboarding-tour-overlay\""), "index.html should contain #onboarding-tour-overlay");
        assertTrue(content.contains("id=\"tour-step-indicator\""), "index.html should contain #tour-step-indicator");
        assertTrue(content.contains("id=\"tour-title\""), "index.html should contain #tour-title");
        assertTrue(content.contains("id=\"tour-description\""), "index.html should contain #tour-description");
        assertTrue(content.contains("id=\"tour-visual\""), "index.html should contain #tour-visual");
        assertTrue(content.contains("id=\"tour-prev-btn\""), "index.html should contain #tour-prev-btn");
        assertTrue(content.contains("id=\"tour-next-btn\""), "index.html should contain #tour-next-btn");
        assertTrue(content.contains("id=\"tour-dots\""), "index.html should contain #tour-dots");
    }

    @Test
    void testStyleCssForWalkthroughAndResponsiveDesign() throws Exception {
        MvcResult result = mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk())
                .andReturn();

        String css = result.getResponse().getContentAsString();

        // 1. Verify walkthrough overlay and card styling
        assertTrue(css.contains(".tour-overlay"), "style.css should style .tour-overlay");
        assertTrue(css.contains(".tour-card"), "style.css should style .tour-card");
        assertTrue(css.contains("backdrop-filter: blur(14px)"), "style.css should feature high quality backdrop blur");

        // 2. Verify spring micro-animations for step transitions
        assertTrue(css.contains(".tour-body.tour-step-transitioning"), "style.css should feature transitioning state");
        assertTrue(css.contains(".tour-body.tour-step-animate-in"), "style.css should feature spring animate-in state");
        assertTrue(css.contains("tourStepSpringIn"), "style.css should contain tourStepSpringIn keyframes");

        // 3. Verify mobile responsive queries
        assertTrue(css.contains("@media (max-width: 640px)"), "style.css should contain responsive @media query for mobile");
        assertTrue(css.contains(".tour-actions"), "style.css should style .tour-actions for mobile layout");
    }

    @Test
    void testAppJsForWalkthroughLogicAndAutoTrigger() throws Exception {
        MvcResult result = mockMvc.perform(get("/js/app.js"))
                .andExpect(status().isOk())
                .andReturn();

        String js = result.getResponse().getContentAsString();

        // 1. Verify localStorage state checks
        assertTrue(js.contains("hasSeenIntro_v2"), "app.js should check localStorage for hasSeenIntro_v2");
        assertTrue(js.contains("hasSeenTour_v1"), "app.js should check localStorage for hasSeenTour_v1");

        // 2. Verify openTourModal is exposed and handles steps
        assertTrue(js.contains("window.openTourModal"), "app.js should define window.openTourModal");
        assertTrue(js.contains("tourStepsData"), "app.js should contain tourStepsData array");

        // 3. Verify step transitioning logic
        assertTrue(js.contains("tour-step-transitioning"), "app.js should add/remove tour-step-transitioning class during step render");
        assertTrue(js.contains("tour-step-animate-in"), "app.js should add/remove tour-step-animate-in class during step render");
    }
}
