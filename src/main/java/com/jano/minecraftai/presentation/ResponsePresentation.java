package com.jano.minecraftai.presentation;

import java.util.Objects;

public class ResponsePresentation {

    private final String voiceText;
    private final String displayText;
    private final PresentationType type;

    public ResponsePresentation(
            String voiceText,
            String displayText,
            PresentationType type
    ) {
        this.voiceText = Objects.requireNonNull(voiceText);
        this.displayText = Objects.requireNonNull(displayText);
        this.type = Objects.requireNonNull(type);
    }

    public String getVoiceText() {
        return voiceText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public PresentationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ResponsePresentation{" +
                "voiceText='" + voiceText + '\'' +
                ", displayText='" + displayText + '\'' +
                ", type=" + type +
                '}';
    }
}