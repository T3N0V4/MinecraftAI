package com.jano.minecraftai.ai.providers;

import com.jano.minecraftai.ai.AIProvider;
import com.jano.minecraftai.ai.AIRequest;
import com.jano.minecraftai.ai.AIResponse;

public class DebugProvider
        implements AIProvider {

    @Override
    public String getProviderName() {
        return "debug";
    }

    @Override
    public String getModelName() {
        return "debug-model";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public AIResponse respond(
            AIRequest request
    ) {

        long start =
                System.currentTimeMillis();

        String response =
                "Pregunta recibida: "
                        + request.question
                        + "\nImagen: "
                        + request.image.length
                        + " bytes"
                        + "\nTarget: "
                        + request.playerContext.target;

        long duration =
                System.currentTimeMillis()
                        - start;

        return new AIResponse(
                true,
                getProviderName(),
                getModelName(),
                response,
                duration
        );
    }
}
