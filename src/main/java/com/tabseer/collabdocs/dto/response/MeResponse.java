package com.tabseer.collabdocs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class MeResponse {

    private UUID id;
    private String username;
    private String email;
}