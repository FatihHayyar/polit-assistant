# WWF Polit-Assistant

Open-Data-basierter Polit-Monitoring-Assistent für WWF Schweiz.

Die Anwendung importiert parlamentarische Daten aus OpenParlData, speichert und verknüpft diese strukturiert, klassifiziert parlamentarische Geschäfte nach WWF-relevanten Themen und stellt Such-, Monitoring- und Benachrichtigungsfunktionen bereit.

Der aktuelle Projektfokus liegt auf einem funktionierenden und transparenten MVP. KI-basierte Funktionen sind optionale Erweiterungen und für das Kernsystem nicht erforderlich.

---

# Projektziel

Der WWF Polit-Assistant unterstützt das Monitoring parlamentarischer Geschäfte in der Schweiz.

Der MVP konzentriert sich auf:

- automatischen Import und regelmässige Synchronisierung parlamentarischer Daten
- strukturierte Speicherung von Geschäften, Dokumenten, Sitzungen und Traktanden
- regelbasierte Klassifikation nach WWF-relevanten Themen
- Suche und Filterung
- Erkennung neuer relevanter parlamentarischer Geschäfte
- Monitoring relevanter Sessionstraktanden
- Themen-Abonnements
- E-Mail-Benachrichtigungen
- eine dokumentierte REST-API
- eine einfache mehrsprachige Weboberfläche

Die Anwendung verwendet parlamentarische Daten von OpenParlData.

**Quelle: OpenParlData.ch**

---

# Quick Start mit Docker

Für die lokale Ausführung des MVP werden lediglich **Git** und **Docker Desktop** benötigt.

Eine separate Installation von Java, Maven oder PostgreSQL ist für diesen Weg nicht erforderlich.

## 1. Repository klonen

```bash
git clone https://github.com/FatihHayyar/polit-assistant.git
cd polit-assistant
```

## 2. Anwendung starten

```bash
docker compose up --build -d
```

Docker startet automatisch:

- die Spring-Boot-Anwendung
- PostgreSQL 17
- die Flyway-Datenbankmigrationen
- Mailpit als lokalen Mailserver

Bei einer neuen und leeren Datenbank wird zusätzlich automatisch ein kleiner Startdatenbestand aus OpenParlData geladen.

Standardmässig werden:

- 50 parlamentarische Geschäfte importiert
- die zugehörigen parlamentarischen Dokumente importiert
- die importierten Geschäfte automatisch nach WWF-Themen klassifiziert

Der Bootstrap wird nur ausgeführt, wenn die Datenbank noch keine parlamentarischen Geschäfte enthält.

Bereits vorhandene Datenbestände werden nicht überschrieben oder zurückgesetzt.

## 3. Anwendung öffnen

Nach dem Start stehen folgende Dienste zur Verfügung:

| Dienst | Adresse |
|---|---|
| Polit-Assistant | `http://localhost:8081` |
| Swagger / OpenAPI | `http://localhost:8081/swagger-ui/index.html` |
| Mailpit | `http://localhost:8025` |
| PostgreSQL | `localhost:5433` |

## Lokale E-Mails testen

Im Docker-Setup werden E-Mails nicht über einen externen SMTP-Provider versendet.

Stattdessen verwendet die Anwendung **Mailpit** als lokalen SMTP-Server. Dadurch können die E-Mail-Funktionen des MVP ohne externe Zugangsdaten getestet werden.

Unter anderem können damit folgende Abläufe getestet werden:

- E-Mail-Verifikation bei einer neuen Themen-Subscription
- Aktivierung eines Abonnements
- Versand eines sicheren Verwaltungslinks
- Änderung bestehender Themen-Abonnements
- Löschung eines Abonnements
- Benachrichtigungen über relevante parlamentarische Entwicklungen

Alle erzeugten E-Mails können unter folgender Adresse eingesehen werden:

```text
http://localhost:8025
```

Für den Docker-Quick-Start werden keine Gmail- oder anderen externen SMTP-Zugangsdaten benötigt.

## Container stoppen

```bash
docker compose down
```

Die PostgreSQL-Daten bleiben dabei im Docker-Volume erhalten und stehen beim nächsten Start wieder zur Verfügung.

> **Achtung:** `docker compose down -v` entfernt zusätzlich das Docker-Volume der Datenbank und löscht damit den lokalen Datenbestand. Dieser Befehl sollte nur verwendet werden, wenn die Datenbank bewusst vollständig zurückgesetzt werden soll.

---

# WWF-Themen

Der aktuelle regelbasierte Klassifikator unterstützt folgende WWF-Themen:

- Energie
- Biodiversität
- Wasser
- Landwirtschaft
- Raumplanung
- Klima
- Mobilität
- Abfall

Geschäfte ohne ausreichende Evidenz für eines dieser Themen werden intern als `SONSTIGES` klassifiziert.

Die Klassifikationsregeln sind konfigurierbar und können sowohl Metadaten eines parlamentarischen Geschäfts als auch importierte Dokumentinhalte berücksichtigen.

---

# Technologie-Stack

- Java 25
- Spring Boot 4.1
- Maven
- PostgreSQL
- PostgreSQL Full-Text Search
- Flyway
- Docker & Docker Compose
- JDBC für Import / ETL
- Spring Data JPA
- Spring Mail
- Mailpit für lokale E-Mail-Tests
- Vanilla HTML / CSS / JavaScript
- Swagger / OpenAPI
- GitHub Actions
- Testcontainers

---

# Architektur

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

Die Anwendung trennt externe Datenintegration, Persistenz, Klassifikation, Monitoring, Benachrichtigung und Präsentation voneinander.

KI-Funktionalität ist bewusst nicht Teil des kritischen MVP-Pfads.

---

# Implementierte Funktionen

## OpenParlData-Integration

- ✅ OpenParlData REST-Integration
- ✅ Import parlamentarischer Geschäfte
- ✅ Inkrementelle Synchronisierung
- ✅ Import parlamentarischer Dokumente
- ✅ Import von Sitzungen
- ✅ Import von Traktanden
- ✅ Verknüpfung zwischen Geschäften und Dokumenten
- ✅ Verknüpfung zwischen Sitzungen und Traktanden
- ✅ Automatischer Import von Geschäften, die durch Traktanden referenziert werden
- ✅ Verwaltung des Synchronisierungsstands
- ✅ Geplante Datenaktualisierung
- ✅ Speicherung importierter Rohdaten als JSON
- ✅ Automatischer Docker-Bootstrap für neue lokale Installationen

---

## Datenhaltung

- ✅ PostgreSQL-Persistenz
- ✅ Flyway-Datenbankmigrationen
- ✅ Normalisiertes relationales Datenmodell
- ✅ JDBC-basierte Import-Pipeline
- ✅ Spring-Data-JPA-Modelle, wo sinnvoll
- ✅ Automatisierte Synchronisierungsjobs
- ✅ Persistente Docker-Volumes für lokale Daten

---

## WWF-Themenklassifikation

- ✅ Regelbasierte Klassifikation
- ✅ Konfigurierbare YAML-Regeln
- ✅ Deutsche und französische Keyword-Regeln
- ✅ Auswertung des Geschäftstitels
- ✅ Auswertung des erweiterten Titels
- ✅ Auswertung importierter Dokumentinhalte
- ✅ Confidence Score
- ✅ Klassifikationsherkunft / Provenance
- ✅ Unterstützung mehrerer Themen pro Geschäft
- ✅ `SONSTIGES` als Fallback-Klassifikation

Die Klassifikationslogik ist deterministisch und benötigt keinen externen KI-Dienst.

---

## Suche und Filterung

- ✅ Suche nach parlamentarischen Geschäften
- ✅ Filterung nach WWF-Themen
- ✅ Keyword-Suche
- ✅ Kombination aus Keyword- und Themenfilter
- ✅ Pagination
- ✅ PostgreSQL Full-Text Search
- ✅ GIN-indexierte Dokumentensuche

Beispiel:

```http
GET /api/v1/affairs?q=Verkehr&limit=20&offset=0
```

---

## Politisches Monitoring

- ✅ Erkennung neuer relevanter parlamentarischer Geschäfte
- ✅ Monitoring relevanter Sessionstraktanden
- ✅ Verbindung zwischen Traktanden und parlamentarischen Geschäften
- ✅ WWF-themenbasierte Relevanzerkennung
- ✅ REST-API für bevorstehende relevante Traktanden
- ✅ Monitoring-Ansichten in der Weboberfläche

---

## E-Mail-Abonnements

Benutzerinnen und Benutzer können ausgewählte WWF-Themen abonnieren, ohne ein passwortbasiertes Benutzerkonto erstellen zu müssen.

Implementierter Subscription-Lifecycle:

- ✅ WWF-Themen auswählen
- ✅ Abonnement über eine E-Mail-Adresse anfordern
- ✅ E-Mail-Verifikation
- ✅ Sichere Verifikationstokens
- ✅ Aktivierung des Abonnements
- ✅ Erkennung bereits aktiver Abonnements
- ✅ Sicheren Verwaltungslink anfordern
- ✅ Bestehendes Abonnement anzeigen
- ✅ Abonnierte Themen aktualisieren
- ✅ Abonnement vollständig löschen
- ✅ Sichere Token-Ablaufzeiten
- ✅ Gehashte Token-Speicherung

Wenn für eine E-Mail-Adresse bereits ein aktives Abonnement besteht, wird kein zusätzliches Abonnement erstellt. Die Anwendung weist stattdessen darauf hin, das bestehende Abonnement über den Verwaltungsprozess zu bearbeiten.

Verwaltungslinks werden per E-Mail versendet. Dadurch müssen Subscription-Daten nicht über eine öffentliche Suche anhand der E-Mail-Adresse bereitgestellt werden.

---

## E-Mail-Benachrichtigungen

- ✅ Alert-Erzeugung
- ✅ Empfängerspezifische Alerts
- ✅ SMTP-basierter E-Mail-Versand
- ✅ Tracking des Benachrichtigungsstatus
- ✅ Retry-Unterstützung
- ✅ Verifikations-E-Mails
- ✅ Bestätigung der Subscription-Aktivierung
- ✅ Sichere Verwaltungslinks per E-Mail
- ✅ Bestätigung von Subscription-Änderungen
- ✅ Bestätigung der Subscription-Löschung
- ✅ Benachrichtigungen über relevante parlamentarische Entwicklungen
- ✅ Lokaler E-Mail-Test mit Mailpit im Docker-Setup

Für reale SMTP-Umgebungen werden Zugangsdaten über Umgebungsvariablen konfiguriert und nicht im Repository gespeichert.

---

## Weboberfläche

Eine leichtgewichtige Weboberfläche ist direkt in die Spring-Boot-Anwendung integriert.

Es wird kein separates Frontend-Framework und kein Node.js-Build benötigt.

Implementiert:

- ✅ Übersicht parlamentarischer Geschäfte
- ✅ Suche
- ✅ WWF-Themenfilter
- ✅ Pagination
- ✅ Bevorstehende relevante Traktanden
- ✅ Neueste relevante Geschäfte
- ✅ Erstellung von Themen-Abonnements
- ✅ Verwaltung bestehender Abonnements
- ✅ Detailansicht parlamentarischer Geschäfte
- ✅ Anzeige importierter parlamentarischer Dokumentinhalte
- ✅ Links zu Originalquellen und Dokumenten
- ✅ Responsive Layout

---

## Mehrsprachige Benutzeroberfläche

Die Benutzeroberfläche unterstützt:

- 🇩🇪 Deutsch
- 🇫🇷 Französisch
- 🇮🇹 Italienisch
- 🇬🇧 Englisch

Die ausgewählte Sprache wird lokal im Browser gespeichert und bleibt beim Wechsel zwischen Dashboard und Detailansicht erhalten.

Aktuell übersetzt sind unter anderem:

- Navigation und Labels
- Such- und Filterelemente
- Subscription-Oberfläche
- Status- und Fehlermeldungen
- WWF-Themenbezeichnungen
- Datumsdarstellung
- Detailansicht parlamentarischer Geschäfte
- Dokumentmetadaten

Parlamentarische Quellinhalte werden derzeit in der jeweils importierten Originalsprache dargestellt.

Eine automatische Übersetzung parlamentarischer Inhalte ist als optionale spätere Erweiterung vorgesehen.

---

# REST-API

Der MVP stellt REST-Endpunkte für seine Kernfunktionen bereit.

## Parlamentarische Geschäfte

```http
GET /api/v1/affairs
GET /api/v1/affairs/{id}
```

Beispiel für Suche und Filterung:

```http
GET /api/v1/affairs?q=Verkehr&topic=Mobilität&limit=20&offset=0
```

## Relevante Traktanden

```http
GET /api/v1/agendas/relevant
```

Beispiel:

```http
GET /api/v1/agendas/relevant?limit=5&offset=0
```

## Subscriptions

Neue Subscription anfordern:

```http
POST /api/v1/subscriptions
```

E-Mail-Adresse verifizieren:

```http
GET /api/v1/subscriptions/verify?token=...
```

Sicheren Verwaltungslink anfordern:

```http
POST /api/v1/subscriptions/manage
```

Subscription über einen Management-Token laden:

```http
GET /api/v1/subscriptions/manage/{token}
```

Abonnierte Themen aktualisieren:

```http
PUT /api/v1/subscriptions/manage/{token}
```

Subscription löschen:

```http
DELETE /api/v1/subscriptions/manage/{token}
```

Die vollständige interaktive API-Dokumentation steht über Swagger zur Verfügung.

Docker:

```text
http://localhost:8081/swagger-ui/index.html
```

Lokale Entwicklung:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Development-Endpunkte

Entwicklungsendpunkte stehen unter folgendem Pfad zur Verfügung:

```text
/api/v1/dev/**
```

Diese Endpunkte unterstützen Entwicklungs- und Testabläufe wie Import, Synchronisierung und Alert-Verarbeitung.

Sie sind nicht als öffentliche Produktions-API vorgesehen.

---

# Alternative: Lokale Entwicklung mit IntelliJ oder Maven

Der Docker-Quick-Start ist der einfachste Weg, um das gesamte System auszuführen.

Für die aktive Entwicklung kann die Spring-Boot-Anwendung alternativ direkt über IntelliJ IDEA oder Maven gestartet werden.

## Infrastruktur starten

PostgreSQL kann über Docker gestartet werden:

```bash
docker compose up -d postgres
```

Die PostgreSQL-Instanz ist lokal über Port `5433` erreichbar.

## Anwendung starten

Über IntelliJ IDEA oder:

```bash
./mvnw spring-boot:run
```

Bei dieser Variante läuft die Spring-Boot-Anwendung standardmässig auf:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

Health Endpoint:

```text
http://localhost:8080/actuator/health
```

> Der vollständige Docker-Quick-Start verwendet dagegen Port `8081` für die Anwendung.

---

# Datenbank

Standardkonfiguration der lokalen Docker-Datenbank:

| Eigenschaft | Wert |
|---|---|
| Host | `localhost` |
| Port | `5433` |
| Datenbank | `polit_assistant` |
| Benutzer | `polit` |
| Passwort | `polit_dev_password` |

Die Daten werden in einem persistenten Docker-Volume gespeichert.

Ein normales:

```bash
docker compose down
```

entfernt die Container, aber nicht den gespeicherten Datenbestand.

Produktionszugangsdaten müssen über umgebungsspezifische Konfiguration bereitgestellt werden und dürfen nicht im Repository gespeichert werden.

---

# Docker-Bootstrap

Bei einer neuen Docker-Installation prüft die Anwendung beim Start, ob bereits parlamentarische Geschäfte in der Datenbank vorhanden sind.

Ist die Datenbank leer, wird automatisch ein kleiner Startdatenbestand aus OpenParlData importiert.

Standardkonfiguration:

```text
APP_BOOTSTRAP_ENABLED=true
APP_BOOTSTRAP_AFFAIR_LIMIT=50
```

Der Bootstrap importiert:

1. parlamentarische Geschäfte
2. zugehörige parlamentarische Dokumente
3. WWF-Themenklassifikationen für die importierten Geschäfte

Der Bootstrap erzeugt keine historischen Benachrichtigungen für diesen initialen Datenbestand.

Ist die Datenbank bereits befüllt, wird der Bootstrap übersprungen.

Dadurch kann dieselbe Docker-Konfiguration sowohl für eine neue Demo-Installation als auch für einen bereits vorhandenen lokalen Datenbestand verwendet werden.

---

# E-Mail-Konfiguration

Der E-Mail-Versand basiert auf Spring Mail.

## Docker / Demo

Im Docker-Quick-Start wird Mailpit verwendet:

```text
SMTP: mailpit:1025
Weboberfläche: http://localhost:8025
```

Es werden keine externen SMTP-Zugangsdaten benötigt.

## Reale SMTP-Umgebung

Für einen realen E-Mail-Versand können SMTP-Zugangsdaten über Umgebungsvariablen bereitgestellt werden.

Zugangsdaten, App-Passwörter und andere Secrets dürfen niemals in Git committed werden.

Der MVP unterstützt SMTP-basierten E-Mail-Versand für:

- E-Mail-Verifikation
- Subscription-Verwaltung
- Bestätigungs-E-Mails
- Monitoring-Benachrichtigungen

---

# Datenbankmigrationen

Änderungen am Datenbankschema werden über Flyway verwaltet.

Das aktuelle Schema umfasst unter anderem:

- parlamentarische Geschäfte
- parlamentarische Dokumente
- Klassifikationen
- Sitzungen
- Traktanden
- Synchronisierungsstatus
- Alerts
- Benachrichtigungsdaten
- Anwendungsbenutzer
- Benutzerpräferenzen
- sichere Subscription-Tokens

Beim Docker-Quick-Start werden die Flyway-Migrationen automatisch ausgeführt.

---

# Testing

Das Projekt verwendet automatisierte Tests für Backend-Funktionalität sowie Testcontainers, wenn eine Datenbankintegration erforderlich ist.

Vor dem Pushen von Änderungen:

```bash
./mvnw test
```

Ein erfolgreicher Testlauf sollte vor dem Zusammenführen von Änderungen durchgeführt werden.

Zusätzlich können die zentralen MVP-Abläufe über die Weboberfläche und Swagger als End-to-End-Szenarien getestet werden.

---

# Continuous Integration

GitHub Actions wird für Continuous Integration verwendet.

Der CI-Workflow validiert das Projekt durch automatisierte Build- und Testschritte.

Das Repository enthält ausserdem eine Docker-Konfiguration für eine reproduzierbare lokale Ausführung.

---

# Projektumfang

Der aktuelle MVP priorisiert bewusst eine zuverlässige funktionierende Anwendung gegenüber experimenteller KI-Funktionalität.

## MVP

- OpenParlData-Integration
- strukturierte Speicherung parlamentarischer Daten
- automatische Datensynchronisierung
- regelbasierte WWF-Themenklassifikation
- Suche und Filterung
- Monitoring relevanter Geschäfte
- Monitoring relevanter Traktanden
- E-Mail-Abonnements
- E-Mail-Benachrichtigungen
- REST-API
- einfache Weboberfläche
- mehrsprachige Benutzeroberfläche

## Optional / zukünftige Erweiterungen

Mögliche spätere Erweiterungen sind:

- automatische Übersetzung parlamentarischer Quellinhalte
- KI-unterstützte Klassifikation
- dialogorientierter Zugriff auf parlamentarische Daten
- erweiterte Visualisierungen
- zusätzliche Benachrichtigungskanäle
- Microsoft-365-Integration
- weiterführende Relevanzbewertung

Diese Erweiterungen sind für den aktuellen Kern-MVP nicht erforderlich.

---

# Aktueller Projektstand

Das Projekt befindet sich in der MVP-Validierungs- und Testphase.

Abgeschlossene Kernbereiche:

- ✅ OpenParlData-Datenintegration
- ✅ Inkrementelle Synchronisierung
- ✅ Strukturierte PostgreSQL-Persistenz
- ✅ Dokumentimport
- ✅ Import von Sitzungen und Traktanden
- ✅ Regelbasierte WWF-Themenklassifikation
- ✅ PostgreSQL Full-Text Search
- ✅ REST-API für parlamentarische Geschäfte
- ✅ Monitoring relevanter Traktanden
- ✅ Sichere Themen-Abonnements
- ✅ E-Mail-Benachrichtigungen
- ✅ Einfache Weboberfläche
- ✅ Detailansicht parlamentarischer Geschäfte
- ✅ Mehrsprachige Benutzeroberfläche DE / FR / IT / EN
- ✅ Swagger / OpenAPI
- ✅ Docker-basierte lokale Infrastruktur
- ✅ Zero-Configuration Docker Quick Start
- ✅ Automatischer Bootstrap eines kleinen Startdatenbestands
- ✅ Mailpit-basierte lokale E-Mail-Tests
- ✅ CI-Pipeline

Aktueller Fokus:

- End-to-End-Tests des MVP anhand konkreter Anwendungsfälle
- Validierung der Klassifikationsergebnisse
- technische Dokumentation
- Vorbereitung des Stakeholder-Feedbacks

---

# Experimentelle Funktionen

Im Repository kann experimenteller Code für dialogorientierten Zugriff beziehungsweise lokale LLM-Integration vorhanden sein.

Diese Funktionalität ist aktuell **nicht Bestandteil des erforderlichen MVP** und wird für den Betrieb der Hauptanwendung nicht benötigt.

Der funktionierende MVP ist weder von einem lokalen LLM noch von einem externen KI-Anbieter abhängig.

---

# Datenquelle und Attribution

Die parlamentarischen Daten werden bereitgestellt von:

**OpenParlData.ch**

Die Daten von OpenParlData werden gemäss den jeweils geltenden Lizenz- und Attributionsbedingungen verwendet.

**Source: OpenParlData.ch**