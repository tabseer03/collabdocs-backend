package com.tabseer.collabdocs.controller;

import com.tabseer.collabdocs.dto.message.CursorMessage;
import com.tabseer.collabdocs.dto.message.EditMessage;
import com.tabseer.collabdocs.dto.message.PresenceMessage;
import com.tabseer.collabdocs.dto.message.PresenceRequest;
import com.tabseer.collabdocs.service.CollaborationService;
import com.tabseer.collabdocs.service.PresenceService;
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

    private final PresenceService presenceService;

    @MessageMapping("/edit")
    public void editDocument(@Payload EditMessage message) {

        log.info("Received edit: {}", message);

        EditMessage updated = collaborationService.processEdit(message);

        messagingTemplate.convertAndSend(
                "/topic/document/" + updated.getDocumentId(),
                updated
        );
    }

    @MessageMapping("/presence/join")
    public void join(@Payload PresenceRequest request) {

        presenceService.join(
                request.getDocumentId(),
                request.getUsername()
        );

        messagingTemplate.convertAndSend(
                "/topic/presence/" + request.getDocumentId(),
                PresenceMessage.builder()
                        .documentId(request.getDocumentId())
                        .users(presenceService.getOnlineUsers(
                                request.getDocumentId()))
                        .build()
        );
    }

    @MessageMapping("/presence/leave")
    public void leave(@Payload PresenceRequest request) {

        presenceService.leave(
                request.getDocumentId(),
                request.getUsername()
        );

        messagingTemplate.convertAndSend(
                "/topic/presence/" + request.getDocumentId(),
                PresenceMessage.builder()
                        .documentId(request.getDocumentId())
                        .users(presenceService.getOnlineUsers(
                                request.getDocumentId()))
                        .build()
        );
    }

    @MessageMapping("/cursor")
    public void cursor(@Payload CursorMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/cursor/" + message.getDocumentId(),
                message
        );
    }
}