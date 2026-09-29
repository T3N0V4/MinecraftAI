package com.jano.minecraftai.tools;

import com.jano.minecraftai.tools.base.GetBlockInfoTool;
import com.jano.minecraftai.tools.base.GetCurrentStructureTool;
import com.jano.minecraftai.tools.base.GetHeldItemTool;
import com.jano.minecraftai.tools.base.GetInstalledModsTool;
import com.jano.minecraftai.tools.base.GetInventoryTool;
import com.jano.minecraftai.tools.base.GetItemInfoTool;
import com.jano.minecraftai.tools.base.GetModOriginTool;
import com.jano.minecraftai.tools.base.GetRecipeTool;
import com.jano.minecraftai.tools.base.GetRecipesUsingTool;
import com.jano.minecraftai.tools.base.GetTargetBlockTool;
import com.jano.minecraftai.tools.base.GetTargetEntityTool;
import com.jano.minecraftai.tools.base.GetVisibleEntitiesTool;
import com.jano.minecraftai.tools.base.SearchModTool;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.Map;

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

        REGISTRY.register(
                new GetItemInfoTool()
        );

        REGISTRY.register(
                new GetBlockInfoTool()
        );

        REGISTRY.register(
                new GetModOriginTool()
        );

        REGISTRY.register(
                new GetRecipeTool()
        );

        REGISTRY.register(
                new GetRecipesUsingTool()
        );

        REGISTRY.register(
                new GetInventoryTool()
        );

        REGISTRY.register(
                new GetInstalledModsTool()
        );

        REGISTRY.register(
                new SearchModTool()
        );
    }

    public static ToolRegistry getRegistry() {
        return REGISTRY;
    }

    public static ToolResult execute(
            String name,
            ServerPlayerEntity player
    ) {

        return execute(
                name,
                player,
                Collections.emptyMap()
        );
    }

    public static ToolResult execute(
            String name,
            ServerPlayerEntity player,
            Map<String, String> arguments
    ) {

        return REGISTRY.execute(
                name,
                new ToolContext(
                        player,
                        arguments
                )
        );
    }

    private ToolManager() {
    }
}