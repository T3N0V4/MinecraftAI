package com.jano.minecraftai.debug;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.TargetContext;

public class DebugContext {

    private final PlayerContext context =
            new PlayerContext();

    public DebugContext() {
        reset();
    }

    public void reset() {

        context.playerName =
                "Jano";

        context.x = 0;
        context.y = 64;
        context.z = 0;

        context.dimension =
                "minecraft:overworld";

        context.biome =
                "minecraft:plains";

        context.gameMode =
                "survival";

        context.health = 20;
        context.maxHealth = 20;

        context.hunger = 20;
        context.saturation = 5;
        context.xpLevel = 0;

        context.air = 300;

        context.sprinting = false;
        context.sneaking = false;
        context.swimming = false;
        context.onGround = true;
        context.burning = false;

        context.direction =
                "north";

        context.worldTime = 6000;
        context.weather = "clear";
        context.difficulty = "normal";

        context.lightLevel = 15;

        context.mainHand = null;
        context.offHand = null;

        context.armor.clear();
        context.effects.clear();

        context.target =
                TargetContext.none();
    }

    public PlayerContext get() {
        return context;
    }

    public boolean set(
            String field,
            String value
    ) {

        try {

            switch (
                    field.toLowerCase()
            ) {

                case "player",
                     "playername" ->

                        context.playerName =
                                value;

                case "biome" ->

                        context.biome =
                                value;

                case "dimension",
                     "dim" ->

                        context.dimension =
                                value;

                case "gamemode",
                     "mode" ->

                        context.gameMode =
                                value;

                case "health" ->

                        context.health =
                                Float.parseFloat(
                                        value
                                );

                case "maxhealth" ->

                        context.maxHealth =
                                Float.parseFloat(
                                        value
                                );

                case "hunger" ->

                        context.hunger =
                                Integer.parseInt(
                                        value
                                );

                case "xp" ->

                        context.xpLevel =
                                Integer.parseInt(
                                        value
                                );

                case "x" ->

                        context.x =
                                Double.parseDouble(
                                        value
                                );

                case "y" ->

                        context.y =
                                Double.parseDouble(
                                        value
                                );

                case "z" ->

                        context.z =
                                Double.parseDouble(
                                        value
                                );

                case "direction" ->

                        context.direction =
                                value;

                case "weather" ->

                        context.weather =
                                value;

                case "difficulty" ->

                        context.difficulty =
                                value;

                case "light" ->

                        context.lightLevel =
                                Integer.parseInt(
                                        value
                                );

                case "target" -> {

                    if (
                            value.equalsIgnoreCase(
                                    "none"
                            )
                    ) {

                        context.target =
                                TargetContext.none();

                    } else {

                        context.target =
                                new TargetContext(
                                        "block",
                                        value,
                                        value,
                                        0,
                                        64,
                                        0,
                                        3.0
                                );
                    }
                }

                default -> {
                    return false;
                }
            }

            return true;

        } catch (
                NumberFormatException e
        ) {

            return false;
        }
    }

    public String describe() {

        return context.toString();
    }
}