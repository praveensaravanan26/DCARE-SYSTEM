package com.dcare.config;

import com.dcare.model.*;
import com.dcare.repository.PolicyRepository;
import com.dcare.repository.UserRepository;
import com.dcare.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PolicyRepository policyRepository,
                           VehicleRepository vehicleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing DCARE Enterprise Users and Core Data...");

        if (userRepository.findByEmail("admin@dcare.local").isEmpty()) {
            userRepository.save(User.builder()
                    .email("admin@dcare.local")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .fullName("DCARE System Administrator")
                    .role(Role.ROLE_ADMIN)
                    .phoneNumber("+1-800-555-0100")
                    .isActive(true)
                    .isApproved(true)
                    .approvalStatus("APPROVED")
                    .build());
        }

        if (userRepository.findByEmail("officer@dcare.local").isEmpty()) {
            userRepository.save(User.builder()
                    .email("officer@dcare.local")
                    .passwordHash(passwordEncoder.encode("Officer@123"))
                    .fullName("Senior Claim Reviewer")
                    .role(Role.ROLE_CLAIM_OFFICER)
                    .phoneNumber("+1-800-555-0101")
                    .isActive(true)
                    .isApproved(true)
                    .approvalStatus("APPROVED")
                    .build());
        }

        if (userRepository.findByEmail("investigator@dcare.local").isEmpty()) {
            userRepository.save(User.builder()
                    .email("investigator@dcare.local")
                    .passwordHash(passwordEncoder.encode("Investigator@123"))
                    .fullName("Lead Fraud Analyst")
                    .role(Role.ROLE_FRAUD_INVESTIGATOR)
                    .phoneNumber("+1-800-555-0102")
                    .isActive(true)
                    .isApproved(true)
                    .approvalStatus("APPROVED")
                    .build());
        }

        User customer = userRepository.findByEmail("customer@dcare.local").orElse(null);
        if (customer == null) {
            customer = userRepository.save(User.builder()
                    .email("customer@dcare.local")
                    .passwordHash(passwordEncoder.encode("Customer@123"))
                    .fullName("Johnathon Doe (Policyholder)")
                    .role(Role.ROLE_CUSTOMER)
                    .phoneNumber("+1-800-555-0103")
                    .isActive(true)
                    .isApproved(true)
                    .approvalStatus("APPROVED")
                    .build());
        }

        if (policyRepository.findByPolicyNumber("POL-99281").isEmpty()) {
            policyRepository.save(Policy.builder()
                    .policyNumber("POL-99281")
                    .customerId(customer.getId())
                    .vehicleNumber("MH-02-CB-4091")
                    .policyType("COMPREHENSIVE_AUTO")
                    .startDate(LocalDate.now().minusMonths(6))
                    .endDate(LocalDate.now().plusMonths(6))
                    .coverageAmount(BigDecimal.valueOf(500000))
                    .premiumAmount(BigDecimal.valueOf(18500))
                    .policyStatus("ACTIVE")
                    .build());
        }

        if (policyRepository.findByPolicyNumber("POL-88314").isEmpty()) {
            policyRepository.save(Policy.builder()
                    .policyNumber("POL-88314")
                    .customerId(customer.getId())
                    .vehicleNumber("KA-01-MJ-8822")
                    .policyType("COMMERCIAL_FLEET")
                    .startDate(LocalDate.now().minusMonths(3))
                    .endDate(LocalDate.now().plusMonths(9))
                    .coverageAmount(BigDecimal.valueOf(750000))
                    .premiumAmount(BigDecimal.valueOf(32000))
                    .policyStatus("ACTIVE")
                    .build());
        }

        if (vehicleRepository.findByVehicleNumber("MH-02-CB-4091").isEmpty()) {
            vehicleRepository.save(Vehicle.builder()
                    .vehicleNumber("MH-02-CB-4091")
                    .make("Toyota")
                    .model("Camry Hybrid")
                    .yearOfManufacture(2023)
                    .registrationDate(LocalDate.of(2023, 5, 12))
                    .chassisNumber("TYT982187389271")
                    .engineNumber("ENG8829102")
                    .customerId(customer.getId())
                    .build());
        }

        log.info("DCARE Core Data initialization complete.");
    }
}
