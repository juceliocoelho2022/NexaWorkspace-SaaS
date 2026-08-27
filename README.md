<div align="center">

# 🚀 NexaWorkspace SaaS

### Plataforma B2B Multi-Tenant para gestão de workspaces, projetos, usuários e assinaturas

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-7-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![GitHub Actions](https://img.shields.io/badge/GitHub-Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/features/actions)

**Um SaaS de portfólio com arquitetura de produto real:** autenticação JWT, RBAC, isolamento por tenant, billing sandbox, auditoria, migrações de banco, CI/CD e execução full stack via Docker.

</div>

---

## 🎯 Sobre o projeto

O **NexaWorkspace SaaS** demonstra como estruturar um produto SaaS B2B moderno, indo muito além de um CRUD tradicional.

Cada empresa possui um **workspace isolado**, com seus próprios usuários, projetos, assinatura e registros de auditoria. O backend determina o tenant autenticado pelo JWT e aplica o escopo diretamente nas consultas ao banco.

> **Regra central de segurança:** o frontend não escolhe o `tenant_id`. O contexto do tenant vem da identidade autenticada e toda operação sensível aplica tenant scoping no backend.

---

## 🧰 Tech Stack

### Backend

| Tecnologia | Uso |
|---|---|
| **Java 21** | Linguagem principal |
| **Spring Boot 4.1** | API REST e infraestrutura backend |
| **Spring Security** | Segurança e autorização |
| **JWT / JJWT** | Autenticação stateless |
| **RBAC** | Perfis `OWNER`, `ADMIN` e `MEMBER` |
| **Spring Data JPA** | Persistência e repositories |
| **Hibernate** | ORM |
| **Flyway** | Versionamento de schema |
| **Spring Boot Actuator** | Health checks e métricas |
| **Maven** | Build e dependências |

### Frontend

| Tecnologia | Uso |
|---|---|
| **React 19** | Interface web |
| **TypeScript** | Tipagem estática |
| **Vite** | Build e ambiente de desenvolvimento |
| **Lucide React** | Ícones |
| **CSS responsivo** | Design system e layout |
| **Nginx** | Servidor do frontend e reverse proxy |

### Dados, DevOps e Infra

| Tecnologia | Uso |
|---|---|
| **PostgreSQL 18** | Banco relacional |
| **Docker** | Containerização |
| **Docker Compose** | Orquestração local full stack |
| **GitHub Actions** | Pipeline CI |
| **Git / GitHub** | Versionamento e entrega |

---

## 🏗️ Arquitetura

```text
┌─────────────────────────────────────────────┐
│                React + TypeScript           │
│          Dashboard / Projects / Billing     │
└──────────────────────┬──────────────────────┘
                       │ HTTPS / REST
                       ▼
┌─────────────────────────────────────────────┐
│                Spring Boot API              │
│                                             │
│  Auth ─ JWT ─ RBAC ─ Tenant Context         │
│    │              │                         │
│    ├─ Projects    ├─ Billing                │
│    ├─ Dashboard   └─ Audit                  │
└──────────────────────┬──────────────────────┘
                       │ JPA / Hibernate
                       ▼
┌─────────────────────────────────────────────┐
│                 PostgreSQL                  │
│ tenants │ users │ projects │ subscriptions  │
│                 audit_logs                  │
└─────────────────────────────────────────────┘
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
query = resource_id + tenant_id
  ↓
PostgreSQL
```

Isso reduz o risco clássico de **cross-tenant data leakage**.

---

## ✨ Funcionalidades

- ✅ Cadastro de empresa e criação automática do tenant
- ✅ Criação automática do usuário `OWNER`
- ✅ Login com JWT
- ✅ Autorização com RBAC
- ✅ Isolamento de dados por `tenant_id`
- ✅ Dashboard com KPIs do workspace
- ✅ CRUD de projetos
- ✅ Planos `FREE`, `PRO` e `BUSINESS`
- ✅ Upgrade/downgrade em billing sandbox
- ✅ Audit Log de operações críticas
- ✅ Migrações versionadas com Flyway
- ✅ Health check com Actuator
- ✅ Frontend responsivo
- ✅ Docker Compose full stack
- ✅ Pipeline CI backend + frontend

---

## 🔐 Segurança

O projeto implementa princípios importantes de segurança para SaaS:

- JWT stateless
- Senhas com BCrypt
- RBAC por perfil
- Tenant scoping no backend
- Queries sensíveis com `id + tenant_id`
- CORS configurado
- Rotas públicas limitadas a autenticação e health check
- Audit trail para ações importantes
- Secrets externalizáveis por variáveis de ambiente

Exemplo conceitual:

```java
repo.findByIdAndTenant_Id(projectId, principal.tenantId())
```

Buscar apenas por `projectId` seria insuficiente em um SaaS multi-tenant.

---

## 📁 Estrutura do projeto

```text
NexaWorkspace-SaaS/
├── backend/
│   ├── src/main/java/com/nexaworkspace/saas/
│   │   ├── audit/
│   │   ├── auth/
│   │   ├── billing/
│   │   ├── common/
│   │   ├── config/
│   │   ├── dashboard/
│   │   ├── project/
│   │   ├── security/
│   │   ├── tenant/
│   │   └── user/
│   ├── src/main/resources/db/migration/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── types/
│   ├── Dockerfile
│   └── nginx.conf
├── docs/
│   └── ARCHITECTURE.md
├── .github/workflows/
│   └── ci.yml
├── docker-compose.yml
└── .env.example
```

---

## 🚀 Executando com Docker

### Pré-requisitos

- Docker Desktop
- Docker Compose

### Subir toda a plataforma

```bash
docker compose up --build
```

### Aplicações

| Serviço | URL |
|---|---|
| Frontend | `http://localhost:3000` |
| Backend | `http://localhost:8080` |
| Health Check | `http://localhost:8080/actuator/health` |
| PostgreSQL | `localhost:5432` |

Para encerrar:

```bash
docker compose down
```

Para remover também os volumes:

```bash
docker compose down -v
```

---

## 💻 Executando em desenvolvimento

### Banco

```bash
docker compose up -d postgres
```

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend de desenvolvimento:

```text
http://localhost:5173
```

---

## 🔌 API

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/auth/register` | Cria tenant + owner |
| `POST` | `/api/auth/login` | Autentica e retorna JWT |
| `GET` | `/api/dashboard` | KPIs do tenant |
| `GET` | `/api/projects` | Lista projetos do tenant |
| `POST` | `/api/projects` | Cria projeto |
| `PUT` | `/api/projects/{id}` | Atualiza projeto |
| `DELETE` | `/api/projects/{id}` | Exclui projeto |
| `GET` | `/api/billing` | Consulta assinatura |
| `PATCH` | `/api/billing/plan` | Altera plano |
| `GET` | `/api/audit` | Consulta auditoria |
| `GET` | `/actuator/health` | Health check |

---

## 🗃️ Modelo de dados

```text
tenants
   │
   ├── users
   ├── projects
   ├── subscriptions
   └── audit_logs
```

O `tenant_id` é a fronteira lógica entre os clientes da plataforma.

---

## ⚙️ CI/CD

O workflow do GitHub Actions executa validações independentes para backend e frontend:

```text
Push / Pull Request
       │
       ├── Backend CI
       │      └── Maven Test
       │
       └── Frontend CI
              └── npm build
```

---

## 🛣️ Roadmap

### Próximas evoluções

- [ ] Refresh Token com rotação e revogação
- [ ] Convite de membros por e-mail
- [ ] OAuth2 / OpenID Connect
- [ ] Stripe ou Mercado Pago
- [ ] Webhooks idempotentes
- [ ] Redis para cache e rate limiting
- [ ] Kafka para eventos de domínio
- [ ] OpenTelemetry
- [ ] Prometheus + Grafana
- [ ] Testcontainers
- [ ] Upload de arquivos em S3 / Blob Storage
- [ ] Deploy AWS / Azure
- [ ] Kubernetes + HPA

---

## 🧠 Conceitos demonstrados

Este projeto foi desenhado para demonstrar conhecimentos valorizados em processos seletivos de engenharia de software:

`Java` · `Spring Boot` · `REST` · `JWT` · `Spring Security` · `RBAC` · `Multi-Tenancy` · `JPA` · `Hibernate` · `PostgreSQL` · `Flyway` · `React` · `TypeScript` · `Docker` · `CI/CD` · `SaaS Architecture` · `Audit Log` · `Clean Separation of Concerns`

---

## 👨‍💻 Autor

**Jucelio Farias Coelho**

GitHub: [@juceliocoelho2022](https://github.com/juceliocoelho2022)

---

<div align="center">

### ⭐ Se este projeto foi útil, considere deixar uma estrela.

**NexaWorkspace SaaS — arquitetura de produto, não apenas CRUD.**

</div>
