# Chapter 3: Database Design & Data Architecture

## 3.1 Relational Schema Architecture
The DCARE database (`dcare_db`) is hosted on PostgreSQL 16. The schema is organized into 11 interconnected relational tables ensuring 3rd Normal Form (3NF) compliance and referential integrity:

### Entity Relationship Structure
1. `users` — Authentication credentials, full names, roles (`ROLE_CUSTOMER`, `ROLE_CLAIM_OFFICER`, `ROLE_FRAUD_INVESTIGATOR`, `ROLE_ADMIN`), and active flags.
2. `policies` — Insurance contracts, coverage amounts, premium values, validity date windows, and assigned customer UUIDs.
3. `vehicles` — Insured vehicle records, VIN numbers, registration plates, make, model, and manufacturing year.
4. `claims` — Central claim records, claimed amounts, incident dates, incident coordinates/locations, repair workshops, invoice references, calculated risk scores, risk levels, and operational statuses.
5. `claim_documents` — Uploaded file metadata, storage paths, MIME types, file sizes, SHA-256 cryptographic hashes, OCR processing statuses, and confidence scores.
6. `extracted_fields` — Granular key-value entities extracted via OCR or manual officer corrections (field name, raw text, normalized value, provenance, confidence).
7. `document_validations` — Consistency matrix evaluations between documents (rule name, category, status `PASSED`/`FAILED`/`WARNING`, severity, description, detected value, expected value).
8. `claim_features` — Derived numerical and statistical features fed to ML models (past claim counts, 90-day frequency, Z-score, amount deviation, garage counts, anomaly score).
9. `risk_assessments` — Comprehensive assessment records storing overall score, sub-scores (supervised, anomaly, mismatch, duplicate, historical), recommendations, and full serialized SHAP attribution JSON.
10. `risk_evidence` — Individual atomic risk indicators and discrepancy records linked to an assessment with severity tiers and quantitative contributions.
11. `claim_reviews` — Human reviewer audit records documenting decisions (`APPROVE`, `REJECT`, `REQUEST_INFORMATION`, `SEND_TO_INVESTIGATION`), notes, and status transitions.
12. `audit_logs` — Immutable system-wide audit trail recording user IDs, IP addresses, action types, entity types, descriptions, and timestamps.

---

## 3.2 Key Indexes for High-Throughput Retrieval
- `idx_claims_customer` on `claims(customer_id)`
- `idx_claims_policy` on `claims(policy_number)`
- `idx_claims_status` on `claims(status)`
- `idx_claims_risk_level` on `claims(risk_level)`
- `idx_claim_documents_claim` on `claim_documents(claim_id)`
- `idx_claim_documents_hash` on `claim_documents(file_hash_sha256)`
- `idx_extracted_fields_claim` on `extracted_fields(claim_id)`
- `idx_validations_claim` on `document_validations(claim_id)`
- `idx_evidence_claim` on `risk_evidence(claim_id)`
- `idx_audit_entity` on `audit_logs(entity_type, entity_id)`
