package com.jano.minecraftai.ai;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RouteDecisionParser {

    public static RouteDecision parse(
            String text
    ) {

        if (
                text == null
                || text.isBlank()
        ) {

            return null;
        }


        String clean =
                text
                        .trim()
                        .replace(
                                "`",
                                ""
                        );


        String[] lines =
                clean.split(
                        "\\R"
                );


        if (
                lines.length == 0
        ) {

            return null;
        }


        String first =
                lines[0]
                        .trim();


        if (
                first.equalsIgnoreCase(
                        "CHAT"
                )
        ) {

            return RouteDecision.chat();
        }


        if (
                first.equalsIgnoreCase(
                        "CONTEXT"
                )
        ) {

            return RouteDecision.context();
        }


        if (
                first.equalsIgnoreCase(
                        "VISION"
                )
        ) {

            return RouteDecision.vision();
        }


        if (
                !first.toUpperCase()
                        .startsWith(
                                "TOOL|"
                        )
        ) {

            return null;
        }


        String[] parts =
                first.split(
                        "\\|"
                );


        if (
                parts.length < 2
        ) {

            return null;
        }


        String toolName =
                parts[1]
                        .trim();


        if (
                toolName.isBlank()
        ) {

            return null;
        }


        Map<String, String> arguments =
                new LinkedHashMap<>();


        for (
                int i = 2;
                i < parts.length;
                i++
        ) {

            String part =
                    parts[i]
                            .trim();


            int separator =
                    part.indexOf(
                            '='
                    );


            if (
                    separator <= 0
            ) {

                continue;
            }


            String key =
                    part.substring(
                            0,
                            separator
                    ).trim();


            String value =
                    part.substring(
                            separator + 1
                    ).trim();


            if (
                    !key.isBlank()
                    && !value.isBlank()
            ) {

                arguments.put(
                        key,
                        value
                );
            }
        }


        return RouteDecision.tool(
                toolName,
                arguments
        );
    }


    private RouteDecisionParser() {
    }
}