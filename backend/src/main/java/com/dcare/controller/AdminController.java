package com.dcare.controller;

import com.dcare.exception.ResourceNotFoundException;
import com.dcare.model.User;
import com.dcare.repository.UserRepository;
import com.dcare.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AuditService auditService;

    public AdminController(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<List<User>> getPendingApprovals() {
        return ResponseEntity.ok(userRepository.findByApprovalStatusOrderByCreatedAtDesc("PENDING_APPROVAL"));
    }

    @PostMapping("/users/{id}/approve")
    public ResponseEntity<User> approveUser(@PathVariable UUID id, @AuthenticationPrincipal String adminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setIsApproved(true);
        user.setIsActive(true);
        user.setApprovalStatus("APPROVED");
        User updated = userRepository.save(user);

        auditService.logAction(updated.getId(), adminEmail, "STAFF_APPROVED", "USER", updated.getId().toString(),
                "Administrator approved staff access for " + updated.getFullName() + " (" + updated.getRole() + ")", null);

        return ResponseEntity.ok(updated);
    }

    @PostMapping("/users/{id}/reject")
    public ResponseEntity<User> rejectUser(@PathVariable UUID id, @AuthenticationPrincipal String adminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setIsApproved(false);
        user.setIsActive(false);
        user.setApprovalStatus("REJECTED");
        User updated = userRepository.save(user);

        auditService.logAction(updated.getId(), adminEmail, "STAFF_REJECTED", "USER", updated.getId().toString(),
                "Administrator rejected staff access for " + updated.getFullName() + " (" + updated.getRole() + ")", null);

        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable UUID id, @AuthenticationPrincipal String adminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        User updated = userRepository.save(user);

        auditService.logAction(updated.getId(), adminEmail, "USER_STATUS_TOGGLED", "USER", updated.getId().toString(),
                "Administrator changed active status to " + updated.getIsActive() + " for " + updated.getEmail(), null);

        return ResponseEntity.ok(Map.of("id", updated.getId(), "isActive", updated.getIsActive()));
    }
}
