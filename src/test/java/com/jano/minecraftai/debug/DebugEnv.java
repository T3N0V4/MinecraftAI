package com.jano.minecraftai.debug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DebugEnv {

    private static final List<Path> ENV_FILES =
            List.of(
                    Path.of(
                            "C:\\Minecraft\\MinecraftAI\\.env"
                    ),
                    Path.of(
                            "C:\\Minecraft\\MinecraftServerManager\\.env"
                    )
            );

    public static String get(
            String name
    ) {

        String systemValue =
                System.getenv(
                        name
                );

        if (
                systemValue != null
                && !systemValue.isBlank()
        ) {

            return systemValue.trim();
        }

        for (
                Path file :
                ENV_FILES
        ) {

            String value =
                    readFromFile(
                            file,
                            name
                    );

            if (
                    value != null
                    && !value.isBlank()
            ) {

                return value;
            }
        }

        return null;
    }

    private static String readFromFile(
            Path file,
            String name
    ) {

        if (
                !Files.exists(
                        file
                )
        ) {
            return null;
        }

        try {

            for (
                    String line :
                    Files.readAllLines(
                            file
                    )
            ) {

                String trimmed =
                        line.trim();

                if (
                        trimmed.isBlank()
                        || trimmed.startsWith(
                                "#"
                        )
                ) {
                    continue;
                }

                int separator =
                        trimmed.indexOf(
                                '='
                        );

                if (
                        separator <= 0
                ) {
                    continue;
                }

                String key =
                        trimmed.substring(
                                0,
                                separator
                        ).trim();

                if (
                        !key.equals(
                                name
                        )
                ) {
                    continue;
                }

                String value =
                        trimmed.substring(
                                separator + 1
                        ).trim();

                if (
                        value.length() >= 2
                        && (
                        (
                                value.startsWith(
                                        "\""
                                )
                                && value.endsWith(
                                        "\""
                                )
                        )
                                || (
                                value.startsWith(
                                        "'"
                                )
                                && value.endsWith(
                                        "'"
                                )
                        )
                )
                ) {

                    value =
                            value.substring(
                                    1,
                                    value.length() - 1
                            );
                }

                return value;
            }

        } catch (
                Exception e
        ) {

            System.err.println(
                    "[MinecraftAI Debug] Error leyendo "
                            + file
                            + ": "
                            + e.getMessage()
            );
        }

        return null;
    }

    private DebugEnv() {
    }
}