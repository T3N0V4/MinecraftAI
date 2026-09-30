package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolCachePolicy;
import com.jano.minecraftai.tools.ToolCacheScope;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.structure.StructureStart;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;

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

        BlockPos playerPos =
                player.getBlockPos();

        ChunkPos chunkPos =
                new ChunkPos(
                        playerPos
                );

        /*
         * IMPORTANTE:
         *
         * Antes recorríamos TODAS las estructuras
         * registradas en el modpack y preguntábamos
         * una por una si contenían al jugador.
         *
         * Ahora pedimos solamente los StructureStart
         * relacionados con el chunk actual.
         */
        List<StructureStart> starts =
                accessor.getStructureStarts(
                        chunkPos,
                        structure -> true
                );

        if (
                starts == null
                || starts.isEmpty()
        ) {

            return notInsideStructure();
        }


        Registry<Structure> registry =
                world.getRegistryManager()
                        .get(
                                RegistryKeys.STRUCTURE
                        );


        for (
                StructureStart start :
                starts
        ) {

            if (
                    start == null
                    || !start.hasChildren()
            ) {
                continue;
            }

            /*
             * structureContains usa la información
             * real de la estructura para comprobar
             * si esta posición pertenece a ella.
             */
            if (
                    !accessor.structureContains(
                            playerPos,
                            start
                    )
            ) {
                continue;
            }


            Structure structure =
                    start.getStructure();

            Identifier id =
                    registry.getId(
                            structure
                    );

            if (
                    id == null
            ) {
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


        return notInsideStructure();
    }


    private ToolResult notInsideStructure() {

        return ToolResult.success(
                """
                {
                  "inside_structure": false
                }
                """
        );
    }
}