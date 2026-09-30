package com.jano.minecraftai.debug.presentation;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DebugTtsClient {

    private static final String BASE_URL =
            "http://127.0.0.1:8766";

    private final HttpClient client =
            HttpClient.newBuilder()
                    .connectTimeout(
                            Duration.ofSeconds(2)
                    )
                    .build();

    private final Gson gson =
            new Gson();


    public CompletableFuture<Boolean> speak(
            String text
    ) {

        if (
                text == null
                        || text.isBlank()
        ) {
            return CompletableFuture.completedFuture(
                    false
            );
        }

        String json =
                gson.toJson(
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
                                        BASE_URL + "/speak"
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
                                HttpRequest.BodyPublishers
                                        .ofString(json)
                        )
                        .build();

        return client.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString()
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


    public CompletableFuture<Boolean> stop() {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL + "/stop"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(2)
                        )
                        .POST(
                                HttpRequest.BodyPublishers.noBody()
                        )
                        .build();

        return client.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString()
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
}