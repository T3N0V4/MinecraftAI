package com.jano.minecraftai.tools;

import net.minecraft.server.network.ServerPlayerEntity;

public class ToolContext {

    public final ServerPlayerEntity player;

    public ToolContext(
            ServerPlayerEntity player
    ) {
        this.player = player;
    }
}