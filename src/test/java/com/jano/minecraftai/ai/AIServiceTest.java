package com.jano.minecraftai.ai;

import com.jano.minecraftai.tools.ToolResult;

import net.minecraft.server.network.ServerPlayerEntity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AIServiceTest {

    @Test
    void toolLoopWorksWithoutMinecraftServer() {

        AIModelRouter router =
                new AIModelRouter();

        MockProvider provider =
                new MockProvider();

        router.addProvider(
                provider
        );

        ToolExecutionGateway fakeTools =
                new ToolExecutionGateway() {

                    @Override
                    public boolean exists(
                            String name
                    ) {

                        return "get_visible_entities"
                                .equals(name);
                    }

                    @Override
                    public ToolResult execute(
                            String name,
                            ServerPlayerEntity player
                    ) {

                        return ToolResult.success(
                                """
                                {
                                  "entities": [
                                    {"id":"minecraft:panda","distance":4.2},
                                    {"id":"minecraft:panda","distance":7.5},
                                    {"id":"minecraft:parrot","distance":6.0},
                                    {"id":"minecraft:phantom","distance":12.4}
                                  ]
                                }
                                """
                        );
                    }
                };

        AIService service =
                new AIService(
                        router,
                        fakeTools
                );

        AIRequest request =
                new AIRequest(
                        "¿Qué criaturas puedo ver?",
                        new byte[0],
                        null
                );

        AIResponse response =
                service.respondWithTools(
                        request,
                        null
                );

        assertTrue(
                response.success
        );

        assertEquals(
                2,
                provider.calls
        );

        assertEquals(
                1,
                request.toolResults.size()
        );

        assertTrue(
                request.toolResults
                        .get(0)
                        .contains(
                                "minecraft:panda"
                        )
        );

        assertTrue(
                response.content
                        .contains(
                                "2 pandas"
                        )
        );

        assertTrue(
                response.content
                        .contains(
                                "loro"
                        )
        );

        assertTrue(
                response.content
                        .contains(
                                "phantom"
                        )
        );
    }


    @Test
    void duplicateToolCallIsStopped() {

        AIModelRouter router =
                new AIModelRouter();

        router.addProvider(
                new RepeatingToolProvider()
        );

        ToolExecutionGateway fakeTools =
                new ToolExecutionGateway() {

                    @Override
                    public boolean exists(
                            String name
                    ) {

                        return true;
                    }

                    @Override
                    public ToolResult execute(
                            String name,
                            ServerPlayerEntity player
                    ) {

                        return ToolResult.success(
                                "{\"entities\":[]}"
                        );
                    }
                };

        AIService service =
                new AIService(
                        router,
                        fakeTools
                );

        AIRequest request =
                new AIRequest(
                        "test",
                        new byte[0],
                        null
                );

        AIResponse response =
                service.respondWithTools(
                        request,
                        null
                );

        assertFalse(
                response.success
        );

        assertTrue(
                response.content
                        .contains(
                                "dos veces"
                        )
        );
    }


    private static class MockProvider
            implements AIProvider {

        private int calls = 0;

        @Override
        public String getProviderName() {
            return "mock";
        }

        @Override
        public String getModelName() {
            return "mock-model";
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public AIResponse respond(
                AIRequest request
        ) {

            calls++;

            if (calls == 1) {

                return new AIResponse(
                        true,
                        "mock",
                        "mock-model",
                        "{\"tool\":\"get_visible_entities\"}",
                        1
                );
            }

            return new AIResponse(
                    true,
                    "mock",
                    "mock-model",
                    "Veo 2 pandas, un loro y un phantom.",
                    1
            );
        }
    }


    private static class RepeatingToolProvider
            implements AIProvider {

        @Override
        public String getProviderName() {
            return "mock";
        }

        @Override
        public String getModelName() {
            return "mock-repeat";
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public AIResponse respond(
                AIRequest request
        ) {

            return new AIResponse(
                    true,
                    "mock",
                    "mock-repeat",
                    "{\"tool\":\"get_visible_entities\"}",
                    1
            );
        }
    }
}