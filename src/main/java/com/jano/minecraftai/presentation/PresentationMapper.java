package com.jano.minecraftai.presentation;

public final class PresentationMapper {

    private PresentationMapper() {
    }

    public static ResponsePresentation map(
            PresentationInput input
    ) {
        if (input == null) {
            return new ResponsePresentation(
                    "",
                    "",
                    PresentationType.CHAT
            );
        }

        PresentationType type =
                PresentationTypeResolver.resolve(
                        input.getToolName()
                );

        String voiceText =
                input.getAiText();

        String displayText =
                buildDisplayText(
                        input,
                        type
                );

        return new ResponsePresentation(
                voiceText,
                displayText,
                type
        );
    }


    private static String buildDisplayText(
            PresentationInput input,
            PresentationType type
    ) {
        if (type == PresentationType.CHAT) {
            return input.getAiText();
        }

        if (!input.hasToolResult()) {
            return input.getAiText();
        }

        if (input.getAiText().isBlank()) {
            return input.getToolResult();
        }

        return input.getAiText()
                + "\n\n"
                + input.getToolResult();
    }
}