# Arquitetura — NexaWorkspace SaaS

## Visão geral

```text
Browser
  |
  v
React + TypeScript (Nginx)
  |
  v
Spring Boot REST API
  |-- JWT Authentication
  |-- RBAC (OWNER / ADMIN / MEMBER)
  |-- Tenant scoping
  |-- Audit trail
  |-- Billing sandbox
  |
  v
PostgreSQL + Flyway
```

## Estratégia multi-tenant
O tenant é criado no onboarding e seu `tenantId` é incluído no JWT. Endpoints de domínio nunca consultam um projeto apenas pelo ID: usam `id + tenant_id`. Isso cria uma barreira explícita contra acesso cruzado entre organizações.

## Evolução recomendada para produção
- Refresh token com rotação e revogação.
- Convites de usuários por e-mail.
- Rate limiting via Redis.
- Stripe/Mercado Pago com webhooks idempotentes.
- OpenTelemetry + Prometheus + Grafana.
- S3/Blob Storage para anexos.
- Testcontainers para testes de integração.
- Kubernetes + HPA quando houver escala que justifique.
