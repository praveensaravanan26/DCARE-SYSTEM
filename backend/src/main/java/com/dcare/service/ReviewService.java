package com.dcare.service;

import com.dcare.dto.ReviewRequest;
import com.dcare.exception.ResourceNotFoundException;
import com.dcare.model.*;
import com.dcare.repository.ClaimRepository;
import com.dcare.repository.ClaimReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final ClaimRepository claimRepository;
    private final ClaimReviewRepository claimReviewRepository;
    private final AuditService auditService;

    public ReviewService(ClaimRepository claimRepository,
                         ClaimReviewRepository claimReviewRepository,
                         AuditService auditService) {
        this.claimRepository = claimRepository;
        this.claimReviewRepository = claimReviewRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ClaimReview submitReview(UUID claimId, ReviewRequest request, User currentUser) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + claimId));

        ClaimStatus previousStatus = claim.getStatus();
        ClaimStatus newStatus;

        switch (request.getDecision()) {
            case APPROVE -> {
                newStatus = ClaimStatus.APPROVED;
                claim.setApprovedAmount(claim.getClaimedAmount());
            }
            case REJECT -> newStatus = ClaimStatus.REJECTED;
            case REQUEST_INFORMATION -> newStatus = ClaimStatus.VALIDATION_REQUIRED;
            case SEND_TO_INVESTIGATION -> {
                newStatus = ClaimStatus.INVESTIGATION;
                claim.setAssignedInvestigatorId(currentUser.getId());
            }
            default -> newStatus = ClaimStatus.ENHANCED_REVIEW;
        }

        claim.setStatus(newStatus);
        claimRepository.save(claim);

        ClaimReview review = ClaimReview.builder()
                .claimId(claimId)
                .reviewerId(currentUser.getId())
                .reviewerName(currentUser.getFullName())
                .reviewerRole(currentUser.getRole().name())
                .decision(request.getDecision())
                .notes(request.getNotes())
                .previousStatus(previousStatus.name())
                .newStatus(newStatus.name())
                .build();

        ClaimReview savedReview = claimReviewRepository.save(review);

        auditService.logAction(currentUser.getId(), currentUser.getEmail(), "DECISION_MADE",
                "CLAIM_REVIEW", savedReview.getId().toString(),
                "Claim #" + claim.getClaimNumber() + " review decision: " + request.getDecision() + ". Notes: " + request.getNotes(), null);

        return savedReview;
    }

    public List<ClaimReview> getReviewsForClaim(UUID claimId) {
        return claimReviewRepository.findByClaimIdOrderByCreatedAtDesc(claimId);
    }
}
