package com.tabseer.collabdocs.dto.response;

import com.tabseer.collabdocs.entity.PermissionRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private UUID id;

    private String title;

    private String content;

    private Long version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private PermissionRole permission;
}