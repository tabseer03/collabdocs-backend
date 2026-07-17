package com.tabseer.collabdocs.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDocumentRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}