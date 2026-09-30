package com.jano.minecraftai.debug;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import java.util.concurrent.TimeUnit;

public class DebugSttClient {

    private static final String BASE_URL =
            "http://127.0.0.1:8765";

    private static final String PROJECT =
            "C:\\Minecraft\\MinecraftAI";

    private static final String PYTHONW =
            PROJECT
                    + "\\.venv\\Scripts\\pythonw.exe";

    private static final String PYTHON =
            PROJECT
                    + "\\.venv\\Scripts\\python.exe";

    private static final String SERVER =
            PROJECT
                    + "\\voice\\stt_server.py";

    private static final String LOG =
            PROJECT
                    + "\\voice\\stt_debug.log";

    private final HttpClient http =
            HttpClient.newBuilder()
                    .connectTimeout(
                            Duration.ofSeconds(2)
                    )
                    .build();

    private Process process;

    private boolean startedByDebugger =
            false;

    public boolean ensureRunning() {

        if (
                isHealthy()
        ) {
            return true;
        }

        try {

            String python =
                    findPython();

            if (
                    python == null
            ) {
                return false;
            }

            ProcessBuilder builder =
                    new ProcessBuilder(
                            python,
                            SERVER
                    );

            builder.directory(
                    new File(
                            PROJECT
                    )
            );

            builder.redirectErrorStream(
                    true
            );

            builder.redirectOutput(
                    ProcessBuilder.Redirect.appendTo(
                            new File(
                                    LOG
                            )
                    )
            );

            process =
                    builder.start();

            startedByDebugger =
                    true;

            for (
                    int i = 0;
                    i < 60;
                    i++
            ) {

                if (
                        isHealthy()
                ) {
                    return true;
                }

                if (
                        !process.isAlive()
                ) {
                    return false;
                }

                Thread.sleep(
                        500
                );
            }

        } catch (
                Exception e
        ) {

            e.printStackTrace();
        }

        return false;
    }

    public boolean isHealthy() {

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            BASE_URL
                                                    + "/health"
                                    )
                            )
                            .timeout(
                                    Duration.ofSeconds(1)
                            )
                            .GET()
                            .build();

            HttpResponse<String> response =
                    http.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            return response.statusCode()
                    >= 200
                    && response.statusCode()
                    < 300;

        } catch (
                Exception e
        ) {

            return false;
        }
    }

    public void startRecording()
            throws Exception {

        post(
                "/start"
        );
    }

    public String stopRecording()
            throws Exception {

        String json =
                post(
                        "/stop"
                );

        JsonObject object =
                JsonParser.parseString(
                        json
                ).getAsJsonObject();

        if (
                !object.has(
                        "text"
                )
                || object.get(
                        "text"
                ).isJsonNull()
        ) {

            return "";
        }

        return object
                .get(
                        "text"
                )
                .getAsString()
                .trim();
    }

    private String post(
            String endpoint
    ) throws Exception {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                                + endpoint
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(30)
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .noBody()
                        )
                        .build();

        HttpResponse<String> response =
                http.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (
                response.statusCode() < 200
                || response.statusCode() >= 300
        ) {

            throw new IllegalStateException(
                    "STT HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body()
            );
        }

        return response.body();
    }

    private String findPython() {

        if (
                new File(
                        PYTHONW
                ).exists()
        ) {

            return PYTHONW;
        }

        if (
                new File(
                        PYTHON
                ).exists()
        ) {

            return PYTHON;
        }

        return null;
    }

    public void close() {

        if (
                !startedByDebugger
                || process == null
        ) {
            return;
        }

        try {

            if (
                    process.isAlive()
            ) {

                process.destroy();

                if (
                        !process.waitFor(
                                2,
                                TimeUnit.SECONDS
                        )
                ) {

                    process.destroyForcibly();
                }
            }

        } catch (
                Exception ignored
        ) {
        }
    }
}