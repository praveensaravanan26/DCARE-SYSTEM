package com.dcare.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AnalyticsSummaryDTO {
    private long totalClaims;
    private long claimsProcessed;
    private long claimsPending;
    private long claimsRequiringReview;
    private long highRiskClaims;
    private BigDecimal totalClaimedVolume;
    private BigDecimal averageRiskScore;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> riskLevelDistribution;
    private Map<String, Object> modelMetrics;

    public AnalyticsSummaryDTO() {}

    public AnalyticsSummaryDTO(long totalClaims, long claimsProcessed, long claimsPending, long claimsRequiringReview, long highRiskClaims, BigDecimal totalClaimedVolume, BigDecimal averageRiskScore, Map<String, Long> statusDistribution, Map<String, Long> riskLevelDistribution, Map<String, Object> modelMetrics) {
        this.totalClaims = totalClaims;
        this.claimsProcessed = claimsProcessed;
        this.claimsPending = claimsPending;
        this.claimsRequiringReview = claimsRequiringReview;
        this.highRiskClaims = highRiskClaims;
        this.totalClaimedVolume = totalClaimedVolume;
        this.averageRiskScore = averageRiskScore;
        this.statusDistribution = statusDistribution;
        this.riskLevelDistribution = riskLevelDistribution;
        this.modelMetrics = modelMetrics;
    }

    public static AnalyticsSummaryDTOBuilder builder() { return new AnalyticsSummaryDTOBuilder(); }

    public static class AnalyticsSummaryDTOBuilder {
        private long totalClaims;
        private long claimsProcessed;
        private long claimsPending;
        private long claimsRequiringReview;
        private long highRiskClaims;
        private BigDecimal totalClaimedVolume;
        private BigDecimal averageRiskScore;
        private Map<String, Long> statusDistribution;
        private Map<String, Long> riskLevelDistribution;
        private Map<String, Object> modelMetrics;

        public AnalyticsSummaryDTOBuilder totalClaims(long totalClaims) { this.totalClaims = totalClaims; return this; }
        public AnalyticsSummaryDTOBuilder claimsProcessed(long claimsProcessed) { this.claimsProcessed = claimsProcessed; return this; }
        public AnalyticsSummaryDTOBuilder claimsPending(long claimsPending) { this.claimsPending = claimsPending; return this; }
        public AnalyticsSummaryDTOBuilder claimsRequiringReview(long claimsRequiringReview) { this.claimsRequiringReview = claimsRequiringReview; return this; }
        public AnalyticsSummaryDTOBuilder highRiskClaims(long highRiskClaims) { this.highRiskClaims = highRiskClaims; return this; }
        public AnalyticsSummaryDTOBuilder totalClaimedVolume(BigDecimal totalClaimedVolume) { this.totalClaimedVolume = totalClaimedVolume; return this; }
        public AnalyticsSummaryDTOBuilder averageRiskScore(BigDecimal averageRiskScore) { this.averageRiskScore = averageRiskScore; return this; }
        public AnalyticsSummaryDTOBuilder statusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; return this; }
        public AnalyticsSummaryDTOBuilder riskLevelDistribution(Map<String, Long> riskLevelDistribution) { this.riskLevelDistribution = riskLevelDistribution; return this; }
        public AnalyticsSummaryDTOBuilder modelMetrics(Map<String, Object> modelMetrics) { this.modelMetrics = modelMetrics; return this; }

        public AnalyticsSummaryDTO build() {
            return new AnalyticsSummaryDTO(totalClaims, claimsProcessed, claimsPending, claimsRequiringReview, highRiskClaims, totalClaimedVolume, averageRiskScore, statusDistribution, riskLevelDistribution, modelMetrics);
        }
    }

    public long getTotalClaims() { return totalClaims; }
    public void setTotalClaims(long totalClaims) { this.totalClaims = totalClaims; }
    public long getClaimsProcessed() { return claimsProcessed; }
    public void setClaimsProcessed(long claimsProcessed) { this.claimsProcessed = claimsProcessed; }
    public long getClaimsPending() { return claimsPending; }
    public void setClaimsPending(long claimsPending) { this.claimsPending = claimsPending; }
    public long getClaimsRequiringReview() { return claimsRequiringReview; }
    public void setClaimsRequiringReview(long claimsRequiringReview) { this.claimsRequiringReview = claimsRequiringReview; }
    public long getHighRiskClaims() { return highRiskClaims; }
    public void setHighRiskClaims(long highRiskClaims) { this.highRiskClaims = highRiskClaims; }
    public BigDecimal getTotalClaimedVolume() { return totalClaimedVolume; }
    public void setTotalClaimedVolume(BigDecimal totalClaimedVolume) { this.totalClaimedVolume = totalClaimedVolume; }
    public BigDecimal getAverageRiskScore() { return averageRiskScore; }
    public void setAverageRiskScore(BigDecimal averageRiskScore) { this.averageRiskScore = averageRiskScore; }
    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; }
    public Map<String, Long> getRiskLevelDistribution() { return riskLevelDistribution; }
    public void setRiskLevelDistribution(Map<String, Long> riskLevelDistribution) { this.riskLevelDistribution = riskLevelDistribution; }
    public Map<String, Object> getModelMetrics() { return modelMetrics; }
    public void setModelMetrics(Map<String, Object> modelMetrics) { this.modelMetrics = modelMetrics; }
}
