package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;
import com.jano.minecraftai.context.TargetContext;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class GetModOriginTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_mod_origin";
    }

    @Override
    public String getDescription() {

        return "Detecta de qué mod proviene el bloque, entidad u objeto actual.";
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

        String id = null;
        String name = null;
        String source = null;

        if (
                target != null
                && target.id != null
                && !"none".equals(target.type)
        ) {

            id =
                    target.id;

            name =
                    target.name;

            source =
                    target.type;

        } else {

            ItemStack stack =
                    context.player
                            .getMainHandStack();

            if (stack.isEmpty()) {

                stack =
                        context.player
                                .getOffHandStack();
            }

            if (!stack.isEmpty()) {

                id =
                        Registries.ITEM
                                .getId(
                                        stack.getItem()
                                )
                                .toString();

                name =
                        stack.getName()
                                .getString();

                source =
                        "item";
            }
        }

        if (id == null) {

            return ToolResult.failure(
                    "No hay un bloque, entidad u objeto actual del que detectar el mod."
            );
        }

        String namespace =
                KnowledgeToolUtil.namespace(
                        id
                );

        return ToolResult.success(
                """
                {
                  "source": "%s",
                  "id": "%s",
                  "name": "%s",
                  "mod_id": "%s",
                  "mod_name": "%s"
                }
                """
                .formatted(
                        source,
                        id,
                        KnowledgeToolUtil.escape(
                                name
                        ),
                        namespace,
                        KnowledgeToolUtil.escape(
                                KnowledgeToolUtil.modName(
                                        namespace
                                )
                        )
                )
        );
    }
}