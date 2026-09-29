package com.jano.minecraftai;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
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
                literal("aiserver")

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

                        .then(
                                literal("context")
                                        .executes(context -> {

                                            ServerPlayerEntity player =
                                                    context.getSource().getPlayer();

                                            if (player == null) {
                                                context.getSource().sendError(
                                                        Text.literal(
                                                                "[AI] Desde consola usa: aiserver context <jugador>"
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
            ServerPlayerEntity player
    ) {

        PlayerContext context =
                PlayerContextService.getContext(player);

        source.sendFeedback(
                () -> Text.literal(
                        context.toString()
                ),
                false
        );
    }
}