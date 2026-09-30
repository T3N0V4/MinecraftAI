package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;
import com.jano.minecraftai.context.TargetContext;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

public class GetTargetEntityTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_target_entity";
    }

    @Override
    public String getDescription() {

        return "Obtiene la entidad exacta que el jugador está mirando.";
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
                || !"entity".equals(target.type)
        ) {

            return ToolResult.failure(
                    "El jugador no está apuntando a una entidad."
            );
        }

        String modId =
                KnowledgeToolUtil.namespace(
                        target.id
                );

        return ToolResult.success(
                """
                {
                  "type": "entity",
                  "id": "%s",
                  "name": "%s",
                  "mod_id": "%s",
                  "position": [%d, %d, %d],
                  "distance": %.2f,
                  "health": %s,
                  "max_health": %s
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
                        String.valueOf(
                                target.health
                        ),
                        String.valueOf(
                                target.maxHealth
                        )
                )
        );
    }
}