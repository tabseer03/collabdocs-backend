package com.tabseer.collabdocs.repository;

import com.tabseer.collabdocs.entity.Document;
import com.tabseer.collabdocs.entity.DocumentPermission;
import com.tabseer.collabdocs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentPermissionRepository extends JpaRepository<DocumentPermission, UUID> {

    Optional<DocumentPermission> findByDocumentAndUser(Document document, User user);

    List<DocumentPermission> findByDocument(Document document);

    List<DocumentPermission> findByUser(User user);

    boolean existsByDocumentAndUser(Document document, User user);

}