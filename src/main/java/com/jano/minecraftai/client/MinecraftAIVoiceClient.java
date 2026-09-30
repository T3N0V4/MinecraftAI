package com.jano.minecraftai.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import java.util.concurrent.CompletableFuture;

public final class MinecraftAIVoiceClient {

    private static final String STT_URL =
            "http://127.0.0.1:8765";

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(
                            Duration.ofSeconds(2)
                    )
                    .build();

    private static KeyBinding voiceKey;

    private static boolean automaticKeyAssigned =
            false;

    private static boolean wasPressed =
            false;

    private static volatile CompletableFuture<HttpResponse<String>>
            startFuture;

    private static final int[] KEY_CANDIDATES = {
            GLFW.GLFW_KEY_V,
            GLFW.GLFW_KEY_X,
            GLFW.GLFW_KEY_L,
            GLFW.GLFW_KEY_P,
            GLFW.GLFW_KEY_O,
            GLFW.GLFW_KEY_I,
            GLFW.GLFW_KEY_F10,
            GLFW.GLFW_KEY_F11
    };

    public static void register() {

        voiceKey =
                KeyBindingHelper.registerKeyBinding(
                        new KeyBinding(
                                "key.minecraftai.voice",
                                InputUtil.Type.KEYSYM,
                                GLFW.GLFW_KEY_UNKNOWN,
                                "category.minecraftai"
                        )
                );

        ClientTickEvents.END_CLIENT_TICK.register(
                MinecraftAIVoiceClient::tick
        );

        System.out.println(
                "[MinecraftAI] Push-to-talk registrado."
        );
    }

    private static void tick(
            MinecraftClient client
    ) {

        if (
                !automaticKeyAssigned
                && client.options != null
                && voiceKey != null
        ) {

            assignAutomaticKey(
                    client
            );
        }

        if (
                voiceKey == null
                || voiceKey.isUnbound()
        ) {
            return;
        }

        boolean pressed =
                voiceKey.isPressed();

        if (
                pressed
                && !wasPressed
        ) {

            startRecording(
                    client
            );
        }

        if (
                !pressed
                && wasPressed
        ) {

            stopRecording(
                    client
            );
        }

        wasPressed =
                pressed;
    }

    private static void assignAutomaticKey(
            MinecraftClient client
    ) {

        if (
                !voiceKey.isUnbound()
        ) {

            automaticKeyAssigned =
                    true;

            return;
        }

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
                    "[MinecraftAI] No se encontro una tecla libre para push-to-talk."
            );

            return;
        }

        InputUtil.Key key =
                InputUtil.Type.KEYSYM
                        .createFromCode(
                                freeKey
                        );

        voiceKey.setBoundKey(
                key
        );

        KeyBinding.updateKeysByCode();

        automaticKeyAssigned =
                true;

        System.out.println(
                "[MinecraftAI] Push-to-talk asignado a: "
                        + voiceKey
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

        InputUtil.Key candidate =
                InputUtil.Type.KEYSYM
                        .createFromCode(
                                keyCode
                        );

        String candidateId =
                candidate.getTranslationKey();

        for (
                KeyBinding binding :
                client.options.allKeys
        ) {

            if (
                    binding == voiceKey
                    || binding.isUnbound()
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

    private static void startRecording(
            MinecraftClient client
    ) {

        AIOverlayState.setThinking(
                true
        );

        AIOverlayState.setStatus(
                "Escuchando..."
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        STT_URL
                                                + "/start"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(3)
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .noBody()
                        )
                        .build();

        startFuture =
                HTTP_CLIENT.sendAsync(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString(
                                        StandardCharsets.UTF_8
                                )
                );

        startFuture
                .thenAccept(
                        response -> {

                            if (
                                    response.statusCode()
                                    != 200
                            ) {

                                showError(
                                        client,
                                        "No pude iniciar el microfono."
                                );
                            }
                        }
                )
                .exceptionally(
                        error -> {

                            showError(
                                    client,
                                    "El servidor de voz no esta disponible."
                            );

                            return null;
                        }
                );
    }

    private static void stopRecording(
            MinecraftClient client
    ) {

        AIOverlayState.setStatus(
                "Transcribiendo..."
        );

        CompletableFuture<HttpResponse<String>>
                pendingStart =
                startFuture;

        if (
                pendingStart == null
        ) {

            showError(
                    client,
                    "La grabacion no se inicio."
            );

            return;
        }

        pendingStart
                .thenCompose(
                        startResponse -> {

                            if (
                                    startResponse.statusCode()
                                    != 200
                            ) {

                                throw new IllegalStateException(
                                        "El microfono no pudo iniciarse."
                                );
                            }

                            HttpRequest request =
                                    HttpRequest.newBuilder()
                                            .uri(
                                                    URI.create(
                                                            STT_URL
                                                                    + "/stop"
                                                    )
                                            )
                                            .timeout(
                                                    Duration.ofSeconds(30)
                                            )
                                            .POST(
                                                    HttpRequest
                                                            .BodyPublishers
                                                            .noBody()
                                            )
                                            .build();

                            return HTTP_CLIENT.sendAsync(
                                    request,
                                    HttpResponse.BodyHandlers
                                            .ofString(
                                                    StandardCharsets.UTF_8
                                            )
                            );
                        }
                )
                .thenAccept(
                        response -> {

                            if (
                                    response.statusCode()
                                    != 200
                            ) {

                                showError(
                                        client,
                                        "No pude transcribir el audio."
                                );

                                return;
                            }

                            try {

                                JsonObject json =
                                        JsonParser
                                                .parseString(
                                                        response.body()
                                                )
                                                .getAsJsonObject();

                                boolean success =
                                        json.has(
                                                "success"
                                        )
                                        && json
                                                .get(
                                                        "success"
                                                )
                                                .getAsBoolean();

                                if (
                                        !success
                                ) {

                                    showError(
                                            client,
                                            "La transcripcion fallo."
                                    );

                                    return;
                                }

                                String text =
                                        json.has(
                                                "text"
                                        )
                                                ? json
                                                        .get(
                                                                "text"
                                                        )
                                                        .getAsString()
                                                        .trim()
                                                : "";

                                if (
                                        text.isBlank()
                                ) {

                                    client.execute(
                                            () -> {

                                                AIOverlayState
                                                        .setThinking(
                                                                false
                                                        );

                                                AIOverlayState
                                                        .setStatus(
                                                                "Listo"
                                                        );


                                            }
                                    );

                                    return;
                                }

                                System.out.println(
                                        "[MinecraftAI] Voz -> "
                                                + text
                                );

                                client.execute(
                                        () ->
                                                MinecraftAIClient.ask(
                                                        client,
                                                        text
                                                )
                                );

                            } catch (
                                    Exception e
                            ) {

                                showError(
                                        client,
                                        "Respuesta STT invalida."
                                );

                                e.printStackTrace();
                            }
                        }
                )
                .exceptionally(
                        error -> {

                            showError(
                                    client,
                                    "Error durante la transcripcion."
                            );

                            error.printStackTrace();

                            return null;
                        }
                )
                .whenComplete(
                        (
                                response,
                                error
                        ) ->
                                startFuture =
                                        null
                );
    }

    private static void showError(
            MinecraftClient client,
            String message
    ) {

        client.execute(
                () -> {

                    AIOverlayState.setThinking(
                            false
                    );

                    AIOverlayState.setStatus(
                            "Voz no disponible"
                    );

                    AIOverlayState.addSystem(
                            message
                    );
                }
        );
    }

    private MinecraftAIVoiceClient() {
    }
}