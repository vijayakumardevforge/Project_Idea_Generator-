package com.aigenerator.project_idea_generator.repository;

import com.aigenerator.project_idea_generator.model.ProjectIdea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

@Repository
public interface ProjectIdeaRepository extends JpaRepository<ProjectIdea, Long> {
    List<ProjectIdea> findAllByOrderByCreatedAtDesc();

    long countByCreatedAtAfter(LocalDateTime date);

    @Query("SELECT COUNT(DISTINCT p.ipAddress) FROM ProjectIdea p WHERE p.createdAt >= :date")
    long countDistinctUsersToday(@Param("date") LocalDateTime date);

    List<ProjectIdea> findByUserOrderByCreatedAtDesc(com.aigenerator.project_idea_generator.model.User user);
}
