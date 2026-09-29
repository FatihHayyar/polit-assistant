# Requirements – WWF Polit-Assistant

## 1. Purpose

The WWF Polit-Assistant supports WWF Switzerland in monitoring parliamentary developments related to selected environmental topics.

The system processes parliamentary data from OpenParlData and makes relevant political affairs available for topic-based search, monitoring and notifications.

This document defines the functional and non-functional requirements of the MVP based on the agreed project scope.
---

## 2. Functional Requirements

### FR-01 – OpenParlData Integration

The system shall retrieve relevant parliamentary data from the OpenParlData API.

### FR-02 – Data Update

The system shall regularly check OpenParlData for new or updated parliamentary data.

### FR-03 – Data Storage

The system shall store and link the parliamentary data required for monitoring.

### FR-04 – Topic Classification

The system shall automatically classify parliamentary affairs into defined WWF environmental topics using rule-based classification.

Relevant affair data and, where appropriate, associated texts and documents shall be considered for classification.
### FR-05 – Search

Users shall be able to search parliamentary affairs.

### FR-06 – Filtering

Users shall be able to filter parliamentary affairs by relevant criteria, including WWF environmental topics.

### FR-07 – New Affairs Monitoring

The system shall detect new parliamentary affairs that match relevant WWF environmental topics.

### FR-08 – Session Agenda Monitoring

The system shall identify relevant parliamentary affairs appearing on parliamentary session agendas.
### FR-09 – Topic Subscription

Users shall be able to subscribe to selected WWF environmental topics by providing an e-mail address.

### FR-10 – E-Mail Notifications

The system shall notify subscribed users by e-mail when relevant new developments are detected for their selected topics.
### FR-11 – REST API

The core functionality of the Polit-Assistant shall be accessible through a documented REST API.

### FR-12 – User Interface

The core functionality shall be accessible through a simple and usable user interface.

The concrete implementation of the user interface will be determined during MVP development.

---

## 3. Future / Optional Requirements

### FUT-01 – Dialog-Oriented Queries

The Polit-Assistant may provide a dialog-oriented interface for querying parliamentary information.

### FUT-02 – AI Integration

The architecture should allow the later integration of AI-based functionality.

Possible applications include:

- natural-language queries
- summaries of parliamentary content
- understandable explanations of political affairs

### FUT-03 – Additional Visualizations

Additional dashboard and visualization functionality may be implemented depending on project progress and WWF feedback.
---

## 4. Non-Functional Requirements

### NFR-01 – Maintainability

The application shall have a modular structure that supports maintenance and further development.

### NFR-02 – Extensibility

The architecture shall support the addition of new functionality and external services without major changes to the core application.

### NFR-03 – Traceability

Relevant processing steps such as data import, classification and notification shall be technically traceable.

### NFR-04 – Usability

The user-facing functionality shall be simple and understandable enough to demonstrate and validate the MVP together with WWF.

### NFR-05 – Documentation

The architecture, setup, REST API and relevant technical decisions shall be documented sufficiently to support handover and further development.
