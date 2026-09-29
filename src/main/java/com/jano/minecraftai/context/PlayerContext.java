package com.jano.minecraftai.context;

import java.util.ArrayList;
import java.util.List;

public class PlayerContext {

    public String playerName;

    public double x;
    public double y;
    public double z;

    public String dimension;
    public String biome;
    public String gameMode;

    public float health;
    public float maxHealth;

    public int hunger;
    public float saturation;
    public int xpLevel;

    public int air;

    public boolean sprinting;
    public boolean sneaking;
    public boolean swimming;
    public boolean onGround;
    public boolean burning;

    public String direction;

    public ItemContext mainHand;
    public ItemContext offHand;

    public List<ItemContext> armor =
            new ArrayList<>();

    public List<String> effects =
            new ArrayList<>();

    public TargetContext target;

    public long worldTime;
    public String weather;
    public String difficulty;

    public int lightLevel;

    @Override
    public String toString() {

        String armorText =
                armor.isEmpty()
                        ? "Ninguna"
                        : armor.toString();

        String effectsText =
                effects.isEmpty()
                        ? "Ninguno"
                        : String.join(", ", effects);

        return "\n[AI] Contexto de "
                + playerName

                + "\n• Posición: "
                + String.format(
                        "X %.1f | Y %.1f | Z %.1f",
                        x,
                        y,
                        z
                )

                + "\n• Dimensión: "
                + dimension

                + "\n• Bioma: "
                + biome

                + "\n• Dirección: "
                + direction

                + "\n• Vida: "
                + health
                + "/"
                + maxHealth

                + "\n• Hambre: "
                + hunger
                + "/20"

                + "\n• Saturación: "
                + String.format("%.1f", saturation)

                + "\n• XP: nivel "
                + xpLevel

                + "\n• Aire: "
                + air

                + "\n• Gamemode: "
                + gameMode

                + "\n• Corriendo: "
                + sprinting

                + "\n• Agachado: "
                + sneaking

                + "\n• Nadando: "
                + swimming

                + "\n• En suelo: "
                + onGround

                + "\n• En fuego: "
                + burning

                + "\n• Mano principal: "
                + mainHand

                + "\n• Mano secundaria: "
                + offHand

                + "\n• Armadura: "
                + armorText

                + "\n• Efectos: "
                + effectsText

                + "\n• Mirando: "
                + target

                + "\n• Luz: "
                + lightLevel

                + "\n• Dificultad: "
                + difficulty

                + "\n• Hora Minecraft: "
                + worldTime

                + "\n• Clima: "
                + weather;
    }
}