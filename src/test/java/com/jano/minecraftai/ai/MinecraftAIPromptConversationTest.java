package com.jano.minecraftai.ai;

import com.jano.minecraftai.context.PlayerContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftAIPromptConversationTest {

    @Test
    void promptContainsPreviousConversationAndCurrentQuestion() {

        PlayerContext context =
                new PlayerContext();

        context.playerName =
                "Jano";

        context.biome =
                "minecraft:plains";

        String history =
                """
                USER: ¿Qué entidades tengo adelante?
                ASSISTANT: Puedo revisar las entidades visibles si querés.
                """;

        AIRequest request =
                new AIRequest(
                        "Sí, por favor.",
                        new byte[0],
                        context,
                        history
                );

        String prompt =
                MinecraftAIPrompt.build(
                        request
                );

        assertTrue(
                prompt.contains(
                        "USER: ¿Qué entidades tengo adelante?"
                )
        );

        assertTrue(
                prompt.contains(
                        "ASSISTANT: Puedo revisar las entidades visibles si querés."
                )
        );

        assertTrue(
                prompt.contains(
                        "Sí, por favor."
                )
        );
    }

    @Test
    void conversationAppearsBeforeCurrentQuestion() {

        PlayerContext context =
                new PlayerContext();

        String history =
                """
                USER: ¿Qué estructura tengo adelante?
                ASSISTANT: Puedo revisarla.
                """;

        AIRequest request =
                new AIRequest(
                        "Dale.",
                        new byte[0],
                        context,
                        history
                );

        String prompt =
                MinecraftAIPrompt.build(
                        request
                );

        int historyPosition =
                prompt.indexOf(
                        "USER: ¿Qué estructura tengo adelante?"
                );

        int currentQuestionPosition =
                prompt.indexOf(
                        "Dale."
                );

        assertTrue(
                historyPosition >= 0
        );

        assertTrue(
                currentQuestionPosition >= 0
        );

        assertTrue(
                historyPosition
                        < currentQuestionPosition
        );
    }

    @Test
    void noHistoryUsesDefaultMessage() {

        PlayerContext context =
                new PlayerContext();

        AIRequest request =
                new AIRequest(
                        "¿Qué bloque estoy viendo?",
                        new byte[0],
                        context
                );

        String prompt =
                MinecraftAIPrompt.build(
                        request
                );

        assertTrue(
                prompt.contains(
                        "Sin conversación previa."
                )
        );
    }
}