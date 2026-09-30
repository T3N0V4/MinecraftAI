package com.jano.minecraftai.ai;

public final class SemanticRoutePrompt {

    public static String build(
            String question
    ) {

        return """
                Tu única tarea es decidir cómo procesar una pregunta de Minecraft.

                RUTAS POSIBLES:

                CHAT
                Conversación normal que no necesita datos del mundo.

                CONTEXT
                Puede responderse usando contexto básico del jugador
                como posición, bioma, dimensión, vida, hambre, clima u hora.

                TOOL
                Hay una tool específica que puede obtener el dato.

                VISION
                Hace falta observar la captura de pantalla o interpretar
                visualmente una escena.

                TOOLS DISPONIBLES:
                %s

                REGLAS:

                - Preferí TOOL si existe una tool claramente adecuada.
                - Preferí CONTEXT si el dato ya pertenece al contexto básico.
                - Usá VISION solamente cuando realmente sea necesario mirar la escena.
                - Los errores de escritura o transcripción de voz no deben impedir entender la intención.
                - No inventes tools.
                - No respondas la pregunta del jugador.
                - Devolvé UNA SOLA línea.

                FORMATO:

                CHAT

                o

                CONTEXT

                o

                VISION

                o

                TOOL|nombre_tool

                o, si requiere argumentos:

                TOOL|nombre_tool|argumento=valor

                PREGUNTA:
                %s
                """
                .formatted(
                        ToolCatalogBuilder.build(),
                        question
                );
    }


    private SemanticRoutePrompt() {
    }
}