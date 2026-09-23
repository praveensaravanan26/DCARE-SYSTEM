package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "claim_documents")
public class ClaimDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID claimId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storedFilename;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false, length = 64)
    private String fileHashSha256;

    private String ocrStatus = "PENDING";

    @Column(columnDefinition = "TEXT")
    private String ocrRawText;

    private BigDecimal extractionConfidence;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public ClaimDocument() {}

    public ClaimDocument(UUID id, UUID claimId, DocumentType documentType, String originalFilename, String storedFilename, String filePath, Long fileSize, String contentType, String fileHashSha256, String ocrStatus, String ocrRawText, BigDecimal extractionConfidence, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.claimId = claimId;
        this.documentType = documentType;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.fileHashSha256 = fileHashSha256;
        this.ocrStatus = ocrStatus != null ? ocrStatus : "PENDING";
        this.ocrRawText = ocrRawText;
        this.extractionConfidence = extractionConfidence;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ClaimDocumentBuilder builder() { return new ClaimDocumentBuilder(); }

    public static class ClaimDocumentBuilder {
        private UUID id;
        private UUID claimId;
        private DocumentType documentType;
        private String originalFilename;
        private String storedFilename;
        private String filePath;
        private Long fileSize;
        private String contentType;
        private String fileHashSha256;
        private String ocrStatus = "PENDING";
        private String ocrRawText;
        private BigDecimal extractionConfidence;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public ClaimDocumentBuilder id(UUID id) { this.id = id; return this; }
        public ClaimDocumentBuilder claimId(UUID claimId) { this.claimId = claimId; return this; }
        public ClaimDocumentBuilder documentType(DocumentType documentType) { this.documentType = documentType; return this; }
        public ClaimDocumentBuilder originalFilename(String originalFilename) { this.originalFilename = originalFilename; return this; }
        public ClaimDocumentBuilder storedFilename(String storedFilename) { this.storedFilename = storedFilename; return this; }
        public ClaimDocumentBuilder filePath(String filePath) { this.filePath = filePath; return this; }
        public ClaimDocumentBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public ClaimDocumentBuilder contentType(String contentType) { this.contentType = contentType; return this; }
        public ClaimDocumentBuilder fileHashSha256(String fileHashSha256) { this.fileHashSha256 = fileHashSha256; return this; }
        public ClaimDocumentBuilder ocrStatus(String ocrStatus) { this.ocrStatus = ocrStatus; return this; }
        public ClaimDocumentBuilder ocrRawText(String ocrRawText) { this.ocrRawText = ocrRawText; return this; }
        public ClaimDocumentBuilder extractionConfidence(BigDecimal extractionConfidence) { this.extractionConfidence = extractionConfidence; return this; }
        public ClaimDocumentBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ClaimDocumentBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ClaimDocument build() {
            return new ClaimDocument(id, claimId, documentType, originalFilename, storedFilename, filePath, fileSize, contentType, fileHashSha256, ocrStatus, ocrRawText, extractionConfidence, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClaimId() { return claimId; }
    public void setClaimId(UUID claimId) { this.claimId = claimId; }
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getFileHashSha256() { return fileHashSha256; }
    public void setFileHashSha256(String fileHashSha256) { this.fileHashSha256 = fileHashSha256; }
    public String getOcrStatus() { return ocrStatus; }
    public void setOcrStatus(String ocrStatus) { this.ocrStatus = ocrStatus; }
    public String getOcrRawText() { return ocrRawText; }
    public void setOcrRawText(String ocrRawText) { this.ocrRawText = ocrRawText; }
    public BigDecimal getExtractionConfidence() { return extractionConfidence; }
    public void setExtractionConfidence(BigDecimal extractionConfidence) { this.extractionConfidence = extractionConfidence; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
