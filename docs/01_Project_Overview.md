# Chapter 1: Project Overview & Executive Summary

## 1.1 Project Title
**AI-Powered Automated Insurance Claim Processing and Fraud Detection Using Document Intelligence and Explainable Risk Assessment**

**System Name:** `DCARE` (Document-Centric Automated Risk Evaluation Platform)

---

## 1.2 Problem Statement
Motor and general insurance claims processing faces severe operational bottlenecks and financial loss due to fraudulent and inflated submissions. The insurance sector loses an estimated **$308.6 Billion annually** to fraudulent claims. Traditional claims management relies on manual scrutiny of unstructured documents (police accident reports, repair workshop invoices, claim forms, driver licenses), which causes:
1. **Prolonged Processing Latency:** Legitimate claims require 7 to 21 days for manual verification, damaging customer satisfaction.
2. **High Fraud Leakage:** Subtle fraud patterns (cross-document date/amount inconsistencies, duplicate billing across claims, altered invoice numbers, and high velocity filings) frequently evade manual inspection.
3. **Black-Box AI Skepticism:** Conventional black-box ML models predict risk probabilities without explaining *why* a claim was flagged, creating compliance risks and preventing claim handlers from trusting AI decisions.
4. **Lack of Evidence Provenance:** Automated systems often fail to trace discrepancies back to the source document, page, or entity.

---

## 1.3 The DCARE Solution
DCARE is an enterprise-grade, document-centric claims intelligence platform designed as an **augmented decision-support system** for insurance claim officers and Special Investigation Units (SIU). 

### Core Pillars of DCARE:
- **Intelligent Document Ingestion & OCR:** Extracts structured entities (VIN, dates, amounts, garage names, invoice IDs) from scanned PDFs and images using native vector extraction and Tesseract OCR with regex boundary normalization.
- **Cross-Document Consistency Engine:** Validates data alignment across multiple submitted documents (Claim Form vs. Police FIR vs. Repair Invoice) detecting date discrepancies, registration variances, amount inflation, and duplicate invoice reutilization.
- **Hybrid Machine Learning & Anomaly Scoring:** Synthesizes supervised classification (XGBoost, Random Forest) with unsupervised multi-variate anomaly detection (Isolation Forest) and empirical risk penalties into a composite 0–100 risk score.
- **Explainable AI (XAI) via TreeSHAP:** Provides localized SHAP (SHapley Additive exPlanations) values quantifying the exact contribution of each feature towards elevating or decreasing the claim's risk score.
- **Human-in-the-Loop Decision Support:** Strictly enforces human authority—the AI assigns risk tiers (Low, Medium, High, Critical) and decision hints ("Straight-Through Processing candidate", "Discrepancy Detected - Verification Required"), while authorized claim officers make the binding decision.
- **Immutable Enterprise Audit Trail:** Records all security, OCR, evaluation, and review events with reviewer IDs, timestamps, and previous/new status snapshots.

---

## 1.4 System Users & Stakeholders
| User Role | Responsibilities & Capabilities |
| :--- | :--- |
| **Policyholder / Customer** | Submits first notice of loss (FNOL), uploads supporting PDFs/images, tracks claim processing status and settlements. |
| **Claim Officer** | Reviews incoming claims, verifies OCR field extractions, evaluates consistency matrix, and issues approvals for low-risk claims. |
| **Fraud Investigator (SIU)** | Investigates escalated high-risk claims, reviews SHAP attributions, checks vehicle chassis/garage histories, and logs investigation milestones. |
| **System Administrator** | Manages user accounts, enforces RBAC policies, reviews system audit logs, and monitors ML microservice performance. |

---

## 1.5 Technology Stack Overview
- **Frontend:** React 18, Vite, Lucide Icons, Chart.js, Tailwind-inspired Enterprise Glassmorphism Design System.
- **Backend API:** Java 21, Spring Boot 3.3.4, Spring Security, JWT (JJWT 0.12.5), Spring Data JPA, PostgreSQL 16, Redis 8.
- **AI & Analytics Service:** Python 3.11, FastAPI, XGBoost, Scikit-Learn, SHAP, pdfplumber, pytesseract, ReportLab, Pillow, Pandas, NumPy.
- **Database & Cache:** PostgreSQL 16 (11 Relational Tables, 10 Indexes), Redis (Session Cache).
