package com.cartagenacorp.lm_sprint.repository;

import com.cartagenacorp.lm_sprint.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, UUID> {
    List<Sprint> findByProjectId(UUID projectId);
    boolean existsById(UUID id);
    boolean existsByProjectIdAndActiveTrueAndIdNot(UUID projectId, UUID sprintId);
    boolean existsByProjectIdAndActiveTrue(UUID projectId);
    Optional<Sprint> findByProjectIdAndActiveTrue(UUID projectId);
}
