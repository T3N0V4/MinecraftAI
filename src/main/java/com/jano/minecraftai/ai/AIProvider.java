package com.jano.minecraftai.ai;

public interface AIProvider {

    String getProviderName();

    String getModelName();

    boolean isAvailable();

    AIResponse respond(AIRequest request);
}
