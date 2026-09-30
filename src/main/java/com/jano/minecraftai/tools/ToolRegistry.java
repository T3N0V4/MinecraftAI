package com.jano.minecraftai.tools;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ToolRegistry {

    private static final long WARNING_MS =
            500;

    private static final long SLOW_MS =
            1500;


    private final Map<String, MinecraftAITool> tools =
            new LinkedHashMap<>();

    private final ToolCache cache =
            new ToolCache();


    public void register(
            MinecraftAITool tool
    ) {

        if (
                tool == null
        ) {

            throw new IllegalArgumentException(
                    "No se puede registrar una tool null."
            );
        }


        String name =
                tool.getName();


        if (
                name == null
                || name.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Una tool debe tener nombre."
            );
        }


        if (
                tools.containsKey(
                        name
                )
        ) {

            throw new IllegalStateException(
                    "Tool duplicada: "
                            + name
            );
        }


        tools.put(
                name,
                tool
        );


        System.out.println(
                "[MinecraftAI][ToolRegistry] "
                        + name
                        + " | tier="
                        + tool.getTier()
                        + " | vision="
                        + tool.requiresVision()
                        + " | cache="
                        + tool.getCachePolicy()
                        + " | scope="
                        + tool.getCacheScope()
        );
    }


    public MinecraftAITool get(
            String name
    ) {

        return tools.get(
                name
        );
    }


    public boolean exists(
            String name
    ) {

        return tools.containsKey(
                name
        );
    }


    public ToolResult execute(
            String name,
            ToolContext context
    ) {

        MinecraftAITool tool =
                tools.get(
                        name
                );


        if (
                tool == null
        ) {

            return ToolResult.failure(
                    "Tool desconocida: "
                            + name
            );
        }


        ToolCachePolicy cachePolicy =
                tool.getCachePolicy();

        String cacheKey =
                null;


        if (
                cachePolicy.isEnabled()
        ) {

            cacheKey =
                    buildCacheKey(
                            tool,
                            context
                    );


            ToolResult cached =
                    cache.get(
                            cacheKey
                    );


            if (
                    cached != null
            ) {

                System.out.println(
                        "[MinecraftAI][Tool] OK "
                                + name
                                + " -> 0 ms"
                                + " | cache=HIT"
                );

                return cached;
            }
        }


        long startedAt =
                System.currentTimeMillis();


        try {

            ToolResult result =
                    tool.execute(
                            context
                    );


            long elapsed =
                    System.currentTimeMillis()
                            - startedAt;


            logTiming(
                    name,
                    elapsed,
                    cachePolicy.isEnabled()
                            ? "MISS"
                            : "OFF"
            );


            if (
                    cachePolicy.isEnabled()
                    && result != null
                    && result.success
                    && cacheKey != null
            ) {

                cache.put(
                        cacheKey,
                        result,
                        cachePolicy.getDurationMs()
                );
            }


            return result;

        } catch (
                Exception e
        ) {

            long elapsed =
                    System.currentTimeMillis()
                            - startedAt;


            logTiming(
                    name,
                    elapsed,
                    cachePolicy.isEnabled()
                            ? "MISS"
                            : "OFF"
            );


            System.err.println(
                    "[MinecraftAI][Tool] ERROR "
                            + name
                            + ": "
                            + e.getClass()
                                    .getSimpleName()
                            + ": "
                            + e.getMessage()
            );


            e.printStackTrace();


            return ToolResult.failure(
                    "Error ejecutando "
                            + name
                            + ": "
                            + e.getMessage()
            );
        }
    }


    private String buildCacheKey(
            MinecraftAITool tool,
            ToolContext context
    ) {

        StringBuilder key =
                new StringBuilder();


        key.append(
                tool.getName()
        );


        ToolCacheScope scope =
                tool.getCacheScope();


        ServerPlayerEntity player =
                context.player;


        if (
                scope == ToolCacheScope.PLAYER
                || scope == ToolCacheScope.CHUNK
        ) {

            if (
                    player != null
            ) {

                key.append(
                        "|player="
                );

                key.append(
                        player.getUuid()
                );
            }
        }


        if (
                scope == ToolCacheScope.CHUNK
                && player != null
        ) {

            key.append(
                    "|dimension="
            );

            key.append(
                    player
                            .getServerWorld()
                            .getRegistryKey()
                            .getValue()
            );


            ChunkPos chunk =
                    new ChunkPos(
                            player.getBlockPos()
                    );


            key.append(
                    "|chunk="
            );

            key.append(
                    chunk.x
            );

            key.append(
                    ","
            );

            key.append(
                    chunk.z
            );
        }


        /*
         * Los argumentos siempre forman parte de la clave.
         *
         * Ejemplo:
         * get_recipe(item=diamond_sword)
         * no puede compartir cache con
         * get_recipe(item=iron_pickaxe).
         */
        if (
                context.arguments != null
                && !context.arguments.isEmpty()
        ) {

            key.append(
                    "|args="
            );

            key.append(
                    context.arguments
            );
        }


        return key.toString();
    }


    private void logTiming(
            String name,
            long elapsed,
            String cacheState
    ) {

        String level;


        if (
                elapsed >= SLOW_MS
        ) {

            level = "SLOW";

        } else if (
                elapsed >= WARNING_MS
        ) {

            level = "WARN";

        } else {

            level = "OK";
        }


        System.out.println(
                "[MinecraftAI][Tool] "
                        + level
                        + " "
                        + name
                        + " -> "
                        + elapsed
                        + " ms"
                        + " | cache="
                        + cacheState
        );
    }


    public void clearCache() {

        cache.clear();
    }


    public Set<String> getNames() {

        return tools.keySet();
    }


    public Collection<MinecraftAITool> getTools() {

        return tools.values();
    }
}