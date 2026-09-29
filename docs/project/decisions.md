# Technische und fachliche Entscheidungen – WWF Polit-Assistant

## 1. Zweck

Dieses Dokument hält zentrale fachliche und technische Entscheidungen des Projekts fest.

Entscheidungen können im Verlauf der iterativen Entwicklung auf Basis neuer technischer Erkenntnisse oder des Feedbacks von WWF angepasst werden. Änderungen werden nachvollziehbar dokumentiert, anstatt frühere Entscheidungen stillschweigend zu ersetzen.

---

## D-01 – OpenParlData als zentrale parlamentarische Datenquelle

**Entscheidung:**  
OpenParlData wird als zentrale externe Datenquelle für parlamentarische Daten verwendet.

**Begründung:**  
OpenParlData stellt strukturierte und harmonisierte Daten aus Schweizer Parlamenten über eine API zur Verfügung und bildet damit die Grundlage für Import, Klassifikation, Suche und Monitoring.

---

## D-02 – Regelbasierte Themenklassifikation im ersten MVP

**Entscheidung:**  
Die automatische Zuordnung parlamentarischer Geschäfte zu WWF-Themenbereichen wird im ersten MVP regelbasiert umgesetzt.

**Begründung:**  
Eine regelbasierte Lösung ermöglicht einen nachvollziehbaren und früh testbaren Ansatz. Neben den Geschäftsdaten können soweit geeignet auch zugehörige Texte und Dokumente berücksichtigt werden.

Eine AI-basierte Klassifikation ist nicht Bestandteil des initialen MVP.

---

## D-03 – Microsoft 365 ausserhalb des initialen MVP

**Entscheidung:**  
Microsoft-365-Dienste wie SharePoint, Teams und Copilot sind nicht Bestandteil des initialen MVP.

**Begründung:**  
Der Fokus des ersten MVP liegt auf den Kernfunktionen des Polit-Assistants und einer technisch unabhängigen Lösung.

---

## D-04 – E-Mail-basierte Themenabonnements

**Entscheidung:**  
Nutzerinnen und Nutzer sollen WWF-Themen über eine E-Mail-Adresse abonnieren können.

**Begründung:**  
Dadurch kann der Polit-Assistant relevante neue politische Entwicklungen proaktiv an interessierte Nutzerinnen und Nutzer übermitteln.

---

## D-05 – Technologie der Benutzerschnittstelle bleibt zunächst offen

**Entscheidung:**  
Die konkrete Technologie und Ausgestaltung der Benutzerschnittstelle wird zu Projektbeginn nicht verbindlich festgelegt.

**Begründung:**  
Der erste MVP soll möglichst früh nutzbar und demonstrierbar sein. Die geeignete Form der Benutzerschnittstelle wird während der iterativen Entwicklung anhand der technischen Anforderungen und des WWF-Feedbacks bestimmt.

---

## D-06 – AI als mögliche spätere Erweiterung

**Entscheidung:**  
AI-gestützte Funktionen sind nicht verpflichtender Bestandteil des ersten MVP. Die Architektur soll eine spätere Integration jedoch ermöglichen.

**Begründung:**  
Zunächst soll ein funktionsfähiger Polit-Assistant mit Datenintegration, regelbasierter Klassifikation, Suche, Monitoring und Benachrichtigungen entstehen.

Nach der Validierung des ersten MVP kann gemeinsam mit WWF bewertet werden, ob AI-Funktionen einen zusätzlichen praktischen Mehrwert bieten.

Mögliche spätere Funktionen sind beispielsweise:

- natürlichsprachliche Abfragen
- automatische Zusammenfassungen
- verständliche Erklärungen parlamentarischer Inhalte

---

## D-07 – Iteratives Vorgehen mit frühem funktionsfähigem MVP

**Entscheidung:**  
Die Entwicklung erfolgt iterativ. Zunächst wird ein erster durchgängiger und demonstrierbarer MVP erstellt.

**Begründung:**  
Der MVP kann früh anhand konkreter Anwendungsfälle mit WWF validiert werden. Das erhaltene Feedback fliesst anschliessend in die Priorisierung und Weiterentwicklung der Lösung ein.

Der Funktionsumfang kann dadurch während des Projekts angepasst werden, ohne das übergeordnete Projektziel aus den Augen zu verlieren.
