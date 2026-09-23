package com.dcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ClaimCreateRequest {
    @NotBlank
    private String policyNumber;

    @NotBlank
    private String claimType;

    @NotNull
    private LocalDate incidentDate;

    @NotBlank
    private String incidentLocation;

    @NotNull
    private BigDecimal claimedAmount;

    private String description;

    @NotBlank
    private String vehicleNumber;

    private String garageName;
    private String invoiceNumber;
    private String priority;

    public ClaimCreateRequest() {}

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    public String getIncidentLocation() { return incidentLocation; }
    public void setIncidentLocation(String incidentLocation) { this.incidentLocation = incidentLocation; }
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getGarageName() { return garageName; }
    public void setGarageName(String garageName) { this.garageName = garageName; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
