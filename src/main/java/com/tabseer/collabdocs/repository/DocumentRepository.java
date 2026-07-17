package com.tabseer.collabdocs.repository;

import com.tabseer.collabdocs.entity.Document;
import com.tabseer.collabdocs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByOwner(User owner);

}