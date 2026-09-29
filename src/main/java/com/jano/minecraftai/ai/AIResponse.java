package com.jano.minecraftai.ai;

public class AIResponse {

    public final boolean success;
    public final String provider;
    public final String model;
    public final String content;
    public final long durationMs;

    public AIResponse(
            boolean success,
            String provider,
            String model,
            String content,
            long durationMs
    ) {
        this.success = success;
        this.provider = provider;
        this.model = model;
        this.content = content;
        this.durationMs = durationMs;
    }

    public static AIResponse failure(
            String provider,
            String model,
            String message,
            long durationMs
    ) {
        return new AIResponse(
                false,
                provider,
                model,
                message,
                durationMs
        );
    }
}
