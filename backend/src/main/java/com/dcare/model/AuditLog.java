package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String ipAddress = "127.0.0.1";

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public AuditLog() {}

    public AuditLog(UUID id, UUID userId, String userEmail, String action, String entityType, String entityId, String description, String ipAddress, String metadataJson, ZonedDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.ipAddress = ipAddress != null ? ipAddress : "127.0.0.1";
        this.metadataJson = metadataJson;
        this.createdAt = createdAt;
    }

    public static AuditLogBuilder builder() { return new AuditLogBuilder(); }

    public static class AuditLogBuilder {
        private UUID id;
        private UUID userId;
        private String userEmail;
        private String action;
        private String entityType;
        private String entityId;
        private String description;
        private String ipAddress = "127.0.0.1";
        private String metadataJson;
        private ZonedDateTime createdAt;

        public AuditLogBuilder id(UUID id) { this.id = id; return this; }
        public AuditLogBuilder userId(UUID userId) { this.userId = userId; return this; }
        public AuditLogBuilder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public AuditLogBuilder action(String action) { this.action = action; return this; }
        public AuditLogBuilder entityType(String entityType) { this.entityType = entityType; return this; }
        public AuditLogBuilder entityId(String entityId) { this.entityId = entityId; return this; }
        public AuditLogBuilder description(String description) { this.description = description; return this; }
        public AuditLogBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public AuditLogBuilder metadataJson(String metadataJson) { this.metadataJson = metadataJson; return this; }
        public AuditLogBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AuditLog build() {
            return new AuditLog(id, userId, userEmail, action, entityType, entityId, description, ipAddress, metadataJson, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
