package com.jano.minecraftai.tools.base;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

public class GetHeldItemTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_held_item";
    }

    @Override
    public String getDescription() {

        return "Obtiene los objetos que el jugador tiene en ambas manos.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        ItemStack main =
                context.player
                        .getMainHandStack();

        ItemStack off =
                context.player
                        .getOffHandStack();

        return ToolResult.success(
                """
                {
                  "main_hand": %s,
                  "off_hand": %s
                }
                """
                .formatted(
                        itemToJson(main),
                        itemToJson(off)
                )
        );
    }

    private String itemToJson(
            ItemStack stack
    ) {

        if (stack.isEmpty()) {
            return """
                   {
                     "empty": true
                   }
                   """;
        }

        String id =
                Registries.ITEM
                        .getId(
                                stack.getItem()
                        )
                        .toString();

        String namespace =
                KnowledgeToolUtil.namespace(
                        id
                );

        int maxDamage =
                stack.getMaxDamage();

        int durability =
                maxDamage > 0
                        ? maxDamage
                        - stack.getDamage()
                        : 0;

        return """
               {
                 "id": "%s",
                 "name": "%s",
                 "mod_id": "%s",
                 "count": %d,
                 "durability": %d,
                 "max_durability": %d
               }
               """
                .formatted(
                        id,
                        stack.getName()
                                .getString(),
                        namespace,
                        stack.getCount(),
                        durability,
                        maxDamage
                );
    }
}