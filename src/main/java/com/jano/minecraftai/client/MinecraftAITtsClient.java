package com.jano.minecraftai.client;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class MinecraftAITtsClient {

    private static final String TTS_URL =
            "http://127.0.0.1:8766";

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(
                            Duration.ofSeconds(2)
                    )
                    .build();

    private static final Gson GSON =
            new Gson();


    public static CompletableFuture<Boolean> speak(
            String text
    ) {

        if (
                !MinecraftAIClientConfig.isVoiceEnabled()
                        || text == null
                        || text.isBlank()
        ) {
            return CompletableFuture.completedFuture(
                    false
            );
        }

        String body =
                GSON.toJson(
                        Map.of(
                                "text",
                                text,
                                "rate",
                                0,
                                "volume",
                                100,
                                "voice",
                                "Helena"
                        )
                );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        TTS_URL
                                                + "/speak"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(4)
                        )
                        .header(
                                "Content-Type",
                                "application/json; charset=utf-8"
                        )
                        .POST(
                                HttpRequest
                                        .BodyPublishers
                                        .ofString(body)
                        )
                        .build();

        return HTTP_CLIENT
                .sendAsync(
                        request,
                        HttpResponse
                                .BodyHandlers
                                .ofString()
                )
                .thenApply(
                        response ->
                                response.statusCode() >= 200
                                        && response.statusCode() < 300
                )
                .exceptionally(
                        error -> {
                            System.out.println(
                                    "[MinecraftAI] TTS no disponible: "
                                            + error.getMessage()
                            );

                            return false;
                        }
                );
    }


    public static CompletableFuture<Boolean> stop() {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        TTS_URL
                                                + "/stop"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(2)
                        )
                        .POST(
                                HttpRequest
                                        .BodyPublishers
                                        .noBody()
                        )
                        .build();

        return HTTP_CLIENT
                .sendAsync(
                        request,
                        HttpResponse
                                .BodyHandlers
                                .ofString()
                )
                .thenApply(
                        response ->
                                response.statusCode() >= 200
                                        && response.statusCode() < 300
                )
                .exceptionally(
                        error -> false
                );
    }


    private MinecraftAITtsClient() {
    }
}