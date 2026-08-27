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
  |-- Redis rate limiting
  |-- Billing adapters (Stripe / Mercado Pago)
  |-- Domain events
  |
  +-------------> PostgreSQL + Flyway
  |
  +-------------> Redis
  |
  +-------------> Kafka
  |
  +-------------> Actuator/Micrometer -> Prometheus -> Grafana
```

## Estratégia multi-tenant

O tenant é criado no onboarding e o `tenantId` é incluído no JWT. Endpoints de domínio nunca consultam um recurso sensível apenas por seu ID; usam `resource_id + tenant_id`.

```java
repo.findByIdAndTenant_Id(resourceId, principal.tenantId())
```

Isso cria uma barreira explícita contra acesso cruzado entre organizações.

## Eventos

A aplicação publica eventos de domínio no Spring durante a transação. O listener Kafka é executado em `AFTER_COMMIT`, evitando que uma mensagem seja publicada antes da confirmação da transação relacional.

A implementação atual prioriza simplicidade e demonstração arquitetural. Para garantia forte de entrega entre PostgreSQL e Kafka, a próxima evolução é o **Transactional Outbox Pattern**.

## Redis

Redis é usado para rate limiting compartilhado entre instâncias. Em caso de indisponibilidade, o limiter trabalha em modo fail-open para não transformar a queda do cache em indisponibilidade total da autenticação.

## Billing

`BILLING_MODE=sandbox` é o padrão. Em sandbox, nenhum provedor externo é chamado.

Em `BILLING_MODE=live`:

- Stripe cria Checkout Sessions de assinatura usando Price IDs configurados por ambiente.
- Mercado Pago cria assinaturas recorrentes via `preapproval`.

A ativação definitiva de planos pagos deve ocorrer por webhooks autenticados e idempotentes. O projeto deliberadamente não confia apenas no redirect do navegador como confirmação de pagamento.

## Observabilidade

Micrometer e Actuator expõem métricas no endpoint `/actuator/prometheus`. Prometheus coleta as séries e Grafana é provisionado com datasource e dashboard inicial.

## Evoluções de produção

- Transactional Outbox + relay Kafka.
- Webhooks Stripe/Mercado Pago com assinatura e idempotência.
- Refresh token com rotação e revogação.
- OAuth2/OIDC.
- OpenTelemetry + tracing distribuído.
- Retry/DLQ para consumidores Kafka.
- Testcontainers para PostgreSQL/Redis/Kafka.
- Secrets manager cloud.
- Kubernetes + HPA quando a escala justificar.
