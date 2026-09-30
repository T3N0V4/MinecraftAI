package com.jano.minecraftai.debug.presentation;

import com.jano.minecraftai.presentation.PresentationInput;
import com.jano.minecraftai.presentation.PresentationMapper;
import com.jano.minecraftai.presentation.ResponsePresentation;

public class SimulatedClientBridge {

    public ResponsePresentation receive(
            MockServerResponse response
    ) {
        PresentationInput input =
                new PresentationInput(
                        response.getAiText(),
                        response.getToolName(),
                        response.getToolResult()
                );

        return PresentationMapper.map(
                input
        );
    }
}