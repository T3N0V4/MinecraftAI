package com.jano.minecraftai.tools;

import com.jano.minecraftai.tools.base.GetCurrentStructureTool;
import com.jano.minecraftai.tools.base.GetHeldItemTool;
import com.jano.minecraftai.tools.base.GetTargetBlockTool;
import com.jano.minecraftai.tools.base.GetTargetEntityTool;
import com.jano.minecraftai.tools.base.GetVisibleEntitiesTool;

import net.minecraft.server.network.ServerPlayerEntity;

public class ToolManager {

    private static final ToolRegistry REGISTRY =
            new ToolRegistry();

    static {

        REGISTRY.register(
                new GetTargetBlockTool()
        );

        REGISTRY.register(
                new GetTargetEntityTool()
        );

        REGISTRY.register(
                new GetHeldItemTool()
        );

        REGISTRY.register(
                new GetVisibleEntitiesTool()
        );

        REGISTRY.register(
                new GetCurrentStructureTool()
        );
    }

    public static ToolRegistry getRegistry() {
        return REGISTRY;
    }

    public static ToolResult execute(
            String name,
            ServerPlayerEntity player
    ) {

        return REGISTRY.execute(
                name,
                new ToolContext(player)
        );
    }

    private ToolManager() {
    }
}