package com.jano.minecraftai.debug.ui;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DebugStatusIndicator
        extends JPanel {

    private Color color;

    private double time =
            0.0;

    public DebugStatusIndicator(
            Color color
    ) {

        this.color =
                color;

        setOpaque(false);

        setPreferredSize(
                new Dimension(
                        14,
                        14
                )
        );

        Timer timer =
                new Timer(
                        35,
                        e -> {
                            time += 0.08;
                            repaint();
                        }
                );

        timer.start();
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

        double pulse =
                0.5
                        + 0.5
                        * Math.sin(time);

        int halo =
                9
                        + (int) (
                        pulse * 3
                );

        g2.setColor(
                new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        40
                )
        );

        g2.fillOval(
                7 - halo / 2,
                7 - halo / 2,
                halo,
                halo
        );

        g2.setColor(color);

        g2.fillOval(
                5,
                5,
                5,
                5
        );

        g2.dispose();
    }
}