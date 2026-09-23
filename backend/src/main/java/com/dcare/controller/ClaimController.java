package com.dcare.controller;

import com.dcare.dto.ClaimCreateRequest;
import com.dcare.dto.FieldCorrectionRequest;
import com.dcare.model.Claim;
import com.dcare.model.User;
import com.dcare.repository.UserRepository;
import com.dcare.service.ClaimService;
import com.dcare.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final DocumentService documentService;
    private final UserRepository userRepository;

    public ClaimController(ClaimService claimService, DocumentService documentService, UserRepository userRepository) {
        this.claimService = claimService;
        this.documentService = documentService;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<Claim> createClaim(@Valid @RequestBody ClaimCreateRequest request,
                                            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(claimService.createClaim(request, getUser(email)));
    }

    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(claimService.getAllClaims(getUser(email)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable UUID id,
                                             @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(claimService.getClaimById(id, getUser(email)));
    }

    @PostMapping("/{id}/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateClaimRisk(@PathVariable UUID id,
                                                                 @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(claimService.evaluateClaimRisk(id, getUser(email)));
    }

    @GetMapping("/{id}/risk")
    public ResponseEntity<Map<String, Object>> getRiskDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(claimService.getRiskDetails(id));
    }

    @PostMapping("/{id}/fields/correct")
    public ResponseEntity<?> correctField(@PathVariable UUID id,
                                          @Valid @RequestBody FieldCorrectionRequest request,
                                          @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(documentService.correctExtractedField(id, request.getFieldName(), request.getCorrectedValue(), getUser(email)));
    }
}
