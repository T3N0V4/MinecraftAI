package com.jano.minecraftai.ai;

import java.text.Normalizer;
import java.util.Locale;

public final class ToolFastPathRouter {

    public static String resolve(
            String question
    ) {

        if (
                question == null
                || question.isBlank()
        ) {
            return null;
        }

        String q =
                normalize(
                        question
                );


        /*
         * ESTRUCTURA / UBICACIÓN
         */
        if (
                q.contains("en que estructura estoy")
                || q.contains("que estructura es esta")
                || q.contains("que estructura estoy")
                || q.contains("donde estoy")
                || q.contains("en donde estoy")
        ) {
            return "get_current_structure";
        }


        /*
         * BLOQUE
         */
        if (
                q.contains("que bloque estoy mirando")
                || q.contains("que bloque es este")
                || q.contains("cual es este bloque")
        ) {
            return "get_target_block";
        }


        /*
         * ENTIDAD / MOB
         */
        if (
                q.contains("que entidad estoy mirando")
                || q.contains("que mob estoy mirando")
                || q.contains("que criatura estoy mirando")
                || q.contains("que animal estoy mirando")
        ) {
            return "get_target_entity";
        }


        /*
         * MANOS
         */
        if (
                q.contains("que tengo en la mano")
                || q.contains("que estoy sosteniendo")
                || q.contains("que tengo equipado en la mano")
        ) {
            return "get_held_item";
        }


        /*
         * INVENTARIO
         */
        if (
                q.contains("que tengo en el inventario")
                || q.contains("que hay en mi inventario")
                || q.equals("mi inventario")
        ) {
            return "get_inventory";
        }


        /*
         * MOD DE ORIGEN
         */
        if (
                q.contains("de que mod es esto")
                || q.contains("de que mod es este bloque")
                || q.contains("de que mod es esta entidad")
                || q.contains("de que mod es este objeto")
        ) {
            return "get_mod_origin";
        }


        /*
         * MODS INSTALADOS
         */
        if (
                q.contains("cuantos mods")
                || q.contains("que mods hay")
                || q.contains("mods instalados")
                || q.contains("lista de mods")
        ) {
            return "get_installed_mods";
        }


        return null;
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

        normalized =
                normalized
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

        return normalized;
    }


    private ToolFastPathRouter() {
    }
}