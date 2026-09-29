# Technische Architektur – WWF Polit-Assistant

## 1. Zweck

Dieses Dokument beschreibt die technische Architektur des WWF Polit-Assistants und dokumentiert die für den MVP gewählten Technologien, zentralen Systemkomponenten und deren Zusammenspiel.

Die Architektur bildet die technische Grundlage für die iterative Entwicklung des MVP und soll so gestaltet sein, dass einzelne Komponenten während der weiteren Projektentwicklung angepasst oder erweitert werden können.

## 2. Architekturziele

Die technische Architektur verfolgt insbesondere folgende Ziele:

- modularer und nachvollziehbarer Aufbau
- klare Trennung zentraler Verantwortlichkeiten
- automatisierte Verarbeitung von OpenParlData-Daten
- Unterstützung von Suche, Klassifikation und Monitoring
- Bereitstellung der Funktionen über eine REST-API
- einfache Erweiterbarkeit der Benutzerschnittstelle
- Vorbereitung auf mögliche spätere AI-gestützte Funktionen

## 3. Technologie-Stack

Für den MVP wird folgende technische Grundlage verwendet:

- **Java 25** – Programmiersprache des Backends
- **Spring Boot 4.1** – Framework für die Backend-Anwendung
- **Maven** – Build- und Dependency-Management
- **PostgreSQL** – relationale Datenbank
- **Flyway** – Verwaltung der Datenbankmigrationen
- **Docker und Docker Compose** – lokale Ausführung und Bereitstellung der benötigten Dienste
- **REST** – Schnittstelle für die Funktionen des Polit-Assistants
- **OpenAPI / Swagger** – Dokumentation und Interaktion mit der REST-API
- **GitHub Actions** – Continuous Integration
- **OpenParlData API** – externe Quelle für parlamentarische Daten

Die konkrete Technologie für die Benutzerschnittstelle wird im Rahmen der iterativen MVP-Entwicklung festgelegt. Eine mögliche spätere AI-Integration wird ebenfalls technologisch noch nicht festgelegt.

## 4. Systemübersicht

Die Anwendung verarbeitet parlamentarische Daten aus OpenParlData und stellt diese für die weiteren Funktionen des Polit-Assistants bereit.

Der grundlegende Datenfluss ist:

OpenParlData → Datenimport und Aktualisierung → Persistenz → Themenklassifikation → Suche und Monitoring → REST-API → Benutzerschnittstelle / Benachrichtigungen

## 5. OpenParlData-Integration und relevantes Datenmodell

OpenParlData dient als zentrale externe Datenquelle des Polit-Assistants. Die Plattform stellt strukturierte parlamentarische Daten über eine REST-API zur Verfügung.

Für den MVP sind insbesondere folgende Datenbereiche relevant:

- **Affairs** – parlamentarische Geschäfte als zentrale fachliche Entität
- **Texts** – zugehörige parlamentarische Texte, die unter anderem für die Themenklassifikation berücksichtigt werden können
- **Docs** – zugehörige Dokumente als zusätzliche Informationsquelle
- **Meetings** – parlamentarische Sitzungen
- **Agendas** – Traktanden von Sitzungen und deren Verknüpfung mit parlamentarischen Geschäften
- **Events** – Ereignisse im Verlauf eines parlamentarischen Geschäfts
- **Bodies** – parlamentarische Organe und Gremien

Für das Monitoring bildet insbesondere die Beziehung zwischen Sitzungen, Traktanden und parlamentarischen Geschäften eine wichtige Grundlage:

`Meetings → Agendas → Affairs`

Für die inhaltliche Verarbeitung und Themenklassifikation können zusätzliche Informationen eines Geschäfts berücksichtigt werden:

`Affairs → Texts / Docs`

Der konkrete Umfang der importierten Daten wird während der MVP-Entwicklung anhand der benötigten Anwendungsfälle überprüft und bei Bedarf angepasst.

## 6. Zentrale Komponenten

Die detaillierte Beschreibung der einzelnen Komponenten wird im Verlauf der MVP-Entwicklung ergänzt und an den tatsächlich implementierten Stand angepasst.

## 7. Weiterentwicklung der Architektur

Die Architektur wird im Rahmen des iterativen Vorgehens überprüft und bei Bedarf auf Basis der technischen Erkenntnisse und des Feedbacks von WWF angepasst.

Insbesondere die Benutzerschnittstelle sowie mögliche spätere AI-gestützte Funktionen werden erst nach entsprechender Validierung konkretisiert.
