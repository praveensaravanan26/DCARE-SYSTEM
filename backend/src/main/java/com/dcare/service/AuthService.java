package com.dcare.service;

import com.dcare.config.JwtTokenProvider;
import com.dcare.dto.AuthRequest;
import com.dcare.dto.AuthResponse;
import com.dcare.dto.RegisterRequest;
import com.dcare.exception.InvalidOperationException;
import com.dcare.exception.UnauthorizedException;
import com.dcare.model.Role;
import com.dcare.model.User;
import com.dcare.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.auditService = auditService;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        if ("REJECTED".equalsIgnoreCase(user.getApprovalStatus())) {
            throw new UnauthorizedException("Access Denied: Your staff registration request was reviewed and declined by the administrator.");
        }

        if ("PENDING_APPROVAL".equalsIgnoreCase(user.getApprovalStatus()) || Boolean.FALSE.equals(user.getIsApproved())) {
            throw new UnauthorizedException("Access Denied: Staff account (" + user.getRole().name().replace("ROLE_", "") + ") is currently pending System Administrator review and activation.");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("Account has been deactivated. Please contact your organization administrator.");
        }

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        auditService.logAction(user.getId(), user.getEmail(), "LOGIN", "USER", user.getId().toString(), "User logged in successfully with role " + user.getRole().name(), null);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isApproved(true)
                .message("Authentication successful.")
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new InvalidOperationException("Email is already registered in the system.");
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.ROLE_CUSTOMER;

        if (assignedRole == Role.ROLE_ADMIN) {
            throw new InvalidOperationException("System Administrator accounts cannot be self-registered.");
        }

        boolean isStaff = (assignedRole == Role.ROLE_CLAIM_OFFICER || assignedRole == Role.ROLE_FRAUD_INVESTIGATOR);
        boolean isApproved = !isStaff;
        boolean isActive = !isStaff;
        String approvalStatus = isStaff ? "PENDING_APPROVAL" : "APPROVED";

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .role(assignedRole)
                .phoneNumber(request.getPhoneNumber())
                .isActive(isActive)
                .isApproved(isApproved)
                .approvalStatus(approvalStatus)
                .build();

        User savedUser = userRepository.save(user);

        if (isStaff) {
            auditService.logAction(savedUser.getId(), savedUser.getEmail(), "STAFF_REGISTRATION_PENDING", "USER", savedUser.getId().toString(),
                    "New staff registration submitted for " + assignedRole + ". Pending System Administrator approval.", null);

            return AuthResponse.builder()
                    .token(null)
                    .userId(savedUser.getId())
                    .email(savedUser.getEmail())
                    .fullName(savedUser.getFullName())
                    .role(savedUser.getRole())
                    .isApproved(false)
                    .message("Staff registration received. Due to enterprise security protocols, your account requires System Administrator approval before you can log in.")
                    .build();
        } else {
            String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());

            auditService.logAction(savedUser.getId(), savedUser.getEmail(), "REGISTER", "USER", savedUser.getId().toString(),
                    "New policyholder registered successfully.", null);

            return AuthResponse.builder()
                    .token(token)
                    .userId(savedUser.getId())
                    .email(savedUser.getEmail())
                    .fullName(savedUser.getFullName())
                    .role(savedUser.getRole())
                    .isApproved(true)
                    .message("Customer registration completed successfully.")
                    .build();
        }
    }
}
