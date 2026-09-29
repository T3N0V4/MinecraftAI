package com.jano.minecraftai;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.PlayerContextService;

import com.jano.minecraftai.tools.ToolManager;
import com.jano.minecraftai.tools.ToolResult;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

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
                (
                        dispatcher,
                        registryAccess,
                        environment
                ) -> registerCommands(
                        dispatcher
                )
        );
    }

    private static void registerCommands(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {

        dispatcher.register(

                literal("aiserver")

                        // ==========================
                        // TEST
                        // ==========================

                        .then(
                                literal("test")
                                        .executes(context -> {

                                            context.getSource()
                                                    .sendFeedback(
                                                            () -> Text.literal(
                                                                    "[AI] MinecraftAI funcionando correctamente."
                                                            ),
                                                            false
                                                    );

                                            return 1;
                                        })
                        )

                        // ==========================
                        // CONTEXT
                        // ==========================

                        .then(
                                literal("context")

                                        .executes(context -> {

                                            ServerPlayerEntity player =
                                                    context.getSource()
                                                            .getPlayer();

                                            if (player == null) {

                                                context.getSource()
                                                        .sendError(
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

                        // ==========================
                        // LISTAR TOOLS
                        // ==========================

                        .then(
                                literal("tools")
                                        .executes(context -> {

                                            String names =
                                                    String.join(
                                                            "\n- ",
                                                            ToolManager
                                                                    .getRegistry()
                                                                    .getNames()
                                                    );

                                            context.getSource()
                                                    .sendFeedback(
                                                            () -> Text.literal(
                                                                    "[AI TOOLS]\n- "
                                                                            + names
                                                            ),
                                                            false
                                                    );

                                            return 1;
                                        })
                        )

                        // ==========================
                        // EJECUTAR TOOL
                        // ==========================

                        .then(
                                literal("tool")

                                        .then(
                                                argument(
                                                        "name",
                                                        StringArgumentType.word()
                                                )

                                                        .executes(context -> {

                                                            ServerPlayerEntity player =
                                                                    context.getSource()
                                                                            .getPlayer();

                                                            if (player == null) {

                                                                context.getSource()
                                                                        .sendError(
                                                                                Text.literal(
                                                                                        "[AI] Este comando debe ejecutarlo un jugador."
                                                                                )
                                                                        );

                                                                return 0;
                                                            }

                                                            String toolName =
                                                                    StringArgumentType
                                                                            .getString(
                                                                                    context,
                                                                                    "name"
                                                                            );

                                                            ToolResult result =
                                                                    ToolManager.execute(
                                                                            toolName,
                                                                            player
                                                                    );

                                                            player.sendMessage(
                                                                    Text.literal(
                                                                            "[AI TOOL] "
                                                                                    + toolName
                                                                                    + "\n"
                                                                                    + result.content
                                                                    ),
                                                                    false
                                                            );

                                                            return result.success
                                                                    ? 1
                                                                    : 0;
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
                PlayerContextService
                        .getContext(
                                player
                        );

        source.sendFeedback(
                () -> Text.literal(
                        context.toString()
                ),
                false
        );
    }
}