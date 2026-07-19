package com.tabseer.collabdocs.dto.response;

import com.tabseer.collabdocs.entity.PermissionRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentPermissionResponse {

    private String username;

    private String email;

    private PermissionRole role;
}