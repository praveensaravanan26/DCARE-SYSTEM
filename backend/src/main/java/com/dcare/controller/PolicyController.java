package com.dcare.controller;

import com.dcare.model.Policy;
import com.dcare.model.User;
import com.dcare.repository.PolicyRepository;
import com.dcare.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;

    public PolicyController(PolicyRepository policyRepository, UserRepository userRepository) {
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Policy>> getPolicies(@AuthenticationPrincipal String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && "ROLE_CUSTOMER".equals(user.getRole().name())) {
            List<Policy> customerPolicies = policyRepository.findByCustomerId(user.getId());
            if (!customerPolicies.isEmpty()) {
                return ResponseEntity.ok(customerPolicies);
            }
        }
        return ResponseEntity.ok(policyRepository.findAll());
    }
}
