<div align="center">

# 🚀 NexaWorkspace SaaS

### Enterprise B2B Multi-Tenant SaaS Platform

**Workspaces · Projects · RBAC · Billing · Events · Rate Limiting · Observability · CI/CD**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-7-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-8.10-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-4.3-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![Prometheus](https://img.shields.io/badge/Prometheus-3.14-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-13.2-F46800?style=for-the-badge&logo=grafana&logoColor=white)](https://grafana.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![GitHub Actions](https://img.shields.io/badge/GitHub-Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/features/actions)

> **Arquitetura de produto, não apenas CRUD.**

O **NexaWorkspace** é uma plataforma SaaS B2B criada para demonstrar conceitos de engenharia de software utilizados em produtos reais: isolamento multi-tenant, segurança, eventos assíncronos, billing, observabilidade, infraestrutura containerizada e CI.

</div>

---

## 🎯 O problema que o projeto resolve

Cada organização possui um workspace isolado com seus próprios usuários, projetos, assinatura e trilha de auditoria. O backend determina o tenant autenticado pelo JWT e aplica o escopo diretamente nas consultas.

> **Regra central de segurança:** o frontend nunca escolhe o `tenant_id`. O tenant vem da identidade autenticada.

```java
repo.findByIdAndTenant_Id(projectId, principal.tenantId())
```

Buscar apenas por `projectId` seria insuficiente em um SaaS e poderia permitir **cross-tenant data leakage**.

---

# 🧰 Tech Stack

## Backend

| Tecnologia | Uso |
|---|---|
| **Java 21** | Linguagem principal |
| **Spring Boot 4.1** | API REST |
| **Spring Security** | Segurança e autorização |
| **JWT / JJWT** | Autenticação stateless |
| **RBAC** | `OWNER`, `ADMIN`, `MEMBER` |
| **Spring Data JPA** | Persistência |
| **Hibernate** | ORM |
| **Flyway** | Migrações de banco |
| **Spring Kafka** | Eventos de domínio |
| **Spring Data Redis** | Rate limiting distribuído |
| **Micrometer** | Instrumentação |
| **Actuator** | Health e métricas |
| **Maven** | Build |

## Frontend

| Tecnologia | Uso |
|---|---|
| **React 19** | UI |
| **TypeScript 7** | Tipagem |
| **Vite 8** | Build/dev server |
| **Lucide React** | Ícones |
| **Nginx** | Servidor web |

## Dados e mensageria

| Tecnologia | Uso |
|---|---|
| **PostgreSQL 18** | Banco principal |
| **Redis 8.10** | Rate limiting / base para cache distribuído |
| **Apache Kafka 4.3** | Event-driven architecture |

## Observabilidade e DevOps

| Tecnologia | Uso |
|---|---|
| **Prometheus 3.14** | Coleta de métricas |
| **Grafana 13.2** | Dashboards |
| **Docker** | Containers |
| **Docker Compose** | Plataforma local |
| **GitHub Actions** | CI |

## Billing

| Provedor | Implementação |
|---|---|
| **Stripe** | Checkout Session para assinatura |
| **Mercado Pago** | Assinatura recorrente via `preapproval` |
| **Sandbox** | Default seguro sem cobrança real |

---

# 🏗️ Arquitetura

```text
┌──────────────────────────────────────────────────────────────────┐
│                  React 19 + TypeScript + Nginx                  │
│           Dashboard · Projects · Billing · Auth                 │
└──────────────────────────────┬───────────────────────────────────┘
                               │ REST / JWT
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                       Spring Boot API                            │
│                                                                  │
│ Auth ─ JWT ─ RBAC ─ Tenant Context ─ Redis Rate Limiting        │
│  │            │              │                 │                │
│  ├ Projects   ├ Dashboard    ├ Billing         └ Audit Log      │
│  │            │              │                                  │
│  └────────────┴────── Domain Events ────────────────┐           │
└──────────────────────────────┬──────────────────────┼───────────┘
                               │ JPA                  │ Kafka
                               ▼                      ▼
                    ┌───────────────────┐   ┌───────────────────┐
                    │   PostgreSQL 18   │   │ Apache Kafka 4.3  │
                    └───────────────────┘   └───────────────────┘
                               │
                               │ Actuator / Micrometer
                               ▼
                    ┌───────────────────┐
                    │ Prometheus 3.14   │
                    └─────────┬─────────┘
                              ▼
                    ┌───────────────────┐
                    │   Grafana 13.2    │
                    └───────────────────┘
```

### Fluxo Multi-Tenant

```text
Login
  ↓
JWT
  ↓
uid + tid + role
  ↓
SaasPrincipal
  ↓
Service Layer
  ↓
resource_id + tenant_id
  ↓
PostgreSQL
```

---

# ✨ Funcionalidades implementadas

### Identidade e segurança

- ✅ Cadastro de organização + tenant
- ✅ Usuário `OWNER` automático
- ✅ JWT stateless
- ✅ BCrypt
- ✅ RBAC
- ✅ Tenant scoping
- ✅ Audit Log
- ✅ Rate limiting Redis para login e cadastro
- ✅ Secrets por variáveis de ambiente

### Projetos

- ✅ CRUD completo
- ✅ Isolamento por tenant
- ✅ `PROJECT_CREATED`
- ✅ `PROJECT_UPDATED`
- ✅ `PROJECT_DELETED`

### Billing

- ✅ `FREE`, `PRO`, `BUSINESS`
- ✅ Sandbox padrão
- ✅ Checkout Stripe
- ✅ Assinatura Mercado Pago
- ✅ Modo LIVE ativado explicitamente
- ✅ UI para seleção do gateway
- ✅ Evento `BILLING_CHECKOUT_CREATED`
- ✅ Evento `SUBSCRIPTION_PLAN_CHANGED`

### Observabilidade

- ✅ `/actuator/health`
- ✅ `/actuator/metrics`
- ✅ `/actuator/prometheus`
- ✅ Prometheus provisionado
- ✅ Grafana provisionado
- ✅ Dashboard de requests, 5xx, heap e p95

### Engenharia / DevOps

- ✅ Flyway
- ✅ Docker Compose
- ✅ Kafka KRaft
- ✅ Redis AOF
- ✅ Health checks
- ✅ CI backend
- ✅ CI frontend
- ✅ Validação `docker compose config`

---

# 📨 Event-Driven Architecture

Os eventos de domínio são disparados durante a operação de negócio e enviados para Kafka **após o commit** da transação.

```text
Database Transaction
       │
       ├── business state
       ├── audit log
       └── Spring domain event
                    │
                    ▼
          TransactionPhase.AFTER_COMMIT
                    │
                    ▼
                 Kafka
```

Tópico padrão:

```text
nexaworkspace.domain-events.v1
```

Exemplo:

```json
{
  "eventId": "uuid",
  "tenantId": "uuid",
  "actorId": "uuid",
  "type": "PROJECT_CREATED",
  "aggregateType": "PROJECT",
  "aggregateId": "uuid",
  "occurredAt": "2026-08-27T12:00:00Z",
  "payload": {
    "name": "Platform Hardening",
    "status": "ACTIVE"
  }
}
```

> Próxima evolução para garantia forte de entrega: **Transactional Outbox Pattern**.

---

# 🚦 Redis Rate Limiting

Endpoints públicos sensíveis possuem limites compartilhados entre instâncias:

```text
POST /api/auth/login
10 tentativas / minuto

POST /api/auth/register
5 tentativas / 10 minutos
```

Se Redis ficar indisponível, o limiter trabalha em **fail-open** e registra a falha, evitando transformar a queda do cache em indisponibilidade total da autenticação.

---

# 💳 Billing: Sandbox x Live

Por padrão:

```env
BILLING_MODE=sandbox
```

Nenhuma cobrança real é feita.

### Checkout

```http
POST /api/billing/checkout
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{
  "plan": "PRO",
  "provider": "STRIPE"
}
```

Também é possível usar:

```json
{
  "plan": "BUSINESS",
  "provider": "MERCADO_PAGO"
}
```

### Ativar cobrança real

```env
BILLING_MODE=live
```

Stripe:

```env
STRIPE_SECRET_KEY=
STRIPE_PRO_PRICE_ID=
STRIPE_BUSINESS_PRICE_ID=
```

Mercado Pago:

```env
MERCADO_PAGO_ACCESS_TOKEN=
MP_PRO_MONTHLY_BRL=49.90
MP_BUSINESS_MONTHLY_BRL=149.90
```

> O redirect do checkout **não deve ser usado como confirmação definitiva de pagamento**. A ativação final de assinatura é uma etapa futura baseada em webhooks autenticados e idempotentes.

---

# 📈 Observabilidade

Prometheus coleta métricas da API a cada 15 segundos.

Grafana já sobe com datasource e dashboard provisionados.

Painéis iniciais:

- Requests HTTP/min
- Erros 5xx/min
- JVM Heap
- Latência HTTP p95

---

# 📁 Estrutura

```text
NexaWorkspace-SaaS/
├── backend/
│   ├── src/main/java/com/nexaworkspace/saas/
│   │   ├── audit/
│   │   ├── auth/
│   │   ├── billing/
│   │   │   └── provider/
│   │   ├── common/
│   │   ├── config/
│   │   ├── dashboard/
│   │   ├── event/
│   │   ├── project/
│   │   ├── ratelimit/
│   │   ├── security/
│   │   ├── tenant/
│   │   └── user/
│   └── src/main/resources/
│
├── frontend/
│   └── src/
│
├── monitoring/
│   ├── prometheus.yml
│   └── grafana/
│       ├── dashboards/
│       └── provisioning/
│
├── docs/
│   ├── ARCHITECTURE.md
│   └── PLATFORM-HARDENING.md
│
├── .github/workflows/ci.yml
├── docker-compose.yml
├── .env.example
└── README.md
```

---

# 🚀 Executando

### 1. Copie as variáveis

```bash
cp .env.example .env
```

### 2. Suba toda a plataforma

```bash
docker compose up --build
```

### Serviços

| Serviço | Endereço |
|---|---|
| Frontend | `http://localhost:3000` |
| Backend | `http://localhost:8080` |
| Health | `http://localhost:8080/actuator/health` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3001` |
| PostgreSQL | `localhost:5432` |
| Redis | `localhost:6379` |
| Kafka | `localhost:9092` |

---

# 🔌 API principal

| Método | Endpoint | Objetivo |
|---|---|---|
| `POST` | `/api/auth/register` | Cria tenant + owner |
| `POST` | `/api/auth/login` | Login JWT |
| `GET` | `/api/dashboard` | KPIs |
| `GET` | `/api/projects` | Lista projetos |
| `POST` | `/api/projects` | Cria projeto |
| `PUT` | `/api/projects/{id}` | Atualiza projeto |
| `DELETE` | `/api/projects/{id}` | Remove projeto |
| `GET` | `/api/billing` | Assinatura atual |
| `POST` | `/api/billing/checkout` | Cria checkout |
| `PATCH` | `/api/billing/plan` | Troca plano em sandbox |
| `GET` | `/api/audit` | Audit trail |
| `GET` | `/actuator/prometheus` | Métricas |

---

# ⚙️ CI

```text
Push / Pull Request
       │
       ├── Maven Test
       ├── npm build
       └── docker compose config
```

---

# 🛣️ Roadmap

- [x] Multi-tenancy
- [x] JWT + RBAC
- [x] PostgreSQL + Flyway
- [x] Redis rate limiting
- [x] Kafka domain events
- [x] Prometheus
- [x] Grafana
- [x] Stripe checkout adapter
- [x] Mercado Pago subscription adapter
- [ ] Billing webhooks com assinatura criptográfica e idempotência
- [ ] Transactional Outbox
- [ ] Refresh Token + rotação/revogação
- [ ] OAuth2 / OpenID Connect
- [ ] Testcontainers
- [ ] OpenTelemetry
- [ ] Retry + DLQ Kafka
- [ ] AWS / Azure
- [ ] Kubernetes + HPA

---

# 🧠 Competências demonstradas

`Java 21` · `Spring Boot` · `REST` · `JWT` · `Spring Security` · `RBAC` · `Multi-Tenancy` · `JPA` · `Hibernate` · `PostgreSQL` · `Flyway` · `Redis` · `Kafka` · `Event-Driven Architecture` · `Prometheus` · `Grafana` · `Micrometer` · `React` · `TypeScript` · `Docker` · `CI/CD` · `Stripe` · `Mercado Pago` · `Audit Log` · `SaaS Architecture`

---

## 👨‍💻 Autor

**Jucelio Farias Coelho**

GitHub: [@juceliocoelho2022](https://github.com/juceliocoelho2022)

---

<div align="center">

### ⭐ NexaWorkspace SaaS

**Backend corporativo · arquitetura SaaS · eventos · observabilidade · DevOps**

</div>
