package com.jano.minecraftai.tools.base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;

import net.minecraft.registry.Registries;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.util.math.Vec3d;

import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolContext;
import com.jano.minecraftai.tools.ToolResult;
import com.jano.minecraftai.tools.ToolTier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GetVisibleEntitiesTool
        implements MinecraftAITool {

    private static final double RANGE =
            24.0;

    /*
     * Aproximación del campo de visión.
     * No representa todavía el frustum exacto
     * de la cámara del cliente.
     */
    private static final double MIN_DOT =
            0.55;

    @Override
    public String getName() {
        return "get_visible_entities";
    }

    @Override
    public String getDescription() {

        return "Obtiene entidades visibles delante del jugador.";
    }

    @Override
    public ToolTier getTier() {
        return ToolTier.BASE;
    }

    @Override
    public ToolResult execute(
            ToolContext context
    ) {

        ServerPlayerEntity player =
                context.player;

        ServerWorld world =
                player.getServerWorld();

        Vec3d camera =
                player.getCameraPosVec(
                        1.0f
                );

        Vec3d direction =
                player.getRotationVec(
                        1.0f
                ).normalize();

        List<EntityInfo> visible =
                new ArrayList<>();

        List<Entity> entities =
                world.getOtherEntities(
                        player,
                        player.getBoundingBox()
                                .expand(RANGE),
                        entity ->
                                !entity.isSpectator()
                                && entity.isAlive()
                );

        for (Entity entity : entities) {

            double distance =
                    player.distanceTo(entity);

            if (distance > RANGE) {
                continue;
            }

            Vec3d entityCenter =
                    entity.getPos()
                            .add(
                                    0,
                                    entity.getHeight()
                                            * 0.5,
                                    0
                            );

            Vec3d toEntity =
                    entityCenter.subtract(
                            camera
                    );

            if (
                    toEntity.lengthSquared()
                    <= 0.0001
            ) {
                continue;
            }

            double dot =
                    direction.dotProduct(
                            toEntity.normalize()
                    );

            if (dot < MIN_DOT) {
                continue;
            }

            if (!player.canSee(entity)) {
                continue;
            }

            String id =
                    Registries.ENTITY_TYPE
                            .getId(
                                    entity.getType()
                            )
                            .toString();

            Float health =
                    null;

            Float maxHealth =
                    null;

            if (
                    entity instanceof LivingEntity living
            ) {

                health =
                        living.getHealth();

                maxHealth =
                        living.getMaxHealth();
            }

            visible.add(
                    new EntityInfo(
                            id,
                            entity.getName()
                                    .getString(),
                            distance,
                            entity instanceof HostileEntity,
                            health,
                            maxHealth
                    )
            );
        }

        visible.sort(
                Comparator.comparingDouble(
                        info -> info.distance
                )
        );

        if (visible.isEmpty()) {

            return ToolResult.success(
                    """
                    {
                      "range": 24,
                      "entities": []
                    }
                    """
            );
        }

        StringBuilder builder =
                new StringBuilder();

        builder.append(
                "{\n  \"range\": 24,\n  \"entities\": [\n"
        );

        int limit =
                Math.min(
                        visible.size(),
                        20
                );

        for (
                int i = 0;
                i < limit;
                i++
        ) {

            EntityInfo info =
                    visible.get(i);

            String namespace =
                    info.id.contains(":")
                            ? info.id.substring(
                                    0,
                                    info.id.indexOf(':')
                            )
                            : "unknown";

            builder.append(
                    """
                        {
                          "id": "%s",
                          "name": "%s",
                          "mod_id": "%s",
                          "distance": %.2f,
                          "hostile": %s,
                          "health": %s,
                          "max_health": %s
                        }
                    """
                    .formatted(
                            info.id,
                            info.name,
                            namespace,
                            info.distance,
                            info.hostile,
                            String.valueOf(
                                    info.health
                            ),
                            String.valueOf(
                                    info.maxHealth
                            )
                    )
            );

            if (i < limit - 1) {
                builder.append(",");
            }

            builder.append("\n");
        }

        builder.append(
                "  ]\n}"
        );

        return ToolResult.success(
                builder.toString()
        );
    }

    private static class EntityInfo {

        private final String id;
        private final String name;
        private final double distance;
        private final boolean hostile;
        private final Float health;
        private final Float maxHealth;

        private EntityInfo(
                String id,
                String name,
                double distance,
                boolean hostile,
                Float health,
                Float maxHealth
        ) {

            this.id = id;
            this.name = name;
            this.distance = distance;
            this.hostile = hostile;
            this.health = health;
            this.maxHealth = maxHealth;
        }
    }
}