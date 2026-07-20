package com.tabseer.collabdocs.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final Map<String, Set<String>> onlineUsers =
            new ConcurrentHashMap<>();

    public void join(String documentId, String username) {

        onlineUsers
                .computeIfAbsent(
                        documentId,
                        id -> ConcurrentHashMap.newKeySet())
                .add(username);
    }

    public void leave(String documentId, String username) {

        Set<String> users = onlineUsers.get(documentId);

        if (users == null) {
            return;
        }

        users.remove(username);

        if (users.isEmpty()) {
            onlineUsers.remove(documentId);
        }
    }

    public Set<String> getOnlineUsers(String documentId) {

        return onlineUsers.getOrDefault(
                documentId,
                Set.of()
        );
    }
}