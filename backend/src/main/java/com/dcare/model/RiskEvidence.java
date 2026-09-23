package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "risk_evidence")
public class RiskEvidence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID claimId;

    private UUID assessmentId;

    @Column(nullable = false)
    private String evidenceType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationSeverity severity;

    private String sourceDocument;
    private String sourceField;

    @Column(columnDefinition = "TEXT")
    private String detectedValue;

    @Column(columnDefinition = "TEXT")
    private String expectedValue;

    private BigDecimal contributionScore = BigDecimal.ZERO;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public RiskEvidence() {}

    public RiskEvidence(UUID id, UUID claimId, UUID assessmentId, String evidenceType, String description, ValidationSeverity severity, String sourceDocument, String sourceField, String detectedValue, String expectedValue, BigDecimal contributionScore, ZonedDateTime createdAt) {
        this.id = id;
        this.claimId = claimId;
        this.assessmentId = assessmentId;
        this.evidenceType = evidenceType;
        this.description = description;
        this.severity = severity;
        this.sourceDocument = sourceDocument;
        this.sourceField = sourceField;
        this.detectedValue = detectedValue;
        this.expectedValue = expectedValue;
        this.contributionScore = contributionScore != null ? contributionScore : BigDecimal.ZERO;
        this.createdAt = createdAt;
    }

    public static RiskEvidenceBuilder builder() { return new RiskEvidenceBuilder(); }

    public static class RiskEvidenceBuilder {
        private UUID id;
        private UUID claimId;
        private UUID assessmentId;
        private String evidenceType;
        private String description;
        private ValidationSeverity severity;
        private String sourceDocument;
        private String sourceField;
        private String detectedValue;
        private String expectedValue;
        private BigDecimal contributionScore = BigDecimal.ZERO;
        private ZonedDateTime createdAt;

        public RiskEvidenceBuilder id(UUID id) { this.id = id; return this; }
        public RiskEvidenceBuilder claimId(UUID claimId) { this.claimId = claimId; return this; }
        public RiskEvidenceBuilder assessmentId(UUID assessmentId) { this.assessmentId = assessmentId; return this; }
        public RiskEvidenceBuilder evidenceType(String evidenceType) { this.evidenceType = evidenceType; return this; }
        public RiskEvidenceBuilder description(String description) { this.description = description; return this; }
        public RiskEvidenceBuilder severity(ValidationSeverity severity) { this.severity = severity; return this; }
        public RiskEvidenceBuilder sourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; return this; }
        public RiskEvidenceBuilder sourceField(String sourceField) { this.sourceField = sourceField; return this; }
        public RiskEvidenceBuilder detectedValue(String detectedValue) { this.detectedValue = detectedValue; return this; }
        public RiskEvidenceBuilder expectedValue(String expectedValue) { this.expectedValue = expectedValue; return this; }
        public RiskEvidenceBuilder contributionScore(BigDecimal contributionScore) { this.contributionScore = contributionScore; return this; }
        public RiskEvidenceBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public RiskEvidence build() {
            return new RiskEvidence(id, claimId, assessmentId, evidenceType, description, severity, sourceDocument, sourceField, detectedValue, expectedValue, contributionScore, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClaimId() { return claimId; }
    public void setClaimId(UUID claimId) { this.claimId = claimId; }
    public UUID getAssessmentId() { return assessmentId; }
    public void setAssessmentId(UUID assessmentId) { this.assessmentId = assessmentId; }
    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ValidationSeverity getSeverity() { return severity; }
    public void setSeverity(ValidationSeverity severity) { this.severity = severity; }
    public String getSourceDocument() { return sourceDocument; }
    public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }
    public String getSourceField() { return sourceField; }
    public void setSourceField(String sourceField) { this.sourceField = sourceField; }
    public String getDetectedValue() { return detectedValue; }
    public void setDetectedValue(String detectedValue) { this.detectedValue = detectedValue; }
    public String getExpectedValue() { return expectedValue; }
    public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
    public BigDecimal getContributionScore() { return contributionScore; }
    public void setContributionScore(BigDecimal contributionScore) { this.contributionScore = contributionScore; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
