package com.nexaworkspace.saas.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="audit_logs")
public class AuditLog {
    @Id private UUID id;
    @Column(name="tenant_id", nullable=false) private UUID tenantId;
    @Column(name="user_id") private UUID userId;
    @Column(nullable=false) private String action;
    @Column(nullable=false) private String resource;
    @Column(name="resource_id") private String resourceId;
    @Column(nullable=false) private Instant createdAt;

    protected AuditLog() {}
    public AuditLog(UUID id, UUID tenantId, UUID userId, String action, String resource, String resourceId, Instant createdAt){this.id=id;this.tenantId=tenantId;this.userId=userId;this.action=action;this.resource=resource;this.resourceId=resourceId;this.createdAt=createdAt;}
    public UUID getId(){return id;} public UUID getTenantId(){return tenantId;} public UUID getUserId(){return userId;} public String getAction(){return action;} public String getResource(){return resource;} public String getResourceId(){return resourceId;} public Instant getCreatedAt(){return createdAt;}
}
