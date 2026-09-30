package com.jano.minecraftai.debug.presentation;

import com.jano.minecraftai.debug.ui.DebugTheme;

import javax.swing.*;
import java.awt.*;

public class PresentationDebugPreview {

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    JFrame frame =
                            new JFrame(
                                    "MinecraftAI Presentation Preview"
                            );

                    frame.setDefaultCloseOperation(
                            WindowConstants.EXIT_ON_CLOSE
                    );

                    frame.setSize(
                            1200,
                            760
                    );

                    frame.setMinimumSize(
                            new Dimension(
                                    1000,
                                    650
                            )
                    );

                    frame.setLocationRelativeTo(
                            null
                    );

                    frame.getContentPane()
                            .setBackground(
                                    DebugTheme.BG
                            );

                    frame.setContentPane(
                            new PresentationPreviewPanel()
                    );

                    frame.setVisible(
                            true
                    );
                }
        );
    }
}