package com.jano.minecraftai.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToolCallParserTest {

    @Test
    void parsesValidToolCall() {

        String tool =
                ToolCallParser.parseToolName(
                        "{\"tool\":\"get_visible_entities\"}"
                );

        assertEquals(
                "get_visible_entities",
                tool
        );
    }

    @Test
    void ignoresNormalAnswer() {

        String tool =
                ToolCallParser.parseToolName(
                        "Veo dos pandas."
                );

        assertNull(
                tool
        );
    }

    @Test
    void ignoresJsonWithExtraText() {

        String tool =
                ToolCallParser.parseToolName(
                        "Voy a consultar: {\"tool\":\"get_visible_entities\"}"
                );

        assertNull(
                tool
        );
    }
}