# Chapter 4: REST API Documentation

## 4.1 Authentication & User Management Endpoints
| HTTP Method | Endpoint | Access Level | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | Authenticates credentials and returns JWT Bearer token with user metadata. |
| `POST` | `/api/auth/register` | Public | Registers a new policyholder customer account. |
| `GET` | `/api/auth/me` | Authenticated | Retrieves current authenticated session user profile and permissions. |
| `GET` | `/api/admin/users` | Admin | Retrieves all registered system users with active statuses. |
| `PATCH` | `/api/admin/users/{id}/toggle-status` | Admin | Toggles user account enabled/disabled status. |

---

## 4.2 Claim Lifecycle Endpoints
| HTTP Method | Endpoint | Access Level | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/claims` | Customer / Officer | Submits a new claim (policy number, vehicle number, amount, incident details). |
| `GET` | `/api/claims` | Authenticated | Retrieves claims list (filtered by customer for policyholders; global for officers). |
| `GET` | `/api/claims/{id}` | Authenticated | Retrieves comprehensive claim record with vehicle and policy relations. |
| `POST` | `/api/claims/{id}/evaluate` | Officer / Investigator | Triggers full AI risk assessment, consistency checks, SHAP generation, and status update. |
| `GET` | `/api/claims/{id}/risk` | Authenticated | Retrieves risk assessment score, sub-scores, SHAP JSON, evidence, and validations. |
| `POST` | `/api/claims/{id}/review` | Officer / Investigator / Admin | Submits human review decision (`APPROVE`, `REJECT`, `REQUEST_INFORMATION`, `SEND_TO_INVESTIGATION`). |
| `GET` | `/api/claims/{id}/reviews` | Authenticated | Retrieves chronological review history and decision notes for a claim. |
| `GET` | `/api/policies` | Authenticated | Retrieves active policies for customer claim filing. |

---

## 4.3 Document Intelligence & OCR Endpoints
| HTTP Method | Endpoint | Access Level | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/claims/{id}/documents` | Customer / Officer | Multipart file upload (PDF/PNG/JPG) with automatic OCR and entity extraction. |
| `GET` | `/api/claims/{id}/documents` | Authenticated | Retrieves all uploaded supporting documents and OCR processing statuses. |
| `GET` | `/api/claims/{id}/extraction` | Authenticated | Retrieves structured extracted fields and provenance flags. |
| `POST` | `/api/documents/{id}/process` | Officer / Investigator | Re-triggers OCR extraction and parsing for a specific document. |
| `POST` | `/api/claims/{id}/fields/correct` | Officer / Investigator | Allows manual correction of an extracted field with audit provenance. |
| `GET` | `/api/documents/download/{id}` | Authenticated | Downloads stored document binary from disk storage. |

---

## 4.4 Analytics & Audit Trail Endpoints
| HTTP Method | Endpoint | Access Level | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/analytics/dashboard` | Officer / Investigator / Admin | Retrieves executive dashboard metrics, volume sums, risk distributions, and ML model scores. |
| `GET` | `/api/audit-logs` | Officer / Investigator / Admin | Retrieves immutable system audit logs with pagination and entity filters. |
