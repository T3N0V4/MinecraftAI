package com.jano.minecraftai.ai;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolCallParser {

    private static final Pattern TOOL_PATTERN =
            Pattern.compile(
                    "\"tool\"\\s*:\\s*\"([a-zA-Z0-9_\\-]+)\""
            );

    private static final Pattern ARGUMENTS_PATTERN =
            Pattern.compile(
                    "\"arguments\"\\s*:\\s*\\{(.*?)\\}",
                    Pattern.DOTALL
            );

    private static final Pattern STRING_PAIR_PATTERN =
            Pattern.compile(
                    "\"([a-zA-Z0-9_\\-]+)\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
            );

    public static ToolCall parse(
            String content
    ) {

        if (content == null) {
            return null;
        }

        String trimmed =
                content.trim();

        if (
                !trimmed.startsWith("{")
                || !trimmed.endsWith("}")
        ) {
            return null;
        }

        Matcher toolMatcher =
                TOOL_PATTERN.matcher(
                        trimmed
                );

        if (!toolMatcher.find()) {
            return null;
        }

        String toolName =
                toolMatcher.group(1);

        Map<String, String> arguments =
                new LinkedHashMap<>();

        Matcher argumentsMatcher =
                ARGUMENTS_PATTERN.matcher(
                        trimmed
                );

        if (argumentsMatcher.find()) {

            String argumentsBody =
                    argumentsMatcher.group(1);

            Matcher pairMatcher =
                    STRING_PAIR_PATTERN.matcher(
                            argumentsBody
                    );

            while (pairMatcher.find()) {

                arguments.put(
                        pairMatcher.group(1),
                        unescape(
                                pairMatcher.group(2)
                        )
                );
            }
        }

        return new ToolCall(
                toolName,
                arguments
        );
    }

    public static String parseToolName(
            String content
    ) {

        ToolCall call =
                parse(content);

        return call == null
                ? null
                : call.name;
    }

    private static String unescape(
            String value
    ) {

        return value
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private ToolCallParser() {
    }
}