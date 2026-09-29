package com.jano.minecraftai.network;

import net.minecraft.util.Identifier;

public class NetworkConstants {

    public static final Identifier ASK_BEGIN =
            new Identifier(
                    "minecraftai",
                    "ask_begin"
            );

    public static final Identifier ASK_CHUNK =
            new Identifier(
                    "minecraftai",
                    "ask_chunk"
            );

    private NetworkConstants() {
    }
}