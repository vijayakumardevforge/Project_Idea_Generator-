package com.aigenerator.project_idea_generator.repository;

import com.aigenerator.project_idea_generator.model.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedIpRepository extends JpaRepository<BlockedIp, Long> {
    Optional<BlockedIp> findByIpAddress(String ipAddress);
    void deleteByIpAddress(String ipAddress);
    boolean existsByIpAddress(String ipAddress);
}
