package com.dcare.repository;

import com.dcare.model.ClaimFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimFeatureRepository extends JpaRepository<ClaimFeature, UUID> {
    Optional<ClaimFeature> findByClaimId(UUID claimId);
}
