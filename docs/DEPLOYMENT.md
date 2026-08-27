# Deploy — NexaWorkspace SaaS

## Estratégia de portfolio-production

A implantação pública usa um único web service para reduzir custo e complexidade operacional:

- React é compilado no build multi-stage.
- O `dist` do React é empacotado em `src/main/resources/static`.
- Spring Boot serve frontend e API no mesmo domínio.
- PostgreSQL e Redis são serviços gerenciados do Render.
- Billing inicia em `sandbox`.
- Kafka fica desligado por padrão no deploy gratuito (`EVENTS_ENABLED=false`).

Isso mantém o ambiente público funcional sem exigir um broker Kafka pago. Para ativar eventos em cloud, configure um cluster Kafka-compatible externo (ex.: Redpanda Cloud ou Confluent Cloud), defina `KAFKA_BOOTSTRAP_SERVERS` e mude `EVENTS_ENABLED=true`.

## Deploy no Render

1. Entre no Render e escolha **New > Blueprint**.
2. Conecte o repositório `juceliocoelho2022/NexaWorkspace-SaaS`.
3. O Render detectará o `render.yaml` na raiz.
4. Confirme a criação de:
   - `nexaworkspace-saas` (web service)
   - `nexaworkspace-db` (PostgreSQL)
   - `nexaworkspace-cache` (Key Value / Redis)
5. Nos secrets de billing, deixe vazio enquanto `BILLING_MODE=sandbox`.
6. Aguarde o health check `/actuator/health` ficar `UP`.

## Modo billing real

Para habilitar checkout real, altere:

```env
BILLING_MODE=live
```

Stripe:

```env
STRIPE_SECRET_KEY=...
STRIPE_PRO_PRICE_ID=...
STRIPE_BUSINESS_PRICE_ID=...
```

Mercado Pago:

```env
MERCADO_PAGO_ACCESS_TOKEN=...
```

Não coloque credenciais no GitHub.

## Kafka em cloud

O deploy gratuito inicia com eventos desligados. Para ativar:

```env
EVENTS_ENABLED=true
KAFKA_BOOTSTRAP_SERVERS=broker:9092
KAFKA_DOMAIN_TOPIC=nexaworkspace.domain-events.v1
```

Se o provedor exigir SASL/SSL, adicione as propriedades correspondentes ao `spring.kafka.properties` via configuração segura antes de habilitar o listener.

## Observabilidade

O ambiente local continua incluindo Prometheus e Grafana. Para produção pública, prefira Grafana Cloud/OTLP ou outro serviço gerenciado em vez de rodar Grafana e Prometheus no mesmo web service da aplicação.

## Limitação do plano gratuito Render

O Postgres gratuito do Render é apropriado para demonstração/preview e expira após 30 dias. Para uma URL de portfólio permanente, migre o banco para um plano persistente ou para um PostgreSQL externo de longa duração.
