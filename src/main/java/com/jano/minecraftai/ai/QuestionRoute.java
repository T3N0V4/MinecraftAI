package com.jano.minecraftai.ai;

import java.text.Normalizer;
import java.util.Locale;

public final class QuestionRoute {

    public enum Mode {
        TOOL,
        CONTEXT,
        VISION
    }

    public static Mode resolve(
            String question
    ) {

        if (
                question == null
                || question.isBlank()
        ) {
            return Mode.VISION;
        }

        /*
         * Si ya existe un fast path de tool,
         * definitivamente no necesitamos imagen.
         */
        if (
                ToolFastPathRouter.resolve(
                        question
                )
                != null
        ) {
            return Mode.TOOL;
        }

        String q =
                normalize(
                        question
                );

        /*
         * Datos que ya existen en PlayerContext.
         */
        if (
                q.equals("donde estoy")
                || q.equals("en donde estoy")
                || q.contains("en que bioma estoy")
                || q.contains("que bioma es este")
                || q.contains("en que dimension estoy")
                || q.contains("que dimension es esta")
                || q.contains("cuales son mis coordenadas")
                || q.contains("dime mis coordenadas")
                || q.contains("decime mis coordenadas")
                || q.contains("que coordenadas tengo")
                || q.contains("cuanta vida tengo")
                || q.contains("cuanta hambre tengo")
                || q.contains("que hora es en minecraft")
                || q.contains("como esta el clima")
        ) {
            return Mode.CONTEXT;
        }

        /*
         * Conversación trivial.
         * Tampoco necesita sacar una captura cada vez.
         */
        if (
                q.equals("hola")
                || q.equals("buenas")
                || q.equals("hey")
                || q.equals("holaa")
                || q.equals("como estas")
                || q.equals("como andas")
        ) {
            return Mode.CONTEXT;
        }

        /*
         * El resto conserva el comportamiento viejo.
         * Así no sacrificamos precisión:
         * ante la duda, imagen.
         */
        return Mode.VISION;
    }


    private static String normalize(
            String text
    ) {

        String normalized =
                Normalizer.normalize(
                        text,
                        Normalizer.Form.NFD
                );

        normalized =
                normalized.replaceAll(
                        "\\p{M}",
                        ""
                );

        return normalized
                .toLowerCase(
                        Locale.ROOT
                )
                .replaceAll(
                        "[^a-z0-9 ]",
                        " "
                )
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }


    private QuestionRoute() {
    }
}