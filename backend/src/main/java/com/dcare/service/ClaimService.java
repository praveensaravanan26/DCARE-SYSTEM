package com.dcare.service;

import com.dcare.dto.ClaimCreateRequest;
import com.dcare.exception.InvalidOperationException;
import com.dcare.exception.ResourceNotFoundException;
import com.dcare.model.*;
import com.dcare.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimService.class);

    private final ClaimRepository claimRepository;
    private final ClaimDocumentRepository claimDocumentRepository;
    private final ExtractedFieldRepository extractedFieldRepository;
    private final DocumentValidationRepository documentValidationRepository;
    private final ClaimFeatureRepository claimFeatureRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final RiskEvidenceRepository riskEvidenceRepository;
    private final AiIntegrationService aiIntegrationService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClaimService(ClaimRepository claimRepository,
                        ClaimDocumentRepository claimDocumentRepository,
                        ExtractedFieldRepository extractedFieldRepository,
                        DocumentValidationRepository documentValidationRepository,
                        ClaimFeatureRepository claimFeatureRepository,
                        RiskAssessmentRepository riskAssessmentRepository,
                        RiskEvidenceRepository riskEvidenceRepository,
                        AiIntegrationService aiIntegrationService,
                        AuditService auditService) {
        this.claimRepository = claimRepository;
        this.claimDocumentRepository = claimDocumentRepository;
        this.extractedFieldRepository = extractedFieldRepository;
        this.documentValidationRepository = documentValidationRepository;
        this.claimFeatureRepository = claimFeatureRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.riskEvidenceRepository = riskEvidenceRepository;
        this.aiIntegrationService = aiIntegrationService;
        this.auditService = auditService;
    }

    private String extractStr(Map<?, ?> map, String key, String defaultVal) {
        if (map == null) return defaultVal;
        Object val = map.get(key);
        return val != null ? val.toString() : defaultVal;
    }

    private Double extractNum(Map<?, ?> map, String key, Double defaultVal) {
        if (map == null) return defaultVal;
        Object val = map.get(key);
        if (val instanceof Number n) return n.doubleValue();
        return defaultVal;
    }

    @Transactional
    public Claim createClaim(ClaimCreateRequest request, User currentUser) {
        String claimNumber = "CLM-" + System.currentTimeMillis() % 1000000;

        Claim claim = Claim.builder()
                .claimNumber(claimNumber)
                .policyNumber(request.getPolicyNumber().trim().toUpperCase())
                .customerId(currentUser.getId())
                .claimType(request.getClaimType())
                .incidentDate(request.getIncidentDate())
                .incidentLocation(request.getIncidentLocation())
                .claimedAmount(request.getClaimedAmount())
                .description(request.getDescription())
                .vehicleNumber(request.getVehicleNumber().trim().toUpperCase())
                .garageName(request.getGarageName())
                .invoiceNumber(request.getInvoiceNumber())
                .status(ClaimStatus.SUBMITTED)
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .riskScore(BigDecimal.ZERO)
                .riskLevel(RiskLevel.LOW)
                .build();

        Claim savedClaim = claimRepository.save(claim);

        auditService.logAction(currentUser.getId(), currentUser.getEmail(), "CLAIM_CREATED",
                "CLAIM", savedClaim.getId().toString(),
                "Claim #" + claimNumber + " created for Policy " + savedClaim.getPolicyNumber(), null);

        return savedClaim;
    }

    public List<Claim> getAllClaims(User currentUser) {
        if (currentUser.getRole() == Role.ROLE_CUSTOMER) {
            return claimRepository.findByCustomerIdOrderByCreatedAtDesc(currentUser.getId());
        }
        return claimRepository.findAllByOrderByCreatedAtDesc();
    }

    public Claim getClaimById(UUID id, User currentUser) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + id));

        if (currentUser.getRole() == Role.ROLE_CUSTOMER && !claim.getCustomerId().equals(currentUser.getId())) {
            throw new InvalidOperationException("You are not authorized to view this claim.");
        }

        return claim;
    }

    @Transactional
    public Map<String, Object> evaluateClaimRisk(UUID claimId, User currentUser) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + claimId));

        List<ClaimDocument> documents = claimDocumentRepository.findByClaimIdOrderByCreatedAtAsc(claimId);
        List<ExtractedField> fields = extractedFieldRepository.findByClaimId(claimId);

        List<Claim> customerPastClaims = claimRepository.findByCustomerIdOrderByCreatedAtDesc(claim.getCustomerId());
        long pastClaimCount = customerPastClaims.stream().filter(c -> !c.getId().equals(claimId)).count();

        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);
        long frequency90d = customerPastClaims.stream()
                .filter(c -> !c.getId().equals(claimId) && c.getIncidentDate() != null && c.getIncidentDate().isAfter(ninetyDaysAgo))
                .count();

        BigDecimal historicalAvgAmount = BigDecimal.valueOf(25000);
        if (pastClaimCount > 0) {
            BigDecimal totalPastAmount = customerPastClaims.stream()
                    .filter(c -> !c.getId().equals(claimId))
                    .map(Claim::getClaimedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            historicalAvgAmount = totalPastAmount.divide(BigDecimal.valueOf(pastClaimCount), 2, RoundingMode.HALF_UP);
        }

        BigDecimal amountDeviation = claim.getClaimedAmount().subtract(historicalAvgAmount);
        BigDecimal zscore = amountDeviation.divide(BigDecimal.valueOf(15000), 2, RoundingMode.HALF_UP);

        long garageRepeatCount = customerPastClaims.stream()
                .filter(c -> !c.getId().equals(claimId) && claim.getGarageName() != null && claim.getGarageName().equalsIgnoreCase(c.getGarageName()))
                .count();

        Map<String, Object> payload = new HashMap<>();
        payload.put("claim_id", claim.getId().toString());
        payload.put("claim_number", claim.getClaimNumber());
        payload.put("policy_number", claim.getPolicyNumber());
        payload.put("vehicle_number", claim.getVehicleNumber());
        payload.put("incident_date", claim.getIncidentDate().toString());
        payload.put("claimed_amount", claim.getClaimedAmount().doubleValue());
        payload.put("garage_name", claim.getGarageName() != null ? claim.getGarageName() : "");
        payload.put("invoice_number", claim.getInvoiceNumber() != null ? claim.getInvoiceNumber() : "");

        List<Map<String, Object>> docsList = new ArrayList<>();
        for (ClaimDocument doc : documents) {
            Map<String, Object> docMap = new HashMap<>();
            docMap.put("document_id", doc.getId().toString());
            docMap.put("document_type", doc.getDocumentType().name());
            docMap.put("file_path", doc.getFilePath());
            docMap.put("file_hash", doc.getFileHashSha256());
            docMap.put("raw_text", doc.getOcrRawText() != null ? doc.getOcrRawText() : "");
            docsList.add(docMap);
        }
        payload.put("documents", docsList);

        Map<String, String> extractedMap = new HashMap<>();
        for (ExtractedField f : fields) {
            extractedMap.put(f.getFieldName(), f.getNormalizedValue());
        }
        payload.put("extracted_fields", extractedMap);

        Map<String, Object> historyMap = new HashMap<>();
        historyMap.put("previous_claim_count", pastClaimCount);
        historyMap.put("claim_frequency_90d", frequency90d);
        historyMap.put("historical_avg_amount", historicalAvgAmount.doubleValue());
        historyMap.put("amount_deviation", amountDeviation.doubleValue());
        historyMap.put("claim_amount_zscore", zscore.doubleValue());
        historyMap.put("garage_repeat_count", garageRepeatCount);
        historyMap.put("policy_age_days", 420);
        historyMap.put("incident_to_claim_delay_days", Math.max(1, ChronoUnit.DAYS.between(claim.getIncidentDate(), LocalDate.now())));
        payload.put("historical_features", historyMap);

        Map<String, Object> aiResult = aiIntegrationService.performFullAssessment(payload);

        documentValidationRepository.deleteByClaimId(claimId);
        List<?> validationsRaw = (List<?>) aiResult.getOrDefault("validations", Collections.emptyList());
        int mismatchCount = 0;
        for (Object item : validationsRaw) {
            if (item instanceof Map<?, ?> vMap) {
                String statusStr = extractStr(vMap, "status", "PASSED");
                String severityStr = extractStr(vMap, "severity", "LOW");
                DocumentValidation val = DocumentValidation.builder()
                        .claimId(claimId)
                        .ruleName(extractStr(vMap, "rule_name", "Consistency Rule"))
                        .ruleCategory(extractStr(vMap, "category", "CROSS_DOCUMENT_CONSISTENCY"))
                        .status(ValidationStatus.valueOf(statusStr))
                        .severity(ValidationSeverity.valueOf(severityStr))
                        .description(extractStr(vMap, "description", ""))
                        .detectedValue(extractStr(vMap, "detected_value", ""))
                        .expectedValue(extractStr(vMap, "expected_value", ""))
                        .sourceDocumentType(extractStr(vMap, "source_document_type", ""))
                        .targetDocumentType(extractStr(vMap, "target_document_type", ""))
                        .build();
                documentValidationRepository.save(val);
                if ("FAILED".equals(statusStr) || "WARNING".equals(statusStr)) {
                    mismatchCount++;
                }
            }
        }

        Number supervisedProbNum = (Number) aiResult.getOrDefault("supervised_prob", 0.15);
        Number anomalyScoreNum = (Number) aiResult.getOrDefault("anomaly_score", 0.10);
        Number duplicateIndNum = (Number) aiResult.getOrDefault("duplicate_indicator", 0);
        Number invoiceReuseNum = (Number) aiResult.getOrDefault("invoice_reuse_indicator", 0);

        ClaimFeature feature = claimFeatureRepository.findByClaimId(claimId)
                .orElse(ClaimFeature.builder().claimId(claimId).build());
        feature.setPreviousClaimCount((int) pastClaimCount);
        feature.setClaimFrequency90d((int) frequency90d);
        feature.setClaimAmountZscore(zscore);
        feature.setAmountDeviation(amountDeviation);
        feature.setGarageRepeatCount((int) garageRepeatCount);
        feature.setDocumentMismatchCount(mismatchCount);
        feature.setDuplicateIndicator(duplicateIndNum.intValue());
        feature.setInvoiceReuseIndicator(invoiceReuseNum.intValue());
        feature.setSupervisedProb(BigDecimal.valueOf(supervisedProbNum.doubleValue()));
        feature.setAnomalyScore(BigDecimal.valueOf(anomalyScoreNum.doubleValue()));
        claimFeatureRepository.save(feature);

        Number overallScoreNum = (Number) aiResult.getOrDefault("risk_score", 25.0);
        String riskLevelStr = (String) aiResult.getOrDefault("risk_level", "LOW");
        String recommendation = (String) aiResult.getOrDefault("recommendation", "Standard Processing");

        RiskAssessment assessment = riskAssessmentRepository.findByClaimId(claimId)
                .orElse(RiskAssessment.builder().claimId(claimId).build());

        assessment.setOverallRiskScore(BigDecimal.valueOf(overallScoreNum.doubleValue()));
        assessment.setRiskLevel(RiskLevel.valueOf(riskLevelStr));
        assessment.setSupervisedRiskScore(BigDecimal.valueOf(((Number) aiResult.getOrDefault("supervised_score", 15.0)).doubleValue()));
        assessment.setAnomalyRiskScore(BigDecimal.valueOf(((Number) aiResult.getOrDefault("anomaly_score", 10.0)).doubleValue()));
        assessment.setDocumentMismatchScore(BigDecimal.valueOf(((Number) aiResult.getOrDefault("mismatch_score", 0.0)).doubleValue()));
        assessment.setDuplicateRiskScore(BigDecimal.valueOf(((Number) aiResult.getOrDefault("duplicate_score", 0.0)).doubleValue()));
        assessment.setHistoricalRiskScore(BigDecimal.valueOf(((Number) aiResult.getOrDefault("historical_score", 0.0)).doubleValue()));
        assessment.setRecommendation(recommendation);

        Object shapSummaryObj = aiResult.get("shap_summary");
        try {
            assessment.setShapSummaryJson(objectMapper.writeValueAsString(shapSummaryObj));
        } catch (Exception ignored) {}

        RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);

        riskEvidenceRepository.deleteByClaimId(claimId);
        List<?> evidenceListRaw = (List<?>) aiResult.getOrDefault("evidence", Collections.emptyList());
        for (Object item : evidenceListRaw) {
            if (item instanceof Map<?, ?> eMap) {
                String sev = extractStr(eMap, "severity", "LOW");
                Double contrib = extractNum(eMap, "contribution", 0.0);
                RiskEvidence ev = RiskEvidence.builder()
                        .claimId(claimId)
                        .assessmentId(savedAssessment.getId())
                        .evidenceType(extractStr(eMap, "type", "MODEL_SIGNAL"))
                        .description(extractStr(eMap, "description", ""))
                        .severity(ValidationSeverity.valueOf(sev))
                        .sourceDocument(extractStr(eMap, "source_doc", "Claim Form"))
                        .sourceField(extractStr(eMap, "source_field", "General"))
                        .detectedValue(extractStr(eMap, "detected_value", ""))
                        .expectedValue(extractStr(eMap, "expected_value", ""))
                        .contributionScore(BigDecimal.valueOf(contrib))
                        .build();
                riskEvidenceRepository.save(ev);
            }
        }

        claim.setRiskScore(BigDecimal.valueOf(overallScoreNum.doubleValue()));
        claim.setRiskLevel(RiskLevel.valueOf(riskLevelStr));
        if (overallScoreNum.doubleValue() >= 60.0) {
            claim.setStatus(ClaimStatus.ENHANCED_REVIEW);
        } else {
            claim.setStatus(ClaimStatus.RISK_ASSESSED);
        }
        claimRepository.save(claim);

        auditService.logAction(currentUser != null ? currentUser.getId() : null,
                currentUser != null ? currentUser.getEmail() : "system",
                "RISK_CALCULATED", "CLAIM", claim.getId().toString(),
                "Risk evaluation completed. Score: " + overallScoreNum + " (" + riskLevelStr + "). Recommendation: " + recommendation, null);

        Map<String, Object> response = new HashMap<>();
        response.put("claim", claim);
        response.put("assessment", savedAssessment);
        response.put("validations", documentValidationRepository.findByClaimId(claimId));
        response.put("evidence", riskEvidenceRepository.findByClaimIdOrderBySeverityDesc(claimId));
        response.put("features", feature);
        return response;
    }

    public Map<String, Object> getRiskDetails(UUID claimId) {
        Map<String, Object> map = new HashMap<>();
        map.put("claim", claimRepository.findById(claimId).orElse(null));
        map.put("assessment", riskAssessmentRepository.findByClaimId(claimId).orElse(null));
        map.put("validations", documentValidationRepository.findByClaimId(claimId));
        map.put("evidence", riskEvidenceRepository.findByClaimIdOrderBySeverityDesc(claimId));
        map.put("features", claimFeatureRepository.findByClaimId(claimId).orElse(null));
        return map;
    }
}
