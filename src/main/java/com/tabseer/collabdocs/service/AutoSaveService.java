package com.tabseer.collabdocs.service;

import com.tabseer.collabdocs.entity.Document;
import com.tabseer.collabdocs.exception.ResourceNotFoundException;
import com.tabseer.collabdocs.repository.DocumentRepository;
import com.tabseer.collabdocs.state.DocumentState;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
/**
 * Periodically persists inactive documents from memory to PostgreSQL.
 * A document is saved only if it has not been edited
 * for at least two seconds.
 */
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoSaveService {

    private final CollaborationService collaborationService;
    private final DocumentRepository documentRepository;

    /**
     * Runs every two seconds and saves documents whose
     * edit activity has paused.
     */
    @Scheduled(fixedDelay = 2000)
    public void autoSave() {

        Map<String, DocumentState> documents =
                collaborationService.getActiveDocuments();

        Map<String, Long> lastEdit =
                collaborationService.getLastEditTime();

        long now = System.currentTimeMillis();

        for (Map.Entry<String, DocumentState> entry : documents.entrySet()) {

            String id = entry.getKey();
            DocumentState state = entry.getValue();

            Long last = lastEdit.get(id);

            if (last == null)
                continue;

            if (now - last < 2000)
                continue;

            Document document =
                    documentRepository.findById(UUID.fromString(id))
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Document not found"));

            if (document.getVersion().equals(state.getVersion()))
                continue;

            document.setContent(state.getContent());
            document.setVersion(state.getVersion());

            documentRepository.save(document);

            collaborationService.removeActiveDocument(id);

            log.info("Auto-saved document {}", id);
        }

    }

}