package com.jano.minecraftai.presentation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponsePresentationFactoryTest {

    @Test
    void createsBlockPresentation() {
        ResponsePresentation result =
                ResponsePresentationFactory.block(
                        "Diamond Ore",
                        "minecraft:diamond_ore",
                        "Minecraft",
                        143,
                        -32,
                        81
                );

        assertEquals(
                PresentationType.BLOCK_INFO,
                result.getType()
        );

        assertEquals(
                "Es Diamond Ore de Minecraft.",
                result.getVoiceText()
        );

        assertTrue(
                result.getDisplayText()
                        .contains("minecraft:diamond_ore")
        );

        assertTrue(
                result.getDisplayText()
                        .contains("143, -32, 81")
        );
    }


    @Test
    void createsHostileEntityPresentation() {
        ResponsePresentation result =
                ResponsePresentationFactory.entity(
                        "Zombie",
                        "minecraft:zombie",
                        4.2,
                        true,
                        16,
                        20
                );

        assertEquals(
                PresentationType.ENTITY_INFO,
                result.getType()
        );

        assertTrue(
                result.getVoiceText()
                        .contains("hostil")
        );

        assertTrue(
                result.getVoiceText()
                        .contains("4.2")
        );

        assertTrue(
                result.getDisplayText()
                        .contains("16/20")
        );
    }


    @Test
    void createsInventoryPresentation() {
        ResponsePresentation result =
                ResponsePresentationFactory.inventory(
                        List.of(
                                "64x Cobblestone",
                                "3x Diamond",
                                "1x Iron Sword"
                        )
                );

        assertEquals(
                PresentationType.INVENTORY,
                result.getType()
        );

        assertEquals(
                "Tenés 3 objetos distintos en el inventario.",
                result.getVoiceText()
        );

        assertTrue(
                result.getDisplayText()
                        .contains("3x Diamond")
        );
    }


    @Test
    void createsRecipePresentation() {
        ResponsePresentation result =
                ResponsePresentationFactory.recipe(
                        "Diamond Pickaxe",
                        List.of(
                                "3x Diamond",
                                "2x Stick"
                        )
                );

        assertEquals(
                PresentationType.RECIPE,
                result.getType()
        );

        assertEquals(
                "La receta de Diamond Pickaxe usa 2 ingredientes.",
                result.getVoiceText()
        );

        assertTrue(
                result.getDisplayText()
                        .contains("3x Diamond")
        );

        assertTrue(
                result.getDisplayText()
                        .contains("2x Stick")
        );
    }


    @Test
    void handlesEmptyInventory() {
        ResponsePresentation result =
                ResponsePresentationFactory.inventory(
                        List.of()
                );

        assertEquals(
                "Tu inventario está vacío.",
                result.getVoiceText()
        );

        assertTrue(
                result.getDisplayText()
                        .contains("Vacío")
        );
    }
}