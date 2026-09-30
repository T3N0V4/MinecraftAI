package com.jano.minecraftai.ai;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConversationMemoryTest {

    @Test
    void storesConversationInOrder() {

        ConversationMemory memory =
                new ConversationMemory();

        UUID player =
                UUID.randomUUID();

        memory.addUser(
                player,
                "¿Qué estructura tengo adelante?"
        );

        memory.addAssistant(
                player,
                "Estás en un templo del desierto."
        );

        memory.addUser(
                player,
                "Sí, por favor."
        );

        List<ConversationMemory.Message> messages =
                memory.getMessages(
                        player
                );

        assertEquals(
                3,
                messages.size()
        );

        assertEquals(
                "USER",
                messages.get(0).role()
        );

        assertEquals(
                "¿Qué estructura tengo adelante?",
                messages.get(0).text()
        );

        assertEquals(
                "ASSISTANT",
                messages.get(1).role()
        );

        assertEquals(
                "Sí, por favor.",
                messages.get(2).text()
        );
    }

    @Test
    void separatesPlayers() {

        ConversationMemory memory =
                new ConversationMemory();

        UUID playerA =
                UUID.randomUUID();

        UUID playerB =
                UUID.randomUUID();

        memory.addUser(
                playerA,
                "Jugador A"
        );

        memory.addUser(
                playerB,
                "Jugador B"
        );

        assertEquals(
                "Jugador A",
                memory
                        .getMessages(playerA)
                        .get(0)
                        .text()
        );

        assertEquals(
                "Jugador B",
                memory
                        .getMessages(playerB)
                        .get(0)
                        .text()
        );
    }

    @Test
    void limitsHistoryToTenMessages() {

        ConversationMemory memory =
                new ConversationMemory();

        UUID player =
                UUID.randomUUID();

        for (
                int i = 0;
                i < 15;
                i++
        ) {

            memory.addUser(
                    player,
                    "mensaje-" + i
            );
        }

        List<ConversationMemory.Message> messages =
                memory.getMessages(
                        player
                );

        assertEquals(
                10,
                messages.size()
        );

        assertEquals(
                "mensaje-5",
                messages.get(0).text()
        );

        assertEquals(
                "mensaje-14",
                messages.get(9).text()
        );
    }

    @Test
    void buildsReadableHistory() {

        ConversationMemory memory =
                new ConversationMemory();

        UUID player =
                UUID.randomUUID();

        memory.addUser(
                player,
                "¿Qué veo?"
        );

        memory.addAssistant(
                player,
                "Un templo."
        );

        String history =
                memory.getHistoryText(
                        player
                );

        assertTrue(
                history.contains(
                        "USER: ¿Qué veo?"
                )
        );

        assertTrue(
                history.contains(
                        "ASSISTANT: Un templo."
                )
        );
    }

    @Test
    void clearRemovesConversation() {

        ConversationMemory memory =
                new ConversationMemory();

        UUID player =
                UUID.randomUUID();

        memory.addUser(
                player,
                "hola"
        );

        memory.clear(
                player
        );

        assertTrue(
                memory
                        .getMessages(player)
                        .isEmpty()
        );
    }
}