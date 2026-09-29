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

public class GetRecipeTool
        implements MinecraftAITool {

    private static final int MAX_RECIPES =
            10;

    @Override
    public String getName() {
        return "get_recipe";
    }

    @Override
    public String getDescription() {

        return "Busca recetas reales del servidor cuyo resultado sea el objeto sostenido.";
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
                    "El jugador no sostiene ningún objeto para buscar su receta."
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

            ItemStack output =
                    recipe.getOutput(
                            world.getRegistryManager()
                    );

            if (
                    !output.isEmpty()
                    && output.isOf(
                            target.getItem()
                    )
            ) {

                matches.add(
                        recipe
                );

                if (
                        matches.size()
                        >= MAX_RECIPES
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

        if (matches.isEmpty()) {

            return ToolResult.success(
                    """
                    {
                      "item": "%s",
                      "recipes_found": 0,
                      "recipes": []
                    }
                    """
                    .formatted(
                            targetId
                    )
            );
        }

        StringBuilder json =
                new StringBuilder();

        json.append(
                "{\n"
        );

        json.append(
                "  \"item\": \""
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

            String typeId =
                    String.valueOf(
                            Registries.RECIPE_TYPE
                                    .getId(
                                            recipe.getType()
                                    )
                    );

            json.append(
                    "    {\n"
            );

            json.append(
                    "      \"recipe_id\": \""
                            + recipe.getId()
                            + "\",\n"
            );

            json.append(
                    "      \"type\": \""
                            + typeId
                            + "\",\n"
            );

            json.append(
                    "      \"output_count\": "
                            + output.getCount()
                            + ",\n"
            );

            json.append(
                    "      \"ingredients\": "
                            + ingredientsToJson(
                                    recipe
                            )
                            + "\n"
            );

            json.append(
                    "    }"
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

    private String ingredientsToJson(
            Recipe<?> recipe
    ) {

        StringBuilder json =
                new StringBuilder(
                        "["
                );

        List<Ingredient> ingredients =
                recipe.getIngredients();

        for (
                int i = 0;
                i < ingredients.size();
                i++
        ) {

            Ingredient ingredient =
                    ingredients.get(i);

            ItemStack[] choices =
                    ingredient
                            .getMatchingStacks();

            json.append("[");

            int maxChoices =
                    Math.min(
                            choices.length,
                            8
                    );

            for (
                    int j = 0;
                    j < maxChoices;
                    j++
            ) {

                String id =
                        Registries.ITEM
                                .getId(
                                        choices[j]
                                                .getItem()
                                )
                                .toString();

                json.append(
                        "\""
                                + KnowledgeToolUtil.escape(
                                        id
                                )
                                + "\""
                );

                if (
                        j < maxChoices - 1
                ) {
                    json.append(",");
                }
            }

            json.append("]");

            if (
                    i < ingredients.size() - 1
            ) {
                json.append(",");
            }
        }

        json.append("]");

        return json.toString();
    }
}