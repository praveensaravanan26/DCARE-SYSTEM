package com.dcare.repository;

import com.dcare.model.ClaimDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, UUID> {
    List<ClaimDocument> findByClaimIdOrderByCreatedAtAsc(UUID claimId);
    List<ClaimDocument> findByFileHashSha256(String fileHashSha256);
    Optional<ClaimDocument> findFirstByFileHashSha256AndClaimIdNot(String fileHashSha256, UUID claimId);
}
