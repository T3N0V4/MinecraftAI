package com.jano.minecraftai.debug.presentation;

public class MockServerResponse {

    private final String aiText;
    private final String toolName;
    private final String toolResult;

    public MockServerResponse(
            String aiText,
            String toolName,
            String toolResult
    ) {
        this.aiText = aiText;
        this.toolName = toolName;
        this.toolResult = toolResult;
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
}