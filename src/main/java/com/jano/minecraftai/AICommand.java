package com.jano.minecraftai;

import com.mojang.brigadier.CommandDispatcher;
import com.jano.minecraftai.context.PlayerContextService;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AICommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> registerCommands(dispatcher)
        );
    }

    private static void registerCommands(
        CommandDispatcher<ServerCommandSource> dispatcher
    ) {

        dispatcher.register(
            literal("ai")

                // /ai test
                .then(
                    literal("test")
                        .executes(context -> {

                            context.getSource().sendFeedback(
                                () -> Text.literal(
                                    "[AI] MinecraftAI funcionando correctamente."
                                ),
                                false
                            );

                            return 1;
                        })
                )

                // /ai context
                .then(
                    literal("context")

                        // Ejecutado por un jugador
                        .executes(context -> {

                            ServerPlayerEntity player =
                                context.getSource().getPlayer();

                            if (player == null) {

                                context.getSource().sendError(
                                    Text.literal(
                                        "[AI] Desde consola usa: ai context <jugador>"
                                    )
                                );

                                return 0;
                            }

                            enviarContexto(
                                context.getSource(),
                                player
                            );

                            return 1;
                        })

                        // ai context <jugador>
                        .then(
                            argument(
                                "player",
                                EntityArgumentType.player()
                            )
                            .executes(context -> {

                                ServerPlayerEntity player =
                                    EntityArgumentType.getPlayer(
                                        context,
                                        "player"
                                    );

                                enviarContexto(
                                    context.getSource(),
                                    player
                                );

                                return 1;
                            })
                        )
                )
        );
    }

    private static void enviarContexto(
        ServerCommandSource source,
        ServerPlayerEntity player) {
        String mensaje =
                PlayerContextService.getContext(player);

        source.sendFeedback(
                () -> Text.literal(mensaje),
                false
        );
    }
}