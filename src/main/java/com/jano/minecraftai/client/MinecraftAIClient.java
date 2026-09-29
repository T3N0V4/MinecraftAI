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

public class MinecraftAIClient
        implements ClientModInitializer {

    private static KeyBinding aiKey;

    private static final int MAX_WIDTH = 640;

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
                                                MinecraftClient
                                                        .getInstance();

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
                                                            StringArgumentType
                                                                    .greedyString()
                                                    )

                                                    .executes(context -> {

                                                        String pregunta =
                                                                StringArgumentType
                                                                        .getString(
                                                                                context,
                                                                                "pregunta"
                                                                        );

                                                        procesarPregunta(
                                                                MinecraftClient
                                                                        .getInstance(),
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

        if (client.player == null) {
            return;
        }

        NativeImage screenshot = null;
        NativeImage resized = null;

        try {

            screenshot =
                    ScreenshotRecorder
                            .takeScreenshot(
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

            PacketByteBuf buffer =
                    PacketByteBufs.create();

            buffer.writeString(
                    pregunta,
                    2048
            );

            buffer.writeByteArray(
                    pngBytes
            );

            ClientPlayNetworking.send(
                    NetworkConstants.ASK_AI,
                    buffer
            );

            int kb =
                    pngBytes.length / 1024;

            client.player.sendMessage(
                    Text.literal(
                            "[AI] Enviando pregunta + imagen ("
                                    + targetWidth
                                    + "x"
                                    + targetHeight
                                    + ", "
                                    + kb
                                    + " KB)"
                    ),
                    false
            );

        } catch (IOException e) {

            client.player.sendMessage(
                    Text.literal(
                            "[AI] Error preparando la imagen."
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

    private static void registerDebugKey() {

        aiKey =
                KeyBindingHelper
                        .registerKeyBinding(
                                new KeyBinding(
                                        "key.minecraftai.debug_capture",
                                        InputUtil.Type.KEYSYM,
                                        GLFW.GLFW_KEY_V,
                                        "category.minecraftai"
                                )
                        );

        ClientTickEvents
                .END_CLIENT_TICK
                .register(
                        client -> {

                            while (
                                    aiKey.wasPressed()
                            ) {

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
                ScreenshotRecorder
                        .takeScreenshot(
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