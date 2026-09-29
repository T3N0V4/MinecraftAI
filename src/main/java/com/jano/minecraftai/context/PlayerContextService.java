package com.jano.minecraftai.context;

import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class PlayerContextService {

    public static String getContext(ServerPlayerEntity player) {

        ServerWorld world = player.getServerWorld();

        // ==========================
        // POSICIÓN
        // ==========================

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String dimension = world
                .getRegistryKey()
                .getValue()
                .toString();

        // ==========================
        // BIOMA
        // ==========================

        RegistryEntry<Biome> biomeEntry =
                world.getBiome(player.getBlockPos());

        String biome = biomeEntry
                .getKey()
                .map(key -> key.getValue().toString())
                .orElse("desconocido");

        // ==========================
        // ESTADO DEL JUGADOR
        // ==========================

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();

        int hunger =
                player.getHungerManager().getFoodLevel();

        float saturation =
                player.getHungerManager().getSaturationLevel();

        int xpLevel =
                player.experienceLevel;

        String gameMode =
                player.interactionManager
                        .getGameMode()
                        .getName();

        // ==========================
        // MANOS
        // ==========================

        String mainHand =
                itemToString(player.getMainHandStack());

        String offHand =
                itemToString(player.getOffHandStack());

        // ==========================
        // ARMADURA
        // ==========================

        List<String> armor = new ArrayList<>();

        for (ItemStack stack : player.getArmorItems()) {

            if (!stack.isEmpty()) {
                armor.add(itemToString(stack));
            }
        }

        String armorText =
                armor.isEmpty()
                        ? "Ninguna"
                        : String.join(", ", armor);

        // ==========================
        // EFECTOS
        // ==========================

        List<String> effects = new ArrayList<>();

        for (
                StatusEffectInstance effect :
                player.getStatusEffects()
        ) {

            String effectId =
                    Registries.STATUS_EFFECT
                            .getId(effect.getEffectType())
                            .toString();

            effects.add(
                    effectId
                            + " nivel "
                            + (effect.getAmplifier() + 1)
            );
        }

        String effectsText =
                effects.isEmpty()
                        ? "Ninguno"
                        : String.join(", ", effects);

        // ==========================
        // BLOQUE APUNTADO
        // ==========================

        String targetBlock =
                getTargetBlock(player);

        // ==========================
        // MUNDO
        // ==========================

        long worldTime =
                world.getTimeOfDay() % 24000;

        String weather;

        if (world.isThundering()) {
            weather = "Tormenta";
        } else if (world.isRaining()) {
            weather = "Lluvia";
        } else {
            weather = "Despejado";
        }

        // ==========================
        // RESPUESTA
        // ==========================

        return "\n[AI] Contexto de "
                + player.getName().getString()

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

                + "\n• Gamemode: "
                + gameMode

                + "\n• Mano principal: "
                + mainHand

                + "\n• Mano secundaria: "
                + offHand

                + "\n• Armadura: "
                + armorText

                + "\n• Efectos: "
                + effectsText

                + "\n• Mirando: "
                + targetBlock

                + "\n• Hora Minecraft: "
                + worldTime

                + "\n• Clima: "
                + weather;
    }

    private static String itemToString(ItemStack stack) {

        if (stack.isEmpty()) {
            return "Vacío";
        }

        String id =
                Registries.ITEM
                        .getId(stack.getItem())
                        .toString();

        return stack.getName().getString()
                + " x"
                + stack.getCount()
                + " ("
                + id
                + ")";
    }

    private static String getTargetBlock(
            ServerPlayerEntity player
    ) {

        HitResult hit =
                player.raycast(
                        6.0,
                        0.0f,
                        false
                );

        if (
                hit.getType()
                != HitResult.Type.BLOCK
        ) {
            return "Ningún bloque";
        }

        BlockHitResult blockHit =
                (BlockHitResult) hit;

        BlockState state =
                player.getWorld()
                        .getBlockState(
                                blockHit.getBlockPos()
                        );

        String blockId =
                Registries.BLOCK
                        .getId(state.getBlock())
                        .toString();

        return state.getBlock()
                .getName()
                .getString()
                + " ("
                + blockId
                + ")"
                + " en "
                + blockHit.getBlockPos().toShortString();
    }
}