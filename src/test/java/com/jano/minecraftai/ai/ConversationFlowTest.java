package com.jano.minecraftai.ai;

import com.jano.minecraftai.context.PlayerContext;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConversationFlowTest {

    @Test
    void secondQuestionReceivesPreviousTurn() {

        ConversationMemory memory =
                new ConversationMemory();

        UUID playerId =
                UUID.randomUUID();

        PlayerContext context =
                new PlayerContext();

        context.playerName =
                "Jano";

        /*
         * Primera conversación.
         */
        String firstQuestion =
                "¿Qué entidades tengo adelante?";

        String firstAnswer =
                "Puedo revisar las entidades visibles si querés.";

        memory.addUser(
                playerId,
                firstQuestion
        );

        memory.addAssistant(
                playerId,
                firstAnswer
        );

        /*
         * Segunda pregunta.
         */
        String secondQuestion =
                "Sí, por favor.";

        String history =
                memory.getHistoryText(
                        playerId
                );

        AIRequest secondRequest =
                new AIRequest(
                        secondQuestion,
                        new byte[0],
                        context,
                        history
                );

        String prompt =
                MinecraftAIPrompt.build(
                        secondRequest
                );

        assertTrue(
                prompt.contains(
                        firstQuestion
                )
        );

        assertTrue(
                prompt.contains(
                        firstAnswer
                )
        );

        assertTrue(
                prompt.contains(
                        secondQuestion
                )
        );
    }

    @Test
    void currentQuestionIsNotDuplicatedInHistory() {

        ConversationMemory memory =
                new ConversationMemory();

        UUID playerId =
                UUID.randomUUID();

        memory.addUser(
                playerId,
                "¿Qué bloque estoy mirando?"
        );

        memory.addAssistant(
                playerId,
                "Estás mirando piedra."
        );

        String history =
                memory.getHistoryText(
                        playerId
                );

        String currentQuestion =
                "¿Y de qué mod es?";

        assertFalse(
                history.contains(
                        currentQuestion
                )
        );

        AIRequest request =
                new AIRequest(
                        currentQuestion,
                        new byte[0],
                        new PlayerContext(),
                        history
                );

        String prompt =
                MinecraftAIPrompt.build(
                        request
                );

        assertTrue(
                prompt.contains(
                        "¿Qué bloque estoy mirando?"
                )
        );

        assertTrue(
                prompt.contains(
                        "Estás mirando piedra."
                )
        );

        assertTrue(
                prompt.contains(
                        currentQuestion
                )
        );
    }

    @Test
    void playersDoNotShareConversation() {

        ConversationMemory memory =
                new ConversationMemory();

        UUID jano =
                UUID.randomUUID();

        UUID otroJugador =
                UUID.randomUUID();

        memory.addUser(
                jano,
                "Estoy buscando una estructura."
        );

        memory.addAssistant(
                jano,
                "Puedo ayudarte a encontrarla."
        );

        memory.addUser(
                otroJugador,
                "¿Qué tengo en el inventario?"
        );

        String janoHistory =
                memory.getHistoryText(
                        jano
                );

        String otherHistory =
                memory.getHistoryText(
                        otroJugador
                );

        assertTrue(
                janoHistory.contains(
                        "Estoy buscando una estructura."
                )
        );

        assertFalse(
                janoHistory.contains(
                        "¿Qué tengo en el inventario?"
                )
        );

        assertTrue(
                otherHistory.contains(
                        "¿Qué tengo en el inventario?"
                )
        );

        assertFalse(
                otherHistory.contains(
                        "Estoy buscando una estructura."
                )
        );
    }
}