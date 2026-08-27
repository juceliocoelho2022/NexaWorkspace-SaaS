# NexaWorkspace SaaS

SaaS B2B multi-tenant completo para portfólio profissional. Empresas criam um workspace isolado, autenticam usuários, gerenciam projetos, consultam indicadores e alteram o plano da assinatura em ambiente sandbox.

## Stack
- Java 21 + Spring Boot 4.1
- Spring Security + JWT + RBAC
- Spring Data JPA + PostgreSQL 18
- Flyway
- React 19 + TypeScript + Vite
- Docker Compose + Nginx
- GitHub Actions
- Spring Boot Actuator

## Funcionalidades implementadas
- Cadastro de empresa + OWNER em uma única transação
- Login JWT
- Multi-tenancy por `tenant_id`
- CRUD de projetos isolado por tenant
- Dashboard com métricas
- Planos FREE / PRO / BUSINESS
- Troca de plano em modo sandbox
- Trilha de auditoria
- RBAC para operações administrativas
- Migrações versionadas
- Health checks
- CI backend + frontend
- UI responsiva e profissional

## Executar com Docker
```bash
docker compose up --build
```
Acesse: `http://localhost:3000`

### Primeiro acesso
Clique em **Criar agora**, cadastre empresa, nome, e-mail e uma senha com pelo menos 8 caracteres.

## Executar em desenvolvimento
Banco:
```bash
docker compose up -d postgres
```
Backend:
```bash
cd backend
mvn spring-boot:run
```
Frontend:
```bash
cd frontend
npm install
npm run dev
```

## Endpoints principais
| Método | Endpoint | Objetivo |
|---|---|---|
| POST | `/api/auth/register` | Cria tenant + owner |
| POST | `/api/auth/login` | Gera JWT |
| GET | `/api/dashboard` | KPIs do tenant |
| GET/POST | `/api/projects` | Lista/cria projetos |
| PUT/DELETE | `/api/projects/{id}` | Atualiza/exclui com tenant scope |
| GET | `/api/billing` | Assinatura atual |
| PATCH | `/api/billing/plan` | Troca plano |
| GET | `/api/audit` | Auditoria (OWNER/ADMIN) |
| GET | `/actuator/health` | Health check |

## Segurança
A regra mais importante do projeto está no repositório de projetos: toda busca de recurso sensível combina `resource_id` + `tenant_id`. O JWT carrega `uid`, `tid` e `role`. A aplicação não confia em um tenant informado pelo frontend.

## Próximo nível
Integre Stripe ou Mercado Pago, Redis para rate limiting/cache, OpenTelemetry, Testcontainers e deploy em AWS/Azure. A arquitetura atual foi mantida simples o suficiente para rodar localmente e forte o suficiente para demonstrar conceitos SaaS reais.
