package com.jano.minecraftai.context;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public class PlayerContextService {

public class PlayerContextService {

    public static PlayerContext getContext(ServerPlayerEntity player) {

        ServerWorld world = player.getServerWorld();

        PlayerContext context = new PlayerContext();

        // ==========================
        // JUGADOR
        // ==========================

        context.playerName =
                player.getName().getString();

        context.x = player.getX();
        context.y = player.getY();
        context.z = player.getZ();

        context.dimension =
                world.getRegistryKey()
                        .getValue()
                        .toString();

        RegistryEntry<Biome> biomeEntry =
                world.getBiome(player.getBlockPos());

        context.biome =
                biomeEntry
                        .getKey()
                        .map(key -> key.getValue().toString())
                        .orElse("desconocido");

        context.gameMode =
                player.interactionManager
                        .getGameMode()
                        .getName();

        // ==========================
        // ESTADO
        // ==========================

        context.health =
                player.getHealth();

        context.maxHealth =
                player.getMaxHealth();

        context.hunger =
                player.getHungerManager()
                        .getFoodLevel();

        context.saturation =
                player.getHungerManager()
                        .getSaturationLevel();

        context.xpLevel =
                player.experienceLevel;

        context.air =
                player.getAir();

        context.sprinting =
                player.isSprinting();

        context.sneaking =
                player.isSneaking();

        context.swimming =
                player.isSwimming();

        context.onGround =
                player.isOnGround();

        context.burning =
                player.isOnFire();

        context.direction =
                player.getHorizontalFacing()
                        .getName();

        // ==========================
        // EQUIPO
        // ==========================

        context.mainHand =
                itemToContext(
                        player.getMainHandStack()
                );

        context.offHand =
                itemToContext(
                        player.getOffHandStack()
                );

        for (ItemStack stack : player.getArmorItems()) {

            if (!stack.isEmpty()) {

                context.armor.add(
                        itemToContext(stack)
                );
            }
        }

        // ==========================
        // EFECTOS
        // ==========================

        for (
                StatusEffectInstance effect :
                player.getStatusEffects()
        ) {

            String effectId =
                    Registries.STATUS_EFFECT
                            .getId(effect.getEffectType())
                            .toString();

            int seconds =
                    effect.getDuration() / 20;

            context.effects.add(
                    effectId
                            + " nivel "
                            + (effect.getAmplifier() + 1)
                            + " ("
                            + seconds
                            + "s)"
            );
        }

        // ==========================
        // OBJETIVO
        // ==========================

        context.target =
                getTarget(player);

        // ==========================
        // MUNDO
        // ==========================

        context.worldTime =
                world.getTimeOfDay() % 24000;

        if (world.isThundering()) {

            context.weather =
                    "Tormenta";

        } else if (world.isRaining()) {

            context.weather =
                    "Lluvia";

        } else {

            context.weather =
                    "Despejado";
        }

        context.difficulty =
                world.getDifficulty()
                        .getName();

        int blockLight =
                world.getLightLevel(
                        LightType.BLOCK,
                        player.getBlockPos()
                );

        int skyLight =
                world.getLightLevel(
                        LightType.SKY,
                        player.getBlockPos()
                );

        context.lightLevel =
                Math.max(
                        blockLight,
                        skyLight
                );

        return context;
    }

    private static ItemContext itemToContext(
            ItemStack stack
    ) {

        if (stack.isEmpty()) {

            return new ItemContext(
                    null,
                    "Vacío",
                    0,
                    0,
                    0
            );
        }

        String id =
                Registries.ITEM
                        .getId(stack.getItem())
                        .toString();

        int maxDurability =
                stack.getMaxDamage();

        int durability = 0;

        if (maxDurability > 0) {

            durability =
                    maxDurability
                    - stack.getDamage();
        }

        return new ItemContext(
                id,
                stack.getName().getString(),
                stack.getCount(),
                durability,
                maxDurability
        );
    }

    private static TargetContext getTarget(
        ServerPlayerEntity player
        ) {

            double maxDistance = 6.0;

            Vec3d start =
                    player.getCameraPosVec(1.0f);

            Vec3d direction =
                    player.getRotationVec(1.0f);

            Vec3d end =
                    start.add(
                            direction.multiply(maxDistance)
                    );

            // ==========================
            // BUSCAR ENTIDAD
            // ==========================

            Box searchBox =
                    player.getBoundingBox()
                            .stretch(
                                    direction.multiply(maxDistance)
                            )
                            .expand(1.0);

            EntityHitResult entityHit =
                    ProjectileUtil.raycast(
                            player,
                            start,
                            end,
                            searchBox,
                            entity ->
                                    !entity.isSpectator()
                                    && entity.canHit(),
                            maxDistance * maxDistance
                    );

            // ==========================
            // BUSCAR BLOQUE
            // ==========================

            HitResult blockHitRaw =
                    player.raycast(
                            maxDistance,
                            0.0f,
                            false
                    );

            double entityDistance =
                    Double.MAX_VALUE;

            if (entityHit != null) {
                entityDistance =
                        start.distanceTo(
                                entityHit.getPos()
                        );
            }

            double blockDistance =
                    Double.MAX_VALUE;

            if (
                    blockHitRaw.getType()
                    == HitResult.Type.BLOCK
            ) {

                blockDistance =
                        start.distanceTo(
                                blockHitRaw.getPos()
                        );
            }

            // ==========================
            // ENTIDAD ES LO MÁS CERCANO
            // ==========================

            if (
                    entityHit != null
                    && entityDistance < blockDistance
            ) {

                Entity entity =
                        entityHit.getEntity();

                String entityId =
                        Registries.ENTITY_TYPE
                                .getId(entity.getType())
                                .toString();

                TargetContext target =
                        new TargetContext(
                                "entity",
                                entityId,
                                entity.getName()
                                        .getString(),
                                entity.getBlockX(),
                                entity.getBlockY(),
                                entity.getBlockZ(),
                                entityDistance
                        );

                if (entity instanceof LivingEntity living) {

                    target.health =
                            living.getHealth();

                    target.maxHealth =
                            living.getMaxHealth();
                }

                return target;
            }

            // ==========================
            // BLOQUE
            // ==========================

            if (
                    blockHitRaw.getType()
                    == HitResult.Type.BLOCK
            ) {

                BlockHitResult blockHit =
                        (BlockHitResult) blockHitRaw;

                BlockState state =
                        player.getWorld()
                                .getBlockState(
                                        blockHit.getBlockPos()
                                );

                String blockId =
                        Registries.BLOCK
                                .getId(state.getBlock())
                                .toString();

                TargetContext target =
                        new TargetContext(
                                "block",
                                blockId,
                                state.getBlock()
                                        .getName()
                                        .getString(),
                                blockHit.getBlockPos()
                                        .getX(),
                                blockHit.getBlockPos()
                                        .getY(),
                                blockHit.getBlockPos()
                                        .getZ(),
                                blockDistance
                        );

                for (
                        Property<?> property :
                        state.getProperties()
                ) {

                    target.properties.put(
                            property.getName(),
                            getPropertyValue(
                                    state,
                                    property
                            )
                    );
                }

                return target;
            }

            return TargetContext.none();
    }
    private static <T extends Comparable<T>>
    String getPropertyValue(
            BlockState state,
            Property<T> property
    ) {

        return property.name(
                state.get(property)
        );
    }
}