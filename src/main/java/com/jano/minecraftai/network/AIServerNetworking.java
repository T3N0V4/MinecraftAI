package com.jano.minecraftai.network;

import com.jano.minecraftai.ai.AIModelRouter;
import com.jano.minecraftai.ai.AIRequest;
import com.jano.minecraftai.ai.AIResponse;
import com.jano.minecraftai.ai.AIService;
import com.jano.minecraftai.ai.providers.DebugProvider;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.text.Text;

public class AIServerNetworking {

    private static final int MAX_IMAGE_SIZE =
            1_000_000;

    private static final AIService aiService;

    static {

        AIModelRouter router =
                new AIModelRouter();

        router.addProvider(
                new DebugProvider()
        );

        aiService =
                new AIService(router);
    }

    public static void register() {

        ServerPlayNetworking.registerGlobalReceiver(
                NetworkConstants.ASK_AI,
                (
                        server,
                        player,
                        handler,
                        buffer,
                        responseSender
                ) -> {

                    String pregunta =
                            buffer.readString(2048);

                    byte[] imagen =
                            buffer.readByteArray(
                                    MAX_IMAGE_SIZE
                            );

                    server.execute(() -> {

                        PlayerContext context =
                                PlayerContextService
                                        .getContext(player);

                        AIRequest request =
                                new AIRequest(
                                        pregunta,
                                        imagen,
                                        context
                                );

                        AIResponse response =
                                aiService.respond(request);

                        player.sendMessage(
                                Text.literal(
                                        "[AI]\n"
                                                + response.content
                                                + "\n\nModelo: "
                                                + response.provider
                                                + " / "
                                                + response.model
                                                + "\nTiempo: "
                                                + response.durationMs
                                                + " ms"
                                ),
                                false
                        );
                    });
                }
        );
    }

    private AIServerNetworking() {
    }
}
