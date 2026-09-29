package com.jano.minecraftai.tools;

public interface MinecraftAITool {

    String getName();

    String getDescription();

    ToolTier getTier();

    ToolResult execute(
            ToolContext context
    );
}