package com.jano.minecraftai.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RouteDecisionParserTest {

    @Test
    void parsesContext() {

        RouteDecision decision =
                RouteDecisionParser.parse(
                        "CONTEXT"
                );

        assertNotNull(
                decision
        );

        assertEquals(
                RouteType.CONTEXT,
                decision.type
        );
    }


    @Test
    void parsesVision() {

        RouteDecision decision =
                RouteDecisionParser.parse(
                        "VISION"
                );

        assertNotNull(
                decision
        );

        assertEquals(
                RouteType.VISION,
                decision.type
        );
    }


    @Test
    void parsesToolWithoutArguments() {

        RouteDecision decision =
                RouteDecisionParser.parse(
                        "TOOL|get_current_structure"
                );

        assertNotNull(
                decision
        );

        assertEquals(
                RouteType.TOOL,
                decision.type
        );

        assertEquals(
                "get_current_structure",
                decision.toolName
        );

        assertTrue(
                decision.arguments.isEmpty()
        );
    }


    @Test
    void parsesToolWithArguments() {

        RouteDecision decision =
                RouteDecisionParser.parse(
                        "TOOL|search_mod|query=Create"
                );

        assertNotNull(
                decision
        );

        assertEquals(
                "search_mod",
                decision.toolName
        );

        assertEquals(
                "Create",
                decision.arguments.get(
                        "query"
                )
        );
    }


    @Test
    void rejectsGarbage() {

        assertNull(
                RouteDecisionParser.parse(
                        "hola soy una respuesta normal"
                )
        );
    }
}