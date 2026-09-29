package com.jano.minecraftai.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

public class MinecraftAIClient implements ClientModInitializer {

    private static KeyBinding aiKey;

    @Override
    public void onInitializeClient() {

        aiKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.minecraftai.ask",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_V,
                        "category.minecraftai"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {

                    while (aiKey.wasPressed()) {
                        captureScreen(client);
                    }
                }
        );

        System.out.println(
                "[MinecraftAI] Cliente iniciado correctamente."
        );
    }

    private static void captureScreen(
            MinecraftClient client
    ) {

        if (client.player == null) {
            return;
        }

        NativeImage image =
                ScreenshotRecorder.takeScreenshot(
                        client.getFramebuffer()
                );

        int width = image.getWidth();
        int height = image.getHeight();

        client.player.sendMessage(
                Text.literal(
                        "[AI] Imagen capturada en memoria: "
                        + width
                        + "x"
                        + height
                ),
                false
        );

        image.close();
    }
}