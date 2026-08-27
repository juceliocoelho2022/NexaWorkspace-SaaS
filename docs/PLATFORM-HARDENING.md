# Platform Hardening

A evolução de plataforma adiciona quatro capacidades principais ao NexaWorkspace SaaS.

## 1. Redis

- Rate limiting distribuído para login e cadastro.
- Persistência AOF no ambiente Docker local.
- Estratégia fail-open na API para preservar disponibilidade.

## 2. Kafka

- Kafka 4.x em modo KRaft.
- Tópico `nexaworkspace.domain-events.v1` com três partições.
- Eventos publicados após commit da transação.
- Eventos iniciais de projeto e billing.

## 3. Observabilidade

- Micrometer + Prometheus registry.
- Endpoint `/actuator/prometheus`.
- Prometheus provisionado no Compose.
- Grafana provisionado com datasource e dashboard.
- Métricas iniciais: throughput HTTP, 5xx, heap JVM e p95.

## 4. Billing

- Sandbox como default.
- Stripe Checkout Session para planos pagos.
- Mercado Pago recurring preapproval.
- Credenciais somente por variáveis de ambiente.
- Mudança direta de plano bloqueada em modo live.

## Limites intencionais

A ativação final de uma assinatura paga ainda não deve confiar no redirect do checkout. A próxima etapa é implementar webhooks validados criptograficamente, idempotência e Transactional Outbox.
