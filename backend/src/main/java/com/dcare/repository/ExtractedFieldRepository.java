package com.dcare.repository;

import com.dcare.model.ExtractedField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExtractedFieldRepository extends JpaRepository<ExtractedField, UUID> {
    List<ExtractedField> findByClaimId(UUID claimId);
    List<ExtractedField> findByClaimIdAndDocumentId(UUID claimId, UUID documentId);
    Optional<ExtractedField> findByClaimIdAndFieldName(UUID claimId, String fieldName);
    Optional<ExtractedField> findByClaimIdAndDocumentIdAndFieldName(UUID claimId, UUID documentId, String fieldName);
}
