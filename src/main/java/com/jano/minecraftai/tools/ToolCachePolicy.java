package com.jano.minecraftai.tools;

public enum ToolCachePolicy {

    NONE(
            0
    ),

    SHORT(
            1500
    ),

    MEDIUM(
            10_000
    ),

    LONG(
            60_000
    );


    private final long durationMs;


    ToolCachePolicy(
            long durationMs
    ) {

        this.durationMs =
                durationMs;
    }


    public long getDurationMs() {

        return durationMs;
    }


    public boolean isEnabled() {

        return durationMs > 0;
    }
}