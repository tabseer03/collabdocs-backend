package com.tabseer.collabdocs.dto.request;

import com.tabseer.collabdocs.entity.PermissionRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareDocumentRequest {

    @Email
    private String email;

    @NotNull
    private PermissionRole role;
}