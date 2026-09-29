package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class GetItemInfoTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_item_info";
    }

    @Override
    public String getDescription() {

        return "Obtiene información real del objeto que el jugador sostiene.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        ItemStack stack =
                context.player
                        .getMainHandStack();

        String hand =
                "main_hand";

        if (stack.isEmpty()) {

            stack =
                    context.player
                            .getOffHandStack();

            hand =
                    "off_hand";
        }

        if (stack.isEmpty()) {

            return ToolResult.failure(
                    "El jugador no tiene ningún objeto en las manos."
            );
        }

        String id =
                Registries.ITEM
                        .getId(stack.getItem())
                        .toString();

        String namespace =
                KnowledgeToolUtil.namespace(id);

        int maxDamage =
                stack.getMaxDamage();

        int durability =
                maxDamage > 0
                        ? maxDamage - stack.getDamage()
                        : 0;

        return ToolResult.success(
                """
                {
                  "id": "%s",
                  "name": "%s",
                  "mod_id": "%s",
                  "mod_name": "%s",
                  "hand": "%s",
                  "count": %d,
                  "max_stack_size": %d,
                  "damageable": %s,
                  "durability": %d,
                  "max_durability": %d
                }
                """
                .formatted(
                        id,
                        KnowledgeToolUtil.escape(
                                stack.getName()
                                        .getString()
                        ),
                        namespace,
                        KnowledgeToolUtil.escape(
                                KnowledgeToolUtil.modName(
                                        namespace
                                )
                        ),
                        hand,
                        stack.getCount(),
                        stack.getMaxCount(),
                        stack.isDamageable(),
                        durability,
                        maxDamage
                )
        );
    }
}