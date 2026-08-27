<div align="center">

# 🚀 NexaWorkspace SaaS

### Plataforma B2B Multi-Tenant com Java, React, eventos, cache, observabilidade e billing

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-8-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-4-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

**SaaS de portfólio com arquitetura de produto real:** JWT, RBAC, multi-tenancy, PostgreSQL, Redis, Kafka, billing, Prometheus/Grafana, Docker e CI/CD.

</div>

---

## 🎯 Sobre

O **NexaWorkspace SaaS** é uma plataforma B2B multi-tenant para gestão de workspaces, usuários, projetos e assinaturas. Cada tenant possui isolamento lógico de dados e o backend determina o `tenant_id` a partir da identidade autenticada.

> Regra central: o frontend não escolhe o tenant. Toda operação sensível combina o recurso solicitado com o `tenant_id` autenticado.

## 🧰 Stack

### Backend
- Java 21
- Spring Boot 4.1
- Spring Security + JWT
- RBAC (`OWNER`, `ADMIN`, `MEMBER`)
- Spring Data JPA + Hibernate
- Flyway
- Micrometer + Actuator
- Redis rate limiting
- Apache Kafka domain events

### Frontend
- React 19
- TypeScript 7
- Vite 8
- Lucide React
- UI responsiva
- Nginx no ambiente Docker local

### Dados e Infra
- PostgreSQL 18
- Redis 8
- Apache Kafka 4 (KRaft)
- Prometheus
- Grafana
- Docker / Docker Compose
- GitHub Actions
- Render Blueprint para deploy público

## ✨ Funcionalidades

- Cadastro de empresa + tenant + usuário OWNER
- Login JWT
- RBAC
- Multi-tenancy
- Dashboard
- CRUD de projetos
- Billing `FREE`, `PRO`, `BUSINESS`
- Stripe e Mercado Pago em camada de gateway
- Billing `SANDBOX`/`LIVE`
- Audit Log
- Redis rate limiting
- Eventos de domínio Kafka
- Flyway migrations
- Health checks
- Prometheus metrics
- Grafana dashboard
- CI backend/frontend/infra

## 🏗️ Arquitetura

```text
React / TypeScript
       │
       ▼
Spring Boot API
  │    │     │
 JWT  Redis Kafka
  │          │
  └──── PostgreSQL
       │
Prometheus → Grafana
```

## 🚀 Rodando localmente

```bash
docker compose up --build
```

Serviços:

| Serviço | URL |
|---|---|
| Frontend | `http://localhost:3000` |
| Backend | `http://localhost:8080` |
| Health | `http://localhost:8080/actuator/health` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3001` |

## ☁️ Deploy público

O repositório possui um `render.yaml` e um Dockerfile multi-stage específico em `deploy/render/Dockerfile`.

No modo **portfolio-production**, React é incorporado ao JAR do Spring Boot e frontend/API usam o mesmo domínio. PostgreSQL e Redis ficam gerenciados no Render. Kafka é opcional no plano público e pode ser ligado com um broker Kafka-compatible externo.

Consulte [`docs/DEPLOYMENT.md`](docs/DEPLOYMENT.md).

## 🔌 API principal

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/auth/register` | Cria tenant + owner |
| POST | `/api/auth/login` | Login JWT |
| GET | `/api/dashboard` | KPIs |
| GET/POST | `/api/projects` | Projetos |
| PUT/DELETE | `/api/projects/{id}` | Alteração/remoção tenant-scoped |
| GET | `/api/billing` | Assinatura |
| PATCH | `/api/billing/plan` | Plano sandbox |
| POST | `/api/billing/checkout` | Checkout provider |
| GET | `/api/audit` | Auditoria |
| GET | `/actuator/health` | Health check |
| GET | `/actuator/prometheus` | Métricas |

## 🔐 Segurança

- BCrypt
- JWT stateless
- RBAC
- Tenant scoping
- Audit trail
- Rate limiting distribuído
- Secrets por ambiente
- Billing real desabilitado por padrão

## ⚙️ CI

O GitHub Actions valida:

```text
Backend → Maven test
Frontend → npm install + TypeScript + Vite build
Infra → docker compose config + production Docker build
```

## 🛣️ Próximos passos

- Transactional Outbox
- Webhooks Stripe/Mercado Pago idempotentes
- DLQ/retry Kafka
- Testcontainers
- OpenTelemetry
- Cloud Kafka (Redpanda/Confluent)
- Grafana Cloud
- Kubernetes

## 👨‍💻 Autor

**Jucelio Farias Coelho**  
GitHub: [@juceliocoelho2022](https://github.com/juceliocoelho2022)

---

<div align="center">

**NexaWorkspace SaaS — arquitetura de produto, não apenas CRUD.**

</div>
