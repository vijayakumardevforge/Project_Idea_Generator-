package com.aigenerator.project_idea_generator.dto;

public class DashboardStats {
    private long totalUsersToday;
    private long totalIdeasToday;
    private long totalUsers;
    private long totalIdeas;

    public DashboardStats() {}

    public DashboardStats(long totalUsersToday, long totalIdeasToday, long totalUsers, long totalIdeas) {
        this.totalUsersToday = totalUsersToday;
        this.totalIdeasToday = totalIdeasToday;
        this.totalUsers = totalUsers;
        this.totalIdeas = totalIdeas;
    }

    // Getters and Setters
    public long getTotalUsersToday() { return totalUsersToday; }
    public void setTotalUsersToday(long totalUsersToday) { this.totalUsersToday = totalUsersToday; }
    public long getTotalIdeasToday() { return totalIdeasToday; }
    public void setTotalIdeasToday(long totalIdeasToday) { this.totalIdeasToday = totalIdeasToday; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalIdeas() { return totalIdeas; }
    public void setTotalIdeas(long totalIdeas) { this.totalIdeas = totalIdeas; }

    // Builder pattern
    public static DashboardStatsBuilder builder() { return new DashboardStatsBuilder(); }

    public static class DashboardStatsBuilder {
        private long totalUsersToday;
        private long totalIdeasToday;
        private long totalUsers;
        private long totalIdeas;

        public DashboardStatsBuilder totalUsersToday(long totalUsersToday) { this.totalUsersToday = totalUsersToday; return this; }
        public DashboardStatsBuilder totalIdeasToday(long totalIdeasToday) { this.totalIdeasToday = totalIdeasToday; return this; }
        public DashboardStatsBuilder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public DashboardStatsBuilder totalIdeas(long totalIdeas) { this.totalIdeas = totalIdeas; return this; }

        public DashboardStats build() {
            return new DashboardStats(totalUsersToday, totalIdeasToday, totalUsers, totalIdeas);
        }
    }
}
