package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;
import com.jano.minecraftai.context.TargetContext;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class GetBlockInfoTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_block_info";
    }

    @Override
    public String getDescription() {

        return "Obtiene información real del bloque que el jugador está mirando.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        PlayerContext playerContext =
                PlayerContextService.getContext(
                        context.player
                );

        TargetContext target =
                playerContext.target;

        if (
                target == null
                || !"block".equals(target.type)
        ) {

            return ToolResult.failure(
                    "El jugador no está mirando un bloque."
            );
        }

        BlockPos pos =
                new BlockPos(
                        target.x,
                        target.y,
                        target.z
                );

        BlockState state =
                context.player
                        .getServerWorld()
                        .getBlockState(pos);

        String namespace =
                KnowledgeToolUtil.namespace(
                        target.id
                );

        float hardness =
                state.getHardness(
                        context.player
                                .getServerWorld(),
                        pos
                );

        float blastResistance =
                state.getBlock()
                        .getBlastResistance();

        return ToolResult.success(
                """
                {
                  "id": "%s",
                  "name": "%s",
                  "mod_id": "%s",
                  "mod_name": "%s",
                  "position": [%d, %d, %d],
                  "distance": %.2f,
                  "air": %s,
                  "luminance": %d,
                  "hardness": %.2f,
                  "blast_resistance": %.2f,
                  "properties": "%s"
                }
                """
                .formatted(
                        target.id,
                        KnowledgeToolUtil.escape(
                                target.name
                        ),
                        namespace,
                        KnowledgeToolUtil.escape(
                                KnowledgeToolUtil.modName(
                                        namespace
                                )
                        ),
                        target.x,
                        target.y,
                        target.z,
                        target.distance,
                        state.isAir(),
                        state.getLuminance(),
                        hardness,
                        blastResistance,
                        KnowledgeToolUtil.escape(
                                target.properties
                                        .toString()
                        )
                )
        );
    }
}