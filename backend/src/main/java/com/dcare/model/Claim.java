package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String claimNumber;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private String claimType;

    @Column(nullable = false)
    private LocalDate incidentDate;

    @Column(nullable = false)
    private String incidentLocation;

    @Column(nullable = false)
    private BigDecimal claimedAmount;

    private BigDecimal approvedAmount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String vehicleNumber;

    private String garageName;
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.DRAFT;

    private String priority = "NORMAL";

    private BigDecimal riskScore = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel = RiskLevel.LOW;

    private UUID assignedOfficerId;
    private UUID assignedInvestigatorId;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public Claim() {}

    public Claim(UUID id, String claimNumber, String policyNumber, UUID customerId, String claimType, LocalDate incidentDate, String incidentLocation, BigDecimal claimedAmount, BigDecimal approvedAmount, String description, String vehicleNumber, String garageName, String invoiceNumber, ClaimStatus status, String priority, BigDecimal riskScore, RiskLevel riskLevel, UUID assignedOfficerId, UUID assignedInvestigatorId, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.claimNumber = claimNumber;
        this.policyNumber = policyNumber;
        this.customerId = customerId;
        this.claimType = claimType;
        this.incidentDate = incidentDate;
        this.incidentLocation = incidentLocation;
        this.claimedAmount = claimedAmount;
        this.approvedAmount = approvedAmount;
        this.description = description;
        this.vehicleNumber = vehicleNumber;
        this.garageName = garageName;
        this.invoiceNumber = invoiceNumber;
        this.status = status != null ? status : ClaimStatus.DRAFT;
        this.priority = priority != null ? priority : "NORMAL";
        this.riskScore = riskScore != null ? riskScore : BigDecimal.ZERO;
        this.riskLevel = riskLevel != null ? riskLevel : RiskLevel.LOW;
        this.assignedOfficerId = assignedOfficerId;
        this.assignedInvestigatorId = assignedInvestigatorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ClaimBuilder builder() { return new ClaimBuilder(); }

    public static class ClaimBuilder {
        private UUID id;
        private String claimNumber;
        private String policyNumber;
        private UUID customerId;
        private String claimType;
        private LocalDate incidentDate;
        private String incidentLocation;
        private BigDecimal claimedAmount;
        private BigDecimal approvedAmount;
        private String description;
        private String vehicleNumber;
        private String garageName;
        private String invoiceNumber;
        private ClaimStatus status = ClaimStatus.DRAFT;
        private String priority = "NORMAL";
        private BigDecimal riskScore = BigDecimal.ZERO;
        private RiskLevel riskLevel = RiskLevel.LOW;
        private UUID assignedOfficerId;
        private UUID assignedInvestigatorId;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public ClaimBuilder id(UUID id) { this.id = id; return this; }
        public ClaimBuilder claimNumber(String claimNumber) { this.claimNumber = claimNumber; return this; }
        public ClaimBuilder policyNumber(String policyNumber) { this.policyNumber = policyNumber; return this; }
        public ClaimBuilder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public ClaimBuilder claimType(String claimType) { this.claimType = claimType; return this; }
        public ClaimBuilder incidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; return this; }
        public ClaimBuilder incidentLocation(String incidentLocation) { this.incidentLocation = incidentLocation; return this; }
        public ClaimBuilder claimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; return this; }
        public ClaimBuilder approvedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; return this; }
        public ClaimBuilder description(String description) { this.description = description; return this; }
        public ClaimBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public ClaimBuilder garageName(String garageName) { this.garageName = garageName; return this; }
        public ClaimBuilder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public ClaimBuilder status(ClaimStatus status) { this.status = status; return this; }
        public ClaimBuilder priority(String priority) { this.priority = priority; return this; }
        public ClaimBuilder riskScore(BigDecimal riskScore) { this.riskScore = riskScore; return this; }
        public ClaimBuilder riskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; return this; }
        public ClaimBuilder assignedOfficerId(UUID assignedOfficerId) { this.assignedOfficerId = assignedOfficerId; return this; }
        public ClaimBuilder assignedInvestigatorId(UUID assignedInvestigatorId) { this.assignedInvestigatorId = assignedInvestigatorId; return this; }
        public ClaimBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ClaimBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Claim build() {
            return new Claim(id, claimNumber, policyNumber, customerId, claimType, incidentDate, incidentLocation, claimedAmount, approvedAmount, description, vehicleNumber, garageName, invoiceNumber, status, priority, riskScore, riskLevel, assignedOfficerId, assignedInvestigatorId, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    public String getIncidentLocation() { return incidentLocation; }
    public void setIncidentLocation(String incidentLocation) { this.incidentLocation = incidentLocation; }
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getGarageName() { return garageName; }
    public void setGarageName(String garageName) { this.garageName = garageName; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public BigDecimal getRiskScore() { return riskScore; }
    public void setRiskScore(BigDecimal riskScore) { this.riskScore = riskScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public UUID getAssignedOfficerId() { return assignedOfficerId; }
    public void setAssignedOfficerId(UUID assignedOfficerId) { this.assignedOfficerId = assignedOfficerId; }
    public UUID getAssignedInvestigatorId() { return assignedInvestigatorId; }
    public void setAssignedInvestigatorId(UUID assignedInvestigatorId) { this.assignedInvestigatorId = assignedInvestigatorId; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
