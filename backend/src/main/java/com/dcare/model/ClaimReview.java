package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "claim_reviews")
public class ClaimReview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID claimId;

    @Column(nullable = false)
    private UUID reviewerId;

    @Column(nullable = false)
    private String reviewerName;

    @Column(nullable = false)
    private String reviewerRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewDecision decision;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private String previousStatus;

    @Column(nullable = false)
    private String newStatus;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public ClaimReview() {}

    public ClaimReview(UUID id, UUID claimId, UUID reviewerId, String reviewerName, String reviewerRole, ReviewDecision decision, String notes, String previousStatus, String newStatus, ZonedDateTime createdAt) {
        this.id = id;
        this.claimId = claimId;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewerRole = reviewerRole;
        this.decision = decision;
        this.notes = notes;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.createdAt = createdAt;
    }

    public static ClaimReviewBuilder builder() { return new ClaimReviewBuilder(); }

    public static class ClaimReviewBuilder {
        private UUID id;
        private UUID claimId;
        private UUID reviewerId;
        private String reviewerName;
        private String reviewerRole;
        private ReviewDecision decision;
        private String notes;
        private String previousStatus;
        private String newStatus;
        private ZonedDateTime createdAt;

        public ClaimReviewBuilder id(UUID id) { this.id = id; return this; }
        public ClaimReviewBuilder claimId(UUID claimId) { this.claimId = claimId; return this; }
        public ClaimReviewBuilder reviewerId(UUID reviewerId) { this.reviewerId = reviewerId; return this; }
        public ClaimReviewBuilder reviewerName(String reviewerName) { this.reviewerName = reviewerName; return this; }
        public ClaimReviewBuilder reviewerRole(String reviewerRole) { this.reviewerRole = reviewerRole; return this; }
        public ClaimReviewBuilder decision(ReviewDecision decision) { this.decision = decision; return this; }
        public ClaimReviewBuilder notes(String notes) { this.notes = notes; return this; }
        public ClaimReviewBuilder previousStatus(String previousStatus) { this.previousStatus = previousStatus; return this; }
        public ClaimReviewBuilder newStatus(String newStatus) { this.newStatus = newStatus; return this; }
        public ClaimReviewBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ClaimReview build() {
            return new ClaimReview(id, claimId, reviewerId, reviewerName, reviewerRole, decision, notes, previousStatus, newStatus, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClaimId() { return claimId; }
    public void setClaimId(UUID claimId) { this.claimId = claimId; }
    public UUID getReviewerId() { return reviewerId; }
    public void setReviewerId(UUID reviewerId) { this.reviewerId = reviewerId; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    public String getReviewerRole() { return reviewerRole; }
    public void setReviewerRole(String reviewerRole) { this.reviewerRole = reviewerRole; }
    public ReviewDecision getDecision() { return decision; }
    public void setDecision(ReviewDecision decision) { this.decision = decision; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
