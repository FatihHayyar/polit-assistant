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
                    +------------------+
                    | OpenParlData API |
                    +---------+--------+
                              |
                              |
                    OpenParlData Adapter
                              |
                              |
                     Import Service (JDBC)
                              |
                              |
                        PostgreSQL
                              |
         +--------------------+--------------------+
         |                    |                    |
         |                    |                    |
 Classification         Search API (JPA)      Alert Engine
         |                    |                    |
         +--------------------+--------------------+
                              |
                      Teams / Outlook
```

---

# Implemented Features

- ✅ OpenParlData integration
- ✅ Affairs import
- ✅ Documents import
- ✅ Raw JSON storage
- ✅ Normalized database model
- ✅ Rule-based topic classification
- ✅ Search REST API
- ✅ Scheduler
- ✅ Import job logging
- ✅ Alert generation
- 🚧 Teams integration
- 🚧 Outlook integration
- 🚧 AI Assistant

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
POST /api/v1/dev/import/affairs/full?offset=0&limit=5
```

## Search

```
GET /api/v1/affairs?topic=ENERGY

GET /api/v1/affairs?q=Elektrizität
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

The backend foundation has been completed.

Upcoming milestones

- Microsoft Teams notifications
- Outlook notifications
- SharePoint integration
- AI-based classification
- Chatbot integration