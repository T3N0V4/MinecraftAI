package com.jano.minecraftai.ai.providers;

import com.jano.minecraftai.ai.AIProvider;
import com.jano.minecraftai.ai.AIRequest;
import com.jano.minecraftai.ai.AIResponse;
import com.jano.minecraftai.ai.MinecraftAIPrompt;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.time.Duration;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiProvider
        implements AIProvider {

    private static final String MODEL =
            "gemini-3.5-flash-lite";

    private static final String API_KEY_ENV =
            "GEMINI_API_KEY";

    private final HttpClient httpClient;

    public GeminiProvider() {

        this.httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(
                                Duration.ofSeconds(8)
                        )
                        .build();
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public String getModelName() {
        return MODEL;
    }

    @Override
    public boolean isAvailable() {

        String apiKey =
                System.getenv(
                        API_KEY_ENV
                );

        return apiKey != null
                && !apiKey.isBlank();
    }

    @Override
    public AIResponse respond(
            AIRequest request
    ) {

        long start =
                System.currentTimeMillis();

        String apiKey =
                System.getenv(
                        API_KEY_ENV
                );

        if (
                apiKey == null
                || apiKey.isBlank()
        ) {

            return AIResponse.failure(
                    getProviderName(),
                    getModelName(),
                    "No se encontró GEMINI_API_KEY.",
                    elapsed(start)
            );
        }

        try {

            String prompt =
                    MinecraftAIPrompt.build(
                            request
                    );

            /*
             * Solo enviamos imagen en la primera llamada.
             *
             * Una vez que una tool fue ejecutada,
             * Gemini ya tiene el resultado real del server.
             */
            boolean includeImage =
                    request.toolResults.isEmpty()
                    && request.image != null
                    && request.image.length > 0;

            String json;

            if (includeImage) {

                String base64Image =
                        Base64.getEncoder()
                                .encodeToString(
                                        request.image
                                );

                json =
                        buildRequestJsonWithImage(
                                prompt,
                                base64Image
                        );

                System.out.println(
                        "[MinecraftAI] Gemini: enviando imagen + texto."
                );

            } else {

                json =
                        buildRequestJsonTextOnly(
                                prompt
                        );

                System.out.println(
                        "[MinecraftAI] Gemini: enviando solo texto."
                );
            }

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + MODEL
                            + ":generateContent?key="
                            + apiKey;

            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            url
                                    )
                            )
                            .timeout(
                                    Duration.ofSeconds(20)
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    json,
                                                    StandardCharsets.UTF_8
                                            )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers
                                    .ofString(
                                            StandardCharsets.UTF_8
                                    )
                    );

            if (
                    response.statusCode() < 200
                    || response.statusCode() >= 300
            ) {

                return AIResponse.failure(
                        getProviderName(),
                        getModelName(),
                        "HTTP "
                                + response.statusCode()
                                + ": "
                                + response.body(),
                        elapsed(start)
                );
            }

            String content =
                    extractText(
                            response.body()
                    );

            if (
                    content == null
                    || content.isBlank()
            ) {

                return AIResponse.failure(
                        getProviderName(),
                        getModelName(),
                        "Gemini respondió sin texto.",
                        elapsed(start)
                );
            }

            return new AIResponse(
                    true,
                    getProviderName(),
                    getModelName(),
                    content,
                    elapsed(start)
            );

        } catch (Exception e) {

            return AIResponse.failure(
                    getProviderName(),
                    getModelName(),
                    e.getClass()
                            .getSimpleName()
                            + ": "
                            + e.getMessage(),
                    elapsed(start)
            );
        }
    }

    private String buildRequestJsonWithImage(
            String prompt,
            String base64Image
    ) {

        return """
                {
                  "contents": [
                    {
                      "role": "user",
                      "parts": [
                        {
                          "text": "%s"
                        },
                        {
                          "inlineData": {
                            "mimeType": "image/png",
                            "data": "%s"
                          }
                        }
                      ]
                    }
                  ],
                  "generationConfig": {
                    "temperature": 0.2,
                    "maxOutputTokens": 300
                  }
                }
                """
                .formatted(
                        escapeJson(prompt),
                        base64Image
                );
    }

    private String buildRequestJsonTextOnly(
            String prompt
    ) {

        return """
                {
                  "contents": [
                    {
                      "role": "user",
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ],
                  "generationConfig": {
                    "temperature": 0.2,
                    "maxOutputTokens": 300
                  }
                }
                """
                .formatted(
                        escapeJson(
                                prompt
                        )
                );
    }

    private String extractText(
            String json
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\\\"text\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\""
                );

        Matcher matcher =
                pattern.matcher(
                        json
                );

        if (!matcher.find()) {
            return null;
        }

        return unescapeJson(
                matcher.group(1)
        );
    }

    private String escapeJson(
            String text
    ) {

        return text
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\t",
                        "\\t"
                );
    }

    private String unescapeJson(
            String text
    ) {

        return text
                .replace(
                        "\\n",
                        "\n"
                )
                .replace(
                        "\\r",
                        "\r"
                )
                .replace(
                        "\\t",
                        "\t"
                )
                .replace(
                        "\\\"",
                        "\""
                )
                .replace(
                        "\\\\",
                        "\\"
                );
    }

    private long elapsed(
            long start
    ) {

        return System.currentTimeMillis()
                - start;
    }
}