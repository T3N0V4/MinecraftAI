package com.jano.minecraftai.presentation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PresentationTypeResolverTest {

    @Test
    void resolvesBlockTools() {
        assertEquals(
                PresentationType.BLOCK_INFO,
                PresentationTypeResolver.resolve(
                        "get_target_block"
                )
        );

        assertEquals(
                PresentationType.BLOCK_INFO,
                PresentationTypeResolver.resolve(
                        "get_block_info"
                )
        );
    }


    @Test
    void resolvesEntityTools() {
        assertEquals(
                PresentationType.ENTITY_INFO,
                PresentationTypeResolver.resolve(
                        "get_target_entity"
                )
        );

        assertEquals(
                PresentationType.ENTITY_INFO,
                PresentationTypeResolver.resolve(
                        "get_visible_entities"
                )
        );
    }


    @Test
    void resolvesInventoryTool() {
        assertEquals(
                PresentationType.INVENTORY,
                PresentationTypeResolver.resolve(
                        "get_inventory"
                )
        );
    }


    @Test
    void resolvesRecipeTools() {
        assertEquals(
                PresentationType.RECIPE,
                PresentationTypeResolver.resolve(
                        "get_recipe"
                )
        );

        assertEquals(
                PresentationType.RECIPE,
                PresentationTypeResolver.resolve(
                        "get_recipes_using"
                )
        );
    }


    @Test
    void resolvesStructureTool() {
        assertEquals(
                PresentationType.STRUCTURE,
                PresentationTypeResolver.resolve(
                        "get_current_structure"
                )
        );
    }


    @Test
    void resolvesModTools() {
        assertEquals(
                PresentationType.MOD_INFO,
                PresentationTypeResolver.resolve(
                        "get_mod_origin"
                )
        );

        assertEquals(
                PresentationType.MOD_INFO,
                PresentationTypeResolver.resolve(
                        "get_installed_mods"
                )
        );
    }


    @Test
    void unknownToolFallsBackToChat() {
        assertEquals(
                PresentationType.CHAT,
                PresentationTypeResolver.resolve(
                        "search_mod"
                )
        );
    }


    @Test
    void nullToolFallsBackToChat() {
        assertEquals(
                PresentationType.CHAT,
                PresentationTypeResolver.resolve(null)
        );
    }
}