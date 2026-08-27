CREATE TABLE tenants (
 id UUID PRIMARY KEY,
 name VARCHAR(160) NOT NULL,
 slug VARCHAR(180) NOT NULL UNIQUE,
 created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE users (
 id UUID PRIMARY KEY,
 tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
 name VARCHAR(160) NOT NULL,
 email VARCHAR(200) NOT NULL UNIQUE,
 password_hash VARCHAR(255) NOT NULL,
 role VARCHAR(20) NOT NULL CHECK (role IN ('OWNER','ADMIN','MEMBER')),
 active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_users_tenant ON users(tenant_id);

CREATE TABLE projects (
 id UUID PRIMARY KEY,
 tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
 name VARCHAR(180) NOT NULL,
 description VARCHAR(1200),
 status VARCHAR(30) NOT NULL CHECK (status IN ('PLANNING','ACTIVE','ON_HOLD','COMPLETED')),
 created_at TIMESTAMPTZ NOT NULL,
 updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_projects_tenant ON projects(tenant_id);

CREATE TABLE subscriptions (
 id UUID PRIMARY KEY,
 tenant_id UUID NOT NULL UNIQUE REFERENCES tenants(id) ON DELETE CASCADE,
 plan VARCHAR(30) NOT NULL CHECK (plan IN ('FREE','PRO','BUSINESS')),
 status VARCHAR(30) NOT NULL,
 updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE audit_logs (
 id UUID PRIMARY KEY,
 tenant_id UUID NOT NULL,
 user_id UUID,
 action VARCHAR(80) NOT NULL,
 resource VARCHAR(80) NOT NULL,
 resource_id VARCHAR(120),
 created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_audit_tenant_created ON audit_logs(tenant_id, created_at DESC);
