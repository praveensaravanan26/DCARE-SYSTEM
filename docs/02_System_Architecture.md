# Chapter 2: System Architecture & Technical Design

## 2.1 High-Level Architecture Overview
DCARE adopts a modular, loosely-coupled microservices architecture designed for enterprise scalability, fault tolerance, and security isolation:

```
+-----------------------------------------------------------------------------------+
|                           PRESENTATION LAYER (PORT 5173)                         |
|  React 18 SPA + Vite • Role-Based Navigation • Chart.js Analytics • Glassmorphism  |
+------------------------------------------+----------------------------------------+
                                           | HTTP / REST (JWT Auth)
                                           v
+-----------------------------------------------------------------------------------+
|                        ENTERPRISE BACKEND SERVICE (PORT 8080)                     |
|  Spring Boot 3.3.4 • Spring Security • JPA / Hibernate • JJWT • Redis Cache       |
|  Controllers: Auth, Claim, Document, Review, Analytics, Audit, Policy, Admin      |
+---------------------+---------------------------------------+---------------------+
                      | JPA / SQL                             | HTTP / JSON REST
                      v                                       v
+-------------------------------------+   +-----------------------------------------+
|     DATABASE & CACHE LAYER          |   |       AI & OCR MICROSERVICE (PORT 8000) |
| PostgreSQL 16 (Port 5432)           |   | Python 3.11 + FastAPI + Uvicorn         |
| 11 Tables, 10 FKs, B-Tree Indexes   |   | • pdfplumber & Tesseract OCR Pipeline   |
| Redis 8 (Port 6379) Token & Cache   |   | • Cross-Document Consistency Engine     |
| Disk Storage (/uploaded-documents)  |   | • XGBoost + Random Forest + IsoForest   |
+-------------------------------------+   | • TreeSHAP Local Explainer Engine       |
                                          +-----------------------------------------+
```

---

## 2.2 Component Specifications

### 1. Presentation Layer (`frontend/`)
- **Framework:** React 18 with Vite bundling.
- **State Management:** React Context (`AuthContext`) managing JWT persistence, user roles, and active session headers.
- **Visual Analytics:** Chart.js and `react-chartjs-2` for interactive risk tier donuts and status breakdown bar charts.
- **UI Components:** Reusable modular cards (`RiskGauge`, `ConsistencyMatrix`, `ShapVisualizer`, `EvidencePanel`, `StatusBadge`).

### 2. Backend Gateway & Business Layer (`backend/`)
- **Framework:** Java 21 / Spring Boot 3.3.4.
- **Security:** Stateless Spring Security filter chain with Bearer JWT tokens, role-based method security (`@PreAuthorize`), and BCrypt password encryption.
- **Data Persistence:** Spring Data JPA with Hibernate 6, HikariCP connection pooling, and optimistic locking.
- **Audit Interceptor:** Synchronous event logger writing to PostgreSQL `audit_logs` table for compliance tracking.

### 3. AI & Document Intelligence Service (`ai-service/`)
- **Framework:** Python 3.11 / FastAPI running on Uvicorn ASGI server.
- **Document Intelligence:** Multi-engine OCR with `pdfplumber` for vector PDFs, `pytesseract` for scanned raster images, and regex entity extractors.
- **Inference Pipeline:** Multi-model ensemble (Supervised XGBoost + Random Forest + Unsupervised Isolation Forest) calibrated into a composite 0–100 evidence-aware risk score.
- **Explainability:** TreeSHAP computing individual Shapley feature attributions and natural-language interpretations.
