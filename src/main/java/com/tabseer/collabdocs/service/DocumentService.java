package com.tabseer.collabdocs.service;

import com.tabseer.collabdocs.dto.request.CreateDocumentRequest;
import com.tabseer.collabdocs.dto.request.ShareDocumentRequest;
import com.tabseer.collabdocs.dto.request.UpdateDocumentRequest;
import com.tabseer.collabdocs.dto.request.UpdatePermissionRequest;
import com.tabseer.collabdocs.dto.response.DocumentPermissionResponse;
import com.tabseer.collabdocs.dto.response.DocumentResponse;
import com.tabseer.collabdocs.entity.Document;
import com.tabseer.collabdocs.entity.User;
import com.tabseer.collabdocs.exception.ResourceNotFoundException;
import com.tabseer.collabdocs.repository.DocumentRepository;
import com.tabseer.collabdocs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.tabseer.collabdocs.entity.DocumentPermission;
import com.tabseer.collabdocs.entity.PermissionRole;
import com.tabseer.collabdocs.repository.DocumentPermissionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentPermissionRepository documentPermissionRepository;

    public DocumentResponse createDocument(CreateDocumentRequest request,
                                           Authentication authentication) {

        User owner = getCurrentUser(authentication);


        Document document = Document.builder()
                .title(request.getTitle())
                .content("")
                .owner(owner)
                .version(1L)
                .build();

        Document savedDocument = documentRepository.save(document);

        DocumentPermission permission = DocumentPermission.builder()
                .document(savedDocument)
                .user(owner)
                .role(PermissionRole.OWNER)
                .build();

        documentPermissionRepository.save(permission);

        return mapToResponse(savedDocument);
    }

    private DocumentPermission getPermission(Document document, User user) {

        return documentPermissionRepository
                .findByDocumentAndUser(document, user)
                .orElseThrow(() ->
                        new RuntimeException("Access denied"));
    }

    public DocumentResponse getDocumentById(String id,
                                            Authentication authentication) {

        User user = getCurrentUser(authentication);

        Document document = documentRepository.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        getPermission(document, user);

        return mapToResponse(document);
    }

    public List<DocumentResponse> getMyDocuments(Authentication authentication) {

        User user = getCurrentUser(authentication);

        return documentPermissionRepository.findByUser(user)
                .stream()
                .map(DocumentPermission::getDocument)
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


        User user = getCurrentUser(authentication);

        Document document = documentRepository.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        DocumentPermission permission = getPermission(document, user);

        if (permission.getRole() == PermissionRole.VIEWER) {
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

        User user = getCurrentUser(authentication);

        Document document = documentRepository.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        DocumentPermission permission = getPermission(document, user);

        if (permission.getRole() != PermissionRole.OWNER) {
            throw new RuntimeException("Only the owner can delete this document");
        }

        documentRepository.delete(document);
    }

    public void shareDocument(String documentId,
                              ShareDocumentRequest request,
                              Authentication authentication) {

        User owner = getCurrentUser(authentication);


        Document document = documentRepository.findById(UUID.fromString(documentId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        DocumentPermission ownerPermission =
                getPermission(document, owner);

        if (ownerPermission.getRole() != PermissionRole.OWNER) {
            throw new RuntimeException("Only the owner can share this document");
        }

        User targetUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (targetUser.getId().equals(owner.getId())) {
            throw new RuntimeException("You already own this document");
        }

        DocumentPermission permission =
                documentPermissionRepository
                        .findByDocumentAndUser(document, targetUser)
                        .orElse(null);

        if (permission != null) {

            permission.setRole(request.getRole());

        }else {

            permission = DocumentPermission.builder()
                    .document(document)
                    .user(targetUser)
                    .role(request.getRole())
                    .build();

        }

        documentPermissionRepository.save(permission);


    }

    private User getCurrentUser(Authentication authentication) {

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    public List<DocumentPermissionResponse> getDocumentPermissions(
            String documentId,
            Authentication authentication) {

        User owner = getCurrentUser(authentication);

        Document document = documentRepository.findById(UUID.fromString(documentId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        DocumentPermission permission = getPermission(document, owner);

        if (permission.getRole() != PermissionRole.OWNER) {
            throw new RuntimeException("Only the owner can view permissions");
        }

        return documentPermissionRepository.findByDocument(document)
                .stream()
                .map(p -> DocumentPermissionResponse.builder()
                        .username(p.getUser().getUsername())
                        .email(p.getUser().getEmail())
                        .role(p.getRole())
                        .build())
                .toList();
    }

    public void updatePermission(String documentId,
                                 String userId,
                                 UpdatePermissionRequest request,
                                 Authentication authentication) {

        User owner = getCurrentUser(authentication);

        Document document = documentRepository.findById(UUID.fromString(documentId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        DocumentPermission ownerPermission = getPermission(document, owner);

        if (ownerPermission.getRole() != PermissionRole.OWNER) {
            throw new RuntimeException("Only the owner can update permissions");
        }

        User targetUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        DocumentPermission permission = documentPermissionRepository
                .findByDocumentAndUser(document, targetUser)
                .orElseThrow(() ->
                        new RuntimeException("User does not have access to this document"));

        if (permission.getRole() == PermissionRole.OWNER) {
            throw new RuntimeException("Owner permission cannot be changed");
        }

        permission.setRole(request.getRole());

        documentPermissionRepository.save(permission);
    }

    public void removePermission(String documentId,
                                 String userId,
                                 Authentication authentication) {

        User owner = getCurrentUser(authentication);

        Document document = documentRepository.findById(UUID.fromString(documentId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        DocumentPermission ownerPermission = getPermission(document, owner);

        if (ownerPermission.getRole() != PermissionRole.OWNER) {
            throw new RuntimeException("Only the owner can remove permissions");
        }

        User targetUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        DocumentPermission permission = documentPermissionRepository
                .findByDocumentAndUser(document, targetUser)
                .orElseThrow(() ->
                        new RuntimeException("User does not have access"));

        if (permission.getRole() == PermissionRole.OWNER) {
            throw new RuntimeException("Owner permission cannot be removed");
        }

        documentPermissionRepository.delete(permission);
    }

}