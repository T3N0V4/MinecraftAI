package com.jano.minecraftai.ai;

import com.jano.minecraftai.tools.MinecraftAITool;
import com.jano.minecraftai.tools.ToolManager;

import java.util.Map;

public final class ToolCatalogBuilder {

    public static String build() {

        StringBuilder builder =
                new StringBuilder();


        for (
                MinecraftAITool tool :
                ToolManager
                        .getRegistry()
                        .getTools()
        ) {

            builder.append(
                    tool.getName()
            );

            builder.append(
                    " | "
            );

            builder.append(
                    tool.getDescription()
            );


            Map<String, String> arguments =
                    tool.getArguments();


            if (
                    arguments != null
                    && !arguments.isEmpty()
            ) {

                builder.append(
                        " | argumentos: "
                );


                boolean first =
                        true;


                for (
                        Map.Entry<String, String> argument :
                        arguments.entrySet()
                ) {

                    if (!first) {

                        builder.append(
                                ", "
                        );
                    }

                    first =
                            false;


                    builder.append(
                            argument.getKey()
                    );

                    builder.append(
                            "="
                    );

                    builder.append(
                            argument.getValue()
                    );
                }
            }


            builder.append(
                    "\n"
            );
        }


        return builder.toString();
    }


    private ToolCatalogBuilder() {
    }
}