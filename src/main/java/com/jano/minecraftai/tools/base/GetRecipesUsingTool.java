package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class GetRecipesUsingTool
        implements MinecraftAITool {

    private static final int MAX_RESULTS =
            20;

    @Override
    public String getName() {
        return "get_recipes_using";
    }

    @Override
    public String getDescription() {

        return "Busca recetas reales que puedan usar el objeto sostenido como ingrediente.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        ItemStack target =
                context.player
                        .getMainHandStack();

        if (target.isEmpty()) {

            target =
                    context.player
                            .getOffHandStack();
        }

        if (target.isEmpty()) {

            return ToolResult.failure(
                    "El jugador no sostiene ningún objeto para buscar sus usos."
            );
        }

        ServerWorld world =
                context.player
                        .getServerWorld();

        RecipeManager manager =
                world.getRecipeManager();

        List<Recipe<?>> matches =
                new ArrayList<>();

        for (
                Recipe<?> recipe :
                manager.values()
        ) {

            boolean usesItem =
                    false;

            for (
                    Ingredient ingredient :
                    recipe.getIngredients()
            ) {

                if (
                        ingredient.test(
                                target
                        )
                ) {

                    usesItem =
                            true;

                    break;
                }
            }

            if (usesItem) {

                matches.add(
                        recipe
                );

                if (
                        matches.size()
                        >= MAX_RESULTS
                ) {
                    break;
                }
            }
        }

        String targetId =
                Registries.ITEM
                        .getId(
                                target.getItem()
                        )
                        .toString();

        StringBuilder json =
                new StringBuilder();

        json.append("{\n");

        json.append(
                "  \"ingredient\": \""
                        + targetId
                        + "\",\n"
        );

        json.append(
                "  \"recipes_found\": "
                        + matches.size()
                        + ",\n"
        );

        json.append(
                "  \"recipes\": [\n"
        );

        for (
                int i = 0;
                i < matches.size();
                i++
        ) {

            Recipe<?> recipe =
                    matches.get(i);

            ItemStack output =
                    recipe.getOutput(
                            world.getRegistryManager()
                    );

            String outputId =
                    output.isEmpty()
                            ? "unknown"
                            : Registries.ITEM
                                    .getId(
                                            output.getItem()
                                    )
                                    .toString();

            String typeId =
                    String.valueOf(
                            Registries.RECIPE_TYPE
                                    .getId(
                                            recipe.getType()
                                    )
                    );

            json.append(
                    """
                        {
                          "recipe_id": "%s",
                          "type": "%s",
                          "output_id": "%s",
                          "output_name": "%s",
                          "output_count": %d
                        }
                    """
                    .formatted(
                            recipe.getId(),
                            typeId,
                            outputId,
                            output.isEmpty()
                                    ? "unknown"
                                    : KnowledgeToolUtil.escape(
                                            output.getName()
                                                    .getString()
                                    ),
                            output.isEmpty()
                                    ? 0
                                    : output.getCount()
                    )
            );

            if (
                    i < matches.size() - 1
            ) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append(
                "  ]\n}"
        );

        return ToolResult.success(
                json.toString()
        );
    }
}