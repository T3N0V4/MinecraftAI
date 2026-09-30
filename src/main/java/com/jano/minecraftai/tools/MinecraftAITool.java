package com.jano.minecraftai.tools;

import java.util.Collections;
import java.util.Map;

public interface MinecraftAITool {

    String getName();

    String getDescription();

    ToolTier getTier();


    default boolean requiresVision() {

        return false;
    }


    default ToolCachePolicy getCachePolicy() {

        return ToolCachePolicy.NONE;
    }


    default ToolCacheScope getCacheScope() {

        return ToolCacheScope.PLAYER;
    }


    /*
     * Argumentos que esta tool acepta.
     *
     * key   = nombre del argumento
     * value = descripción corta
     */
    default Map<String, String> getArguments() {

        return Collections.emptyMap();
    }


    ToolResult execute(
            ToolContext context
    );
}