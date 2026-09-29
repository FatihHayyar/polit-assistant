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
