package com.dcare.controller;

import com.dcare.model.ClaimDocument;
import com.dcare.model.DocumentType;
import com.dcare.model.User;
import com.dcare.repository.ClaimDocumentRepository;
import com.dcare.repository.UserRepository;
import com.dcare.service.DocumentService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;
    private final ClaimDocumentRepository claimDocumentRepository;
    private final UserRepository userRepository;

    public DocumentController(DocumentService documentService, ClaimDocumentRepository claimDocumentRepository, UserRepository userRepository) {
        this.documentService = documentService;
        this.claimDocumentRepository = claimDocumentRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @PostMapping("/claims/{id}/documents")
    public ResponseEntity<ClaimDocument> uploadDocument(@PathVariable UUID id,
                                                        @RequestParam("documentType") DocumentType documentType,
                                                        @RequestParam("file") MultipartFile file,
                                                        @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(documentService.uploadDocument(id, documentType, file, getUser(email)));
    }

    @GetMapping("/claims/{id}/documents")
    public ResponseEntity<List<ClaimDocument>> getDocumentsForClaim(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocumentsForClaim(id));
    }

    @GetMapping("/claims/{id}/extraction")
    public ResponseEntity<?> getExtractedFields(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getExtractedFieldsForClaim(id));
    }

    @PostMapping("/documents/{id}/process")
    public ResponseEntity<ClaimDocument> processDocument(@PathVariable UUID id,
                                                         @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(documentService.processDocument(id, getUser(email)));
    }

    @GetMapping("/documents/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID id) {
        ClaimDocument doc = claimDocumentRepository.findById(id).orElseThrow();
        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(resource);
    }
}
