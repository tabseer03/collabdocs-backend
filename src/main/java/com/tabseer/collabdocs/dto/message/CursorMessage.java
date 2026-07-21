package com.tabseer.collabdocs.dto.message;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursorMessage {

    private String documentId;

    private String username;

    private int from;

    private int to;
}