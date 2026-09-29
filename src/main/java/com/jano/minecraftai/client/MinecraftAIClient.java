package com.jano.minecraftai.client;

import com.jano.minecraftai.network.NetworkConstants;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotRecorder;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class MinecraftAIClient
        implements ClientModInitializer {

    private static final int MAX_WIDTH =
            640;

    /*
     * Minecraft 1.20.1 limita mucho el tamaño
     * de cada custom payload.
     *
     * Dejamos margen usando chunks de 24 KB.
     */
    private static final int CHUNK_SIZE =
            24_000;

    private static final int MAX_IMAGE_SIZE =
            1_000_000;

    private static KeyBinding aiKey;

    @Override
    public void onInitializeClient() {

        registerAICommand();
        registerDebugKey();

        System.out.println(
                "[MinecraftAI] Cliente iniciado correctamente."
        );
    }

    private static void registerAICommand() {

        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> {

                    dispatcher.register(
                            ClientCommandManager
                                    .literal("ai")

                                    .executes(context -> {

                                        MinecraftClient client =
                                                MinecraftClient.getInstance();

                                        if (client.player != null) {

                                            client.player.sendMessage(
                                                    Text.literal(
                                                            "[AI] Usa: /ai <pregunta>"
                                                    ),
                                                    false
                                            );
                                        }

                                        return 1;
                                    })

                                    .then(
                                            ClientCommandManager
                                                    .argument(
                                                            "pregunta",
                                                            StringArgumentType.greedyString()
                                                    )

                                                    .executes(context -> {

                                                        String pregunta =
                                                                StringArgumentType.getString(
                                                                        context,
                                                                        "pregunta"
                                                                );

                                                        procesarPregunta(
                                                                MinecraftClient.getInstance(),
                                                                pregunta
                                                        );

                                                        return 1;
                                                    })
                                    )
                    );
                }
        );
    }

    private static void procesarPregunta(
            MinecraftClient client,
            String pregunta
    ) {

        if (
                client.player == null
                || client.getNetworkHandler() == null
        ) {
            return;
        }

        NativeImage screenshot = null;
        NativeImage resized = null;

        try {

            screenshot =
                    ScreenshotRecorder.takeScreenshot(
                            client.getFramebuffer()
                    );

            int originalWidth =
                    screenshot.getWidth();

            int originalHeight =
                    screenshot.getHeight();

            int targetWidth =
                    Math.min(
                            MAX_WIDTH,
                            originalWidth
                    );

            int targetHeight =
                    (int) (
                            originalHeight
                            * (
                                    targetWidth
                                    / (double) originalWidth
                            )
                    );

            resized =
                    new NativeImage(
                            targetWidth,
                            targetHeight,
                            false
                    );

            screenshot.resizeSubRectTo(
                    0,
                    0,
                    originalWidth,
                    originalHeight,
                    resized
            );

            byte[] pngBytes =
                    resized.getBytes();

            if (
                    pngBytes.length
                    > MAX_IMAGE_SIZE
            ) {

                client.player.sendMessage(
                        Text.literal(
                                "[AI] La captura pesa demasiado: "
                                        + (pngBytes.length / 1024)
                                        + " KB"
                        ),
                        false
                );

                return;
            }

            enviarPreguntaEnChunks(
                    client,
                    pregunta,
                    pngBytes
            );

            client.player.sendMessage(
                    Text.literal(
                            "[AI] Enviando pregunta + imagen "
                                    + targetWidth
                                    + "x"
                                    + targetHeight
                                    + " ("
                                    + (pngBytes.length / 1024)
                                    + " KB)"
                    ),
                    false
            );

        } catch (IOException e) {

            client.player.sendMessage(
                    Text.literal(
                            "[AI] Error preparando la captura."
                    ),
                    false
            );

            e.printStackTrace();

        } finally {

            if (resized != null) {
                resized.close();
            }

            if (screenshot != null) {
                screenshot.close();
            }
        }
    }

    private static void enviarPreguntaEnChunks(
            MinecraftClient client,
            String pregunta,
            byte[] image
    ) {

        UUID requestId =
                UUID.randomUUID();

        int totalChunks =
                (int) Math.ceil(
                        image.length
                                / (double) CHUNK_SIZE
                );

        // -------------------------
        // INICIO DE PETICION
        // -------------------------

        PacketByteBuf beginBuffer =
                PacketByteBufs.create();

        beginBuffer.writeUuid(
                requestId
        );

        beginBuffer.writeString(
                pregunta,
                2048
        );

        beginBuffer.writeInt(
                image.length
        );

        beginBuffer.writeInt(
                totalChunks
        );

        ClientPlayNetworking.send(
                NetworkConstants.ASK_BEGIN,
                beginBuffer
        );

        // -------------------------
        // CHUNKS DE IMAGEN
        // -------------------------

        for (
                int index = 0;
                index < totalChunks;
                index++
        ) {

            int start =
                    index * CHUNK_SIZE;

            int end =
                    Math.min(
                            start + CHUNK_SIZE,
                            image.length
                    );

            byte[] chunk =
                    Arrays.copyOfRange(
                            image,
                            start,
                            end
                    );

            PacketByteBuf chunkBuffer =
                    PacketByteBufs.create();

            chunkBuffer.writeUuid(
                    requestId
            );

            chunkBuffer.writeInt(
                    index
            );

            chunkBuffer.writeByteArray(
                    chunk
            );

            ClientPlayNetworking.send(
                    NetworkConstants.ASK_CHUNK,
                    chunkBuffer
            );
        }
    }

    private static void registerDebugKey() {

        aiKey =
                KeyBindingHelper.registerKeyBinding(
                        new KeyBinding(
                                "key.minecraftai.debug_capture",
                                InputUtil.Type.KEYSYM,
                                GLFW.GLFW_KEY_V,
                                "category.minecraftai"
                        )
                );

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {

                    while (aiKey.wasPressed()) {
                        debugCapture(client);
                    }
                }
        );
    }

    private static void debugCapture(
            MinecraftClient client
    ) {

        if (client.player == null) {
            return;
        }

        NativeImage image =
                ScreenshotRecorder.takeScreenshot(
                        client.getFramebuffer()
                );

        client.player.sendMessage(
                Text.literal(
                        "[AI DEBUG] Captura: "
                                + image.getWidth()
                                + "x"
                                + image.getHeight()
                ),
                false
        );

        image.close();
    }
}