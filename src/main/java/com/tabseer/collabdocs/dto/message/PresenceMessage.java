package com.tabseer.collabdocs.dto.message;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class PresenceMessage {

    private String documentId;

    private Set<String> users;
}