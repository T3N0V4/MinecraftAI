package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class GetInventoryTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_inventory";
    }

    @Override
    public String getDescription() {

        return "Obtiene el inventario real del jugador.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        PlayerInventory inventory =
                context.player
                        .getInventory();

        StringBuilder json =
                new StringBuilder();

        json.append(
                "{\n  \"items\": [\n"
        );

        boolean first =
                true;

        for (
                int slot = 0;
                slot < inventory.size();
                slot++
        ) {

            ItemStack stack =
                    inventory.getStack(
                            slot
                    );

            if (stack.isEmpty()) {
                continue;
            }

            String id =
                    Registries.ITEM
                            .getId(
                                    stack.getItem()
                            )
                            .toString();

            if (!first) {
                json.append(",\n");
            }

            first =
                    false;

            json.append(
                    """
                        {
                          "slot": %d,
                          "id": "%s",
                          "name": "%s",
                          "mod_id": "%s",
                          "count": %d
                        }
                    """
                    .formatted(
                            slot,
                            id,
                            KnowledgeToolUtil.escape(
                                    stack.getName()
                                            .getString()
                            ),
                            KnowledgeToolUtil.namespace(
                                    id
                            ),
                            stack.getCount()
                    )
            );
        }

        json.append(
                "\n  ]\n}"
        );

        return ToolResult.success(
                json.toString()
        );
    }
}