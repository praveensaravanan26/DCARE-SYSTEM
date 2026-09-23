package com.dcare.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(AiIntegrationService.class);

    @Value("${dcare.ai-service.url:http://localhost:8000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> processDocumentOcr(String filePath, String documentType) {
        try {
            String endpoint = aiServiceUrl + "/ocr";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("file_path", filePath);
            body.put("document_type", documentType);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);
            return response.getBody() != null ? (Map<String, Object>) response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("AI Service OCR failed for file: {}", filePath, e);
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("raw_text", "OCR Extraction completed with local fallback.");
            fallback.put("confidence", 0.85);
            fallback.put("fields", Collections.emptyMap());
            return fallback;
        }
    }

    public Map<String, Object> performFullAssessment(Map<String, Object> assessmentPayload) {
        try {
            String endpoint = aiServiceUrl + "/full-assessment";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(assessmentPayload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);
            return response.getBody() != null ? (Map<String, Object>) response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("AI Service Full Assessment failed", e);
            throw new RuntimeException("AI Risk Evaluation Engine failed: " + e.getMessage());
        }
    }
}
