package com.tabseer.collabdocs.state;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentState {

    private String content;

    private Long version;

    public DocumentState(String content, Long version) {
        this.content = content;
        this.version = version;
    }
}