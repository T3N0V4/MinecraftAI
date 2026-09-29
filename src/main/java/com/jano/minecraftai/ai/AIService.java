package com.jano.minecraftai.ai;

import com.jano.minecraftai.tools.ToolManager;
import com.jano.minecraftai.tools.ToolResult;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

public class AIService {

    private static final int MAX_TOOL_CALLS =
            3;

    private final AIModelRouter router;

    public AIService(
            AIModelRouter router
    ) {

        this.router =
                router;
    }

    public AIResponse respond(
            AIRequest request
    ) {

        return router.respond(
                request
        );
    }

    public AIResponse respondWithTools(
            AIRequest request,
            ServerPlayerEntity player
    ) {

        Set<String> usedTools =
                new HashSet<>();

        long totalStart =
                System.currentTimeMillis();

        for (
                int iteration = 0;
                iteration <= MAX_TOOL_CALLS;
                iteration++
        ) {

            AIResponse modelResponse =
                    router.respond(
                            request
                    );

            if (
                    modelResponse == null
                    || !modelResponse.success
            ) {

                return modelResponse;
            }

            String toolName =
                    ToolCallParser.parseToolName(
                            modelResponse.content
                    );

            /*
             * No pidió tool:
             * ya tenemos la respuesta final.
             */
            if (toolName == null) {

                return new AIResponse(
                        true,
                        modelResponse.provider,
                        modelResponse.model,
                        modelResponse.content,
                        System.currentTimeMillis()
                                - totalStart
                );
            }

            /*
             * Evitamos loops del modelo.
             */
            if (usedTools.contains(toolName)) {

                return AIResponse.failure(
                        modelResponse.provider,
                        modelResponse.model,
                        "El modelo intentó ejecutar dos veces la misma tool: "
                                + toolName,
                        System.currentTimeMillis()
                                - totalStart
                );
            }

            /*
             * Verificamos que la tool exista.
             */
            if (
                    ToolManager
                            .getRegistry()
                            .get(toolName)
                    == null
            ) {

                return AIResponse.failure(
                        modelResponse.provider,
                        modelResponse.model,
                        "El modelo pidió una tool inexistente: "
                                + toolName,
                        System.currentTimeMillis()
                                - totalStart
                );
            }

            usedTools.add(
                    toolName
            );

            System.out.println(
                    "[MinecraftAI] Ejecutando tool: "
                            + toolName
            );

            ToolResult toolResult =
                    ToolManager.execute(
                            toolName,
                            player
                    );

            request.addToolResult(
                    toolName,
                    toolResult.content
            );
        }

        return AIResponse.failure(
                "router",
                "tool-loop",
                "Se alcanzó el máximo de tools para una sola pregunta.",
                System.currentTimeMillis()
                        - totalStart
        );
    }
}