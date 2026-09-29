package com.jano.minecraftai.ai;

import com.jano.minecraftai.context.PlayerContext;

public class AIRequest {

    public final String question;
    public final byte[] image;
    public final PlayerContext playerContext;

    public AIRequest(
            String question,
            byte[] image,
            PlayerContext playerContext
    ) {
        this.question = question;
        this.image = image;
        this.playerContext = playerContext;
    }
}
