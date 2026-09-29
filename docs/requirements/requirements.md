# Anforderungen – WWF Polit-Assistant

## 1. Zweck

Der WWF Polit-Assistant unterstützt WWF Schweiz beim Monitoring parlamentarischer Entwicklungen in ausgewählten umweltpolitischen Themenbereichen.

Das System verarbeitet parlamentarische Daten aus OpenParlData und stellt relevante politische Geschäfte für die themenbasierte Suche, das Monitoring und Benachrichtigungen zur Verfügung.

Dieses Dokument definiert die funktionalen und nicht-funktionalen Anforderungen an den MVP auf Basis des vereinbarten Projektumfangs.

---

## 2. Funktionale Anforderungen

### FR-01 – OpenParlData-Integration

Das System soll relevante parlamentarische Daten über die OpenParlData API beziehen.

### FR-02 – Datenaktualisierung

Das System soll OpenParlData regelmässig auf neue oder aktualisierte parlamentarische Daten prüfen.

### FR-03 – Datenspeicherung

Das System soll die für das Monitoring benötigten parlamentarischen Daten strukturiert speichern und miteinander verknüpfen.

### FR-04 – Themenklassifikation

Das System soll parlamentarische Geschäfte automatisch und regelbasiert den definierten WWF-Themenbereichen zuordnen.

Für die Klassifikation sollen relevante Geschäftsdaten sowie, soweit geeignet, zugehörige Texte und Dokumente berücksichtigt werden.

### FR-05 – Suche

Nutzerinnen und Nutzer sollen parlamentarische Geschäfte durchsuchen können.

### FR-06 – Filterung

Nutzerinnen und Nutzer sollen parlamentarische Geschäfte nach relevanten Kriterien, insbesondere nach WWF-Themenbereichen, filtern können.

### FR-07 – Monitoring neuer Geschäfte

Das System soll neue parlamentarische Geschäfte erkennen, die relevanten WWF-Themenbereichen zugeordnet werden.

### FR-08 – Monitoring von Sessionstraktanden

Das System soll erkennen, wenn relevante parlamentarische Geschäfte auf den Traktanden parlamentarischer Sitzungen erscheinen.

### FR-09 – Themenabonnement

Nutzerinnen und Nutzer sollen durch Angabe einer E-Mail-Adresse ausgewählte WWF-Themenbereiche abonnieren können.

### FR-10 – E-Mail-Benachrichtigungen

Das System soll Abonnentinnen und Abonnenten per E-Mail über relevante neue Entwicklungen in den von ihnen ausgewählten Themenbereichen informieren.

### FR-11 – REST-API

Die Kernfunktionen des Polit-Assistants sollen über eine dokumentierte REST-API zugänglich sein.

### FR-12 – Benutzerschnittstelle

Die Kernfunktionen des Polit-Assistants sollen über eine einfache und verständliche Benutzerschnittstelle zugänglich sein.

Die konkrete Ausgestaltung der Benutzerschnittstelle wird im Verlauf der MVP-Entwicklung festgelegt.

---

## 3. Zukünftige / optionale Anforderungen

### FUT-01 – Dialogorientierte Abfragen

Der Polit-Assistant kann zukünftig um eine dialogorientierte Schnittstelle zur Abfrage parlamentarischer Informationen erweitert werden.

### FUT-02 – AI-Integration

Die Architektur soll eine spätere Integration AI-gestützter Funktionen ermöglichen.

Mögliche Anwendungsbereiche sind:

- natürlichsprachliche Abfragen
- automatische Zusammenfassungen parlamentarischer Inhalte
- verständliche Erklärungen politischer Geschäfte

### FUT-03 – Zusätzliche Visualisierungen

Abhängig vom Projektfortschritt und vom Feedback des WWF können zusätzliche Dashboard- und Visualisierungsfunktionen umgesetzt werden.

---

## 4. Nicht-funktionale Anforderungen

### NFR-01 – Wartbarkeit

Die Anwendung soll modular aufgebaut sein, sodass Wartung und Weiterentwicklung unterstützt werden.

### NFR-02 – Erweiterbarkeit

Die Architektur soll die Integration neuer Funktionen und externer Dienste ermöglichen, ohne dass dafür grundlegende Änderungen am Kernsystem erforderlich sind.

### NFR-03 – Nachvollziehbarkeit

Relevante Verarbeitungsschritte wie Datenimport, Klassifikation und Benachrichtigungen sollen technisch nachvollziehbar sein.

### NFR-04 – Benutzerfreundlichkeit

Die benutzerseitigen Funktionen sollen einfach und verständlich gestaltet sein, sodass der MVP gemeinsam mit WWF demonstriert und validiert werden kann.

### NFR-05 – Dokumentation

Architektur, Installation und Betrieb, REST-API sowie relevante technische Entscheidungen sollen ausreichend dokumentiert werden, um die Übergabe und spätere Weiterentwicklung der Lösung zu unterstützen.

---

## 5. Projektumfang

### Umfang des MVP

Der initiale MVP umfasst:

- Integration von OpenParlData und regelmässige Datenaktualisierung
- strukturierte Speicherung und Verknüpfung relevanter parlamentarischer Daten
- regelbasierte Themenklassifikation
- Suche und Filterung
- Monitoring neuer relevanter parlamentarischer Geschäfte
- Monitoring relevanter Sessionstraktanden
- Themenabonnements
- E-Mail-Benachrichtigungen
- dokumentierte REST-API
- einfache und verständliche Benutzerschnittstelle
- technische Dokumentation

### Ausserhalb des initialen MVP

Folgende Funktionen sind für den initialen MVP nicht verpflichtend:

- AI-basierte Klassifikation
- Integration eines Large Language Models (LLM)
- AI-generierte Zusammenfassungen und Erklärungen
- Microsoft-365-Integration
- SharePoint-Integration
- Microsoft-Teams-Integration
- Microsoft-Copilot-Integration

Diese Funktionen können abhängig vom Projektfortschritt, von der technischen Machbarkeit und vom Feedback des WWF als mögliche Erweiterungen evaluiert werden.
