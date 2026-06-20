package com.aigenerator.project_idea_generator.config;

import com.aigenerator.project_idea_generator.model.FailedLoginAttempt;
import com.aigenerator.project_idea_generator.repository.FailedLoginAttemptRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final FailedLoginAttemptRepository repository;
    private final HttpServletRequest request;

    public AuthenticationFailureListener(FailedLoginAttemptRepository repository, HttpServletRequest request) {
        this.repository = repository;
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        
        String ipAddress = "";
        Object details = event.getAuthentication().getDetails();
        if (details instanceof WebAuthenticationDetails) {
            ipAddress = ((WebAuthenticationDetails) details).getRemoteAddress();
        } else {
            // Fallback
            ipAddress = request.getRemoteAddr();
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader != null) {
                ipAddress = xfHeader.split(",")[0];
            }
        }

        FailedLoginAttempt attempt = FailedLoginAttempt.builder()
                .username(username)
                .ipAddress(ipAddress)
                .build();

        repository.save(attempt);
    }
}
