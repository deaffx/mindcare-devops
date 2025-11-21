# 🧠 MindCare - Plataforma de Bem-Estar Emocional

> Sistema completo de acompanhamento de saúde mental e metas pessoais com IA Generativa

[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-orange?logo=rabbitmq)](https://www.rabbitmq.com/)

**Desenvolvido por:** Thiago Moreno RM554507 | Celso Canaveze RM556118
**Instituição:** FIAP

---

## 📋 Sobre o Projeto

Plataforma web Full MVC focada em bem-estar emocional no ambiente de trabalho. Combina registro de humor, gestão de metas e assistente virtual com IA para promover saúde mental e produtividade.

### ✨ Funcionalidades

- 📊 **Registro de Humor** - Acompanhamento diário com escalas e categorias emocionais
- 🎯 **Metas Pessoais** - Sistema com metas de prazo e consecutivas (streak tracking)
- 🤖 **Assistente IA** - Chat MindBot com mensagens motivacionais contextualizadas
- 🔔 **Notificações Assíncronas** - Sistema de mensageria RabbitMQ
- 📈 **Dashboard** - Visualização de estatísticas e progresso em tempo real
- 🌐 **i18n** - Português (pt-BR) e Inglês (en-US)
- 🔒 **Segurança** - Spring Security com JWT + Form Login
- ⚡ **Cache** - Otimização de queries frequentes

**Alinhado aos ODS 3 (Saúde e bem-estar) e ODS 8 (Trabalho decente)**

---

## 🏗️ Stack Tecnológica

### Backend
- **Java 17** + **Spring Boot 3.5.4**
- **Spring MVC** - Controllers REST e Web
- **Spring Data JPA** - Persistência com Hibernate
- **Spring Security 6** - Autenticação JWT + Form Login
- **Spring AI 1.0.0-M5** - Integração via Groq
- **Spring AMQP** - Mensageria assíncrona com RabbitMQ
- **Spring Cache** - Cache em memória (ConcurrentMapCacheManager)
- **Bean Validation** - Validação declarativa (Jakarta)
- **Flyway** - Versionamento de banco de dados
- **JJWT 0.12.5** - Tokens JWT
- **Lombok** - Redução de boilerplate

### Frontend
- **Thymeleaf** - Template engine server-side
- **HTML5/CSS3** - Interface responsiva
- **JavaScript** - Interatividade client-side

### Infraestrutura
- **PostgreSQL 16** - Banco de dados relacional
- **RabbitMQ 3.13** - Message broker
- **Gradle 8** - Build automation

---

## 🚀 Configuração e Execução

### Pré-requisitos

- Java 17+
- Gradle 8.x (wrapper incluído)
- PostgreSQL 16+
- RabbitMQ 3.13+

### 1. Clone e Configure o Banco

```bash
git clone https://github.com/deaffx/mindcare.git
cd mindcare
```

```sql
psql -U postgres
CREATE DATABASE mindcare_db;
```

### 2. Configure o RabbitMQ

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
```

### 3. Configure Variáveis de Ambiente

Crie arquivo `.env` ou configure no sistema:

```properties
GROQ_API_KEY=sua-chave-groq-aqui
```

**Obter chave Groq:** https://console.groq.com/ → API Keys

### 4. Execute

```bash
.\gradlew.bat bootRun
```

**Acesso:** http://localhost:8080

---

## 📚 API REST - Principais Endpoints
## 📚 Exemplos de Operações CRUD (JSON)

### Usuários

**Create (POST /usuarios)**
```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "senha": "senhaSegura123",
  "cargo": "Psicólogo",
  "preferencia_idioma": "pt-BR"
}
```

**Read (GET /usuarios/{id})**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "cargo": "Psicólogo",
  "preferencia_idioma": "pt-BR",
  "role": "USER",
  "ativo": true,
  "criado_em": "2025-11-21T10:00:00Z"
}
```

**Update (PUT /usuarios/{id})**
```json
{
  "cargo": "Coordenador",
  "preferencia_idioma": "en-US"
}
```

**Delete (DELETE /usuarios/{id})**
```json
{
  "message": "Usuário removido com sucesso"
}
```

### Registros de Humor

**Create (POST /registros-humor)**
```json
{
  "usuario_id": 1,
  "nivel_humor": 4,
  "emocao": "Feliz",
  "descricao": "Dia produtivo e animado",
  "data": "2025-11-21"
}
```

**Read (GET /registros-humor/{id})**
```json
{
  "id": 10,
  "usuario_id": 1,
  "nivel_humor": 4,
  "emocao": "Feliz",
  "descricao": "Dia produtivo e animado",
  "data": "2025-11-21",
  "criado_em": "2025-11-21T18:00:00Z"
}
```

**Update (PUT /registros-humor/{id})**
```json
{
  "nivel_humor": 5,
  "emocao": "Muito Feliz",
  "descricao": "Recebi boas notícias"
}
```

### Metas

**Delete (DELETE /metas/{id})**
```json
{
  "message": "Meta removida com sucesso"
}
```

**Base URL:** `http://localhost:8080/api`  
**Auth:** Header `Authorization: Bearer {token}`

### Autenticação

```bash
# Registrar
POST /api/auth/register
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "cargo": "Desenvolvedor"
}

# Login
POST /api/auth/login
{
  "email": "joao@email.com",
  "senha": "senha123"
}
```

### Humor

```bash
# Registrar
POST /api/humor
{
  "nivelHumor": 4,
  "emocao": "FELIZ",
  "descricao": "Dia produtivo!"
}

# Listar (paginado)
GET /api/humor?page=0&size=10

# Média semanal
GET /api/humor/media-semanal
```

### Metas

```bash
# Criar meta consecutiva
POST /api/metas
{
  "titulo": "10 dias sem fumar",
  "categoria": "HABITO",
  "tipo": "CONSECUTIVO",
  "duracaoDias": 10
}

# Registrar progresso
POST /api/metas/{id}/progresso
{
  "observacao": "Mais um dia!"
}

# Listar ativas
GET /api/metas/ativas
```

### Chat IA

```bash
# Conversar com MindBot
POST /api/mensagens/chat
{
  "mensagem": "Como melhorar minha produtividade?",
  "contexto": "trabalho"
}
```

---

## 🧪 Testando

### Interface Web

1. Acesse http://localhost:8080
2. Crie uma conta em "Registrar-se"
3. Faça login e explore o dashboard

### API REST (cURL)

```bash
# Registrar
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"Test","email":"test@email.com","senha":"senha123","cargo":"Dev"}'

# Login e copiar token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@email.com","senha":"senha123"}'

# Registrar humor (use o token)
curl -X POST http://localhost:8080/api/humor \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nivelHumor":5,"emocao":"FELIZ","descricao":"Dia excelente!"}'
```

---

## 🏆 Conformidade com Requisitos

✅ **Anotações Spring** - Injeção de dependências e configuração de beans  
✅ **Model/DTO** - Camadas bem definidas com métodos de acesso  
✅ **Spring Data JPA** - Persistência com Hibernate  
✅ **Bean Validation** - Validação declarativa em todas as entidades  
✅ **Cache** - Spring Cache com ConcurrentMapCacheManager  
✅ **i18n** - Suporte para pt-BR e en-US  
✅ **Paginação** - Implementada em todos os endpoints de listagem  
✅ **Spring Security** - Autenticação JWT + Form Login + Autorização  
✅ **Exception Handling** - GlobalExceptionHandler para tratamento centralizado  
✅ **Mensageria** - RabbitMQ com filas assíncronas  
✅ **IA Generativa** - Spring AI com OpenAI via Groq
✅ **REST API** - Verbos HTTP e status codes adequados  

**Caches configurados:** `usuarios`, `metas`, `humor`, `estatisticas`  
**RabbitMQ Console:** http://localhost:15672 (guest/guest)  
**Actuator:** http://localhost:8080/actuator/health

---

## 🛠️ Estrutura do Projeto

```
mindcare/
├── src/main/
│   ├── java/br/com/fiap/mindcare/
│   │   ├── config/          # Security, Cache, RabbitMQ, i18n
│   │   ├── controller/      # REST API + MVC Controllers
│   │   ├── exception/       # Exception handlers
│   │   ├── model/           # Entidades JPA + DTOs
│   │   ├── repository/      # Spring Data repositories
│   │   └── service/         # Lógica de negócio
│   └── resources/
│       ├── db/migration/    # Flyway migrations
│       ├── static/          # CSS, JS
│       ├── templates/       # Thymeleaf views
│       ├── application.properties
│       └── messages_*.properties  # i18n
└── build.gradle
```

### Build

```bash
.\gradlew.bat build      # Build completo
.\gradlew.bat test       # Executar testes
.\gradlew.bat bootRun    # Executar aplicação
```

---

## 📄 Licença

Projeto acadêmico - FIAP - Devops e Cloud - 2025
