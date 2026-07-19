package com.tabseer.collabdocs.controller;

import com.tabseer.collabdocs.dto.message.EditMessage;
import com.tabseer.collabdocs.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DocumentWebSocketController {

    private final CollaborationService collaborationService;

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/edit")
    public void editDocument(@Payload EditMessage message) {

        log.info("Received edit: {}", message);

        EditMessage updated = collaborationService.processEdit(message);

        messagingTemplate.convertAndSend(
                "/topic/document/" + updated.getDocumentId(),
                updated
        );
    }
}