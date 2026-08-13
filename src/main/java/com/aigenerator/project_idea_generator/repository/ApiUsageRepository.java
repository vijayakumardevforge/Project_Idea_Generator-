package com.aigenerator.project_idea_generator.repository;

import com.aigenerator.project_idea_generator.model.ApiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ApiUsageRepository extends JpaRepository<ApiUsage, Long> {
    Optional<ApiUsage> findByEndpointTypeAndCallDate(String endpointType, LocalDate callDate);
}
