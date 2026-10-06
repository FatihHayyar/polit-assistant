# WWF Polit-Assistant

Open-data-based political monitoring assistant for WWF Switzerland.

The application imports parliamentary data from OpenParlData, structures and classifies parliamentary affairs into WWF-relevant topics, provides search and monitoring functionality, and notifies subscribed users about relevant developments.

The current project focus is a working, transparent MVP. AI-based features are considered optional extensions and are not required for the core system.

---

# Project Goal

The WWF Polit-Assistant supports the monitoring of Swiss parliamentary affairs.

The MVP focuses on:

- automatic import and synchronization of parliamentary data
- structured storage of affairs, documents, meetings and agenda items
- rule-based classification into WWF-relevant topics
- search and filtering
- detection of new relevant parliamentary affairs
- monitoring of relevant session agenda items
- topic subscriptions
- email notifications
- a documented REST API
- a simple multilingual web interface

The application uses parliamentary data provided by OpenParlData.

Source: OpenParlData.ch

---

# WWF Topics

The current rule-based classifier supports the following WWF topics:

- Energy
- Biodiversity
- Water
- Agriculture
- Spatial Planning
- Climate
- Mobility
- Waste

Affairs without sufficient evidence for one of these topics are classified internally as `SONSTIGES`.

Classification rules are configurable and can use both affair metadata and imported parliamentary document content.

---

# Tech Stack

- Java 25
- Spring Boot 4.1
- Maven
- PostgreSQL
- PostgreSQL Full-Text Search
- Flyway
- Docker & Docker Compose
- JDBC for import / ETL
- Spring Data JPA
- Spring Mail
- Vanilla HTML / CSS / JavaScript
- Swagger / OpenAPI
- GitHub Actions
- Testcontainers

---

# Architecture

```text
                         +----------------------+
                         |   OpenParlData API   |
                         +----------+-----------+
                                    |
                                    v
                         OpenParlData REST Client
                                    |
                                    v
                         Import / Sync Services
                                    |
              +---------------------+----------------------+
              |                     |                      |
              v                     v                      v
           Affairs               Meetings               Agendas
              |
              v
           Documents
              |
              v
        PostgreSQL Database
              |
              +----------------------+----------------------+
              |                      |                      |
              v                      v                      v
     Classification Engine       Search API          Monitoring Logic
       (rule-based/YAML)       PostgreSQL FTS       Affairs + Agendas
              |                                             |
              v                                             v
       WWF Topic Results                              Alert Generation
                                                            |
                                                            v
                                                   User Subscriptions
                                                            |
                                                            v
                                                   Email Notifications

                         REST API / Swagger
                                |
                                v
                    Multilingual Web Interface
                         DE / FR / IT / EN
```

The application separates external data integration, persistence, classification, monitoring, notification and presentation concerns.

AI functionality is intentionally not part of the critical MVP path.

---

# Implemented Features

## OpenParlData Integration

- ✅ OpenParlData REST integration
- ✅ Parliamentary affair import
- ✅ Incremental affair synchronization
- ✅ Parliamentary document import
- ✅ Meeting import
- ✅ Agenda item import
- ✅ Affair/document relationships
- ✅ Meeting/agenda relationships
- ✅ Automatic import of affairs referenced by agenda items
- ✅ Sync-state tracking
- ✅ Scheduled data updates
- ✅ Raw JSON persistence for imported data

---

## Data Management

- ✅ PostgreSQL persistence
- ✅ Flyway database migrations
- ✅ Normalized relational data model
- ✅ JDBC-based import pipeline
- ✅ Spring Data JPA read/write models where appropriate
- ✅ Automated synchronization jobs

---

## WWF Topic Classification

- ✅ Rule-based classification
- ✅ Configurable YAML rules
- ✅ German and French keyword rules
- ✅ Affair title evaluation
- ✅ Extended title evaluation
- ✅ Imported document-content evaluation
- ✅ Confidence score
- ✅ Classification provenance
- ✅ Multi-topic classification support
- ✅ `SONSTIGES` fallback classification

The classification engine is deterministic and does not require an external AI service.

---

## Search and Filtering

- ✅ Parliamentary affair search
- ✅ WWF topic filtering
- ✅ Keyword search
- ✅ Combined keyword + topic filtering
- ✅ Pagination
- ✅ PostgreSQL Full-Text Search
- ✅ GIN-indexed document search

Example:

```http
GET /api/v1/affairs?q=Verkehr&limit=20&offset=0
```

---

## Political Monitoring

- ✅ Detection of new relevant parliamentary affairs
- ✅ Monitoring of relevant session agenda items
- ✅ Connection between agenda items and parliamentary affairs
- ✅ WWF-topic-based relevance detection
- ✅ Upcoming relevant agenda REST API
- ✅ Monitoring views in the web interface

---

## Email Subscriptions

Users can subscribe to selected WWF topics without creating a password-based account.

Implemented subscription lifecycle:

- ✅ Select WWF topics
- ✅ Subscribe using an email address
- ✅ Email verification
- ✅ Secure verification tokens
- ✅ Subscription activation
- ✅ Detection of already active subscriptions
- ✅ Request secure management link
- ✅ View current subscription
- ✅ Update subscribed topics
- ✅ Delete subscription
- ✅ Complete removal of deleted subscription data
- ✅ Token expiration
- ✅ Hashed token storage

Management links are sent by email instead of exposing subscription data through a public email lookup.

If an active subscription already exists, the application informs the user and directs them to the subscription management workflow instead of creating a duplicate subscription.

## Email Notifications

- ✅ Alert generation
- ✅ Recipient-specific alerts
- ✅ Real email delivery
- ✅ Notification status tracking
- ✅ Retry support
- ✅ Verification emails
- ✅ Subscription activation confirmation
- ✅ Secure management-link emails
- ✅ Subscription update confirmation
- ✅ Subscription cancellation confirmation
- ✅ Notifications for relevant parliamentary developments

SMTP credentials are configured through environment variables and are not stored in the repository.

---

## Web Interface

A lightweight frontend is included directly in the Spring Boot application.

No separate frontend framework or Node.js build is required.

Implemented:

- ✅ Parliamentary affair overview
- ✅ Search
- ✅ WWF topic filtering
- ✅ Pagination
- ✅ Upcoming relevant agenda items
- ✅ Latest relevant affairs
- ✅ Subscription creation
- ✅ Subscription management
- ✅ Affair detail page
- ✅ Imported parliamentary document content
- ✅ Links to original parliamentary sources/documents
- ✅ Responsive layout

---

## Multilingual Interface

The application interface supports:

- 🇩🇪 German
- 🇫🇷 French
- 🇮🇹 Italian
- 🇬🇧 English

The selected language is stored locally in the browser and remains active when navigating between the dashboard and affair detail pages.

Currently translated:

- application navigation and labels
- search/filter controls
- subscription interface
- status/error messages
- WWF topic labels
- dates
- affair detail interface
- document metadata

Parliamentary source content itself is currently displayed in its original imported language.

Automatic translation of parliamentary content is considered an optional future extension.

---

## REST API

The MVP exposes REST endpoints for its core functionality.

### Affairs

```http
GET /api/v1/affairs
GET /api/v1/affairs/{id}
```

Search/filter example:

```http
GET /api/v1/affairs?q=Verkehr&topic=Mobilität&limit=20&offset=0
```

### Relevant Agenda Items

```http
GET /api/v1/agendas/relevant
```

Example:

```http
GET /api/v1/agendas/relevant?limit=5&offset=0
```

### Subscriptions

Create or request a subscription:

```http
POST /api/v1/subscriptions
```

Verify an email address:

```http
GET /api/v1/subscriptions/verify?token=...
```

Request a secure management link:

```http
POST /api/v1/subscriptions/manage
```

Load subscription using a management token:

```http
GET /api/v1/subscriptions/manage/{token}
```

Update subscribed topics:

```http
PUT /api/v1/subscriptions/manage/{token}
```

Delete subscription:

```http
DELETE /api/v1/subscriptions/manage/{token}
```

---

# Development Endpoints

Development endpoints are available under:

```text
/api/v1/dev/**
```

These endpoints support development and testing workflows such as imports, synchronization and alert processing.

They are not intended to represent the public production API.

---

# Local Development

## Start PostgreSQL

```bash
docker compose up -d postgres
```

The Docker Compose PostgreSQL instance is exposed locally on port `5433`.

---

## Run Application

Using IntelliJ IDEA

or:

```bash
./mvnw spring-boot:run
```

---

# Local URLs

## Application

```text
http://localhost:8080
```

## Swagger / OpenAPI

```text
http://localhost:8080/swagger-ui/index.html
```

## Health

```text
http://localhost:8080/actuator/health
```

---

# Database

Default local Docker development configuration:

| Property | Value |
|---|---|
| Host | localhost |
| Port | 5433 |
| Database | polit_assistant |
| User | polit |
| Password | polit_dev_password |

Production credentials must be supplied through environment-specific configuration and must not be committed to the repository.

---

# Email Configuration

Email delivery is configured through Spring Mail environment variables.

Credentials and application passwords must never be committed to Git.

The current MVP uses SMTP-based email delivery for verification, subscription management and monitoring notifications.

---

# Database Migrations

Database schema changes are managed through Flyway.

The current schema includes, among other things:

- parliamentary affairs
- parliamentary documents
- classifications
- meetings
- agenda items
- synchronization state
- alerts
- notification data
- application users
- user preferences
- secure subscription tokens

---

# Testing

The project uses automated tests for backend functionality and Testcontainers where database integration is required.

Before pushing changes:

```bash
./mvnw test
```

A successful test run should be completed before merging changes into the main development branch.

---

# Continuous Integration

GitHub Actions is used for continuous integration.

The CI workflow validates the project through automated build/test steps.

The repository also contains Docker configuration for reproducible local execution.

---

# Project Scope

The current MVP deliberately prioritizes a reliable working application over experimental AI functionality.

## MVP

- OpenParlData integration
- structured parliamentary data storage
- automatic data synchronization
- rule-based WWF topic classification
- search and filtering
- relevant-affair monitoring
- relevant agenda monitoring
- email subscriptions
- email notifications
- REST API
- simple web interface
- multilingual user interface

## Optional / Future Extensions

Possible later extensions include:

- translation of parliamentary source content
- AI-assisted classification
- conversational access to parliamentary data
- improved visualisations
- additional notification channels
- Microsoft 365 integration
- more advanced relevance evaluation

These extensions are not required for the current core MVP.

---

# Current Project Status

The project is currently in active MVP development.

Completed core areas:

- OpenParlData data integration
- incremental synchronization
- structured PostgreSQL persistence
- document import
- meeting and agenda import
- rule-based WWF topic classification
- PostgreSQL Full-Text Search
- parliamentary affair REST API
- relevant agenda monitoring
- secure topic subscriptions
- real email notifications
- simple web frontend
- affair detail view
- multilingual DE / FR / IT / EN interface
- Swagger / OpenAPI
- Docker-based local infrastructure
- CI pipeline

Current focus:

- frontend refinement and validation
- end-to-end MVP testing
- validation of classification results
- documentation
- preparation for stakeholder feedback

---

# Experimental Features

Experimental code may exist for conversational access / local LLM integration.

This functionality is currently **not part of the required MVP** and is not required to run or use the main application.

The working MVP does not depend on a local LLM or an external AI provider.

---

# Data Attribution

Parliamentary data is provided by:

**OpenParlData.ch**

OpenParlData data is used according to its applicable licence and attribution requirements.
