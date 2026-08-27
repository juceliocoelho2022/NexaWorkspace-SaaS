package com.nexaworkspace.saas.project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findAllByTenant_IdOrderByUpdatedAtDesc(UUID tenantId);
    Optional<Project> findByIdAndTenant_Id(UUID id, UUID tenantId);
    long countByTenant_Id(UUID tenantId);
    long countByTenant_IdAndStatus(UUID tenantId, ProjectStatus status);
}
