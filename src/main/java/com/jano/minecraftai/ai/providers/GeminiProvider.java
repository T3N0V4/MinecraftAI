package com.jano.minecraftai.ai.providers;
import com.jano.minecraftai.ai.MinecraftAIPrompt;
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

public class GeminiProvider implements AIProvider {

    private static final String MODEL =
            "gemini-3.5-flash-lite";

    private static final String API_KEY_ENV =
            "GEMINI_API_KEY";

    private final HttpClient httpClient;

    public GeminiProvider() {

        this.httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(
                                Duration.ofSeconds(10)
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
                System.getenv(API_KEY_ENV);

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
                System.getenv(API_KEY_ENV);

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

            String base64Image =
                    Base64.getEncoder()
                            .encodeToString(
                                    request.image
                            );

            String prompt =
                    MinecraftAIPrompt.build(request);
            String json =
                    buildRequestJson(
                            prompt,
                            base64Image
                    );

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + MODEL
                            + ":generateContent?key="
                            + apiKey;

            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(url)
                            )
                            .timeout(
                                    Duration.ofSeconds(45)
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

    private String buildPrompt(
            AIRequest request
    ) {

        return """
                Sos MinecraftAI, un asistente dentro de Minecraft.

                Respondé en español claro y breve.

                Tenés dos fuentes de información:

                1. Una captura de pantalla del jugador.
                2. Contexto real obtenido directamente del servidor.

                El contexto del servidor tiene prioridad para datos exactos.
                La imagen sirve para interpretar qué está viendo o a qué se refiere el jugador.

                No inventes datos que no aparecen en ninguna de las dos fuentes.
                Si no podés identificar algo con seguridad, decilo.

                Pregunta del jugador:
                %s

                Contexto del servidor:
                %s
                """
                .formatted(
                        request.question,
                        request.playerContext
                                .toString()
                );
    }

    private String buildRequestJson(
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
                    "maxOutputTokens": 500
                  }
                }
                """
                .formatted(
                        escapeJson(prompt),
                        base64Image
                );
    }

    private String extractText(
            String json
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\"text\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
                );

        Matcher matcher =
                pattern.matcher(json);

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
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private String unescapeJson(
            String text
    ) {

        return text
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private long elapsed(
            long start
    ) {

        return System.currentTimeMillis()
                - start;
    }
}