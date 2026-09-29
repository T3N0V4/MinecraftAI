package com.jano.minecraftai.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class MinecraftAIHud {

    public static void register() {

        HudRenderCallback.EVENT.register(
                MinecraftAIHud::render
        );
    }

    private static void render(
            DrawContext context,
            float tickDelta
    ) {

        MinecraftClient client =
                MinecraftClient.getInstance();

        if (
                client.player == null
                || client.options.hudHidden
                || client.currentScreen
                instanceof MinecraftAIScreen
        ) {
            return;
        }

        String text;

        if (AIOverlayState.isThinking()) {

            text =
                    "[ AI ... ]";

        } else {

            text =
                    "[ AI ]";
        }

        int width =
                client.textRenderer
                        .getWidth(text)
                        + 12;

        int x =
                client.getWindow()
                        .getScaledWidth()
                        - width
                        - 10;

        int y =
                10;

        context.fill(
                x,
                y,
                x + width,
                y + 20,
                0xAA111827
        );

        context.fill(
                x,
                y,
                x + 2,
                y + 20,
                AIOverlayState.isThinking()
                        ? 0xFFFFC857
                        : 0xFF66D9EF
        );

        context.drawTextWithShadow(
                client.textRenderer,
                text,
                x + 6,
                y + 6,
                0xFFFFFFFF
        );
    }

    private MinecraftAIHud() {
    }
}