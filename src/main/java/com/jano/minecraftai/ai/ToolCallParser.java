package com.jano.minecraftai.ai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolCallParser {

    private static final Pattern TOOL_PATTERN =
            Pattern.compile(
                    "\\{\\s*\"tool\"\\s*:\\s*\"([a-zA-Z0-9_\\-]+)\"\\s*\\}"
            );

    public static String parseToolName(
            String content
    ) {

        if (content == null) {
            return null;
        }

        Matcher matcher =
                TOOL_PATTERN.matcher(
                        content.trim()
                );

        if (!matcher.matches()) {
            return null;
        }

        return matcher.group(1);
    }

    private ToolCallParser() {
    }
}