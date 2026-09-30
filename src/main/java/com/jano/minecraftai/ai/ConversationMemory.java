package com.jano.minecraftai.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConversationMemory {

    private static final int MAX_MESSAGES =
            10;

    private final Map<UUID, Deque<Message>> conversations =
            new ConcurrentHashMap<>();

    public void addUser(
            UUID playerId,
            String text
    ) {

        add(
                playerId,
                "USER",
                text
        );
    }

    public void addAssistant(
            UUID playerId,
            String text
    ) {

        add(
                playerId,
                "ASSISTANT",
                text
        );
    }

    private void add(
            UUID playerId,
            String role,
            String text
    ) {

        if (
                playerId == null
                || text == null
                || text.isBlank()
        ) {
            return;
        }

        Deque<Message> conversation =
                conversations.computeIfAbsent(
                        playerId,
                        id -> new ArrayDeque<>()
                );

        synchronized (conversation) {

            conversation.addLast(
                    new Message(
                            role,
                            text.trim()
                    )
            );

            while (
                    conversation.size()
                    > MAX_MESSAGES
            ) {

                conversation.removeFirst();
            }
        }
    }

    public List<Message> getMessages(
            UUID playerId
    ) {

        Deque<Message> conversation =
                conversations.get(
                        playerId
                );

        if (
                conversation == null
        ) {
            return List.of();
        }

        synchronized (conversation) {

            return new ArrayList<>(
                    conversation
            );
        }
    }

    public String getHistoryText(
            UUID playerId
    ) {

        List<Message> messages =
                getMessages(
                        playerId
                );

        if (
                messages.isEmpty()
        ) {
            return "Sin conversación previa.";
        }

        StringBuilder result =
                new StringBuilder();

        for (
                Message message :
                messages
        ) {

            result.append(
                    message.role()
            );

            result.append(
                    ": "
            );

            result.append(
                    message.text()
            );

            result.append(
                    "\n"
            );
        }

        return result
                .toString()
                .trim();
    }

    public void clear(
            UUID playerId
    ) {

        conversations.remove(
                playerId
        );
    }

    public record Message(
            String role,
            String text
    ) {
    }
}