package com.tabseer.collabdocs.service;

import com.tabseer.collabdocs.dto.request.CreateDocumentRequest;
import com.tabseer.collabdocs.dto.request.UpdateDocumentRequest;
import com.tabseer.collabdocs.dto.response.DocumentResponse;
import com.tabseer.collabdocs.entity.Document;
import com.tabseer.collabdocs.entity.User;
import com.tabseer.collabdocs.exception.ResourceNotFoundException;
import com.tabseer.collabdocs.repository.DocumentRepository;
import com.tabseer.collabdocs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentResponse createDocument(CreateDocumentRequest request,
                                           Authentication authentication) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Document document = Document.builder()
                .title(request.getTitle())
                .content("")
                .owner(owner)
                .version(1L)
                .build();

        Document savedDocument = documentRepository.save(document);

        return mapToResponse(savedDocument);
    }

    public DocumentResponse getDocumentById(String id) {

        Document document = documentRepository.findById(java.util.UUID.fromString(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        return mapToResponse(document);
    }

    public List<DocumentResponse> getMyDocuments(Authentication authentication) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return documentRepository.findByOwner(owner)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private DocumentResponse mapToResponse(Document document) {

        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .version(document.getVersion())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    public DocumentResponse updateDocument(String id,
                                           UpdateDocumentRequest request,
                                           Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Document document = documentRepository.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        if (!document.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        document.setTitle(request.getTitle());
        document.setContent(request.getContent());

        document.setVersion(document.getVersion() + 1);

        Document updated = documentRepository.save(document);

        return mapToResponse(updated);
    }

    public void deleteDocument(String id,
                               Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Document document = documentRepository.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        if (!document.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        documentRepository.delete(document);
    }
}