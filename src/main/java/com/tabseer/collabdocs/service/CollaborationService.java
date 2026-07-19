package com.tabseer.collabdocs.service;

import com.tabseer.collabdocs.dto.message.EditMessage;
import com.tabseer.collabdocs.entity.Document;
import com.tabseer.collabdocs.exception.ResourceNotFoundException;
import com.tabseer.collabdocs.repository.DocumentRepository;
import com.tabseer.collabdocs.state.DocumentState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maintains the in-memory state of documents currently being edited.
 *
 * Instead of writing every keystroke to PostgreSQL, edits are cached
 * in memory and periodically flushed by AutoSaveService.
 */
@Service
@RequiredArgsConstructor
public class CollaborationService {
    private final DocumentRepository documentRepository;
    private final Map<String, DocumentState> activeDocuments =
            new ConcurrentHashMap<>();
    private final Map<String, Long> lastEditTime =
            new ConcurrentHashMap<>();
    /**
     * Processes an incoming edit.
     * Loads the document into memory on the first edit,
     * updates the cached content and version,
     * then returns the latest state for broadcasting.
     */
    public EditMessage processEdit(EditMessage message) {

        DocumentState state = activeDocuments.computeIfAbsent(
                message.getDocumentId(),
                id -> {

                    Document document = documentRepository
                            .findById(UUID.fromString(id))
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Document not found"));

                    return new DocumentState(
                            document.getContent(),
                            document.getVersion()
                    );

                });

        state.setContent(message.getContent());

        state.setVersion(state.getVersion() + 1);

        lastEditTime.put(
                message.getDocumentId(),
                System.currentTimeMillis()
        );

        return EditMessage.builder()
                .documentId(message.getDocumentId())
                .content(state.getContent())
                .version(state.getVersion())
                .build();
    }

    public Map<String, DocumentState> getActiveDocuments() {
        return activeDocuments;
    }

    public Map<String, Long> getLastEditTime() {
        return lastEditTime;
    }

    public void removeActiveDocument(String documentId) {

        activeDocuments.remove(documentId);
        lastEditTime.remove(documentId);
    }
}