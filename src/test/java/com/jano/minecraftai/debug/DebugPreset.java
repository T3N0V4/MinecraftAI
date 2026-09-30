package com.jano.minecraftai.debug;

import java.util.ArrayList;
import java.util.List;

public final class DebugPreset {

    public final String name;
    public final String description;

    public String playerName = "Jano";

    public String biome = "minecraft:plains";
    public String dimension = "minecraft:overworld";
    public String gameMode = "survival";

    public float health = 20;
    public int hunger = 20;
    public int xp = 0;

    public String structureId = "none";
    public String targetId = "none";

    public String weather = "Despejado";
    public String difficulty = "normal";
    public String direction = "north";

    public int worldTime = 6000;
    public int light = 15;

    public int saturation = 5;
    public int air = 300;

    public String mainHand = "none";
    public String offHand = "none";

    public String helmet = "none";
    public String chestplate = "none";
    public String leggings = "none";
    public String boots = "none";

    public final List<InventoryEntry> inventory =
            new ArrayList<>();

    public final List<EffectEntry> effects =
            new ArrayList<>();

    public final List<VisibleEntityEntry> visibleEntities =
            new ArrayList<>();

    public DebugPreset(
            String name,
            String description
    ) {

        this.name =
                name;

        this.description =
                description;
    }

    public DebugPreset player(
            float health,
            int hunger,
            int xp,
            String gameMode
    ) {

        this.health =
                health;

        this.hunger =
                hunger;

        this.xp =
                xp;

        this.gameMode =
                gameMode;

        return this;
    }

    public DebugPreset environment(
            String biome,
            String dimension,
            String structureId
    ) {

        this.biome =
                biome;

        this.dimension =
                dimension;

        this.structureId =
                structureId;

        return this;
    }

    public DebugPreset world(
            String weather,
            String difficulty,
            String direction,
            int worldTime,
            int light
    ) {

        this.weather =
                weather;

        this.difficulty =
                difficulty;

        this.direction =
                direction;

        this.worldTime =
                worldTime;

        this.light =
                light;

        return this;
    }

    public DebugPreset state(
            int saturation,
            int air
    ) {

        this.saturation =
                saturation;

        this.air =
                air;

        return this;
    }

    public DebugPreset target(
            String targetId
    ) {

        this.targetId =
                targetId;

        return this;
    }

    public DebugPreset equipment(
            String mainHand,
            String offHand
    ) {

        this.mainHand =
                mainHand;

        this.offHand =
                offHand;

        return this;
    }

    public DebugPreset armor(
            String helmet,
            String chestplate,
            String leggings,
            String boots
    ) {

        this.helmet =
                helmet;

        this.chestplate =
                chestplate;

        this.leggings =
                leggings;

        this.boots =
                boots;

        return this;
    }

    public DebugPreset inventory(
            String id,
            int count
    ) {

        inventory.add(
                new InventoryEntry(
                        id,
                        count
                )
        );

        return this;
    }

    public DebugPreset effect(
            String id,
            int level,
            int seconds
    ) {

        effects.add(
                new EffectEntry(
                        id,
                        level,
                        seconds
                )
        );

        return this;
    }

    public DebugPreset entity(
            String id,
            double distance,
            boolean hostile,
            float health
    ) {

        visibleEntities.add(
                new VisibleEntityEntry(
                        id,
                        distance,
                        hostile,
                        health
                )
        );

        return this;
    }

    @Override
    public String toString() {

        return name;
    }

    public record InventoryEntry(
            String id,
            int count
    ) {
    }

    public record EffectEntry(
            String id,
            int level,
            int seconds
    ) {
    }

    public record VisibleEntityEntry(
            String id,
            double distance,
            boolean hostile,
            float health
    ) {
    }
}