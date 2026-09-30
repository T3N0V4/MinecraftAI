package com.jano.minecraftai.presentation;

public class PresentationInput {

    private final String aiText;
    private final String toolName;
    private final String toolResult;

    public PresentationInput(
            String aiText,
            String toolName,
            String toolResult
    ) {
        this.aiText = aiText == null ? "" : aiText;
        this.toolName = toolName;
        this.toolResult = toolResult;
    }

    public static PresentationInput chat(String aiText) {
        return new PresentationInput(
                aiText,
                null,
                null
        );
    }

    public String getAiText() {
        return aiText;
    }

    public String getToolName() {
        return toolName;
    }

    public String getToolResult() {
        return toolResult;
    }

    public boolean hasTool() {
        return toolName != null
                && !toolName.isBlank();
    }

    public boolean hasToolResult() {
        return toolResult != null
                && !toolResult.isBlank();
    }
}