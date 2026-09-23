# DCARE — AI-Powered Automated Insurance Claim Processing & Fraud Detection

> **Document-Centric Automated Risk Evaluation Platform**  
> *Final Year Academic & Enterprise Capstone Project*  
> *Author: Praveen Saravanan*

[![FastAPI](https://img.shields.io/badge/FastAPI-0.110.0-009688.svg?logo=fastapi)](https://fastapi.tiangolo.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3.1-61DAFB.svg?logo=react)](https://reactjs.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1.svg?logo=postgresql)](https://www.postgresql.org)
[![XGBoost](https://img.shields.io/badge/XGBoost-2.0.3-EB7C28.svg)](https://xgboost.readthedocs.io)
[![SHAP](https://img.shields.io/badge/SHAP-TreeExplainer-FF6F00.svg)](https://shap.readthedocs.io)

---

## 📌 Executive Summary
**DCARE** is an end-to-end, enterprise-grade insurance claim intelligence platform that automates document ingestion, entity extraction, cross-document consistency auditing, and multi-model fraud risk evaluation. Designed under the **augmented decision-support paradigm**, DCARE accelerates legitimate claims through Straight-Through Processing (STP) while flagging anomalies and cross-document inconsistencies for Claim Officers and Special Investigation Units (SIU).

---

## 🏛️ System Architecture

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

## 🚀 Quick Start (Running Locally)

### 1. Prerequisites
- **Java:** JDK 21+ (`java -version`)
- **Maven:** 3.9+ (`mvn -version`)
- **Python:** Python 3.11 (`python3 --version`)
- **Node.js:** Node 18+ and npm (`node -v`)
- **Database:** PostgreSQL 16 on port 5432 with `dcare_db` created.
- **Cache:** Redis on port 6379.

### 2. One-Click Launch Script
```bash
cd /Users/praveensaravanan/Desktop/DCARE_Final_Year_Project
./run-project.sh
```

### 3. Individual Service Startup Commands
```bash
# Terminal 1: Python AI Microservice (Port 8000)
cd ai-service
export DYLD_FALLBACK_LIBRARY_PATH=/opt/homebrew/opt/libomp/lib
./venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8000

# Terminal 2: Spring Boot Backend (Port 8080)
cd backend
mvn spring-boot:run

# Terminal 3: React Frontend (Port 5173)
cd frontend
npm run dev -- --host 0.0.0.0 --port 5173
```

---

## 🔑 Demo Personas & Credentials
The login page features **1-click login buttons** for all pre-seeded personas:

| Role | Email | Password | Primary Use Case |
| :--- | :--- | :--- | :--- |
| **Claim Officer** | `officer@dcare.local` | `Officer@123` | Document validation, STP approvals, triage. |
| **Fraud Investigator** | `investigator@dcare.local` | `Investigator@123` | SIU investigations, deep SHAP feature analysis. |
| **Customer / Policyholder** | `customer@dcare.local` | `Customer@123` | First notice of loss (FNOL), upload documents. |
| **Administrator** | `admin@dcare.local` | `Admin@123` | User accounts, system security, compliance audit. |

---

## 🧪 4 Pre-Configured Benchmark Scenarios

| Scenario | Objective | Ground Truth | Expected System Response |
| :--- | :--- | :--- | :--- |
| **Scenario 1: Clean Claim** | Verify Straight-Through-Processing | Consistent dates & amounts across Claim Form, FIR, and Invoice. | **LOW Risk (12.3/100)**, Green Gauge, STP Candidate Hint, 1-Click Officer Approval. |
| **Scenario 2: Document Discrepancy** | Detect cross-document fraud | Date Mismatch (Aug 12 vs Aug 14) + Amount Variance (\$42,000 vs \$49,500). | **HIGH Risk (62.0/100)**, Red Consistency Flags, Auto-Escalation to SIU. |
| **Scenario 3: Repeated Velocity** | Detect statistical outlier & frequency | 3 claims in 90 days + \$128,000 outlier amount (+7.55 Z-Score). | **HIGH Risk**, TreeSHAP isolates amount deviation (+6.71) and Z-Score (+1.41). |
| **Scenario 4: Duplicate Invoice** | Detect document recycling | Reused invoice serial number (`INV-2026-891`) previously billed. | **EVIDENCE ALERT**, Duplicate invoice penalty applied, fraud evidence flagged. |

---

## 📊 Machine Learning Model Evaluation
Trained and validated on 6,000 synthetic insurance claim records (`datasets/controlled_synthetic_insurance_claims.csv`):

| Model Architecture | Accuracy | Precision | Recall | F1-Score | ROC-AUC |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **XGBoost Classifier (Primary)** | **94.2%** | **91.5%** | **89.7%** | **0.906** | **0.968** |
| **Random Forest Ensemble** | 93.1% | 89.4% | 88.0% | 0.887 | 0.954 |
| **Isolation Forest (Unsupervised)** | 88.5% | — | — | — | 0.892 |

---

## 📁 Repository Structure
```
DCARE_Final_Year_Project/
├── ai-service/              # Python 3.11 + FastAPI microservice
│   ├── app/                 # FastAPI routes (OCR, Extract, Validate, Assess)
│   ├── preprocessing/       # Document processor (OCR) & Consistency engine
│   ├── explainability/      # TreeSHAP explainer engine
│   ├── inference/           # Multi-model risk scoring engine
│   └── training/            # Synthetic data generation & model training
├── backend/                 # Java 21 / Spring Boot 3.3.4 enterprise core
│   ├── src/main/java/       # 64 Java POJO models, controllers, services, repositories
│   └── src/main/resources/  # application.properties configuration
├── frontend/                # React 18 + Vite SPA
│   ├── src/pages/           # Dashboard, Claims, RiskHub, Investigations, Analytics, Audit
│   └── src/components/      # RiskGauge, ConsistencyMatrix, ShapVisualizer, EvidencePanel
├── database/migrations/     # PostgreSQL 16 schema creation & index DDL
├── datasets/                # 6,000 synthetic records & 8 vector PDF test documents
├── models/                  # Serialized ML models (joblib), scalers, and metric JSONs
├── docs/                    # 12 comprehensive documentation chapters
├── research-paper/          # Formal IEEE research paper draft
└── scripts/                 # E2E test verification & document generation scripts
```

---

## 📜 Academic Research Paper
A complete IEEE-compliant research paper is provided at:  
`research-paper/IEEE_DCARE_Research_Paper.md`  
*(Includes Abstract, Related Work, Mathematical Formulations, Experimental Evaluations, and IEEE References).*
