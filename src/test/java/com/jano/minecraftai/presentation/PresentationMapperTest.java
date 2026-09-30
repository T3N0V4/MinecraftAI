package com.jano.minecraftai.presentation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PresentationMapperTest {

    @Test
    void mapsNormalChat() {
        PresentationInput input =
                PresentationInput.chat(
                        "Hola Jano, ¿qué necesitás?"
                );

        ResponsePresentation result =
                PresentationMapper.map(input);

        assertEquals(
                PresentationType.CHAT,
                result.getType()
        );

        assertEquals(
                "Hola Jano, ¿qué necesitás?",
                result.getVoiceText()
        );

        assertEquals(
                "Hola Jano, ¿qué necesitás?",
                result.getDisplayText()
        );
    }


    @Test
    void mapsInventoryTool() {
        PresentationInput input =
                new PresentationInput(
                        "Tenés tres tipos de objetos.",
                        "get_inventory",
                        """
                        64x Cobblestone
                        3x Diamond
                        1x Iron Sword
                        """
                );

        ResponsePresentation result =
                PresentationMapper.map(input);

        assertEquals(
                PresentationType.INVENTORY,
                result.getType()
        );

        assertEquals(
                "Tenés tres tipos de objetos.",
                result.getVoiceText()
        );

        assertTrue(
                result.getDisplayText()
                        .contains("3x Diamond")
        );
    }


    @Test
    void mapsBlockTool() {
        PresentationInput input =
                new PresentationInput(
                        "Es mena de diamante.",
                        "get_target_block",
                        """
                        ID: minecraft:diamond_ore
                        Mod: Minecraft
                        Posición: 143, -32, 81
                        """
                );

        ResponsePresentation result =
                PresentationMapper.map(input);

        assertEquals(
                PresentationType.BLOCK_INFO,
                result.getType()
        );

        assertTrue(
                result.getDisplayText()
                        .contains("minecraft:diamond_ore")
        );

        assertEquals(
                "Es mena de diamante.",
                result.getVoiceText()
        );
    }


    @Test
    void mapsEntityTool() {
        PresentationInput input =
                new PresentationInput(
                        "Tenés un zombie bastante cerca.",
                        "get_target_entity",
                        """
                        ID: minecraft:zombie
                        Distancia: 4.2
                        Vida: 16/20
                        Hostil: true
                        """
                );

        ResponsePresentation result =
                PresentationMapper.map(input);

        assertEquals(
                PresentationType.ENTITY_INFO,
                result.getType()
        );

        assertTrue(
                result.getDisplayText()
                        .contains("16/20")
        );
    }


    @Test
    void mapsRecipeTool() {
        PresentationInput input =
                new PresentationInput(
                        "Necesitás diamantes y palos.",
                        "get_recipe",
                        """
                        Resultado: Diamond Pickaxe
                        3x Diamond
                        2x Stick
                        """
                );

        ResponsePresentation result =
                PresentationMapper.map(input);

        assertEquals(
                PresentationType.RECIPE,
                result.getType()
        );

        assertTrue(
                result.getDisplayText()
                        .contains("Diamond Pickaxe")
        );
    }


    @Test
    void unknownToolFallsBackToChat() {
        PresentationInput input =
                new PresentationInput(
                        "Encontré información.",
                        "tool_que_no_existe",
                        "datos técnicos"
                );

        ResponsePresentation result =
                PresentationMapper.map(input);

        assertEquals(
                PresentationType.CHAT,
                result.getType()
        );

        assertEquals(
                "Encontré información.",
                result.getDisplayText()
        );
    }


    @Test
    void handlesNullInput() {
        ResponsePresentation result =
                PresentationMapper.map(null);

        assertEquals(
                PresentationType.CHAT,
                result.getType()
        );

        assertEquals(
                "",
                result.getVoiceText()
        );
    }
}