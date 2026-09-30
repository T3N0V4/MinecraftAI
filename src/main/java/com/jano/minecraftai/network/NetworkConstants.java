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

    public static final Identifier AI_STATUS =
            new Identifier(
                    "minecraftai",
                    "ai_status"
            );

    public static final Identifier AI_RESPONSE =
            new Identifier(
                    "minecraftai",
                    "ai_response"
            );

    public static final Identifier NEED_VISION =
            new Identifier(
                    "minecraftai",
                    "need_vision"
            );

    private NetworkConstants() {
    }
}