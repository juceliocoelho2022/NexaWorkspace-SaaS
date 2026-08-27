package com.nexaworkspace.saas.project;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;
public final class ProjectDtos {
 private ProjectDtos(){}
 public record Upsert(@NotBlank String name,String description,ProjectStatus status){}
 public record View(UUID id,String name,String description,ProjectStatus status,Instant createdAt,Instant updatedAt){}
}
