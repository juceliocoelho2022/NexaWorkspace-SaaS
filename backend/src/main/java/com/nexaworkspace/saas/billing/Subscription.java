package com.nexaworkspace.saas.billing;

import com.nexaworkspace.saas.tenant.Tenant;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="subscriptions")
public class Subscription {
    @Id private UUID id;
    @OneToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="tenant_id", unique=true) private Tenant tenant;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Plan plan;
    @Column(nullable=false) private String status;
    @Column(nullable=false) private Instant updatedAt;

    protected Subscription() {}
    public Subscription(UUID id, Tenant tenant, Plan plan, String status, Instant updatedAt){this.id=id;this.tenant=tenant;this.plan=plan;this.status=status;this.updatedAt=updatedAt;}
    public void changePlan(Plan plan){this.plan=plan;this.status="ACTIVE";this.updatedAt=Instant.now();}
    public UUID getId(){return id;} public Tenant getTenant(){return tenant;} public Plan getPlan(){return plan;} public String getStatus(){return status;} public Instant getUpdatedAt(){return updatedAt;}
}
