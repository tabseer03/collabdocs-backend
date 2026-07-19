package com.tabseer.collabdocs.controller;

import com.tabseer.collabdocs.dto.request.CreateDocumentRequest;
import com.tabseer.collabdocs.dto.request.ShareDocumentRequest;
import com.tabseer.collabdocs.dto.request.UpdateDocumentRequest;
import com.tabseer.collabdocs.dto.request.UpdatePermissionRequest;
import com.tabseer.collabdocs.dto.response.DocumentPermissionResponse;
import com.tabseer.collabdocs.dto.response.DocumentResponse;
import com.tabseer.collabdocs.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public DocumentResponse createDocument(
            @Valid @RequestBody CreateDocumentRequest request,
            Authentication authentication) {

        return documentService.createDocument(request, authentication);
    }

    @GetMapping("/{id}")
    public DocumentResponse getDocument(
            @PathVariable String id,
            Authentication authentication) {

        return documentService.getDocumentById(id, authentication);
    }

    @GetMapping
    public List<DocumentResponse> getMyDocuments(
            Authentication authentication) {

        return documentService.getMyDocuments(authentication);
    }

    @PutMapping("/{id}")
    public DocumentResponse updateDocument(
            @PathVariable String id,
            @Valid @RequestBody UpdateDocumentRequest request,
            Authentication authentication) {

        return documentService.updateDocument(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(
            @PathVariable String id,
            Authentication authentication) {

        documentService.deleteDocument(id, authentication);
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<String> shareDocument(
            @PathVariable String id,
            @Valid @RequestBody ShareDocumentRequest request,
            Authentication authentication) {

        documentService.shareDocument(id, request, authentication);

        return ResponseEntity.ok("Document shared successfully");
    }

    @GetMapping("/{id}/permissions")
    public List<DocumentPermissionResponse> getPermissions(
            @PathVariable String id,
            Authentication authentication) {

        return documentService.getDocumentPermissions(id, authentication);
    }

    @PatchMapping("/{documentId}/permissions/{userId}")
    public ResponseEntity<String> updatePermission(
            @PathVariable String documentId,
            @PathVariable String userId,
            @RequestBody @Valid UpdatePermissionRequest request,
            Authentication authentication) {

        documentService.updatePermission(
                documentId,
                userId,
                request,
                authentication);

        return ResponseEntity.ok("Permission updated successfully");
    }

    @DeleteMapping("/{documentId}/permissions/{userId}")
    public ResponseEntity<String> removePermission(
            @PathVariable String documentId,
            @PathVariable String userId,
            Authentication authentication) {

        documentService.removePermission(
                documentId,
                userId,
                authentication);

        return ResponseEntity.ok("Permission removed successfully");
    }
}