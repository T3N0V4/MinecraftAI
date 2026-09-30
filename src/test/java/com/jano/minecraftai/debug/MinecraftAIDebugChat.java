package com.jano.minecraftai.debug;

import com.jano.minecraftai.ai.AIRequest;
import com.jano.minecraftai.ai.AIResponse;
import com.jano.minecraftai.ai.ConversationMemory;
import com.jano.minecraftai.ai.MinecraftAIPrompt;

import com.jano.minecraftai.ai.providers.GeminiProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class MinecraftAIDebugChat {

    private final DebugContext debugContext =
            new DebugContext();

    private final ConversationMemory memory =
            new ConversationMemory();

    private final UUID playerId =
            UUID.randomUUID();

    private final GeminiProvider gemini =
            new GeminiProvider();

    private final Map<String, String> simulatedTools =
            new LinkedHashMap<>();

    private String lastPrompt =
            "";

    public static void main(
            String[] args
    ) {

        new MinecraftAIDebugChat()
                .run();
    }

    private void run() {

        printHeader();

        Scanner scanner =
                new Scanner(
                        System.in
                );

        while (true) {

            System.out.print(
                    "\nAI-Debug > "
            );

            if (
                    !scanner.hasNextLine()
            ) {
                break;
            }

            String input =
                    scanner.nextLine()
                            .trim();

            if (
                    input.isBlank()
            ) {
                continue;
            }

            if (
                    input.startsWith("/")
            ) {

                if (
                        handleCommand(
                                input
                        )
                ) {
                    break;
                }

                continue;
            }

            ask(
                    input
            );
        }
    }

    private void ask(
            String question
    ) {

        String history =
                memory.getHistoryText(
                        playerId
                );

        AIRequest request =
                new AIRequest(
                        question,
                        new byte[0],
                        debugContext.get(),
                        history
                );

        for (
                Map.Entry<String, String> tool :
                simulatedTools.entrySet()
        ) {

            request.addToolResult(
                    tool.getKey(),
                    tool.getValue()
            );
        }

        lastPrompt =
                MinecraftAIPrompt.build(
                        request
                );

        if (
                !gemini.isAvailable()
        ) {

            System.out.println(
                    "\n[ERROR] GEMINI_API_KEY no está disponible."
            );

            System.out.println(
                    "Podés seguir usando /show, /set, /tool y /prompt."
            );

            return;
        }

        System.out.println(
                "\nPensando..."
        );

        AIResponse response =
                gemini.respond(
                        request
                );

        if (
                response == null
                || !response.success
        ) {

            System.out.println(
                    "\n[ERROR] "
                            + (
                            response == null
                                    ? "Respuesta nula."
                                    : response.content
                    )
            );

            return;
        }

        System.out.println(
                "\nAI > "
                        + response.content
        );

        memory.addUser(
                playerId,
                question
        );

        memory.addAssistant(
                playerId,
                response.content
        );
    }

    private boolean handleCommand(
            String command
    ) {

        if (
                command.equalsIgnoreCase(
                        "/exit"
                )
        ) {

            System.out.println(
                    "Cerrando MinecraftAI Debug."
            );

            return true;
        }

        if (
                command.equalsIgnoreCase(
                        "/help"
                )
        ) {

            printHelp();
            return false;
        }

        if (
                command.equalsIgnoreCase(
                        "/show"
                )
        ) {

            showContext();
            return false;
        }

        if (
                command.equalsIgnoreCase(
                        "/history"
                )
        ) {

            System.out.println(
                    "\n"
                            + memory.getHistoryText(
                            playerId
                    )
            );

            return false;
        }

        if (
                command.equalsIgnoreCase(
                        "/clear"
                )
        ) {

            memory.clear(
                    playerId
            );

            System.out.println(
                    "Historial borrado."
            );

            return false;
        }

        if (
                command.equalsIgnoreCase(
                        "/reset"
                )
        ) {

            debugContext.reset();

            memory.clear(
                    playerId
            );

            simulatedTools.clear();

            lastPrompt =
                    "";

            System.out.println(
                    "Contexto, memoria y tools reiniciados."
            );

            return false;
        }

        if (
                command.equalsIgnoreCase(
                        "/prompt"
                )
        ) {

            if (
                    lastPrompt.isBlank()
            ) {

                System.out.println(
                        "Todavía no se generó ningún prompt."
                );

            } else {

                System.out.println(
                        "\n========== PROMPT ==========\n"
                                + lastPrompt
                                + "\n============================"
                );
            }

            return false;
        }

        if (
                command.startsWith(
                        "/set "
                )
        ) {

            handleSet(
                    command
            );

            return false;
        }

        if (
                command.startsWith(
                        "/tool "
                )
        ) {

            handleTool(
                    command
            );

            return false;
        }

        System.out.println(
                "Comando desconocido. Usá /help."
        );

        return false;
    }

    private void handleSet(
            String command
    ) {

        String body =
                command.substring(
                        "/set ".length()
                ).trim();

        int separator =
                body.indexOf(
                        ' '
                );

        if (
                separator <= 0
        ) {

            System.out.println(
                    "Uso: /set campo valor"
            );

            return;
        }

        String field =
                body.substring(
                        0,
                        separator
                );

        String value =
                body.substring(
                        separator + 1
                ).trim();

        boolean changed =
                debugContext.set(
                        field,
                        value
                );

        if (
                !changed
        ) {

            System.out.println(
                    "Campo o valor inválido."
            );

            return;
        }

        System.out.println(
                field
                        + " = "
                        + value
        );
    }

    private void handleTool(
            String command
    ) {

        String body =
                command.substring(
                        "/tool ".length()
                ).trim();

        if (
                body.equalsIgnoreCase(
                        "clear"
                )
        ) {

            simulatedTools.clear();

            System.out.println(
                    "Tools simuladas borradas."
            );

            return;
        }

        int separator =
                body.indexOf(
                        ' '
                );

        if (
                separator <= 0
        ) {

            System.out.println(
                    "Uso: /tool nombre resultado"
            );

            return;
        }

        String name =
                body.substring(
                        0,
                        separator
                );

        String result =
                body.substring(
                        separator + 1
                ).trim();

        simulatedTools.put(
                name,
                result
        );

        System.out.println(
                "Tool simulada: "
                        + name
                        + " -> "
                        + result
        );
    }

    private void showContext() {

        System.out.println(
                "\n========== CONTEXTO =========="
        );

        System.out.println(
                debugContext.describe()
        );

        System.out.println(
                "\nTOOLS SIMULADAS:"
        );

        if (
                simulatedTools.isEmpty()
        ) {

            System.out.println(
                    "Ninguna"
            );

        } else {

            simulatedTools.forEach(
                    (name, result) ->
                            System.out.println(
                                    "- "
                                            + name
                                            + ": "
                                            + result
                            )
            );
        }

        System.out.println(
                "==============================="
        );
    }

    private void printHeader() {

        System.out.println(
                """
                ==================================
                  MinecraftAI Debug Console
                ==================================

                /help para ver comandos.
                """
        );

        showContext();
    }

    private void printHelp() {

        System.out.println(
                """
                
                COMANDOS:

                /show
                    Muestra contexto y tools simuladas.

                /set campo valor
                    Modifica el contexto.

                Campos disponibles:
                    player
                    biome
                    dimension
                    gamemode
                    health
                    maxhealth
                    hunger
                    xp
                    x
                    y
                    z
                    direction
                    weather
                    difficulty
                    light
                    target

                Ejemplos:
                    /set biome minecraft:desert
                    /set target minecraft:cut_sandstone
                    /set health 8

                /tool nombre resultado
                    Simula el resultado de una tool.

                Ejemplo:
                    /tool get_current_structure betterdeserttemples:desert_temple
                    /tool get_mod_origin minecraft / vanilla

                /tool clear
                    Borra tools simuladas.

                /history
                    Muestra memoria conversacional.

                /clear
                    Borra solo la conversación.

                /prompt
                    Muestra el último prompt exacto enviado a Gemini.

                /reset
                    Reinicia contexto, memoria y tools.

                /exit
                    Cierra la consola.
                """
        );
    }
}