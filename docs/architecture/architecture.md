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

## 5. Zentrale Komponenten

Die detaillierte Beschreibung der einzelnen Komponenten wird im Verlauf der MVP-Entwicklung ergänzt und an den tatsächlich implementierten Stand angepasst.

## 6. Weiterentwicklung der Architektur

Die Architektur wird im Rahmen des iterativen Vorgehens überprüft und bei Bedarf auf Basis der technischen Erkenntnisse und des Feedbacks von WWF angepasst.

Insbesondere die Benutzerschnittstelle sowie mögliche spätere AI-gestützte Funktionen werden erst nach entsprechender Validierung konkretisiert.
