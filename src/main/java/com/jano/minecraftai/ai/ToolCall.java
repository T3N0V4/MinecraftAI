package com.jano.minecraftai.ai;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToolCall {

    public final String name;

    public final Map<String, String> arguments;

    public ToolCall(
            String name,
            Map<String, String> arguments
    ) {

        this.name =
                name;

        this.arguments =
                arguments == null
                        ? Collections.emptyMap()
                        : new LinkedHashMap<>(
                                arguments
                        );
    }
}