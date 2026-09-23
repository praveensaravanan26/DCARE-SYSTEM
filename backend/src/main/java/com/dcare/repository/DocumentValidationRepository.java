package com.dcare.repository;

import com.dcare.model.DocumentValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentValidationRepository extends JpaRepository<DocumentValidation, UUID> {
    List<DocumentValidation> findByClaimId(UUID claimId);
    void deleteByClaimId(UUID claimId);
}
