package com.jano.minecraftai.ai;

import com.jano.minecraftai.tools.ToolResult;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public interface ToolExecutionGateway {

    boolean exists(
            String name
    );

    ToolResult execute(
            String name,
            ServerPlayerEntity player,
            Map<String, String> arguments
    );
}