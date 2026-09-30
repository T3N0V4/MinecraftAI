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

public class GetInstalledModsTool
        implements MinecraftAITool {

    @Override
    public String getName() {
        return "get_installed_mods";
    }

    @Override
    public String getDescription() {

        return "Obtiene la lista real de mods cargados en el servidor.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        List<ModContainer> mods =
                new ArrayList<>(
                        FabricLoader
                                .getInstance()
                                .getAllMods()
                );

        mods.sort(
                Comparator.comparing(
                        mod ->
                                mod.getMetadata()
                                        .getId()
                )
        );

        StringBuilder json =
                new StringBuilder();

        json.append(
                "{\n"
        );

        json.append(
                "  \"count\": "
                        + mods.size()
                        + ",\n"
        );

        json.append(
                "  \"mods\": [\n"
        );

        for (
                int i = 0;
                i < mods.size();
                i++
        ) {

            ModContainer mod =
                    mods.get(i);
            json.append(
                    KnowledgeToolUtil.modToJson(
                            mod
                    )
            );

            if (
                    i < mods.size() - 1
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