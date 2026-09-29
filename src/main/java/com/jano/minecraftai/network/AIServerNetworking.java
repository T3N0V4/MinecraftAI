package com.jano.minecraftai.network;

import com.jano.minecraftai.ai.AIModelRouter;
import com.jano.minecraftai.ai.AIRequest;
import com.jano.minecraftai.ai.AIResponse;
import com.jano.minecraftai.ai.AIService;

import com.jano.minecraftai.ai.providers.DebugProvider;
import com.jano.minecraftai.ai.providers.GeminiProvider;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.network.PacketByteBuf;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.ByteArrayOutputStream;

import java.util.Map;
import java.util.UUID;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AIServerNetworking {

    private static final int MAX_IMAGE_SIZE =
            1_000_000;

    private static final int MAX_CHUNK_SIZE =
            25_000;

    private static final int MAX_CHUNKS =
            100;

    /*
     * Cada request tiene un UUID distinto.
     * Acá guardamos temporalmente sus partes
     * hasta recibir la imagen completa.
     */
    private static final Map<UUID, PendingRequest>
            pendingRequests =
            new ConcurrentHashMap<>();

    private static final AIService aiService;

    static {

        AIModelRouter router =
                new AIModelRouter();

        router.addProvider(
                new GeminiProvider()
        );

        router.addProvider(
                new DebugProvider()
        );

        aiService =
                new AIService(router);
    }

    public static void register() {

        registerBeginReceiver();
        registerChunkReceiver();
    }

    private static void registerBeginReceiver() {

        ServerPlayNetworking.registerGlobalReceiver(
                NetworkConstants.ASK_BEGIN,
                (
                        server,
                        player,
                        handler,
                        buffer,
                        responseSender
                ) -> {

                    UUID requestId =
                            buffer.readUuid();

                    String pregunta =
                            buffer.readString(2048);

                    int totalBytes =
                            buffer.readInt();

                    int totalChunks =
                            buffer.readInt();

                    if (
                            totalBytes <= 0
                            || totalBytes > MAX_IMAGE_SIZE
                            || totalChunks <= 0
                            || totalChunks > MAX_CHUNKS
                    ) {

                        System.err.println(
                                "[MinecraftAI] Request inválido de "
                                        + player.getName().getString()
                        );

                        return;
                    }

                    PendingRequest request =
                            new PendingRequest(
                                    player.getUuid(),
                                    pregunta,
                                    totalBytes,
                                    totalChunks
                            );

                    pendingRequests.put(
                            requestId,
                            request
                    );

                    System.out.println(
                            "[MinecraftAI] Inicio request "
                                    + requestId
                                    + " de "
                                    + player.getName().getString()
                                    + " | "
                                    + totalBytes
                                    + " bytes | "
                                    + totalChunks
                                    + " chunks"
                    );
                }
        );
    }

    private static void registerChunkReceiver() {

        ServerPlayNetworking.registerGlobalReceiver(
                NetworkConstants.ASK_CHUNK,
                (
                        server,
                        player,
                        handler,
                        buffer,
                        responseSender
                ) -> {

                    UUID requestId =
                            buffer.readUuid();

                    int chunkIndex =
                            buffer.readInt();

                    byte[] chunk =
                            buffer.readByteArray(
                                    MAX_CHUNK_SIZE
                            );

                    PendingRequest request =
                            pendingRequests.get(
                                    requestId
                            );

                    if (request == null) {
                        return;
                    }

                    /*
                     * Evita que otro jugador pueda
                     * completar un request ajeno.
                     */
                    if (
                            !request.playerId.equals(
                                    player.getUuid()
                            )
                    ) {
                        return;
                    }

                    if (
                            chunkIndex < 0
                            || chunkIndex
                            >= request.totalChunks
                    ) {
                        return;
                    }

                    boolean completed =
                            request.addChunk(
                                    chunkIndex,
                                    chunk
                            );

                    if (!completed) {
                        return;
                    }

                    pendingRequests.remove(
                            requestId
                    );

                    byte[] image =
                            request.assemble();

                    if (
                            image == null
                            || image.length
                            != request.totalBytes
                    ) {

                        System.err.println(
                                "[MinecraftAI] Imagen incompleta en request "
                                        + requestId
                        );

                        return;
                    }

                    procesarPregunta(
                            server,
                            player,
                            request.question,
                            image
                    );
                }
        );
    }

    private static void procesarPregunta(
            MinecraftServer server,
            ServerPlayerEntity player,
            String pregunta,
            byte[] imagen
    ) {

        server.execute(() -> {

            PlayerContext context =
                    PlayerContextService
                            .getContext(player);

            AIRequest request =
                    new AIRequest(
                            pregunta,
                            imagen,
                            context
                    );

            sendStatus(player, "Pensando...");

            new Thread(
                    () -> {

                        AIResponse response =
                                aiService.respondWithTools(request, player);

                        server.execute(() -> {

                            if (
                                    player.isDisconnected()
                            ) {
                                return;
                            }

                            sendResponse(player, response);
                        });
                    },
                    "MinecraftAI-Request"
            ).start();
        });
    }


    private static void sendStatus(
            ServerPlayerEntity player,
            String status
    ) {

        PacketByteBuf buffer =
                PacketByteBufs.create();

        buffer.writeString(
                status,
                256
        );

        ServerPlayNetworking.send(
                player,
                NetworkConstants.AI_STATUS,
                buffer
        );
    }

    private static void sendResponse(
            ServerPlayerEntity player,
            AIResponse response
    ) {

        PacketByteBuf buffer =
                PacketByteBufs.create();

        buffer.writeString(
                response.content == null
                        ? ""
                        : response.content,
                32767
        );

        buffer.writeString(
                response.provider == null
                        ? ""
                        : response.provider,
                128
        );

        buffer.writeString(
                response.model == null
                        ? ""
                        : response.model,
                128
        );

        buffer.writeLong(
                response.durationMs
        );

        ServerPlayNetworking.send(
                player,
                NetworkConstants.AI_RESPONSE,
                buffer
        );

        sendStatus(
                player,
                "Listo"
        );
    }
    private static class PendingRequest {

        private final UUID playerId;

        private final String question;

        private final int totalBytes;

        private final int totalChunks;

        private final byte[][] chunks;

        private final AtomicInteger received =
                new AtomicInteger(0);

        private PendingRequest(
                UUID playerId,
                String question,
                int totalBytes,
                int totalChunks
        ) {

            this.playerId =
                    playerId;

            this.question =
                    question;

            this.totalBytes =
                    totalBytes;

            this.totalChunks =
                    totalChunks;

            this.chunks =
                    new byte[totalChunks][];
        }

        private synchronized boolean addChunk(
                int index,
                byte[] data
        ) {

            /*
             * Si llega duplicado, no lo contamos
             * dos veces.
             */
            if (chunks[index] != null) {
                return false;
            }

            chunks[index] =
                    data;

            return received.incrementAndGet()
                    == totalChunks;
        }

        private synchronized byte[] assemble() {

            try {

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream(
                                totalBytes
                        );

                for (byte[] chunk : chunks) {

                    if (chunk == null) {
                        return null;
                    }

                    output.write(
                            chunk
                    );
                }

                return output.toByteArray();

            } catch (Exception e) {

                e.printStackTrace();

                return null;
            }
        }
    }

    private AIServerNetworking() {
    }
}