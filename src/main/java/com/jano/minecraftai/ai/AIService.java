package com.jano.minecraftai.ai;

import com.jano.minecraftai.tools.ToolManager;
import com.jano.minecraftai.tools.ToolResult;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Map;
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
                            ServerPlayerEntity player,
                            Map<String, String> arguments
                    ) {

                        return ToolManager.execute(
                                name,
                                player,
                                arguments
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


    public AIResponse respondWithTools(
            AIRequest request,
            ServerPlayerEntity player
    ) {

        Set<String> usedToolCalls =
                new HashSet<>();

        long totalStart =
                System.currentTimeMillis();

        /*
         * FAST PATH
         *
         * Para preguntas muy claras no necesitamos gastar
         * una llamada a Gemini preguntándole qué tool usar.
         *
         * Ejecutamos la tool primero y Gemini recibe
         * directamente el resultado real.
         */
        String fastTool =
                ToolFastPathRouter.resolve(
                        request.question
                );

        if (
                fastTool != null
                && toolGateway.exists(
                        fastTool
                )
        ) {

            System.out.println(
                    "[MinecraftAI] FAST PATH -> "
                            + fastTool
            );

            long fastToolStart =
                    System.currentTimeMillis();

            ToolResult fastResult =
                    toolGateway.execute(
                            fastTool,
                            player,
                            Map.of()
                    );

            if (
                    fastResult != null
            ) {

                request.addToolResult(
                        fastTool,
                        fastResult.content
                );

                usedToolCalls.add(
                        fastTool
                                + "::{}"
                );
            }

            System.out.println(
                    "[MinecraftAI] FAST PATH tool terminada en "
                            + (
                                    System.currentTimeMillis()
                                            - fastToolStart
                            )
                            + " ms"
            );
        }

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

            if (
                    "NEED_VISION".equalsIgnoreCase(
                            modelResponse.content.trim()
                    )
            ) {

                System.out.println(
                        "[MinecraftAI][Route] semantic -> VISION"
                );

                return new AIResponse(
                        true,
                        "vision",
                        modelResponse.model,
                        "NEED_VISION",
                        System.currentTimeMillis()
                                - totalStart
                );
            }

            ToolCall toolCall =
                    ToolCallParser.parse(
                            modelResponse.content
                    );

            if (toolCall == null) {

                long totalElapsed =
                        System.currentTimeMillis()
                                - totalStart;

                System.out.println(
                        "[MinecraftAI] TOTAL request: "
                                + totalElapsed
                                + " ms"
                );

                return new AIResponse(
                        true,
                        modelResponse.provider,
                        modelResponse.model,
                        modelResponse.content,
                        System.currentTimeMillis()
                                - totalStart
                );
            }

            String signature =
                    toolCall.name
                            + "::"
                            + toolCall.arguments;

            if (
                    usedToolCalls.contains(
                            signature
                    )
            ) {

                return AIResponse.failure(
                        modelResponse.provider,
                        modelResponse.model,
                        "El modelo intentó ejecutar dos veces la misma tool con los mismos argumentos: "
                                + toolCall.name,
                        System.currentTimeMillis()
                                - totalStart
                );
            }

            if (
                    !toolGateway.exists(
                            toolCall.name
                    )
            ) {

                return AIResponse.failure(
                        modelResponse.provider,
                        modelResponse.model,
                        "El modelo pidió una tool inexistente: "
                                + toolCall.name,
                        System.currentTimeMillis()
                                - totalStart
                );
            }

            usedToolCalls.add(
                    signature
            );

            System.out.println(
                    "[MinecraftAI] Ejecutando tool: "
                            + toolCall.name
                            + " "
                            + toolCall.arguments
            );

            long toolStart =
                    System.currentTimeMillis();

            ToolResult toolResult =
                    toolGateway.execute(
                            toolCall.name,
                            player,
                            toolCall.arguments
                    );

            System.out.println(
                    "[MinecraftAI] Tool "
                            + toolCall.name
                            + " terminada en "
                            + (
                                    System.currentTimeMillis()
                                    - toolStart
                            )
                            + " ms"
            );

            request.addToolResult(
                    toolCall.name,
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