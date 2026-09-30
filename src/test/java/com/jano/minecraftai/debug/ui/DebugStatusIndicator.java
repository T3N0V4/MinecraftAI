package com.jano.minecraftai.debug.ui;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DebugStatusIndicator
        extends JPanel {

    private Color color;

    public DebugStatusIndicator(
            Color color
    ) {

        this.color =
                color;

        setOpaque(
                false
        );

        setPreferredSize(
                new Dimension(
                        14,
                        14
                )
        );
    }

    public void setColor(
            Color color
    ) {

        this.color =
                color;

        repaint();
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
                new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        45
                )
        );

        g2.fillOval(
                0,
                0,
                getWidth(),
                getHeight()
        );

        g2.setColor(
                color
        );

        g2.fillOval(
                4,
                4,
                getWidth() - 8,
                getHeight() - 8
        );

        g2.dispose();
    }
}