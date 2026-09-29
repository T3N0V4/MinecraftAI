package com.jano.minecraftai.ai;

import java.util.ArrayList;
import java.util.List;

public class AIModelRouter {

    private final List<AIProvider> providers =
            new ArrayList<>();

    public void addProvider(
            AIProvider provider
    ) {

        providers.add(
                provider
        );
    }

    public AIResponse respond(
            AIRequest request
    ) {

        for (
                AIProvider provider :
                providers
        ) {

            if (!provider.isAvailable()) {

                System.out.println(
                        "[MinecraftAI] Provider no disponible: "
                                + provider.getProviderName()
                );

                continue;
            }

            AIResponse response;

            try {

                long start =
                        System.currentTimeMillis();

                response =
                        provider.respond(
                                request
                        );

                long elapsed =
                        System.currentTimeMillis()
                                - start;

                System.out.println(
                        "[MinecraftAI] "
                                + provider.getProviderName()
                                + " respondió en "
                                + elapsed
                                + " ms"
                );

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

            if (response != null) {

                System.err.println(
                        "[MinecraftAI] "
                                + provider.getProviderName()
                                + " falló: "
                                + response.content
                );
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