package com.jano.minecraftai.debug;

import java.util.List;
import java.util.Map;

public final class DebugCatalog {

    public static final List<String> BIOMES =
            List.of(
                    "minecraft:badlands",
                    "minecraft:cherry_grove",
                    "minecraft:crimson_forest",
                    "minecraft:deep_dark",
                    "minecraft:desert",
                    "minecraft:forest",
                    "minecraft:jungle",
                    "minecraft:nether_wastes",
                    "minecraft:plains",
                    "minecraft:savanna",
                    "minecraft:snowy_plains",
                    "minecraft:swamp",
                    "minecraft:taiga",
                    "minecraft:the_end",
                    "minecraft:warped_forest"
            );

    public static final List<String> DIMENSIONS =
            List.of(
                    "minecraft:overworld",
                    "minecraft:the_end",
                    "minecraft:the_nether"
            );

    public static final List<String> GAMEMODES =
            List.of(
                    "adventure",
                    "creative",
                    "spectator",
                    "survival"
            );

    public static final List<String> TARGETS =
            List.of(
                    "create:andesite_casing",
                    "create:depot",
                    "create:fluid_tank",
                    "create:mechanical_press",
                    "farmersdelight:cooking_pot",
                    "farmersdelight:cutting_board",
                    "farmersdelight:stove",
                    "minecraft:chest",
                    "minecraft:crafting_table",
                    "minecraft:creeper",
                    "minecraft:cut_sandstone",
                    "minecraft:diamond_ore",
                    "minecraft:enderman",
                    "minecraft:stone",
                    "minecraft:zombie"
            );

    public static final Map<String, String> DISPLAY_NAMES =
            Map.ofEntries(
                    Map.entry("minecraft:stone", "Piedra"),
                    Map.entry("minecraft:cut_sandstone", "Arenisca cortada"),
                    Map.entry("minecraft:diamond_ore", "Mena de diamante"),
                    Map.entry("minecraft:chest", "Cofre"),
                    Map.entry("minecraft:crafting_table", "Mesa de crafteo"),
                    Map.entry("minecraft:creeper", "Creeper"),
                    Map.entry("minecraft:enderman", "Enderman"),
                    Map.entry("minecraft:zombie", "Zombie"),

                    Map.entry("create:andesite_casing", "Andesite Casing"),
                    Map.entry("create:mechanical_press", "Mechanical Press"),
                    Map.entry("create:depot", "Depot"),
                    Map.entry("create:fluid_tank", "Fluid Tank"),

                    Map.entry("farmersdelight:cutting_board", "Cutting Board"),
                    Map.entry("farmersdelight:cooking_pot", "Cooking Pot"),
                    Map.entry("farmersdelight:stove", "Stove")
            );

    public static final List<String> STRUCTURES =
            List.of(
                    "none",
                    "betterdeserttemples:desert_temple",
                    "minecraft:ancient_city",
                    "minecraft:desert_pyramid",
                    "minecraft:end_city",
                    "minecraft:fortress",
                    "minecraft:mineshaft",
                    "minecraft:stronghold",
                    "minecraft:village_plains"
            );

    public static final Map<String, String> STRUCTURE_NAMES =
            Map.ofEntries(
                    Map.entry("none", "Ninguna"),
                    Map.entry(
                            "betterdeserttemples:desert_temple",
                            "Better Desert Temple"
                    ),
                    Map.entry(
                            "minecraft:ancient_city",
                            "Ancient City"
                    ),
                    Map.entry(
                            "minecraft:desert_pyramid",
                            "Desert Pyramid"
                    ),
                    Map.entry(
                            "minecraft:end_city",
                            "End City"
                    ),
                    Map.entry(
                            "minecraft:fortress",
                            "Nether Fortress"
                    ),
                    Map.entry(
                            "minecraft:mineshaft",
                            "Mineshaft"
                    ),
                    Map.entry(
                            "minecraft:stronghold",
                            "Stronghold"
                    ),
                    Map.entry(
                            "minecraft:village_plains",
                            "Plains Village"
                    )
            );

    public static String getModOrigin(
            String id
    ) {

        if (
                id == null
                || !id.contains(":")
        ) {

            return "Origen desconocido";
        }

        String namespace =
                id.substring(
                        0,
                        id.indexOf(':')
                );

        return switch (
                namespace
        ) {

            case "minecraft" ->
                    "Minecraft / vanilla";

            case "create" ->
                    "Create";

            case "farmersdelight" ->
                    "Farmer's Delight";

            case "betterdeserttemples" ->
                    "Better Desert Temples";

            default ->
                    namespace;
        };
    }

    private DebugCatalog() {
    }
}