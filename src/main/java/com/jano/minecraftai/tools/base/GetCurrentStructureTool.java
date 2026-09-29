package com.jano.minecraftai.tools.base;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.structure.StructureStart;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;

import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;

import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

public class GetCurrentStructureTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_current_structure";
    }

    @Override
    public String getDescription() {

        return "Detecta si el jugador se encuentra dentro de una estructura generada.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        ServerPlayerEntity player =
                context.player;

        ServerWorld world =
                player.getServerWorld();

        StructureAccessor accessor =
                world.getStructureAccessor();

        Registry<Structure> registry =
                world.getRegistryManager()
                        .get(
                                RegistryKeys.STRUCTURE
                        );

        for (
                Structure structure :
                registry
        ) {

            StructureStart start =
                    accessor.getStructureContaining(
                            player.getBlockPos(),
                            structure
                    );

            if (
                    start == null
                    || !start.hasChildren()
            ) {
                continue;
            }

            Identifier id =
                    registry.getId(
                            structure
                    );

            if (id == null) {
                continue;
            }

            BlockBox box =
                    start.getBoundingBox();

            return ToolResult.success(
                    """
                    {
                      "inside_structure": true,
                      "id": "%s",
                      "mod_id": "%s",
                      "start_chunk": [%d, %d],
                      "bounding_box": {
                        "min": [%d, %d, %d],
                        "max": [%d, %d, %d]
                      }
                    }
                    """
                    .formatted(
                            id,
                            id.getNamespace(),
                            start.getPos().x,
                            start.getPos().z,
                            box.getMinX(),
                            box.getMinY(),
                            box.getMinZ(),
                            box.getMaxX(),
                            box.getMaxY(),
                            box.getMaxZ()
                    )
            );
        }

        return ToolResult.success(
                """
                {
                  "inside_structure": false
                }
                """
        );
    }
}