package com.jano.minecraftai.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToolCallParserTest {

    @Test
    void parsesValidToolCall() {

        ToolCall call =
                ToolCallParser.parse(
                        "{\"tool\":\"get_visible_entities\"}"
                );

        assertNotNull(
                call
        );

        assertEquals(
                "get_visible_entities",
                call.name
        );

        assertTrue(
                call.arguments.isEmpty()
        );
    }

    @Test
    void parsesToolArguments() {

        ToolCall call =
                ToolCallParser.parse(
                        "{\"tool\":\"search_mod\",\"arguments\":{\"query\":\"create\"}}"
                );

        assertNotNull(
                call
        );

        assertEquals(
                "search_mod",
                call.name
        );

        assertEquals(
                "create",
                call.arguments.get(
                        "query"
                )
        );
    }

    @Test
    void ignoresNormalAnswer() {

        ToolCall call =
                ToolCallParser.parse(
                        "Veo dos pandas."
                );

        assertNull(
                call
        );
    }

    @Test
    void ignoresJsonWithExtraText() {

        ToolCall call =
                ToolCallParser.parse(
                        "Voy a consultar: {\"tool\":\"get_visible_entities\"}"
                );

        assertNull(
                call
        );
    }
}