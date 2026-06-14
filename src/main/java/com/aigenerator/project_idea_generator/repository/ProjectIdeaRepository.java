package com.aigenerator.project_idea_generator.repository;

import com.aigenerator.project_idea_generator.model.ProjectIdea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectIdeaRepository extends JpaRepository<ProjectIdea, Long> {
    List<ProjectIdea> findAllByOrderByCreatedAtDesc();
}
