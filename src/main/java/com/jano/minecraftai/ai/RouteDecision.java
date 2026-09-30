package com.jano.minecraftai.ai;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RouteDecision {

    public final RouteType type;

    public final String toolName;

    public final Map<String, String> arguments;


    private RouteDecision(
            RouteType type,
            String toolName,
            Map<String, String> arguments
    ) {

        this.type =
                type;

        this.toolName =
                toolName;

        this.arguments =
                arguments == null
                        ? Collections.emptyMap()
                        : Collections.unmodifiableMap(
                                new LinkedHashMap<>(
                                        arguments
                                )
                        );
    }


    public static RouteDecision chat() {

        return new RouteDecision(
                RouteType.CHAT,
                null,
                Collections.emptyMap()
        );
    }


    public static RouteDecision context() {

        return new RouteDecision(
                RouteType.CONTEXT,
                null,
                Collections.emptyMap()
        );
    }


    public static RouteDecision vision() {

        return new RouteDecision(
                RouteType.VISION,
                null,
                Collections.emptyMap()
        );
    }


    public static RouteDecision tool(
            String toolName,
            Map<String, String> arguments
    ) {

        return new RouteDecision(
                RouteType.TOOL,
                toolName,
                arguments
        );
    }


    @Override
    public String toString() {

        if (
                type != RouteType.TOOL
        ) {

            return type.name();
        }

        return "TOOL "
                + toolName
                + " "
                + arguments;
    }
}