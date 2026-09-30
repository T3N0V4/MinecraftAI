package com.jano.minecraftai.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftAIAnimationTest {

    @Test
    void clampMantieneValoresEntreCeroYUno() {

        assertEquals(
                0.0,
                MinecraftAIAnimation.clamp01(
                        -1.0
                )
        );

        assertEquals(
                0.5,
                MinecraftAIAnimation.clamp01(
                        0.5
                )
        );

        assertEquals(
                1.0,
                MinecraftAIAnimation.clamp01(
                        2.0
                )
        );
    }


    @Test
    void lerpInterpolaCorrectamente() {

        assertEquals(
                50.0,
                MinecraftAIAnimation.lerp(
                        0.0,
                        100.0,
                        0.5
                )
        );
    }


    @Test
    void curvasEmpiezanYCierranCorrectamente() {

        for (
                MinecraftAIAnimation.Curve curve :
                MinecraftAIAnimation.Curve.values()
        ) {

            assertEquals(
                    0.0,
                    MinecraftAIAnimation.ease(
                            0.0,
                            curve
                    ),
                    0.0001
            );

            assertEquals(
                    1.0,
                    MinecraftAIAnimation.ease(
                            1.0,
                            curve
                    ),
                    0.0001
            );
        }
    }


    @Test
    void alphaAplicaTransparencia() {

        int color =
                MinecraftAIAnimation.alpha(
                        0x00FFFFFF,
                        0.5
                );

        int alpha =
                (
                        color >>> 24
                )
                        & 0xFF;

        assertTrue(
                alpha >= 127
                        && alpha <= 128
        );
    }
}