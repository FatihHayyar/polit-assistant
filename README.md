# Polit Assistant

Open Data + AI-based political monitoring assistant for environmental policy topics.

---

# Goal

This project builds an MVP for monitoring parliamentary affairs from OpenParlData and classifying them into WWF-relevant topics such as:

- Energy
- Biodiversity
- Water
- Agriculture
- Spatial Planning
- Mobility

The application automatically imports new parliamentary affairs, downloads related documents, classifies them and prepares notifications for Microsoft Teams and Outlook.

---

# Tech Stack

- Java 25
- Spring Boot 4.1
- Maven
- PostgreSQL
- Flyway
- Docker & Docker Compose
- JDBC (ETL / Import)
- Spring Data JPA (Read Model)
- GitHub Actions
- Hexagonal Architecture

---

# Architecture

```text
                    +----------------------+
                    |   OpenParlData API   |
                    +----------+-----------+
                               |
                               v
                  OpenParlData Adapter (REST)
                               |
                               v
                  Import Orchestrator Service
                               |
          +--------------------+----------------------+
          |                    |                      |
          v                    v                      v
   Import Service       Classification        Alert Service
      (JDBC)               Engine                     |
          |                                           |
          |                                   User Preferences
          |                                           |
          |                                           v
          |                                   Notification Service
          |                                           |
          |                                   Notification Dispatcher
          |                                           |
          |                         +-----------------+----------------+
          |                         |                                  |
          v                         v                                  v
     PostgreSQL           Teams Notification Adapter            Outlook Adapter 
          |
          +------------------------------------------------------------+
                                         |
                                 Search API (PostgreSQL FTS)
                                         |
                                         v
                                   Chat Service
                                         |
                                         v
                                   Chat REST API
```

---

# Implemented Features

## Data Integration

- ✅ OpenParlData REST integration
- ✅ Full import
- ✅ Incremental import
- ✅ Parliamentary document import
- ✅ Sync state tracking

## Data Management

- ✅ PostgreSQL
- ✅ Flyway
- ✅ Raw JSON persistence
- ✅ Normalized database model
- ✅ Scheduler for automated imports

## Search

- ✅ PostgreSQL Full-Text Search
- ✅ Topic search
- ✅ Keyword search
- ✅ Pagination

## Classification

- ✅ Rule-based topic classification
- ✅ Configurable YAML rules
- ✅ Confidence score

## Notifications

- ✅ Alert generation
- ✅ User preferences
- ✅ Recipient-specific notifications
- ✅ Notification retry
- ✅ Multi-channel notification dispatcher
- ✅ Teams adapter (mock)
- ✅ Outlook adapter (mock)

## Chat

- ✅ Chat endpoint
- ✅ Intent parser
- ✅ Chatbot-ready API

## DevOps

- ✅ Docker
- ✅ Docker Compose
- ✅ GitHub Actions
- ✅ Swagger / OpenAPI

---

# Local Development

## Start PostgreSQL

```bash
docker compose up -d postgres
```

## Run application

Using IntelliJ

or

```bash
./mvnw spring-boot:run
```

---

# Local URLs

Application

```
http://localhost:8080
```

Health

```
http://localhost:8080/actuator/health
```

---

# Database

| Property | Value |
|----------|-------|
| Host | localhost |
| Port | 5433 |
| Database | polit_assistant |
| User | polit |
| Password | polit_dev_password |

---

# Useful Endpoints

## Import

```
## Import

Full import

POST /api/v1/dev/import/affairs/full?offset=0&limit=50

Incremental import

POST /api/v1/dev/import/affairs/incremental?limit=50
```

## Search

```
GET /api/v1/affairs?q=Verkehr

GET /api/v1/affairs?topic=ENERGY

GET /api/v1/affairs?q=Verkehr&topic=MOBILITY

GET /api/v1/affairs?q=Verkehr&topic=MOBILITY&limit=20&offset=0
```

## Pending Alerts

```
GET /api/v1/dev/alerts/pending
```

---
### Send pending notifications

POST /api/v1/dev/alerts/send-pending

## User Preferences

Add preference:

POST /api/v1/dev/users/preferences

Example body:

{
"email": "test@hslu.ch",
"displayName": "Test User",
"topic": "ENERGY",
"channel": "OUTLOOK"
}

## List preferences:

GET /api/v1/dev/users/preferences

## Chat

POST /api/v1/chat

{
"question":"Show me all energy affairs"
}

## Dachboard
GET /api/v1/dev/dashboard/summary

# Continuous Integration

GitHub Actions automatically executes

- Maven Build
- Unit Tests
- PostgreSQL Test Container
- Docker Build

---

# Project Status

Current version

```
v0.1.0
```

Backend MVP completed.

Implemented:

- OpenParlData monitoring
- Incremental synchronization
- Rule-based topic classification
- PostgreSQL Full-Text Search
- Chatbot-ready REST API
- User preference management
- Multi-channel notification architecture
- Docker deployment
- CI/CD pipeline

Remaining work:

- Real Microsoft 365 integration
- AI-powered classification
- Copilot integration