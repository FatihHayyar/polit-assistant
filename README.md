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
      (JDBC)               Engine                 |
          |                                       v
          |                              Notification Service
          |                                       |
          |                               Notification Port
          |                                       |
          |                     +-----------------+----------------+
          |                     |                                  |
          v                     v                                  v
     PostgreSQL       Teams Notification Adapter          Outlook Adapter
          |
          +-----------------------------------------------+
                          |
                    Search API (JPA + PostgreSQL FTS)
```

---

# Implemented Features

- ✅ OpenParlData REST integration
- ✅ Full and incremental affairs import
- ✅ Parliamentary document import
- ✅ Raw JSON persistence
- ✅ Normalized PostgreSQL data model
- ✅ Flyway database migrations
- ✅ Scheduler for automated imports
- ✅ Import job tracking
- ✅ Sync state tracking for incremental imports
- ✅ Rule-based topic classification
- ✅ Configurable classification rules (application.yml)
- ✅ Alert generation
- ✅ Notification pipeline
- ✅ Teams notification adapter (mock)
- ✅ PostgreSQL Full-Text Search (GIN + tsvector)
- ✅ Search by keyword
- ✅ Search by topic
- ✅ Pagination (limit / offset)
- ✅ Docker & Docker Compose
- ✅ GitHub Actions CI
- 🚧 Real Teams webhook integration
- 🚧 Outlook integration
- 🚧 SharePoint integration
- 🚧 AI Assistant / Chatbot

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

- OpenParlData ingestion
- Incremental synchronization
- PostgreSQL full-text search
- Topic classification
- Alert generation
- Notification architecture
- REST API
- Docker deployment
- GitHub Actions CI

The remaining work mainly focuses on Microsoft 365 integration and AI-powered features.

Upcoming milestones

## Phase 1

- Microsoft Teams Webhook
- Outlook notifications
- SharePoint integration
- Notification retry mechanism

## Phase 2

- AI-assisted topic classification
- AI chatbot / Copilot integration
- Semantic search