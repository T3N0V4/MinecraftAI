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

    public static final List<String> WEATHER =
            List.of(
                    "Despejado",
                    "Lluvia",
                    "Tormenta"
            );

    public static final List<String> DIFFICULTIES =
            List.of(
                    "easy",
                    "hard",
                    "normal",
                    "peaceful"
            );

    public static final List<String> DIRECTIONS =
            List.of(
                    "east",
                    "north",
                    "south",
                    "west"
            );

    public static final List<String> TARGETS =
            List.of(
                    "none",

                    "create:andesite_casing",
                    "create:depot",
                    "create:fluid_tank",
                    "create:mechanical_press",

                    "farmersdelight:cooking_pot",
                    "farmersdelight:cutting_board",
                    "farmersdelight:stove",

                    "minecraft:chest",
                    "minecraft:crafting_table",
                    "minecraft:cut_sandstone",
                    "minecraft:diamond_ore",
                    "minecraft:stone",

                    "minecraft:creeper",
                    "minecraft:enderman",
                    "minecraft:skeleton",
                    "minecraft:villager",
                    "minecraft:zombie"
            );

    public static final Map<String, String> DISPLAY_NAMES =
            Map.ofEntries(
                    Map.entry("none", "Nada"),

                    Map.entry("minecraft:stone", "Piedra"),
                    Map.entry("minecraft:cut_sandstone", "Arenisca cortada"),
                    Map.entry("minecraft:diamond_ore", "Mena de diamante"),
                    Map.entry("minecraft:chest", "Cofre"),
                    Map.entry("minecraft:crafting_table", "Mesa de crafteo"),

                    Map.entry("minecraft:creeper", "Creeper"),
                    Map.entry("minecraft:enderman", "Enderman"),
                    Map.entry("minecraft:skeleton", "Skeleton"),
                    Map.entry("minecraft:villager", "Villager"),
                    Map.entry("minecraft:zombie", "Zombie"),

                    Map.entry("create:andesite_casing", "Andesite Casing"),
                    Map.entry("create:mechanical_press", "Mechanical Press"),
                    Map.entry("create:depot", "Depot"),
                    Map.entry("create:fluid_tank", "Fluid Tank"),

                    Map.entry("farmersdelight:cutting_board", "Cutting Board"),
                    Map.entry("farmersdelight:cooking_pot", "Cooking Pot"),
                    Map.entry("farmersdelight:stove", "Stove")
            );

    public static final Map<String, String> TARGET_TYPES =
            Map.ofEntries(
                    Map.entry("none", "none"),

                    Map.entry("minecraft:stone", "block"),
                    Map.entry("minecraft:cut_sandstone", "block"),
                    Map.entry("minecraft:diamond_ore", "block"),
                    Map.entry("minecraft:chest", "block"),
                    Map.entry("minecraft:crafting_table", "block"),

                    Map.entry("create:andesite_casing", "block"),
                    Map.entry("create:mechanical_press", "block"),
                    Map.entry("create:depot", "block"),
                    Map.entry("create:fluid_tank", "block"),

                    Map.entry("farmersdelight:cutting_board", "block"),
                    Map.entry("farmersdelight:cooking_pot", "block"),
                    Map.entry("farmersdelight:stove", "block"),

                    Map.entry("minecraft:creeper", "entity"),
                    Map.entry("minecraft:enderman", "entity"),
                    Map.entry("minecraft:skeleton", "entity"),
                    Map.entry("minecraft:villager", "entity"),
                    Map.entry("minecraft:zombie", "entity")
            );

    public static final List<String> ITEMS =
            List.of(
                    "none",
                    "create:andesite_alloy",
                    "farmersdelight:tomato",
                    "minecraft:apple",
                    "minecraft:bread",
                    "minecraft:diamond",
                    "minecraft:diamond_pickaxe",
                    "minecraft:golden_apple",
                    "minecraft:iron_ingot",
                    "minecraft:iron_pickaxe",
                    "minecraft:iron_sword",
                    "minecraft:shield",
                    "minecraft:torch"
            );

    public static final Map<String, String> ITEM_NAMES =
            Map.ofEntries(
                    Map.entry("none", "Vacío"),
                    Map.entry("minecraft:apple", "Manzana"),
                    Map.entry("minecraft:bread", "Pan"),
                    Map.entry("minecraft:diamond", "Diamante"),
                    Map.entry("minecraft:diamond_pickaxe", "Pico de diamante"),
                    Map.entry("minecraft:golden_apple", "Manzana dorada"),
                    Map.entry("minecraft:iron_ingot", "Lingote de hierro"),
                    Map.entry("minecraft:iron_pickaxe", "Pico de hierro"),
                    Map.entry("minecraft:iron_sword", "Espada de hierro"),
                    Map.entry("minecraft:shield", "Escudo"),
                    Map.entry("minecraft:torch", "Antorcha"),
                    Map.entry("create:andesite_alloy", "Andesite Alloy"),
                    Map.entry("farmersdelight:tomato", "Tomato")
            );

    public static final List<String> ARMOR =
            List.of(
                    "none",
                    "minecraft:diamond_boots",
                    "minecraft:diamond_chestplate",
                    "minecraft:diamond_helmet",
                    "minecraft:diamond_leggings",
                    "minecraft:iron_boots",
                    "minecraft:iron_chestplate",
                    "minecraft:iron_helmet",
                    "minecraft:iron_leggings"
            );

    public static final Map<String, String> ARMOR_NAMES =
            Map.ofEntries(
                    Map.entry("none", "Vacío"),
                    Map.entry("minecraft:diamond_boots", "Botas de diamante"),
                    Map.entry("minecraft:diamond_chestplate", "Pechera de diamante"),
                    Map.entry("minecraft:diamond_helmet", "Casco de diamante"),
                    Map.entry("minecraft:diamond_leggings", "Pantalones de diamante"),
                    Map.entry("minecraft:iron_boots", "Botas de hierro"),
                    Map.entry("minecraft:iron_chestplate", "Pechera de hierro"),
                    Map.entry("minecraft:iron_helmet", "Casco de hierro"),
                    Map.entry("minecraft:iron_leggings", "Pantalones de hierro")
            );

    public static final List<String> EFFECTS =
            List.of(
                    "minecraft:absorption",
                    "minecraft:fire_resistance",
                    "minecraft:haste",
                    "minecraft:night_vision",
                    "minecraft:poison",
                    "minecraft:regeneration",
                    "minecraft:resistance",
                    "minecraft:slowness",
                    "minecraft:speed",
                    "minecraft:strength",
                    "minecraft:weakness"
            );

    public static final List<String> ENTITIES =
            List.of(
                    "minecraft:creeper",
                    "minecraft:enderman",
                    "minecraft:skeleton",
                    "minecraft:villager",
                    "minecraft:zombie"
            );

    public static final Map<String, String> ENTITY_NAMES =
            Map.ofEntries(
                    Map.entry("minecraft:creeper", "Creeper"),
                    Map.entry("minecraft:enderman", "Enderman"),
                    Map.entry("minecraft:skeleton", "Skeleton"),
                    Map.entry("minecraft:villager", "Villager"),
                    Map.entry("minecraft:zombie", "Zombie")
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
                    Map.entry("betterdeserttemples:desert_temple", "Better Desert Temple"),
                    Map.entry("minecraft:ancient_city", "Ancient City"),
                    Map.entry("minecraft:desert_pyramid", "Desert Pyramid"),
                    Map.entry("minecraft:end_city", "End City"),
                    Map.entry("minecraft:fortress", "Nether Fortress"),
                    Map.entry("minecraft:mineshaft", "Mineshaft"),
                    Map.entry("minecraft:stronghold", "Stronghold"),
                    Map.entry("minecraft:village_plains", "Plains Village")
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

        return switch (namespace) {

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