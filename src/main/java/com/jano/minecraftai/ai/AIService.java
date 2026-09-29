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

    private final ToolExecutionGateway toolGateway;

    public AIService(
            AIModelRouter router
    ) {

        this(
                router,
                new ToolExecutionGateway() {

                    @Override
                    public boolean exists(
                            String name
                    ) {

                        return ToolManager
                                .getRegistry()
                                .get(name)
                                != null;
                    }

                    @Override
                    public ToolResult execute(
                            String name,
                            ServerPlayerEntity player
                    ) {

                        return ToolManager.execute(
                                name,
                                player
                        );
                    }
                }
        );
    }

    public AIService(
            AIModelRouter router,
            ToolExecutionGateway toolGateway
    ) {

        this.router =
                router;

        this.toolGateway =
                toolGateway;
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

            System.out.println(
                    "[MinecraftAI] Llamada IA #"
                            + (iteration + 1)
            );

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
             * Si no pidió tool,
             * es la respuesta final.
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
             * Evitar loops.
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
             * Comprobar existencia.
             */
            if (!toolGateway.exists(toolName)) {

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

            long toolStart =
                    System.currentTimeMillis();

            ToolResult toolResult =
                    toolGateway.execute(
                            toolName,
                            player
                    );

            System.out.println(
                    "[MinecraftAI] Tool "
                            + toolName
                            + " terminada en "
                            + (
                                    System.currentTimeMillis()
                                    - toolStart
                            )
                            + " ms"
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