package com.nexaworkspace.saas.user;

import com.nexaworkspace.saas.tenant.Tenant;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserAccount {
    @Id private UUID id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="tenant_id") private Tenant tenant;
    @Column(nullable=false) private String name;
    @Column(nullable=false, unique=true) private String email;
    @Column(name="password_hash", nullable=false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
    @Column(nullable=false) private boolean active;
    @Column(nullable=false) private Instant createdAt;

    protected UserAccount() {}
    public UserAccount(UUID id, Tenant tenant, String name, String email, String passwordHash, Role role, boolean active, Instant createdAt) {
        this.id=id; this.tenant=tenant; this.name=name; this.email=email; this.passwordHash=passwordHash; this.role=role; this.active=active; this.createdAt=createdAt;
    }
    public UUID getId(){return id;} public Tenant getTenant(){return tenant;} public String getName(){return name;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public Role getRole(){return role;} public boolean isActive(){return active;} public Instant getCreatedAt(){return createdAt;}
}
