package com.jano.minecraftai.ai;

import java.util.ArrayList;
import java.util.List;

public class AIModelRouter {

    private final List<AIProvider> providers =
            new ArrayList<>();

    public void addProvider(
            AIProvider provider
    ) {
        providers.add(provider);
    }

    public AIResponse respond(
            AIRequest request
    ) {

        for (AIProvider provider : providers) {

            if (!provider.isAvailable()) {
                continue;
            }

            AIResponse response;

            try {

                response =
                        provider.respond(request);

            } catch (Exception e) {

                System.err.println(
                        "[MinecraftAI] Error en "
                                + provider.getProviderName()
                                + ": "
                                + e.getMessage()
                );

                continue;
            }

            if (
                    response != null
                    && response.success
            ) {
                return response;
            }
        }

        return new AIResponse(
                false,
                "none",
                "none",
                "Ningún modelo pudo responder.",
                0
        );
    }
}
