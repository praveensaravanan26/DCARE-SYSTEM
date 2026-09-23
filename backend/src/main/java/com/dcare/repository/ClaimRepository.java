package com.dcare.repository;

import com.dcare.model.Claim;
import com.dcare.model.ClaimStatus;
import com.dcare.model.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status);
    List<Claim> findByRiskLevelOrderByCreatedAtDesc(RiskLevel riskLevel);
    List<Claim> findAllByOrderByCreatedAtDesc();
    List<Claim> findByPolicyNumber(String policyNumber);
    List<Claim> findByVehicleNumber(String vehicleNumber);
    long countByStatus(ClaimStatus status);
    long countByRiskLevel(RiskLevel riskLevel);
}
