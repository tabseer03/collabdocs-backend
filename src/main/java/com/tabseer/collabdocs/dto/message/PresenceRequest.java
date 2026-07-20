package com.tabseer.collabdocs.dto.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresenceRequest {

    private String documentId;

    private String username;
}