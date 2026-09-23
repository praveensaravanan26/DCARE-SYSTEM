package com.dcare.service;

import com.dcare.exception.InvalidOperationException;
import com.dcare.exception.ResourceNotFoundException;
import com.dcare.model.*;
import com.dcare.repository.ClaimDocumentRepository;
import com.dcare.repository.ClaimRepository;
import com.dcare.repository.ExtractedFieldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final ClaimDocumentRepository claimDocumentRepository;
    private final ClaimRepository claimRepository;
    private final ExtractedFieldRepository extractedFieldRepository;
    private final AiIntegrationService aiIntegrationService;
    private final AuditService auditService;

    @Value("${dcare.storage.upload-dir}")
    private String uploadDir;

    public DocumentService(ClaimDocumentRepository claimDocumentRepository,
                           ClaimRepository claimRepository,
                           ExtractedFieldRepository extractedFieldRepository,
                           AiIntegrationService aiIntegrationService,
                           AuditService auditService) {
        this.claimDocumentRepository = claimDocumentRepository;
        this.claimRepository = claimRepository;
        this.extractedFieldRepository = extractedFieldRepository;
        this.aiIntegrationService = aiIntegrationService;
        this.auditService = auditService;
    }

    @Transactional
    public ClaimDocument uploadDocument(UUID claimId, DocumentType documentType, MultipartFile file, User currentUser) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + claimId));

        if (file.isEmpty()) {
            throw new InvalidOperationException("Cannot upload empty file.");
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.bin";
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        List<String> allowedTypes = List.of("application/pdf", "image/png", "image/jpeg", "image/jpg");
        if (!allowedTypes.contains(contentType.toLowerCase()) && !originalFilename.toLowerCase().endsWith(".pdf") && !originalFilename.toLowerCase().endsWith(".png") && !originalFilename.toLowerCase().endsWith(".jpg") && !originalFilename.toLowerCase().endsWith(".jpeg")) {
            throw new InvalidOperationException("Invalid file format. Allowed formats: PDF, PNG, JPG, JPEG.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = file.getBytes();
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String sha256 = sb.toString();

            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".bin";
            String storedFilename = UUID.randomUUID() + "_" + documentType.name().toLowerCase() + extension;
            Path targetPath = dirPath.resolve(storedFilename);

            try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                fos.write(fileBytes);
            }

            ClaimDocument document = ClaimDocument.builder()
                    .claimId(claimId)
                    .documentType(documentType)
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .filePath(targetPath.toAbsolutePath().toString())
                    .fileSize(file.getSize())
                    .contentType(contentType)
                    .fileHashSha256(sha256)
                    .ocrStatus("PENDING")
                    .build();

            ClaimDocument savedDoc = claimDocumentRepository.save(document);

            auditService.logAction(currentUser != null ? currentUser.getId() : null,
                    currentUser != null ? currentUser.getEmail() : "system",
                    "DOCUMENT_UPLOADED", "CLAIM_DOCUMENT", savedDoc.getId().toString(),
                    "Uploaded " + documentType + " (" + originalFilename + ") for Claim #" + claim.getClaimNumber(), null);

            processDocument(savedDoc.getId(), currentUser);

            return savedDoc;
        } catch (Exception e) {
            log.error("Failed to store file: {}", originalFilename, e);
            throw new InvalidOperationException("Failed to upload document: " + e.getMessage());
        }
    }

    @Transactional
    public ClaimDocument processDocument(UUID documentId, User currentUser) {
        ClaimDocument document = claimDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        try {
            document.setOcrStatus("PROCESSING");
            claimDocumentRepository.save(document);

            Map<String, Object> ocrResult = aiIntegrationService.processDocumentOcr(document.getFilePath(), document.getDocumentType().name());

            String rawText = (String) ocrResult.getOrDefault("raw_text", "");
            Number confidenceNum = (Number) ocrResult.getOrDefault("confidence", 0.90);
            BigDecimal confidence = BigDecimal.valueOf(confidenceNum.doubleValue());

            document.setOcrRawText(rawText);
            document.setExtractionConfidence(confidence);
            document.setOcrStatus("COMPLETED");
            ClaimDocument updatedDoc = claimDocumentRepository.save(document);

            Object fieldsObj = ocrResult.get("fields");
            if (fieldsObj instanceof Map<?, ?> fieldMap) {
                for (Map.Entry<?, ?> entry : fieldMap.entrySet()) {
                    String fieldName = String.valueOf(entry.getKey());
                    String value = String.valueOf(entry.getValue());
                    if (value != null && !value.isBlank() && !"null".equalsIgnoreCase(value)) {
                        ExtractedField field = extractedFieldRepository
                                .findByClaimIdAndDocumentIdAndFieldName(document.getClaimId(), document.getId(), fieldName)
                                .orElse(ExtractedField.builder()
                                        .claimId(document.getClaimId())
                                        .documentId(document.getId())
                                        .fieldName(fieldName)
                                        .build());

                        field.setRawValue(value);
                        field.setNormalizedValue(value.trim());
                        field.setConfidenceScore(confidence);
                        field.setProvenance(ProvenanceType.OCR);
                        extractedFieldRepository.save(field);
                    }
                }
            }

            auditService.logAction(currentUser != null ? currentUser.getId() : null,
                    currentUser != null ? currentUser.getEmail() : "system",
                    "OCR_COMPLETED", "CLAIM_DOCUMENT", document.getId().toString(),
                    "OCR processing completed for document " + document.getOriginalFilename(), null);

            return updatedDoc;
        } catch (Exception e) {
            log.error("OCR process error for document: {}", documentId, e);
            document.setOcrStatus("FAILED");
            claimDocumentRepository.save(document);
            return document;
        }
    }

    public List<ClaimDocument> getDocumentsForClaim(UUID claimId) {
        return claimDocumentRepository.findByClaimIdOrderByCreatedAtAsc(claimId);
    }

    public List<ExtractedField> getExtractedFieldsForClaim(UUID claimId) {
        return extractedFieldRepository.findByClaimId(claimId);
    }

    @Transactional
    public ExtractedField correctExtractedField(UUID claimId, String fieldName, String correctedValue, User currentUser) {
        ExtractedField field = extractedFieldRepository.findByClaimIdAndFieldName(claimId, fieldName)
                .orElse(ExtractedField.builder()
                        .claimId(claimId)
                        .fieldName(fieldName)
                        .build());

        String oldValue = field.getNormalizedValue();
        field.setRawValue(correctedValue);
        field.setNormalizedValue(correctedValue.trim());
        field.setProvenance(ProvenanceType.MANUAL_CORRECTION);
        field.setIsVerified(true);
        ExtractedField savedField = extractedFieldRepository.save(field);

        auditService.logAction(currentUser != null ? currentUser.getId() : null,
                currentUser != null ? currentUser.getEmail() : "system",
                "FIELD_CORRECTED", "EXTRACTED_FIELD", savedField.getId().toString(),
                "Field '" + fieldName + "' corrected from '" + oldValue + "' to '" + correctedValue + "'", null);

        return savedField;
    }
}
