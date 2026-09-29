# Anwendungsfälle – WWF Polit-Assistant

## 1. Zweck

Dieses Dokument beschreibt die zentralen Anwendungsfälle des WWF Polit-Assistants aus Sicht der Nutzerinnen und Nutzer.

Die Anwendungsfälle konkretisieren die funktionalen Anforderungen des MVP und bilden eine Grundlage für die Implementierung und spätere Validierung der Lösung.

## 2. Akteure

### Nutzerin / Nutzer

Eine Person, die den Polit-Assistant verwendet, um relevante parlamentarische Geschäfte zu suchen, zu filtern und zu beobachten.

### Abonnentin / Abonnent

Eine Nutzerin oder ein Nutzer, die bzw. der eine E-Mail-Adresse hinterlegt und ausgewählte WWF-Themen abonniert, um Benachrichtigungen über relevante neue Entwicklungen zu erhalten.

### OpenParlData

Externes System, das die parlamentarischen Daten für den Polit-Assistant bereitstellt.
---

## 3. Anwendungsfälle

### UC-01 – Parlamentarische Geschäfte suchen

**Ziel:**  
Nutzerinnen und Nutzer können gezielt nach parlamentarischen Geschäften suchen, um relevante politische Informationen schnell zu finden.

**Akteur:**  
Nutzerin / Nutzer

**Vorbedingung:**  
Parlamentarische Daten wurden aus OpenParlData importiert und stehen im Polit-Assistant zur Verfügung.

**Hauptablauf:**

1. Die Nutzerin oder der Nutzer öffnet die Suchfunktion.
2. Ein Suchbegriff wird eingegeben.
3. Das System durchsucht die verfügbaren parlamentarischen Geschäfte.
4. Das System zeigt die gefundenen Geschäfte als Ergebnisliste an.
5. Ein Geschäft kann ausgewählt werden, um die verfügbaren Informationen einzusehen.

**Ergebnis:**  
Passende parlamentarische Geschäfte werden gefunden und dargestellt.

**Zugehörige Anforderungen:**  
FR-05 – Suche

---

### UC-02 – Parlamentarische Geschäfte filtern

**Ziel:**  
Nutzerinnen und Nutzer können parlamentarische Geschäfte nach relevanten Kriterien und WWF-Themenbereichen filtern.

**Akteur:**  
Nutzerin / Nutzer

**Vorbedingung:**  
Parlamentarische Geschäfte wurden importiert und den definierten WWF-Themenbereichen zugeordnet.

**Hauptablauf:**

1. Die Nutzerin oder der Nutzer öffnet die Such- bzw. Übersichtsansicht.
2. Ein oder mehrere Filterkriterien werden ausgewählt.
3. Das System wendet die gewählten Filter auf die vorhandenen Geschäfte an.
4. Das System zeigt nur die passenden parlamentarischen Geschäfte an.
5. Die Filter können angepasst oder entfernt werden.

**Ergebnis:**  
Die angezeigten Geschäfte entsprechen den ausgewählten Filterkriterien.

**Zugehörige Anforderungen:**  
FR-04 – Themenklassifikation  
FR-06 – Filterung

---

### UC-03 – Neue relevante Geschäfte erkennen

**Ziel:**  
Neue parlamentarische Geschäfte sollen automatisch erkannt und relevanten WWF-Themenbereichen zugeordnet werden.

**Akteure:**  
OpenParlData, System

**Vorbedingung:**  
Die OpenParlData-Integration und die regelbasierte Themenklassifikation sind verfügbar.

**Hauptablauf:**

1. Das System prüft OpenParlData auf neue oder aktualisierte parlamentarische Daten.
2. Neue relevante Geschäfte werden importiert und gespeichert.
3. Das System analysiert die für die Klassifikation relevanten Daten.
4. Das Geschäft wird einem oder mehreren WWF-Themenbereichen zugeordnet.
5. Das klassifizierte Geschäft steht für Suche, Filterung und Monitoring zur Verfügung.

**Ergebnis:**  
Neue relevante parlamentarische Geschäfte werden automatisch erkannt, klassifiziert und im Polit-Assistant verfügbar gemacht.

**Zugehörige Anforderungen:**  
FR-01 – OpenParlData-Integration  
FR-02 – Datenaktualisierung  
FR-03 – Datenspeicherung  
FR-04 – Themenklassifikation  
FR-07 – Monitoring neuer Geschäfte

---

### UC-04 – Relevante Sessionstraktanden erkennen

**Ziel:**  
Das System erkennt, wenn ein für WWF relevantes parlamentarisches Geschäft auf den Traktanden einer parlamentarischen Sitzung erscheint.

**Akteure:**  
OpenParlData, System

**Vorbedingung:**  
Relevante parlamentarische Geschäfte wurden klassifiziert und die benötigten Sitzungs- und Traktandendaten stehen zur Verfügung.

**Hauptablauf:**

1. Das System aktualisiert die relevanten Sitzungs- und Traktandendaten aus OpenParlData.
2. Das System verknüpft die Traktanden mit den zugehörigen parlamentarischen Geschäften.
3. Das System prüft, ob ein Geschäft einem relevanten WWF-Themenbereich zugeordnet ist.
4. Relevante Sessionstraktanden werden für das Monitoring gekennzeichnet.

**Ergebnis:**  
Relevante Geschäfte auf parlamentarischen Traktanden können frühzeitig im Monitoring berücksichtigt werden.

**Zugehörige Anforderungen:**  
FR-01 – OpenParlData-Integration  
FR-02 – Datenaktualisierung  
FR-04 – Themenklassifikation  
FR-08 – Monitoring von Sessionstraktanden

---

### UC-05 – WWF-Themen abonnieren

**Ziel:**  
Nutzerinnen und Nutzer können ausgewählte WWF-Themen abonnieren, um über relevante neue Entwicklungen informiert zu werden.

**Akteur:**  
Nutzerin / Nutzer

**Vorbedingung:**  
Die Abonnementfunktion steht zur Verfügung.

**Hauptablauf:**

1. Die Nutzerin oder der Nutzer öffnet die Abonnementfunktion.
2. Eine E-Mail-Adresse wird angegeben.
3. Ein oder mehrere WWF-Themenbereiche werden ausgewählt.
4. Das Abonnement wird gespeichert.
5. Die ausgewählten Themen werden der angegebenen E-Mail-Adresse zugeordnet.

**Ergebnis:**  
Für die angegebene E-Mail-Adresse besteht ein themenbezogenes Abonnement.

**Zugehörige Anforderungen:**  
FR-09 – Themenabonnement

---

### UC-06 – E-Mail-Benachrichtigung erhalten

**Ziel:**  
Abonnentinnen und Abonnenten werden über neue relevante Entwicklungen in ihren ausgewählten Themenbereichen informiert.

**Akteur:**  
Abonnentin / Abonnent

**Vorbedingung:**  
Ein gültiges Themenabonnement ist vorhanden und das System hat eine relevante neue Entwicklung erkannt.

**Hauptablauf:**

1. Das System erkennt eine neue relevante Entwicklung.
2. Das System bestimmt den zugehörigen WWF-Themenbereich.
3. Das System ermittelt die für diesen Themenbereich vorhandenen Abonnements.
4. Das System erstellt eine Benachrichtigung mit den relevanten Informationen.
5. Die Benachrichtigung wird an die hinterlegte E-Mail-Adresse gesendet.

**Ergebnis:**  
Die Abonnentin oder der Abonnent wird proaktiv über eine relevante neue Entwicklung informiert.

**Zugehörige Anforderungen:**  
FR-07 – Monitoring neuer Geschäfte  
FR-08 – Monitoring von Sessionstraktanden  
FR-09 – Themenabonnement  
FR-10 – E-Mail-Benachrichtigungen

---

## 4. Zukünftige Anwendungsfälle

Die folgenden Anwendungsfälle gehören nicht zum verpflichtenden Umfang des initialen MVP und können in einer späteren Projektphase evaluiert werden.

### UC-FUT-01 – Parlamentarische Informationen dialogorientiert abfragen

**Ziel:**  
Nutzerinnen und Nutzer können parlamentarische Informationen über eine dialogorientierte Schnittstelle abfragen.

**Mögliche Erweiterungen:**

- natürlichsprachliche Fragen
- thematische Abfragen
- Verknüpfung mit vorhandenen Suchfunktionen

**Zugehörige Anforderung:**  
FUT-01 – Dialogorientierte Abfragen

---

### UC-FUT-02 – Politische Inhalte mit AI aufbereiten

**Ziel:**  
AI-gestützte Funktionen können parlamentarische Inhalte für Nutzerinnen und Nutzer verständlicher aufbereiten.

**Mögliche Erweiterungen:**

- automatische Zusammenfassungen
- verständliche Erklärungen politischer Geschäfte
- natürlichsprachliche Abfragen
- kontextbezogene Antworten auf Basis der vorhandenen parlamentarischen Daten

**Zugehörige Anforderung:**  
FUT-02 – AI-Integration
