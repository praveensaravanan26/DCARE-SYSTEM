package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_validations")
public class DocumentValidation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID claimId;

    @Column(nullable = false)
    private String ruleName;

    @Column(nullable = false)
    private String ruleCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationSeverity severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String detectedValue;

    @Column(columnDefinition = "TEXT")
    private String expectedValue;

    private String sourceDocumentType;
    private String targetDocumentType;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public DocumentValidation() {}

    public DocumentValidation(UUID id, UUID claimId, String ruleName, String ruleCategory, ValidationStatus status, ValidationSeverity severity, String description, String detectedValue, String expectedValue, String sourceDocumentType, String targetDocumentType, ZonedDateTime createdAt) {
        this.id = id;
        this.claimId = claimId;
        this.ruleName = ruleName;
        this.ruleCategory = ruleCategory;
        this.status = status;
        this.severity = severity;
        this.description = description;
        this.detectedValue = detectedValue;
        this.expectedValue = expectedValue;
        this.sourceDocumentType = sourceDocumentType;
        this.targetDocumentType = targetDocumentType;
        this.createdAt = createdAt;
    }

    public static DocumentValidationBuilder builder() { return new DocumentValidationBuilder(); }

    public static class DocumentValidationBuilder {
        private UUID id;
        private UUID claimId;
        private String ruleName;
        private String ruleCategory;
        private ValidationStatus status;
        private ValidationSeverity severity;
        private String description;
        private String detectedValue;
        private String expectedValue;
        private String sourceDocumentType;
        private String targetDocumentType;
        private ZonedDateTime createdAt;

        public DocumentValidationBuilder id(UUID id) { this.id = id; return this; }
        public DocumentValidationBuilder claimId(UUID claimId) { this.claimId = claimId; return this; }
        public DocumentValidationBuilder ruleName(String ruleName) { this.ruleName = ruleName; return this; }
        public DocumentValidationBuilder ruleCategory(String ruleCategory) { this.ruleCategory = ruleCategory; return this; }
        public DocumentValidationBuilder status(ValidationStatus status) { this.status = status; return this; }
        public DocumentValidationBuilder severity(ValidationSeverity severity) { this.severity = severity; return this; }
        public DocumentValidationBuilder description(String description) { this.description = description; return this; }
        public DocumentValidationBuilder detectedValue(String detectedValue) { this.detectedValue = detectedValue; return this; }
        public DocumentValidationBuilder expectedValue(String expectedValue) { this.expectedValue = expectedValue; return this; }
        public DocumentValidationBuilder sourceDocumentType(String sourceDocumentType) { this.sourceDocumentType = sourceDocumentType; return this; }
        public DocumentValidationBuilder targetDocumentType(String targetDocumentType) { this.targetDocumentType = targetDocumentType; return this; }
        public DocumentValidationBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DocumentValidation build() {
            return new DocumentValidation(id, claimId, ruleName, ruleCategory, status, severity, description, detectedValue, expectedValue, sourceDocumentType, targetDocumentType, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClaimId() { return claimId; }
    public void setClaimId(UUID claimId) { this.claimId = claimId; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getRuleCategory() { return ruleCategory; }
    public void setRuleCategory(String ruleCategory) { this.ruleCategory = ruleCategory; }
    public ValidationStatus getStatus() { return status; }
    public void setStatus(ValidationStatus status) { this.status = status; }
    public ValidationSeverity getSeverity() { return severity; }
    public void setSeverity(ValidationSeverity severity) { this.severity = severity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDetectedValue() { return detectedValue; }
    public void setDetectedValue(String detectedValue) { this.detectedValue = detectedValue; }
    public String getExpectedValue() { return expectedValue; }
    public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
    public String getSourceDocumentType() { return sourceDocumentType; }
    public void setSourceDocumentType(String sourceDocumentType) { this.sourceDocumentType = sourceDocumentType; }
    public String getTargetDocumentType() { return targetDocumentType; }
    public void setTargetDocumentType(String targetDocumentType) { this.targetDocumentType = targetDocumentType; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
