package com.jano.minecraftai.ai;

import com.jano.minecraftai.context.PlayerContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftAIPromptBehaviorTest {

    private String buildPrompt() {

        PlayerContext context =
                new PlayerContext();

        context.playerName =
                "Jano";

        return MinecraftAIPrompt.build(
                new AIRequest(
                        "¿De qué mod es este bloque?",
                        new byte[0],
                        context
                )
        );
    }

    @Test
    void distinguishesStructureFromBlockOrigin() {

        String prompt =
                buildPrompt();

        assertTrue(
                prompt.contains(
                        "NO significa que el bloque pertenezca a ese mod"
                )
        );

        assertTrue(
                prompt.contains(
                        "minecraft:cut_sandstone"
                )
        );

        assertTrue(
                prompt.contains(
                        "el bloque es vanilla de Minecraft"
                )
        );
    }

    @Test
    void requiresExactSourceForModOrigin() {

        String prompt =
                buildPrompt();

        assertTrue(
                prompt.contains(
                        "get_mod_origin"
                )
        );

        assertTrue(
                prompt.contains(
                        "Si el identificador empieza con:"
                )
        );

        assertTrue(
                prompt.contains(
                        "minecraft:"
                )
        );
    }

    @Test
    void encouragesDirectAnswers() {

        String prompt =
                buildPrompt();

        assertTrue(
                prompt.contains(
                        "Primero respondé exactamente lo que preguntó el jugador."
                )
        );

        assertTrue(
                prompt.contains(
                        "no termines cada respuesta con una pregunta"
                )
        );
    }

    @Test
    void definesAriscaLazyPersonality() {

        String prompt =
                buildPrompt();

        assertTrue(
                prompt.contains(
                        "algo arisca y bastante vaga"
                )
        );

        assertTrue(
                prompt.contains(
                        "Tu humor es seco."
                )
        );

        assertTrue(
                prompt.contains(
                        "No uses sarcasmo en todas las respuestas."
                )
        );

        assertTrue(
                prompt.contains(
                        "primero respondé correctamente"
                )
        );

        assertTrue(
                prompt.contains(
                        "No hagas roleplay constante."
                )
        );
    }

    @Test
    void toolsDoNotRequireConfirmation() {

        String prompt =
                buildPrompt();

        assertTrue(
                prompt.contains(
                        "No le preguntes al jugador si querés usarlas."
                )
        );

        assertTrue(
                prompt.contains(
                        "Simplemente usalas cuando sean necesarias."
                )
        );
    }
}