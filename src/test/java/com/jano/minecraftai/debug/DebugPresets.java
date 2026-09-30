package com.jano.minecraftai.debug;

import java.util.List;

public final class DebugPresets {

    public static List<DebugPreset> all() {

        return List.of(

                new DebugPreset(
                        "Básico / vacío",
                        "Jugador normal en una zona tranquila. Sirve para preguntas generales."
                )
                        .environment(
                                "minecraft:plains",
                                "minecraft:overworld",
                                "none"
                        )
                        .world(
                                "Despejado",
                                "normal",
                                "north",
                                6000,
                                15
                        ),

                new DebugPreset(
                        "Explorando una aldea",
                        "Jugador dentro de una aldea con un aldeano cerca."
                )
                        .environment(
                                "minecraft:plains",
                                "minecraft:overworld",
                                "minecraft:village_plains"
                        )
                        .world(
                                "Despejado",
                                "normal",
                                "east",
                                7000,
                                15
                        )
                        .equipment(
                                "minecraft:iron_sword",
                                "minecraft:shield"
                        )
                        .inventory(
                                "minecraft:bread",
                                8
                        )
                        .inventory(
                                "minecraft:torch",
                                16
                        )
                        .entity(
                                "minecraft:villager",
                                7,
                                false,
                                20
                        ),

                new DebugPreset(
                        "Better Desert Temple",
                        "Jugador dentro de un templo modificado por Better Desert Temples mirando un bloque vanilla."
                )
                        .environment(
                                "minecraft:desert",
                                "minecraft:overworld",
                                "betterdeserttemples:desert_temple"
                        )
                        .target(
                                "minecraft:cut_sandstone"
                        )
                        .world(
                                "Despejado",
                                "normal",
                                "south",
                                8000,
                                12
                        )
                        .equipment(
                                "minecraft:iron_pickaxe",
                                "none"
                        )
                        .inventory(
                                "minecraft:torch",
                                24
                        ),

                new DebugPreset(
                        "Combate con zombie",
                        "Un zombie está muy cerca y el jugador está preparado para combatir."
                )
                        .environment(
                                "minecraft:plains",
                                "minecraft:overworld",
                                "none"
                        )
                        .target(
                                "minecraft:zombie"
                        )
                        .world(
                                "Despejado",
                                "normal",
                                "north",
                                14000,
                                7
                        )
                        .equipment(
                                "minecraft:iron_sword",
                                "minecraft:shield"
                        )
                        .inventory(
                                "minecraft:bread",
                                6
                        )
                        .entity(
                                "minecraft:zombie",
                                4,
                                true,
                                20
                        ),

                new DebugPreset(
                        "Creeper muy cerca",
                        "Hay un creeper peligrosamente cerca del jugador."
                )
                        .environment(
                                "minecraft:plains",
                                "minecraft:overworld",
                                "none"
                        )
                        .target(
                                "minecraft:creeper"
                        )
                        .world(
                                "Despejado",
                                "hard",
                                "west",
                                15000,
                                6
                        )
                        .equipment(
                                "minecraft:iron_sword",
                                "minecraft:shield"
                        )
                        .entity(
                                "minecraft:creeper",
                                3,
                                true,
                                20
                        ),

                new DebugPreset(
                        "Minería · diamante",
                        "Jugador minando bajo tierra y mirando mena de diamante."
                )
                        .environment(
                                "minecraft:deep_dark",
                                "minecraft:overworld",
                                "none"
                        )
                        .target(
                                "minecraft:diamond_ore"
                        )
                        .world(
                                "Despejado",
                                "normal",
                                "north",
                                9000,
                                4
                        )
                        .equipment(
                                "minecraft:diamond_pickaxe",
                                "minecraft:torch"
                        )
                        .inventory(
                                "minecraft:torch",
                                32
                        )
                        .inventory(
                                "minecraft:iron_ingot",
                                12
                        ),

                new DebugPreset(
                        "Create · máquina",
                        "Jugador mirando una máquina del mod Create."
                )
                        .environment(
                                "minecraft:plains",
                                "minecraft:overworld",
                                "none"
                        )
                        .target(
                                "create:andesite_casing"
                        )
                        .equipment(
                                "minecraft:iron_pickaxe",
                                "none"
                        )
                        .inventory(
                                "create:andesite_alloy",
                                8
                        ),

                new DebugPreset(
                        "Farmer's Delight · cocina",
                        "Jugador frente a una Cooking Pot de Farmer's Delight."
                )
                        .environment(
                                "minecraft:plains",
                                "minecraft:overworld",
                                "none"
                        )
                        .target(
                                "farmersdelight:cooking_pot"
                        )
                        .inventory(
                                "farmersdelight:tomato",
                                12
                        )
                        .inventory(
                                "minecraft:bread",
                                4
                        ),

                new DebugPreset(
                        "Jugador con poca vida",
                        "Jugador herido con poca comida y un zombie visible."
                )
                        .player(
                                5,
                                6,
                                8,
                                "survival"
                        )
                        .environment(
                                "minecraft:forest",
                                "minecraft:overworld",
                                "none"
                        )
                        .world(
                                "Lluvia",
                                "normal",
                                "south",
                                16000,
                                5
                        )
                        .equipment(
                                "minecraft:iron_sword",
                                "none"
                        )
                        .inventory(
                                "minecraft:golden_apple",
                                1
                        )
                        .inventory(
                                "minecraft:bread",
                                2
                        )
                        .entity(
                                "minecraft:zombie",
                                8,
                                true,
                                20
                        ),

                new DebugPreset(
                        "Jugador con efectos",
                        "Jugador bajo varios efectos para probar si la IA interpreta su estado."
                )
                        .environment(
                                "minecraft:forest",
                                "minecraft:overworld",
                                "none"
                        )
                        .effect(
                                "minecraft:speed",
                                2,
                                90
                        )
                        .effect(
                                "minecraft:strength",
                                1,
                                45
                        )
                        .effect(
                                "minecraft:regeneration",
                                1,
                                15
                        ),

                new DebugPreset(
                        "Nether",
                        "Jugador explorando el Nether con poca luz."
                )
                        .environment(
                                "minecraft:nether_wastes",
                                "minecraft:the_nether",
                                "minecraft:fortress"
                        )
                        .world(
                                "Despejado",
                                "normal",
                                "east",
                                6000,
                                8
                        )
                        .equipment(
                                "minecraft:iron_sword",
                                "minecraft:shield"
                        )
                        .inventory(
                                "minecraft:golden_apple",
                                2
                        )
                        .inventory(
                                "minecraft:torch",
                                12
                        ),

                new DebugPreset(
                        "Deep Dark",
                        "Jugador explorando una Ancient City en condiciones de muy poca luz."
                )
                        .environment(
                                "minecraft:deep_dark",
                                "minecraft:overworld",
                                "minecraft:ancient_city"
                        )
                        .world(
                                "Despejado",
                                "hard",
                                "north",
                                18000,
                                1
                        )
                        .equipment(
                                "minecraft:diamond_pickaxe",
                                "none"
                        )
                        .inventory(
                                "minecraft:torch",
                                20
                        )
        );
    }

    private DebugPresets() {
    }
}