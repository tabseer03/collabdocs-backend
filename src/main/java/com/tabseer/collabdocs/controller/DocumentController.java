package com.tabseer.collabdocs.controller;

import com.tabseer.collabdocs.dto.request.CreateDocumentRequest;
import com.tabseer.collabdocs.dto.request.UpdateDocumentRequest;
import com.tabseer.collabdocs.dto.response.DocumentResponse;
import com.tabseer.collabdocs.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public DocumentResponse getDocument(@PathVariable String id) {

        return documentService.getDocumentById(id);
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
}