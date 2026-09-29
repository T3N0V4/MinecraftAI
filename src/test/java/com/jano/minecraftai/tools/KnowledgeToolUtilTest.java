package com.jano.minecraftai.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KnowledgeToolUtilTest {

    @Test
    void extractsMinecraftNamespace() {

        assertEquals(
                "minecraft",
                KnowledgeToolUtil.namespace(
                        "minecraft:diamond"
                )
        );
    }

    @Test
    void extractsModNamespace() {

        assertEquals(
                "create",
                KnowledgeToolUtil.namespace(
                        "create:brass_ingot"
                )
        );
    }

    @Test
    void handlesInvalidIdentifier() {

        assertEquals(
                "unknown",
                KnowledgeToolUtil.namespace(
                        "invalid"
                )
        );
    }

    @Test
    void escapesJsonText() {

        assertEquals(
                "hola \\\"mundo\\\"",
                KnowledgeToolUtil.escape(
                        "hola \"mundo\""
                )
        );
    }
}