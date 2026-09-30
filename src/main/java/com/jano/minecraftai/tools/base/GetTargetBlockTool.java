package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;
import com.jano.minecraftai.context.TargetContext;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

public class GetTargetBlockTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_target_block";
    }

    @Override
    public String getDescription() {

        return "Obtiene el bloque exacto que el jugador está mirando.";
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
                    "El jugador no está apuntando a un bloque."
            );
        }

        String modId =
                KnowledgeToolUtil.namespace(
                        target.id
                );

        return ToolResult.success(
                """
                {
                  "type": "block",
                  "id": "%s",
                  "name": "%s",
                  "mod_id": "%s",
                  "position": [%d, %d, %d],
                  "distance": %.2f,
                  "properties": "%s"
                }
                """
                .formatted(
                        target.id,
                        target.name,
                        modId,
                        target.x,
                        target.y,
                        target.z,
                        target.distance,
                        target.properties
                )
        );
    }
}