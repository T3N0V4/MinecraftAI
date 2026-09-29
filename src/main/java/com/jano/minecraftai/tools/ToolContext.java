package com.jano.minecraftai.tools;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToolContext {

    public final ServerPlayerEntity player;

    public final Map<String, String> arguments;

    public ToolContext(
            ServerPlayerEntity player
    ) {

        this(
                player,
                Collections.emptyMap()
        );
    }

    public ToolContext(
            ServerPlayerEntity player,
            Map<String, String> arguments
    ) {

        this.player =
                player;

        this.arguments =
                arguments == null
                        ? Collections.emptyMap()
                        : new LinkedHashMap<>(
                                arguments
                        );
    }

    public String getArgument(
            String name
    ) {

        return arguments.get(name);
    }
}