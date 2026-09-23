package com.dcare.repository;

import com.dcare.model.RiskEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RiskEvidenceRepository extends JpaRepository<RiskEvidence, UUID> {
    List<RiskEvidence> findByClaimIdOrderBySeverityDesc(UUID claimId);
    void deleteByClaimId(UUID claimId);
}
