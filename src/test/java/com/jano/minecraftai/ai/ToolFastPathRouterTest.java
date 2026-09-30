package com.jano.minecraftai.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToolFastPathRouterTest {

    @Test
    void detectaEstructura() {

        assertEquals(
                "get_current_structure",
                ToolFastPathRouter.resolve(
                        "¿En qué estructura estoy?"
                )
        );

        assertEquals(
                "get_current_structure",
                ToolFastPathRouter.resolve(
                        "donde estoy"
                )
        );
    }


    @Test
    void detectaBloque() {

        assertEquals(
                "get_target_block",
                ToolFastPathRouter.resolve(
                        "qué bloque estoy mirando?"
                )
        );
    }


    @Test
    void detectaInventario() {

        assertEquals(
                "get_inventory",
                ToolFastPathRouter.resolve(
                        "qué tengo en el inventario?"
                )
        );
    }


    @Test
    void noInterceptaPreguntasGenerales() {

        assertNull(
                ToolFastPathRouter.resolve(
                        "qué te parece este lugar?"
                )
        );

        assertNull(
                ToolFastPathRouter.resolve(
                        "hola"
                )
        );
    }
}