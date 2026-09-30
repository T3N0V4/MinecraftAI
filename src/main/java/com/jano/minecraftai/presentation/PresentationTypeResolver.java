package com.jano.minecraftai.presentation;

public final class PresentationTypeResolver {

    private PresentationTypeResolver() {
    }

    public static PresentationType resolve(String toolName) {

        if (toolName == null || toolName.isBlank()) {
            return PresentationType.CHAT;
        }

        return switch (toolName) {

            case "get_target_block",
                 "get_block_info" ->
                    PresentationType.BLOCK_INFO;

            case "get_target_entity",
                 "get_visible_entities" ->
                    PresentationType.ENTITY_INFO;

            case "get_inventory" ->
                    PresentationType.INVENTORY;

            case "get_recipe",
                 "get_recipes_using" ->
                    PresentationType.RECIPE;

            case "get_current_structure" ->
                    PresentationType.STRUCTURE;

            case "get_mod_origin",
                 "get_installed_mods" ->
                    PresentationType.MOD_INFO;

            default ->
                    PresentationType.CHAT;
        };
    }
}