package com.jano.minecraftai.debug.ui;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DebugCard
        extends JPanel {

    private final int arc =
            18;

    public DebugCard() {

        setOpaque(
                false
        );

        setBorder(
                new EmptyBorder(
                        12,
                        12,
                        12,
                        12
                )
        );
    }

    @Override
    protected void paintComponent(
            Graphics g
    ) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(
                DebugTheme.PANEL_ALT
        );

        g2.fillRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                arc,
                arc
        );

        g2.setColor(
                DebugTheme.BORDER
        );

        g2.drawRoundRect(
                0,
                0,
                getWidth() - 1,
                getHeight() - 1,
                arc,
                arc
        );

        g2.dispose();

        super.paintComponent(
                g
        );
    }
}