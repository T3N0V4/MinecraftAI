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

        if (
                q.contains("en que estructura estoy")
                || q.contains("que estructura es esta")
                || q.contains("que estructura estoy")
        ) {
            return "get_current_structure";
        }

        if (
                q.contains("que bloque estoy mirando")
                || q.contains("que bloque es este")
                || q.contains("cual es este bloque")
        ) {
            return "get_target_block";
        }

        if (
                q.contains("que entidad estoy mirando")
                || q.contains("que mob estoy mirando")
                || q.contains("que criatura estoy mirando")
                || q.contains("que animal estoy mirando")
        ) {
            return "get_target_entity";
        }

        if (
                q.contains("que tengo en la mano")
                || q.contains("que estoy sosteniendo")
                || q.contains("que tengo equipado en la mano")
        ) {
            return "get_held_item";
        }

        if (
                q.contains("que tengo en el inventario")
                || q.contains("que hay en mi inventario")
                || q.equals("mi inventario")
        ) {
            return "get_inventory";
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

        return normalized
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private ToolFastPathRouter() {
    }
}