package com.nexaworkspace.saas.project;

import com.nexaworkspace.saas.tenant.Tenant;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="projects")
public class Project {
    @Id private UUID id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="tenant_id") private Tenant tenant;
    @Column(nullable=false) private String name;
    @Column(length=1200) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private ProjectStatus status;
    @Column(nullable=false) private Instant createdAt;
    @Column(nullable=false) private Instant updatedAt;

    protected Project() {}
    public Project(UUID id, Tenant tenant, String name, String description, ProjectStatus status, Instant createdAt, Instant updatedAt) {
        this.id=id; this.tenant=tenant; this.name=name; this.description=description; this.status=status; this.createdAt=createdAt; this.updatedAt=updatedAt;
    }
    public void update(String name, String description, ProjectStatus status){ this.name=name; this.description=description; this.status=status; this.updatedAt=Instant.now(); }
    public UUID getId(){return id;} public Tenant getTenant(){return tenant;} public String getName(){return name;} public String getDescription(){return description;} public ProjectStatus getStatus(){return status;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
