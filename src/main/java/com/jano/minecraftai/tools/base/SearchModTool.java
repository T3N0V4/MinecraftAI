package com.jano.minecraftai.tools.base;

import com.jano.minecraftai.tools.KnowledgeToolUtil;
import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class SearchModTool
        implements MinecraftAITool {

    private static final int MAX_RESULTS =
            20;

    @Override
    public String getName() {
        return "search_mod";
    }

    @Override
    public String getDescription() {

        return "Busca mods instalados por nombre o id.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }


    @Override
    public Map<String, String> getArguments() {

        return Map.of(
                "query",
                "Nombre o id del mod que se desea buscar."
        );
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        String query =
                context.getArgument(
                        "query"
                );

        if (
                query == null
                || query.isBlank()
        ) {

            return ToolResult.failure(
                    "Falta el argumento 'query'."
            );
        }

        String normalized =
                query.toLowerCase(
                        Locale.ROOT
                );

        List<ModContainer> matches =
                new ArrayList<>();

        for (
                ModContainer mod :
                FabricLoader
                        .getInstance()
                        .getAllMods()
        ) {

            String id =
                    mod.getMetadata()
                            .getId();

            String name =
                    mod.getMetadata()
                            .getName();

            if (
                    id.toLowerCase(
                            Locale.ROOT
                    ).contains(normalized)
                    ||
                    name.toLowerCase(
                            Locale.ROOT
                    ).contains(normalized)
            ) {

                matches.add(
                        mod
                );
            }
        }

        matches.sort(
                Comparator.comparing(
                        mod ->
                                mod.getMetadata()
                                        .getId()
                )
        );

        if (
                matches.size()
                > MAX_RESULTS
        ) {

            matches =
                    new ArrayList<>(
                            matches.subList(
                                    0,
                                    MAX_RESULTS
                            )
                    );
        }

        StringBuilder json =
                new StringBuilder();

        json.append(
                "{\n"
        );

        json.append(
                "  \"query\": \""
                        + KnowledgeToolUtil.escape(
                                query
                        )
                        + "\",\n"
        );

        json.append(
                "  \"matches\": [\n"
        );

        for (
                int i = 0;
                i < matches.size();
                i++
        ) {

            ModContainer mod =
                    matches.get(i);
            json.append(
                    KnowledgeToolUtil.modToJson(
                            mod
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