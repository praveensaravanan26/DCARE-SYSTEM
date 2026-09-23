package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "extracted_fields")
public class ExtractedField {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID claimId;

    private UUID documentId;

    @Column(nullable = false)
    private String fieldName;

    @Column(columnDefinition = "TEXT")
    private String rawValue;

    @Column(columnDefinition = "TEXT")
    private String normalizedValue;

    private BigDecimal confidenceScore = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProvenanceType provenance = ProvenanceType.OCR;

    private Boolean isVerified = false;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public ExtractedField() {}

    public ExtractedField(UUID id, UUID claimId, UUID documentId, String fieldName, String rawValue, String normalizedValue, BigDecimal confidenceScore, ProvenanceType provenance, Boolean isVerified, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.claimId = claimId;
        this.documentId = documentId;
        this.fieldName = fieldName;
        this.rawValue = rawValue;
        this.normalizedValue = normalizedValue;
        this.confidenceScore = confidenceScore != null ? confidenceScore : BigDecimal.ONE;
        this.provenance = provenance != null ? provenance : ProvenanceType.OCR;
        this.isVerified = isVerified != null ? isVerified : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ExtractedFieldBuilder builder() { return new ExtractedFieldBuilder(); }

    public static class ExtractedFieldBuilder {
        private UUID id;
        private UUID claimId;
        private UUID documentId;
        private String fieldName;
        private String rawValue;
        private String normalizedValue;
        private BigDecimal confidenceScore = BigDecimal.ONE;
        private ProvenanceType provenance = ProvenanceType.OCR;
        private Boolean isVerified = false;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public ExtractedFieldBuilder id(UUID id) { this.id = id; return this; }
        public ExtractedFieldBuilder claimId(UUID claimId) { this.claimId = claimId; return this; }
        public ExtractedFieldBuilder documentId(UUID documentId) { this.documentId = documentId; return this; }
        public ExtractedFieldBuilder fieldName(String fieldName) { this.fieldName = fieldName; return this; }
        public ExtractedFieldBuilder rawValue(String rawValue) { this.rawValue = rawValue; return this; }
        public ExtractedFieldBuilder normalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; return this; }
        public ExtractedFieldBuilder confidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public ExtractedFieldBuilder provenance(ProvenanceType provenance) { this.provenance = provenance; return this; }
        public ExtractedFieldBuilder isVerified(Boolean isVerified) { this.isVerified = isVerified; return this; }
        public ExtractedFieldBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ExtractedFieldBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ExtractedField build() {
            return new ExtractedField(id, claimId, documentId, fieldName, rawValue, normalizedValue, confidenceScore, provenance, isVerified, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClaimId() { return claimId; }
    public void setClaimId(UUID claimId) { this.claimId = claimId; }
    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getRawValue() { return rawValue; }
    public void setRawValue(String rawValue) { this.rawValue = rawValue; }
    public String getNormalizedValue() { return normalizedValue; }
    public void setNormalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }
    public ProvenanceType getProvenance() { return provenance; }
    public void setProvenance(ProvenanceType provenance) { this.provenance = provenance; }
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
