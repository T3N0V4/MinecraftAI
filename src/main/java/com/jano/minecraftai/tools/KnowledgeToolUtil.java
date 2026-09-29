package com.jano.minecraftai.tools;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;

public class KnowledgeToolUtil {

    public static String namespace(
            String id
    ) {

        if (
                id == null
                || !id.contains(":")
        ) {
            return "unknown";
        }

        return id.substring(
                0,
                id.indexOf(':')
        );
    }

    public static String modName(
            String namespace
    ) {

        if ("minecraft".equals(namespace)) {
            return "Minecraft";
        }

        Optional<ModContainer> container =
                FabricLoader
                        .getInstance()
                        .getModContainer(namespace);

        return container
                .map(mod ->
                        mod.getMetadata()
                                .getName()
                )
                .orElse(namespace);
    }

    public static String escape(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private KnowledgeToolUtil() {
    }
}