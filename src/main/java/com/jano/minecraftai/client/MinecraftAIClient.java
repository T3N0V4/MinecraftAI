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

import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class MinecraftAIClient
        implements ClientModInitializer {

    private static final int MAX_WIDTH =
            640;

    private static final int CHUNK_SIZE =
            24_000;

    private static final int MAX_IMAGE_SIZE =
            1_000_000;

    private static KeyBinding openAIKey;

    private static boolean automaticKeyAssigned =
            false;

    private static final int[] KEY_CANDIDATES = {

            GLFW.GLFW_KEY_J,
            GLFW.GLFW_KEY_K,
            GLFW.GLFW_KEY_U,
            GLFW.GLFW_KEY_Y,
            GLFW.GLFW_KEY_H,
            GLFW.GLFW_KEY_B,
            GLFW.GLFW_KEY_N,
            GLFW.GLFW_KEY_M,
            GLFW.GLFW_KEY_G,
            GLFW.GLFW_KEY_R,
            GLFW.GLFW_KEY_C,
            GLFW.GLFW_KEY_Z,

            GLFW.GLFW_KEY_F6,
            GLFW.GLFW_KEY_F7,
            GLFW.GLFW_KEY_F8,
            GLFW.GLFW_KEY_F9
    };

    @Override
    public void onInitializeClient() {

        registerAICommand();

        registerNetworking();

        MinecraftAIHud.register();

        /*
         * IMPORTANTE:
         *
         * El keybind se REGISTRA ahora,
         * antes de que GameOptions termine de inicializarse.
         *
         * Pero lo dejamos sin tecla.
         */
        registerUnboundKey();

        /*
         * Después, cuando GameOptions ya existe,
         * buscamos una tecla libre y cambiamos el binding.
         */
        registerClientTick();

        System.out.println(
                "[MinecraftAI] Cliente iniciado correctamente."
        );
    }


    // ========================================================
    // KEYBIND
    // ========================================================

    private static void registerUnboundKey() {

        openAIKey =
                KeyBindingHelper.registerKeyBinding(
                        new KeyBinding(
                                "key.minecraftai.open",
                                InputUtil.Type.KEYSYM,
                                GLFW.GLFW_KEY_UNKNOWN,
                                "category.minecraftai"
                        )
                );

        System.out.println(
                "[MinecraftAI] Keybind registrado sin tecla inicial."
        );
    }

    private static void registerClientTick() {

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {

                    if (
                            !automaticKeyAssigned
                            && client.options != null
                            && openAIKey != null
                    ) {

                        assignAutomaticKey(
                                client
                        );
                    }

                    if (
                            openAIKey != null
                    ) {

                        while (
                                openAIKey.wasPressed()
                        ) {

                            toggleScreen(
                                    client
                            );
                        }
                    }
                }
        );
    }

    private static void assignAutomaticKey(
            MinecraftClient client
    ) {

        int freeKey =
                findFreeKey(
                        client
                );

        if (
                freeKey
                == GLFW.GLFW_KEY_UNKNOWN
        ) {

            automaticKeyAssigned =
                    true;

            System.out.println(
                    "[MinecraftAI] No se encontro una tecla libre. "
                            + "Configurala desde Controles."
            );

            return;
        }

        InputUtil.Key key =
                InputUtil.Type.KEYSYM
                        .createFromCode(
                                freeKey
                        );

        openAIKey.setBoundKey(
                key
        );

        /*
         * Reconstruye el mapa interno:
         * tecla -> keybind.
         */
        KeyBinding.updateKeysByCode();

        automaticKeyAssigned =
                true;

        System.out.println(
                "[MinecraftAI] Tecla automatica asignada: "
                        + openAIKey
                                .getBoundKeyLocalizedText()
                                .getString()
        );
    }

    private static int findFreeKey(
            MinecraftClient client
    ) {

        for (
                int candidate :
                KEY_CANDIDATES
        ) {

            if (
                    !isKeyUsed(
                            client,
                            candidate
                    )
            ) {

                return candidate;
            }
        }

        return GLFW.GLFW_KEY_UNKNOWN;
    }

    private static boolean isKeyUsed(
            MinecraftClient client,
            int keyCode
    ) {

        if (
                client.options == null
        ) {
            return true;
        }

        InputUtil.Key candidate =
                InputUtil.Type.KEYSYM
                        .createFromCode(
                                keyCode
                        );

        String candidateId =
                candidate
                        .getTranslationKey();

        for (
                KeyBinding binding :
                client.options.allKeys
        ) {

            if (
                    binding == openAIKey
            ) {
                continue;
            }

            if (
                    binding.isUnbound()
            ) {
                continue;
            }

            if (
                    candidateId.equals(
                            binding
                                    .getBoundKeyTranslationKey()
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    private static void toggleScreen(
            MinecraftClient client
    ) {

        if (
                client.currentScreen
                instanceof MinecraftAIScreen
        ) {

            client.setScreen(
                    null
            );

            return;
        }

        if (
                client.currentScreen
                == null
        ) {

            client.setScreen(
                    new MinecraftAIScreen()
            );
        }
    }


    // ========================================================
    // NETWORKING
    // ========================================================

    private static void registerNetworking() {

        ClientPlayNetworking.registerGlobalReceiver(
                NetworkConstants.AI_STATUS,
                (
                        client,
                        handler,
                        buffer,
                        responseSender
                ) -> {

                    String status =
                            buffer.readString(
                                    256
                            );

                    client.execute(
                            () -> {

                                AIOverlayState.setStatus(
                                        status
                                );

                                AIOverlayState.setThinking(
                                        !"Listo".equalsIgnoreCase(
                                                status
                                        )
                                );
                            }
                    );
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                NetworkConstants.AI_RESPONSE,
                (
                        client,
                        handler,
                        buffer,
                        responseSender
                ) -> {

                    String content =
                            buffer.readString(
                                    32767
                            );

                    String provider =
                            buffer.readString(
                                    128
                            );

                    String model =
                            buffer.readString(
                                    128
                            );

                    long duration =
                            buffer.readLong();

                    client.execute(
                            () -> {

                                AIOverlayState.addAssistant(
                                        content
                                );

                                AIOverlayState.setThinking(
                                        false
                                );

                                AIOverlayState.setStatus(
                                        "Listo"
                                );

                                System.out.println(
                                        "[MinecraftAI] "
                                                + provider
                                                + " / "
                                                + model
                                                + " - "
                                                + duration
                                                + " ms"
                                );
                            }
                    );
                }
        );
    }


    // ========================================================
    // COMMAND /ai
    // ========================================================

    private static void registerAICommand() {

        ClientCommandRegistrationCallback.EVENT.register(
                (
                        dispatcher,
                        registryAccess
                ) -> {

                    dispatcher.register(
                            ClientCommandManager
                                    .literal(
                                            "ai"
                                    )

                                    .executes(
                                            context -> {

                                                MinecraftClient client =
                                                        MinecraftClient
                                                                .getInstance();

                                                if (
                                                        client.player
                                                        != null
                                                ) {

                                                    client.setScreen(
                                                            new MinecraftAIScreen()
                                                    );
                                                }

                                                return 1;
                                            }
                                    )

                                    .then(
                                            ClientCommandManager
                                                    .argument(
                                                            "pregunta",
                                                            StringArgumentType
                                                                    .greedyString()
                                                    )

                                                    .executes(
                                                            context -> {

                                                                String question =
                                                                        StringArgumentType
                                                                                .getString(
                                                                                        context,
                                                                                        "pregunta"
                                                                                );

                                                                ask(
                                                                        MinecraftClient
                                                                                .getInstance(),
                                                                        question
                                                                );

                                                                return 1;
                                                            }
                                                    )
                                    )
                    );
                }
        );
    }


    // ========================================================
    // ASK
    // ========================================================

    public static void ask(
            MinecraftClient client,
            String question
    ) {

        if (
                client.player == null
                || client.getNetworkHandler() == null
                || question == null
                || question.isBlank()
        ) {
            return;
        }

        AIOverlayState.addUser(
                question
        );

        AIOverlayState.setThinking(
                true
        );

        AIOverlayState.setStatus(
                "Observando..."
        );

        NativeImage screenshot =
                null;

        NativeImage resized =
                null;

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

            if (
                    pngBytes.length
                    > MAX_IMAGE_SIZE
            ) {

                AIOverlayState.setThinking(
                        false
                );

                AIOverlayState.setStatus(
                        "Error"
                );

                AIOverlayState.addSystem(
                        "La captura pesa demasiado."
                );

                return;
            }

            AIOverlayState.setStatus(
                    "Enviando..."
            );

            enviarPreguntaEnChunks(
                    question,
                    pngBytes
            );

        } catch (
                IOException e
        ) {

            AIOverlayState.setThinking(
                    false
            );

            AIOverlayState.setStatus(
                    "Error"
            );

            AIOverlayState.addSystem(
                    "No pude preparar la captura."
            );

            e.printStackTrace();

        } finally {

            if (
                    resized != null
            ) {
                resized.close();
            }

            if (
                    screenshot != null
            ) {
                screenshot.close();
            }
        }
    }


    // ========================================================
    // CHUNKS
    // ========================================================

    private static void enviarPreguntaEnChunks(
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

        for (
                int index = 0;
                index < totalChunks;
                index++
        ) {

            int start =
                    index
                            * CHUNK_SIZE;

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
}