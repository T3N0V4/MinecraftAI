package com.jano.minecraftai.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionRouteTest {

    @Test
    void estructuraUsaToolSinImagen() {

        assertEquals(
                QuestionRoute.Mode.TOOL,
                QuestionRoute.resolve(
                        "¿En qué estructura estoy?"
                )
        );
    }


    @Test
    void ubicacionUsaContextoSinImagen() {

        assertEquals(
                QuestionRoute.Mode.CONTEXT,
                QuestionRoute.resolve(
                        "¿Dónde estoy?"
                )
        );

        assertEquals(
                QuestionRoute.Mode.CONTEXT,
                QuestionRoute.resolve(
                        "¿En qué bioma estoy?"
                )
        );

        assertEquals(
                QuestionRoute.Mode.CONTEXT,
                QuestionRoute.resolve(
                        "¿En qué dimensión estoy?"
                )
        );
    }


    @Test
    void saludoNoNecesitaImagen() {

        assertEquals(
                QuestionRoute.Mode.CONTEXT,
                QuestionRoute.resolve(
                        "hola"
                )
        );
    }


    @Test
    void preguntaVisualConservaImagen() {

        assertEquals(
                QuestionRoute.Mode.VISION,
                QuestionRoute.resolve(
                        "¿Qué ves enfrente mío?"
                )
        );

        assertEquals(
                QuestionRoute.Mode.VISION,
                QuestionRoute.resolve(
                        "¿Qué te parece esto?"
                )
        );
    }
}