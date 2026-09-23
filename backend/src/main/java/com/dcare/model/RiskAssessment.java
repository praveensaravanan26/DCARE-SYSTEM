package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID claimId;

    @Column(nullable = false)
    private BigDecimal overallRiskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private BigDecimal supervisedRiskScore;

    @Column(nullable = false)
    private BigDecimal anomalyRiskScore;

    @Column(nullable = false)
    private BigDecimal documentMismatchScore;

    @Column(nullable = false)
    private BigDecimal duplicateRiskScore;

    @Column(nullable = false)
    private BigDecimal historicalRiskScore;

    @Column(nullable = false)
    private String recommendation;

    @Column(columnDefinition = "TEXT")
    private String shapSummaryJson;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public RiskAssessment() {}

    public RiskAssessment(UUID id, UUID claimId, BigDecimal overallRiskScore, RiskLevel riskLevel, BigDecimal supervisedRiskScore, BigDecimal anomalyRiskScore, BigDecimal documentMismatchScore, BigDecimal duplicateRiskScore, BigDecimal historicalRiskScore, String recommendation, String shapSummaryJson, ZonedDateTime createdAt) {
        this.id = id;
        this.claimId = claimId;
        this.overallRiskScore = overallRiskScore;
        this.riskLevel = riskLevel;
        this.supervisedRiskScore = supervisedRiskScore;
        this.anomalyRiskScore = anomalyRiskScore;
        this.documentMismatchScore = documentMismatchScore;
        this.duplicateRiskScore = duplicateRiskScore;
        this.historicalRiskScore = historicalRiskScore;
        this.recommendation = recommendation;
        this.shapSummaryJson = shapSummaryJson;
        this.createdAt = createdAt;
    }

    public static RiskAssessmentBuilder builder() { return new RiskAssessmentBuilder(); }

    public static class RiskAssessmentBuilder {
        private UUID id;
        private UUID claimId;
        private BigDecimal overallRiskScore;
        private RiskLevel riskLevel;
        private BigDecimal supervisedRiskScore;
        private BigDecimal anomalyRiskScore;
        private BigDecimal documentMismatchScore;
        private BigDecimal duplicateRiskScore;
        private BigDecimal historicalRiskScore;
        private String recommendation;
        private String shapSummaryJson;
        private ZonedDateTime createdAt;

        public RiskAssessmentBuilder id(UUID id) { this.id = id; return this; }
        public RiskAssessmentBuilder claimId(UUID claimId) { this.claimId = claimId; return this; }
        public RiskAssessmentBuilder overallRiskScore(BigDecimal overallRiskScore) { this.overallRiskScore = overallRiskScore; return this; }
        public RiskAssessmentBuilder riskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; return this; }
        public RiskAssessmentBuilder supervisedRiskScore(BigDecimal supervisedRiskScore) { this.supervisedRiskScore = supervisedRiskScore; return this; }
        public RiskAssessmentBuilder anomalyRiskScore(BigDecimal anomalyRiskScore) { this.anomalyRiskScore = anomalyRiskScore; return this; }
        public RiskAssessmentBuilder documentMismatchScore(BigDecimal documentMismatchScore) { this.documentMismatchScore = documentMismatchScore; return this; }
        public RiskAssessmentBuilder duplicateRiskScore(BigDecimal duplicateRiskScore) { this.duplicateRiskScore = duplicateRiskScore; return this; }
        public RiskAssessmentBuilder historicalRiskScore(BigDecimal historicalRiskScore) { this.historicalRiskScore = historicalRiskScore; return this; }
        public RiskAssessmentBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public RiskAssessmentBuilder shapSummaryJson(String shapSummaryJson) { this.shapSummaryJson = shapSummaryJson; return this; }
        public RiskAssessmentBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public RiskAssessment build() {
            return new RiskAssessment(id, claimId, overallRiskScore, riskLevel, supervisedRiskScore, anomalyRiskScore, documentMismatchScore, duplicateRiskScore, historicalRiskScore, recommendation, shapSummaryJson, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClaimId() { return claimId; }
    public void setClaimId(UUID claimId) { this.claimId = claimId; }
    public BigDecimal getOverallRiskScore() { return overallRiskScore; }
    public void setOverallRiskScore(BigDecimal overallRiskScore) { this.overallRiskScore = overallRiskScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public BigDecimal getSupervisedRiskScore() { return supervisedRiskScore; }
    public void setSupervisedRiskScore(BigDecimal supervisedRiskScore) { this.supervisedRiskScore = supervisedRiskScore; }
    public BigDecimal getAnomalyRiskScore() { return anomalyRiskScore; }
    public void setAnomalyRiskScore(BigDecimal anomalyRiskScore) { this.anomalyRiskScore = anomalyRiskScore; }
    public BigDecimal getDocumentMismatchScore() { return documentMismatchScore; }
    public void setDocumentMismatchScore(BigDecimal documentMismatchScore) { this.documentMismatchScore = documentMismatchScore; }
    public BigDecimal getDuplicateRiskScore() { return duplicateRiskScore; }
    public void setDuplicateRiskScore(BigDecimal duplicateRiskScore) { this.duplicateRiskScore = duplicateRiskScore; }
    public BigDecimal getHistoricalRiskScore() { return historicalRiskScore; }
    public void setHistoricalRiskScore(BigDecimal historicalRiskScore) { this.historicalRiskScore = historicalRiskScore; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public String getShapSummaryJson() { return shapSummaryJson; }
    public void setShapSummaryJson(String shapSummaryJson) { this.shapSummaryJson = shapSummaryJson; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
