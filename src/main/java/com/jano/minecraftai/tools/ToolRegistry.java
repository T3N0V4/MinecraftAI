package com.jano.minecraftai.tools;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ToolRegistry {

    private final Map<String, MinecraftAITool> tools =
            new LinkedHashMap<>();

    public void register(
            MinecraftAITool tool
    ) {

        tools.put(
                tool.getName(),
                tool
        );
    }

    public MinecraftAITool get(
            String name
    ) {

        return tools.get(name);
    }

    public ToolResult execute(
            String name,
            ToolContext context
    ) {

        MinecraftAITool tool =
                tools.get(name);

        if (tool == null) {

            return ToolResult.failure(
                    "Tool desconocida: "
                            + name
            );
        }

        try {

            return tool.execute(
                    context
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ToolResult.failure(
                    "Error ejecutando "
                            + name
                            + ": "
                            + e.getMessage()
            );
        }
    }

    public Set<String> getNames() {
        return tools.keySet();
    }

    public Collection<MinecraftAITool> getTools() {
        return tools.values();
    }
}