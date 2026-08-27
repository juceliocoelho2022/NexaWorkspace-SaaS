package com.nexaworkspace.saas.tenant;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id private UUID id;
    @Column(nullable=false) private String name;
    @Column(nullable=false, unique=true) private String slug;
    @Column(nullable=false) private Instant createdAt;

    protected Tenant() {}
    public Tenant(UUID id, String name, String slug, Instant createdAt) { this.id=id; this.name=name; this.slug=slug; this.createdAt=createdAt; }
    public UUID getId(){return id;} public String getName(){return name;} public String getSlug(){return slug;} public Instant getCreatedAt(){return createdAt;}
}
