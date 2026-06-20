package com.aigenerator.project_idea_generator.repository;

import com.aigenerator.project_idea_generator.model.FailedLoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedLoginAttemptRepository extends JpaRepository<FailedLoginAttempt, Long> {
    List<FailedLoginAttempt> findAllByOrderByAttemptTimeDesc();
}
