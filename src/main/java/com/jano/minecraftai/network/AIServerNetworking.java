package com.jano.minecraftai.network;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.text.Text;

public class AIServerNetworking {

    private static final int MAX_IMAGE_SIZE =
            1_000_000;

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

                        int kb =
                                imagen.length / 1024;

                        player.sendMessage(
                                Text.literal(
                                        "[AI SERVER]\n"
                                                + "Pregunta recibida: "
                                                + pregunta
                                                + "\nImagen recibida: "
                                                + kb
                                                + " KB"
                                                + "\nTarget: "
                                                + context.target
                                ),
                                false
                        );

                        System.out.println(
                                "[MinecraftAI] Pregunta de "
                                        + player.getName()
                                                .getString()
                                        + ": "
                                        + pregunta
                                        + " | imagen="
                                        + imagen.length
                                        + " bytes"
                        );
                    });
                }
        );
    }

    private AIServerNetworking() {
    }
}