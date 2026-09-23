package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "policies")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String policyNumber;

    private UUID customerId;

    @Column(nullable = false)
    private String vehicleNumber;

    private String policyType = "COMPREHENSIVE_AUTO";

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal coverageAmount;

    @Column(nullable = false)
    private BigDecimal premiumAmount;

    private String policyStatus = "ACTIVE";

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public Policy() {}

    public Policy(UUID id, String policyNumber, UUID customerId, String vehicleNumber, String policyType, LocalDate startDate, LocalDate endDate, BigDecimal coverageAmount, BigDecimal premiumAmount, String policyStatus, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.customerId = customerId;
        this.vehicleNumber = vehicleNumber;
        this.policyType = policyType != null ? policyType : "COMPREHENSIVE_AUTO";
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverageAmount = coverageAmount;
        this.premiumAmount = premiumAmount;
        this.policyStatus = policyStatus != null ? policyStatus : "ACTIVE";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PolicyBuilder builder() { return new PolicyBuilder(); }

    public static class PolicyBuilder {
        private UUID id;
        private String policyNumber;
        private UUID customerId;
        private String vehicleNumber;
        private String policyType = "COMPREHENSIVE_AUTO";
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal coverageAmount;
        private BigDecimal premiumAmount;
        private String policyStatus = "ACTIVE";
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public PolicyBuilder id(UUID id) { this.id = id; return this; }
        public PolicyBuilder policyNumber(String policyNumber) { this.policyNumber = policyNumber; return this; }
        public PolicyBuilder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public PolicyBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public PolicyBuilder policyType(String policyType) { this.policyType = policyType; return this; }
        public PolicyBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public PolicyBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public PolicyBuilder coverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; return this; }
        public PolicyBuilder premiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; return this; }
        public PolicyBuilder policyStatus(String policyStatus) { this.policyStatus = policyStatus; return this; }
        public PolicyBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PolicyBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Policy build() {
            return new Policy(id, policyNumber, customerId, vehicleNumber, policyType, startDate, endDate, coverageAmount, premiumAmount, policyStatus, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getCoverageAmount() { return coverageAmount; }
    public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }
    public String getPolicyStatus() { return policyStatus; }
    public void setPolicyStatus(String policyStatus) { this.policyStatus = policyStatus; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
