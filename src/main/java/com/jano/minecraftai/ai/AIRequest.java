package com.jano.minecraftai.ai;

import com.jano.minecraftai.context.PlayerContext;

import java.util.ArrayList;
import java.util.List;

public class AIRequest {

    public final String question;
    public final byte[] image;
    public final PlayerContext playerContext;

    public final List<String> toolResults =
            new ArrayList<>();

    public AIRequest(
            String question,
            byte[] image,
            PlayerContext playerContext
    ) {

        this.question =
                question;

        this.image =
                image;

        this.playerContext =
                playerContext;
    }

    public void addToolResult(
            String toolName,
            String result
    ) {

        toolResults.add(
                "TOOL: "
                        + toolName
                        + "\nRESULTADO:\n"
                        + result
        );
    }

    public String getToolResultsText() {

        if (toolResults.isEmpty()) {
            return "Todavía no se ejecutó ninguna tool.";
        }

        return String.join(
                "\n\n",
                toolResults
        );
    }
}