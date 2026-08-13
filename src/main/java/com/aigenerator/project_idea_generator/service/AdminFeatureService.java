package com.aigenerator.project_idea_generator.service;

import com.aigenerator.project_idea_generator.model.ApiUsage;
import com.aigenerator.project_idea_generator.model.BlockedIp;
import com.aigenerator.project_idea_generator.model.ProjectIdea;
import com.aigenerator.project_idea_generator.repository.ApiUsageRepository;
import com.aigenerator.project_idea_generator.repository.BlockedIpRepository;
import com.aigenerator.project_idea_generator.repository.ProjectIdeaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminFeatureService {

    private final ApiUsageRepository apiUsageRepository;
    private final BlockedIpRepository blockedIpRepository;
    private final ProjectIdeaRepository projectIdeaRepository;

    public AdminFeatureService(ApiUsageRepository apiUsageRepository, BlockedIpRepository blockedIpRepository, ProjectIdeaRepository projectIdeaRepository) {
        this.apiUsageRepository = apiUsageRepository;
        this.blockedIpRepository = blockedIpRepository;
        this.projectIdeaRepository = projectIdeaRepository;
    }

    @Transactional
    public void recordApiCall(String endpointType) {
        LocalDate today = LocalDate.now();
        ApiUsage usage = apiUsageRepository.findByEndpointTypeAndCallDate(endpointType, today)
                .orElse(ApiUsage.builder()
                        .endpointType(endpointType)
                        .callDate(today)
                        .callCount(0)
                        .build());

        usage.setCallCount(usage.getCallCount() + 1);
        apiUsageRepository.save(usage);
    }

    public boolean isIpBlocked(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) return false;
        return blockedIpRepository.existsByIpAddress(ipAddress);
    }

    @Transactional
    public void blockIp(String ipAddress, String reason) {
        if (ipAddress != null && !ipAddress.isEmpty() && !blockedIpRepository.existsByIpAddress(ipAddress)) {
            BlockedIp blockedIp = BlockedIp.builder()
                    .ipAddress(ipAddress)
                    .reason(reason != null ? reason : "Manual block by admin")
                    .build();
            blockedIpRepository.save(blockedIp);
        }
    }

    @Transactional
    public void unblockIp(String ipAddress) {
        if (ipAddress != null && !ipAddress.isEmpty()) {
            blockedIpRepository.deleteByIpAddress(ipAddress);
        }
    }

    public List<BlockedIp> getBlockedIps() {
        return blockedIpRepository.findAll();
    }

    public Map<String, Object> getApiUsageStats() {
        LocalDate today = LocalDate.now();
        
        long ideaCallsToday = apiUsageRepository.findByEndpointTypeAndCallDate("IDEA", today)
                .map(ApiUsage::getCallCount).orElse(0L);
        long roadmapCallsToday = apiUsageRepository.findByEndpointTypeAndCallDate("ROADMAP", today)
                .map(ApiUsage::getCallCount).orElse(0L);
                
        // Simple monthly calculation: sum calls in current month
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        long ideaCallsMonth = apiUsageRepository.findAll().stream()
                .filter(u -> "IDEA".equals(u.getEndpointType()) && !u.getCallDate().isBefore(startOfMonth))
                .mapToLong(ApiUsage::getCallCount).sum();
                
        long roadmapCallsMonth = apiUsageRepository.findAll().stream()
                .filter(u -> "ROADMAP".equals(u.getEndpointType()) && !u.getCallDate().isBefore(startOfMonth))
                .mapToLong(ApiUsage::getCallCount).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("ideaCallsToday", ideaCallsToday);
        stats.put("roadmapCallsToday", roadmapCallsToday);
        stats.put("ideaCallsMonth", ideaCallsMonth);
        stats.put("roadmapCallsMonth", roadmapCallsMonth);
        return stats;
    }

    public List<Map<String, String>> getRecentUniqueUsers() {
        List<ProjectIdea> allIdeas = projectIdeaRepository.findAll();
        
        Map<String, Integer> ipCounts = new HashMap<>();
        Map<String, ProjectIdea> latestIdeaPerIp = new HashMap<>();
        
        for (ProjectIdea idea : allIdeas) {
            String ip = idea.getIpAddress();
            if (ip == null || ip.isEmpty()) continue;
            
            ipCounts.put(ip, ipCounts.getOrDefault(ip, 0) + 1);
            
            ProjectIdea existingLatest = latestIdeaPerIp.get(ip);
            if (existingLatest == null || 
                (idea.getCreatedAt() != null && existingLatest.getCreatedAt() != null && idea.getCreatedAt().isAfter(existingLatest.getCreatedAt()))) {
                latestIdeaPerIp.put(ip, idea);
            }
        }
        
        List<Map.Entry<String, Integer>> sortedIps = new ArrayList<>(ipCounts.entrySet());
        sortedIps.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // Descending by count
        
        List<Map<String, String>> topUsers = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedIps) {
            String ip = entry.getKey();
            int count = entry.getValue();
            ProjectIdea latestIdea = latestIdeaPerIp.get(ip);
            
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("ipAddress", ip);
            userInfo.put("userAgent", latestIdea.getUserAgent() != null ? latestIdea.getUserAgent() : "Unknown");
            userInfo.put("lastActive", latestIdea.getCreatedAt() != null ? latestIdea.getCreatedAt().toString() : "");
            userInfo.put("ideaCount", String.valueOf(count));
            userInfo.put("isBlocked", String.valueOf(blockedIpRepository.existsByIpAddress(ip)));
            topUsers.add(userInfo);
            
            if (topUsers.size() >= 50) break;
        }
        return topUsers;
    }

    public List<Map<String, String>> getRateLimitedUsers(int dailyLimit) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        List<ProjectIdea> todaysIdeas = projectIdeaRepository.findAll().stream()
                .filter(idea -> idea.getCreatedAt() != null && !idea.getCreatedAt().isBefore(startOfDay))
                .toList();

        Map<String, Integer> ipCounts = new HashMap<>();
        Map<String, ProjectIdea> latestIdeaPerIp = new HashMap<>();

        for (ProjectIdea idea : todaysIdeas) {
            String ip = idea.getIpAddress();
            if (ip == null || ip.isEmpty()) continue;

            ipCounts.put(ip, ipCounts.getOrDefault(ip, 0) + 1);

            ProjectIdea existingLatest = latestIdeaPerIp.get(ip);
            if (existingLatest == null || idea.getCreatedAt().isAfter(existingLatest.getCreatedAt())) {
                latestIdeaPerIp.put(ip, idea);
            }
        }

        List<Map<String, String>> rateLimitedUsers = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ipCounts.entrySet()) {
            if (entry.getValue() >= dailyLimit) {
                String ip = entry.getKey();
                ProjectIdea latestIdea = latestIdeaPerIp.get(ip);
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("ipAddress", ip);
                userInfo.put("userAgent", latestIdea.getUserAgent() != null ? latestIdea.getUserAgent() : "Unknown");
                userInfo.put("hitLimitAt", latestIdea.getCreatedAt().toString());
                userInfo.put("isBlocked", String.valueOf(blockedIpRepository.existsByIpAddress(ip)));
                rateLimitedUsers.add(userInfo);
            }
        }
        
        // Sort by hitLimitAt descending
        rateLimitedUsers.sort((u1, u2) -> u2.get("hitLimitAt").compareTo(u1.get("hitLimitAt")));
        return rateLimitedUsers;
    }

    public List<Map<String, String>> getRecent24HourUsers() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<ProjectIdea> recentIdeas = projectIdeaRepository.findAll().stream()
                .filter(idea -> idea.getCreatedAt() != null && idea.getCreatedAt().isAfter(twentyFourHoursAgo))
                .toList();

        Map<String, ProjectIdea> latestIdeaPerIp = new HashMap<>();

        for (ProjectIdea idea : recentIdeas) {
            String ip = idea.getIpAddress();
            if (ip == null || ip.isEmpty()) continue;

            ProjectIdea existingLatest = latestIdeaPerIp.get(ip);
            if (existingLatest == null || idea.getCreatedAt().isAfter(existingLatest.getCreatedAt())) {
                latestIdeaPerIp.put(ip, idea);
            }
        }

        List<Map<String, String>> recentUsers = new ArrayList<>();
        for (Map.Entry<String, ProjectIdea> entry : latestIdeaPerIp.entrySet()) {
            String ip = entry.getKey();
            ProjectIdea latestIdea = entry.getValue();
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("ipAddress", ip);
            userInfo.put("userAgent", latestIdea.getUserAgent() != null ? latestIdea.getUserAgent() : "Unknown");
            userInfo.put("lastActive", latestIdea.getCreatedAt().toString());
            userInfo.put("isBlocked", String.valueOf(blockedIpRepository.existsByIpAddress(ip)));
            recentUsers.add(userInfo);
        }

        // Sort by lastActive descending (most recent first)
        recentUsers.sort((u1, u2) -> u2.get("lastActive").compareTo(u1.get("lastActive")));
        return recentUsers;
    }
}
