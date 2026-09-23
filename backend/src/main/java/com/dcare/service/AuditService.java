package com.dcare.service;

import com.dcare.model.AuditLog;
import com.dcare.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(UUID userId, String userEmail, String action, String entityType, String entityId, String description, String metadataJson) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .userEmail(userEmail != null ? userEmail : "system@dcare.local")
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .metadataJson(metadataJson)
                    .ipAddress("127.0.0.1")
                    .build();
            auditLogRepository.save(auditLog);
            log.info("[AUDIT] User: {} | Action: {} | Entity: {}:{} | Desc: {}", userEmail, action, entityType, entityId, description);
        } catch (Exception e) {
            log.error("Failed to write audit log", e);
        }
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getLogsForEntity(String entityId) {
        return auditLogRepository.findByEntityIdOrderByCreatedAtDesc(entityId);
    }
}
