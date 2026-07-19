package com.tabseer.collabdocs.dto.message;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EditMessage {

    private String documentId;
    private String content;
    private Long version;
}