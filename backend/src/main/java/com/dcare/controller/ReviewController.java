package com.dcare.controller;

import com.dcare.dto.ReviewRequest;
import com.dcare.model.ClaimReview;
import com.dcare.model.User;
import com.dcare.repository.UserRepository;
import com.dcare.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/claims")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    public ReviewController(ReviewService reviewService, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyAuthority('ROLE_CLAIM_OFFICER', 'ROLE_FRAUD_INVESTIGATOR', 'ROLE_ADMIN')")
    public ResponseEntity<ClaimReview> submitReview(@PathVariable UUID id,
                                                    @Valid @RequestBody ReviewRequest request,
                                                    @AuthenticationPrincipal String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(reviewService.submitReview(id, request, user));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ClaimReview>> getReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.getReviewsForClaim(id));
    }
}
