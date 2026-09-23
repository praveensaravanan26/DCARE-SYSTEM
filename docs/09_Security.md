# Chapter 9: Security Architecture & Compliance

## 9.1 Authentication & RBAC Authorization
- **Stateless Bearer Tokens:** Generated via HMAC-SHA256 (JJWT 0.12.5) with cryptographic signing keys, subject UUIDs, email, roles, and 24-hour expiration timestamps.
- **Role-Based Access Control (RBAC):** Enforced via Spring Security `@PreAuthorize("hasAnyAuthority(...)")` annotations protecting every REST endpoint.
- **Password Security:** Multi-round BCrypt hashing (10 salt rounds) with zero plaintext credential persistence.

---

## 9.2 File Storage & Ingestion Security
- **MIME Type & Extension Whitelisting:** Strict rejection of executable and unverified binaries; only `application/pdf`, `image/png`, `image/jpeg` allowed.
- **Storage Sanitization:** Uploaded files stored outside web root with randomly generated UUID basenames to prevent path traversal attacks.
- **Cryptographic Hashing:** Every uploaded file is hashed using SHA-256 upon ingestion for duplicate detection and document integrity validation.

---

## 9.3 Immutable Compliance Audit Trail
Every state-modifying action (Logins, Claim Creations, Document Uploads, OCR Extractions, Risk Calculations, Review Decisions, Field Corrections) is recorded synchronously to the `audit_logs` table with:
- User ID and Email
- Action Type (`LOGIN`, `CLAIM_CREATED`, `DOCUMENT_UPLOADED`, `OCR_COMPLETED`, `RISK_CALCULATED`, `DECISION_MADE`, `FIELD_CORRECTED`)
- Target Entity Type and Entity UUID
- Detailed change description and client IP
