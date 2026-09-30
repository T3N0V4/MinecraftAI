package com.jano.minecraftai.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AIRequestConversationTest {

    @Test
    void acceptsConversationHistory() {

        AIRequest request =
                new AIRequest(
                        "Sí, por favor.",
                        new byte[0],
                        null,
                        """
                        USER: ¿Qué entidades tengo adelante?
                        ASSISTANT: Puedo revisarlo si querés.
                        """
                );

        assertTrue(
                request.conversationHistory.contains(
                        "¿Qué entidades tengo adelante?"
                )
        );

        assertTrue(
                request.conversationHistory.contains(
                        "Puedo revisarlo si querés."
                )
        );

        assertEquals(
                "Sí, por favor.",
                request.question
        );
    }

    @Test
    void oldConstructorStillWorks() {

        AIRequest request =
                new AIRequest(
                        "hola",
                        new byte[0],
                        null
                );

        assertEquals(
                "Sin conversación previa.",
                request.conversationHistory
        );
    }
}