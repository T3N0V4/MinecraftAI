package com.jano.minecraftai.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MinecraftAISTTProcess {

    private static final String STT_HEALTH_URL =
            "http://127.0.0.1:8765/health";

    private static final String PROJECT_PATH =
            "C:\\Minecraft\\MinecraftAI";

    private static final String PYTHONW_PATH =
            PROJECT_PATH
                    + "\\.venv\\Scripts\\pythonw.exe";

    private static final String PYTHON_PATH =
            PROJECT_PATH
                    + "\\.venv\\Scripts\\python.exe";

    private static final String SERVER_PATH =
            PROJECT_PATH
                    + "\\voice\\stt_server.py";

    private static final String LOG_PATH =
            PROJECT_PATH
                    + "\\voice\\stt_service.log";

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(
                            Duration.ofSeconds(1)
                    )
                    .build();

    private static volatile Process process;

    private static volatile boolean startedByMinecraft =
            false;

    public static void register() {

        startAsync();

        ClientLifecycleEvents.CLIENT_STOPPING.register(
                client -> stop()
        );
    }

    public static void startAsync() {

        CompletableFuture.runAsync(
                MinecraftAISTTProcess::start
        );
    }

    private static synchronized void start() {

        if (isHealthy()) {

            System.out.println(
                    "[MinecraftAI] STT ya está funcionando."
            );

            return;
        }

        File serverFile =
                new File(
                        SERVER_PATH
                );

        if (!serverFile.exists()) {

            System.err.println(
                    "[MinecraftAI] No existe stt_server.py: "
                            + SERVER_PATH
            );

            return;
        }

        String pythonExecutable =
                choosePythonExecutable();

        if (pythonExecutable == null) {

            System.err.println(
                    "[MinecraftAI] No se encontró Python del entorno virtual."
            );

            return;
        }

        try {

            File logFile =
                    new File(
                            LOG_PATH
                    );

            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            pythonExecutable,
                            SERVER_PATH
                    );

            processBuilder.directory(
                    new File(
                            PROJECT_PATH
                    )
            );

            processBuilder.redirectErrorStream(
                    true
            );

            processBuilder.redirectOutput(
                    ProcessBuilder.Redirect.appendTo(
                            logFile
                    )
            );

            process =
                    processBuilder.start();

            startedByMinecraft =
                    true;

            System.out.println(
                    "[MinecraftAI] Iniciando servicio STT..."
            );

            waitUntilHealthy();

        } catch (Exception e) {

            System.err.println(
                    "[MinecraftAI] No se pudo iniciar STT: "
                            + e.getMessage()
            );
        }
    }

    private static String choosePythonExecutable() {

        File pythonw =
                new File(
                        PYTHONW_PATH
                );

        if (pythonw.exists()) {
            return PYTHONW_PATH;
        }

        File python =
                new File(
                        PYTHON_PATH
                );

        if (python.exists()) {
            return PYTHON_PATH;
        }

        return null;
    }

    private static void waitUntilHealthy() {

        for (
                int attempt = 0;
                attempt < 60;
                attempt++
        ) {

            if (isHealthy()) {

                System.out.println(
                        "[MinecraftAI] Servicio STT listo."
                );

                return;
            }

            if (
                    process != null
                    && !process.isAlive()
            ) {

                System.err.println(
                        "[MinecraftAI] El servicio STT terminó antes de estar listo."
                );

                return;
            }

            try {

                Thread.sleep(
                        500
                );

            } catch (InterruptedException e) {

                Thread.currentThread()
                        .interrupt();

                return;
            }
        }

        System.err.println(
                "[MinecraftAI] STT tardó demasiado en iniciar."
        );
    }

    public static boolean isHealthy() {

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            STT_HEALTH_URL
                                    )
                            )
                            .timeout(
                                    Duration.ofSeconds(1)
                            )
                            .GET()
                            .build();

            HttpResponse<String> response =
                    HTTP_CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            return response.statusCode() >= 200
                    && response.statusCode() < 300;

        } catch (Exception e) {

            return false;
        }
    }

    public static synchronized void stop() {

        if (
                !startedByMinecraft
                || process == null
        ) {
            return;
        }

        try {

            if (process.isAlive()) {

                System.out.println(
                        "[MinecraftAI] Cerrando servicio STT..."
                );

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

        } catch (Exception e) {

            System.err.println(
                    "[MinecraftAI] Error cerrando STT: "
                            + e.getMessage()
            );

        } finally {

            process = null;

            startedByMinecraft =
                    false;
        }
    }

    private MinecraftAISTTProcess() {
    }
}