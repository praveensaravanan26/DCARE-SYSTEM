package com.dcare.repository;

import com.dcare.model.ClaimReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimReviewRepository extends JpaRepository<ClaimReview, UUID> {
    List<ClaimReview> findByClaimIdOrderByCreatedAtDesc(UUID claimId);
}
