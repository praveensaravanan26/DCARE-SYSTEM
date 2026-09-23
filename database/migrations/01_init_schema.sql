-- DCARE Database Schema Initialization
-- PostgreSQL 16+

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- ROLE_CUSTOMER, ROLE_CLAIM_OFFICER, ROLE_FRAUD_INVESTIGATOR, ROLE_ADMIN
    phone_number VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: policies
CREATE TABLE IF NOT EXISTS policies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    policy_number VARCHAR(100) UNIQUE NOT NULL,
    customer_id UUID REFERENCES users(id) ON DELETE SET NULL,
    vehicle_number VARCHAR(50) NOT NULL,
    policy_type VARCHAR(50) DEFAULT 'COMPREHENSIVE_AUTO',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    coverage_amount NUMERIC(14, 2) NOT NULL,
    premium_amount NUMERIC(10, 2) NOT NULL,
    policy_status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: vehicles
CREATE TABLE IF NOT EXISTS vehicles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vehicle_number VARCHAR(50) UNIQUE NOT NULL,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year_of_manufacture INT NOT NULL,
    registration_date DATE,
    chassis_number VARCHAR(100),
    engine_number VARCHAR(100),
    customer_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: claims
CREATE TABLE IF NOT EXISTS claims (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_number VARCHAR(100) UNIQUE NOT NULL,
    policy_number VARCHAR(100) NOT NULL,
    customer_id UUID REFERENCES users(id) ON DELETE CASCADE,
    claim_type VARCHAR(50) NOT NULL, -- ACCIDENT, THEFT, THIRD_PARTY, WINDSHIELD, NATURAL_DISASTER
    incident_date DATE NOT NULL,
    incident_location VARCHAR(255) NOT NULL,
    claimed_amount NUMERIC(14, 2) NOT NULL,
    approved_amount NUMERIC(14, 2),
    description TEXT,
    vehicle_number VARCHAR(50) NOT NULL,
    garage_name VARCHAR(255),
    invoice_number VARCHAR(100),
    status VARCHAR(50) DEFAULT 'DRAFT', -- DRAFT, SUBMITTED, DOCUMENT_PROCESSING, VALIDATION_REQUIRED, RISK_ASSESSED, ENHANCED_REVIEW, APPROVED, REJECTED, INVESTIGATION, CLOSED
    priority VARCHAR(50) DEFAULT 'NORMAL', -- LOW, NORMAL, HIGH, URGENT
    risk_score NUMERIC(5, 2) DEFAULT 0.0,
    risk_level VARCHAR(50) DEFAULT 'LOW', -- LOW, MEDIUM, HIGH, CRITICAL
    assigned_officer_id UUID REFERENCES users(id) ON DELETE SET NULL,
    assigned_investigator_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: claim_documents
CREATE TABLE IF NOT EXISTS claim_documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    document_type VARCHAR(50) NOT NULL, -- POLICY_DOCUMENT, CLAIM_FORM, ACCIDENT_REPORT, REPAIR_ESTIMATE, REPAIR_INVOICE, DRIVING_LICENSE, VEHICLE_RC, OTHER
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_hash_sha256 VARCHAR(64) NOT NULL,
    ocr_status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, FAILED
    ocr_raw_text TEXT,
    extraction_confidence NUMERIC(5, 2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: extracted_fields
CREATE TABLE IF NOT EXISTS extracted_fields (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    document_id UUID REFERENCES claim_documents(id) ON DELETE CASCADE,
    field_name VARCHAR(100) NOT NULL,
    raw_value TEXT,
    normalized_value TEXT,
    confidence_score NUMERIC(5, 2) DEFAULT 1.0,
    provenance VARCHAR(50) DEFAULT 'OCR', -- OCR, MANUAL_CORRECTION, SYSTEM_INFERENCE
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: document_validations
CREATE TABLE IF NOT EXISTS document_validations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    rule_name VARCHAR(100) NOT NULL,
    rule_category VARCHAR(100) NOT NULL, -- CROSS_DOCUMENT_CONSISTENCY, DUPLICATE_DETECTION, POLICY_ELIGIBILITY, AMOUNT_TOLERANCE
    status VARCHAR(50) NOT NULL, -- PASSED, FAILED, WARNING
    severity VARCHAR(50) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    description TEXT NOT NULL,
    detected_value TEXT,
    expected_value TEXT,
    source_document_type VARCHAR(50),
    target_document_type VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: claim_features
CREATE TABLE IF NOT EXISTS claim_features (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID UNIQUE NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    claim_frequency_90d INT DEFAULT 0,
    claim_amount_zscore NUMERIC(8, 4) DEFAULT 0.0,
    previous_claim_count INT DEFAULT 0,
    garage_repeat_count INT DEFAULT 0,
    days_since_previous_claim INT DEFAULT 365,
    amount_deviation NUMERIC(14, 2) DEFAULT 0.0,
    document_mismatch_count INT DEFAULT 0,
    duplicate_indicator INT DEFAULT 0,
    invoice_reuse_indicator INT DEFAULT 0,
    policy_age_days INT DEFAULT 365,
    incident_to_claim_delay_days INT DEFAULT 1,
    anomaly_score NUMERIC(8, 4) DEFAULT 0.0,
    supervised_prob NUMERIC(8, 4) DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: risk_assessments
CREATE TABLE IF NOT EXISTS risk_assessments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID UNIQUE NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    overall_risk_score NUMERIC(5, 2) NOT NULL,
    risk_level VARCHAR(50) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    supervised_risk_score NUMERIC(5, 2) NOT NULL,
    anomaly_risk_score NUMERIC(5, 2) NOT NULL,
    document_mismatch_score NUMERIC(5, 2) NOT NULL,
    duplicate_risk_score NUMERIC(5, 2) NOT NULL,
    historical_risk_score NUMERIC(5, 2) NOT NULL,
    recommendation VARCHAR(100) NOT NULL,
    shap_summary_json JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: risk_evidence
CREATE TABLE IF NOT EXISTS risk_evidence (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    assessment_id UUID REFERENCES risk_assessments(id) ON DELETE CASCADE,
    evidence_type VARCHAR(100) NOT NULL, -- DOCUMENT_MISMATCH, DUPLICATE_DOCUMENT, DUPLICATE_INVOICE, HIGH_AMOUNT_DEVIATION, HIGH_CLAIM_FREQUENCY, ANOMALY_SIGNAL, MODEL_SIGNAL
    description TEXT NOT NULL,
    severity VARCHAR(50) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    source_document VARCHAR(100),
    source_field VARCHAR(100),
    detected_value TEXT,
    expected_value TEXT,
    contribution_score NUMERIC(5, 2) DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: claim_reviews
CREATE TABLE IF NOT EXISTS claim_reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    reviewer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reviewer_name VARCHAR(255) NOT NULL,
    reviewer_role VARCHAR(50) NOT NULL,
    decision VARCHAR(50) NOT NULL, -- APPROVE, REJECT, REQUEST_INFORMATION, SEND_TO_INVESTIGATION
    notes TEXT NOT NULL,
    previous_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: audit_logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID,
    user_email VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL, -- LOGIN, CLAIM_CREATED, DOCUMENT_UPLOADED, OCR_COMPLETED, FIELD_CORRECTED, VALIDATION_COMPLETED, RISK_CALCULATED, REVIEW_STARTED, DECISION_MADE
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    ip_address VARCHAR(50) DEFAULT '127.0.0.1',
    metadata_json JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_claims_customer ON claims(customer_id);
CREATE INDEX IF NOT EXISTS idx_claims_status ON claims(status);
CREATE INDEX IF NOT EXISTS idx_claims_risk_level ON claims(risk_level);
CREATE INDEX IF NOT EXISTS idx_claim_docs_claim ON claim_documents(claim_id);
CREATE INDEX IF NOT EXISTS idx_claim_docs_hash ON claim_documents(file_hash_sha256);
CREATE INDEX IF NOT EXISTS idx_extracted_claim ON extracted_fields(claim_id);
CREATE INDEX IF NOT EXISTS idx_validations_claim ON document_validations(claim_id);
CREATE INDEX IF NOT EXISTS idx_evidence_claim ON risk_evidence(claim_id);
CREATE INDEX IF NOT EXISTS idx_reviews_claim ON claim_reviews(claim_id);
CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_logs(created_at DESC);
