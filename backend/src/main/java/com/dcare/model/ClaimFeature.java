package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "claim_features")
public class ClaimFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID claimId;

    private Integer claimFrequency90d = 0;
    private BigDecimal claimAmountZscore = BigDecimal.ZERO;
    private Integer previousClaimCount = 0;
    private Integer garageRepeatCount = 0;
    private Integer daysSincePreviousClaim = 365;
    private BigDecimal amountDeviation = BigDecimal.ZERO;
    private Integer documentMismatchCount = 0;
    private Integer duplicateIndicator = 0;
    private Integer invoiceReuseIndicator = 0;
    private Integer policyAgeDays = 365;
    private Integer incidentToClaimDelayDays = 1;
    private BigDecimal anomalyScore = BigDecimal.ZERO;
    private BigDecimal supervisedProb = BigDecimal.ZERO;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public ClaimFeature() {}

    public ClaimFeature(UUID id, UUID claimId, Integer claimFrequency90d, BigDecimal claimAmountZscore, Integer previousClaimCount, Integer garageRepeatCount, Integer daysSincePreviousClaim, BigDecimal amountDeviation, Integer documentMismatchCount, Integer duplicateIndicator, Integer invoiceReuseIndicator, Integer policyAgeDays, Integer incidentToClaimDelayDays, BigDecimal anomalyScore, BigDecimal supervisedProb, ZonedDateTime createdAt) {
        this.id = id;
        this.claimId = claimId;
        this.claimFrequency90d = claimFrequency90d != null ? claimFrequency90d : 0;
        this.claimAmountZscore = claimAmountZscore != null ? claimAmountZscore : BigDecimal.ZERO;
        this.previousClaimCount = previousClaimCount != null ? previousClaimCount : 0;
        this.garageRepeatCount = garageRepeatCount != null ? garageRepeatCount : 0;
        this.daysSincePreviousClaim = daysSincePreviousClaim != null ? daysSincePreviousClaim : 365;
        this.amountDeviation = amountDeviation != null ? amountDeviation : BigDecimal.ZERO;
        this.documentMismatchCount = documentMismatchCount != null ? documentMismatchCount : 0;
        this.duplicateIndicator = duplicateIndicator != null ? duplicateIndicator : 0;
        this.invoiceReuseIndicator = invoiceReuseIndicator != null ? invoiceReuseIndicator : 0;
        this.policyAgeDays = policyAgeDays != null ? policyAgeDays : 365;
        this.incidentToClaimDelayDays = incidentToClaimDelayDays != null ? incidentToClaimDelayDays : 1;
        this.anomalyScore = anomalyScore != null ? anomalyScore : BigDecimal.ZERO;
        this.supervisedProb = supervisedProb != null ? supervisedProb : BigDecimal.ZERO;
        this.createdAt = createdAt;
    }

    public static ClaimFeatureBuilder builder() { return new ClaimFeatureBuilder(); }

    public static class ClaimFeatureBuilder {
        private UUID id;
        private UUID claimId;
        private Integer claimFrequency90d = 0;
        private BigDecimal claimAmountZscore = BigDecimal.ZERO;
        private Integer previousClaimCount = 0;
        private Integer garageRepeatCount = 0;
        private Integer daysSincePreviousClaim = 365;
        private BigDecimal amountDeviation = BigDecimal.ZERO;
        private Integer documentMismatchCount = 0;
        private Integer duplicateIndicator = 0;
        private Integer invoiceReuseIndicator = 0;
        private Integer policyAgeDays = 365;
        private Integer incidentToClaimDelayDays = 1;
        private BigDecimal anomalyScore = BigDecimal.ZERO;
        private BigDecimal supervisedProb = BigDecimal.ZERO;
        private ZonedDateTime createdAt;

        public ClaimFeatureBuilder id(UUID id) { this.id = id; return this; }
        public ClaimFeatureBuilder claimId(UUID claimId) { this.claimId = claimId; return this; }
        public ClaimFeatureBuilder claimFrequency90d(Integer claimFrequency90d) { this.claimFrequency90d = claimFrequency90d; return this; }
        public ClaimFeatureBuilder claimAmountZscore(BigDecimal claimAmountZscore) { this.claimAmountZscore = claimAmountZscore; return this; }
        public ClaimFeatureBuilder previousClaimCount(Integer previousClaimCount) { this.previousClaimCount = previousClaimCount; return this; }
        public ClaimFeatureBuilder garageRepeatCount(Integer garageRepeatCount) { this.garageRepeatCount = garageRepeatCount; return this; }
        public ClaimFeatureBuilder daysSincePreviousClaim(Integer daysSincePreviousClaim) { this.daysSincePreviousClaim = daysSincePreviousClaim; return this; }
        public ClaimFeatureBuilder amountDeviation(BigDecimal amountDeviation) { this.amountDeviation = amountDeviation; return this; }
        public ClaimFeatureBuilder documentMismatchCount(Integer documentMismatchCount) { this.documentMismatchCount = documentMismatchCount; return this; }
        public ClaimFeatureBuilder duplicateIndicator(Integer duplicateIndicator) { this.duplicateIndicator = duplicateIndicator; return this; }
        public ClaimFeatureBuilder invoiceReuseIndicator(Integer invoiceReuseIndicator) { this.invoiceReuseIndicator = invoiceReuseIndicator; return this; }
        public ClaimFeatureBuilder policyAgeDays(Integer policyAgeDays) { this.policyAgeDays = policyAgeDays; return this; }
        public ClaimFeatureBuilder incidentToClaimDelayDays(Integer incidentToClaimDelayDays) { this.incidentToClaimDelayDays = incidentToClaimDelayDays; return this; }
        public ClaimFeatureBuilder anomalyScore(BigDecimal anomalyScore) { this.anomalyScore = anomalyScore; return this; }
        public ClaimFeatureBuilder supervisedProb(BigDecimal supervisedProb) { this.supervisedProb = supervisedProb; return this; }
        public ClaimFeatureBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ClaimFeature build() {
            return new ClaimFeature(id, claimId, claimFrequency90d, claimAmountZscore, previousClaimCount, garageRepeatCount, daysSincePreviousClaim, amountDeviation, documentMismatchCount, duplicateIndicator, invoiceReuseIndicator, policyAgeDays, incidentToClaimDelayDays, anomalyScore, supervisedProb, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClaimId() { return claimId; }
    public void setClaimId(UUID claimId) { this.claimId = claimId; }
    public Integer getClaimFrequency90d() { return claimFrequency90d; }
    public void setClaimFrequency90d(Integer claimFrequency90d) { this.claimFrequency90d = claimFrequency90d; }
    public BigDecimal getClaimAmountZscore() { return claimAmountZscore; }
    public void setClaimAmountZscore(BigDecimal claimAmountZscore) { this.claimAmountZscore = claimAmountZscore; }
    public Integer getPreviousClaimCount() { return previousClaimCount; }
    public void setPreviousClaimCount(Integer previousClaimCount) { this.previousClaimCount = previousClaimCount; }
    public Integer getGarageRepeatCount() { return garageRepeatCount; }
    public void setGarageRepeatCount(Integer garageRepeatCount) { this.garageRepeatCount = garageRepeatCount; }
    public Integer getDaysSincePreviousClaim() { return daysSincePreviousClaim; }
    public void setDaysSincePreviousClaim(Integer daysSincePreviousClaim) { this.daysSincePreviousClaim = daysSincePreviousClaim; }
    public BigDecimal getAmountDeviation() { return amountDeviation; }
    public void setAmountDeviation(BigDecimal amountDeviation) { this.amountDeviation = amountDeviation; }
    public Integer getDocumentMismatchCount() { return documentMismatchCount; }
    public void setDocumentMismatchCount(Integer documentMismatchCount) { this.documentMismatchCount = documentMismatchCount; }
    public Integer getDuplicateIndicator() { return duplicateIndicator; }
    public void setDuplicateIndicator(Integer duplicateIndicator) { this.duplicateIndicator = duplicateIndicator; }
    public Integer getInvoiceReuseIndicator() { return invoiceReuseIndicator; }
    public void setInvoiceReuseIndicator(Integer invoiceReuseIndicator) { this.invoiceReuseIndicator = invoiceReuseIndicator; }
    public Integer getPolicyAgeDays() { return policyAgeDays; }
    public void setPolicyAgeDays(Integer policyAgeDays) { this.policyAgeDays = policyAgeDays; }
    public Integer getIncidentToClaimDelayDays() { return incidentToClaimDelayDays; }
    public void setIncidentToClaimDelayDays(Integer incidentToClaimDelayDays) { this.incidentToClaimDelayDays = incidentToClaimDelayDays; }
    public BigDecimal getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(BigDecimal anomalyScore) { this.anomalyScore = anomalyScore; }
    public BigDecimal getSupervisedProb() { return supervisedProb; }
    public void setSupervisedProb(BigDecimal supervisedProb) { this.supervisedProb = supervisedProb; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
