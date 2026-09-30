package com.jano.minecraftai.debug.ui;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public class DebugBackdropPanel
        extends JPanel {

    private double time =
            0.0;

    public DebugBackdropPanel(
            LayoutManager layout
    ) {

        super(layout);

        setOpaque(false);

        Timer timer =
                new Timer(
                        50,
                        e -> {
                            time += 0.012;
                            repaint();
                        }
                );

        timer.start();
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

        GradientPaint gradient =
                new GradientPaint(
                        0,
                        0,
                        DebugTheme.BG,
                        getWidth(),
                        getHeight(),
                        DebugTheme.BG_2
                );

        g2.setPaint(gradient);

        g2.fillRect(
                0,
                0,
                getWidth(),
                getHeight()
        );

        // grid casi invisible
        g2.setColor(
                new Color(
                        34,
                        167,
                        255,
                        9
                )
        );

        int grid =
                48;

        for (
                int x = 0;
                x < getWidth();
                x += grid
        ) {

            g2.drawLine(
                    x,
                    0,
                    x,
                    getHeight()
            );
        }

        for (
                int y = 0;
                y < getHeight();
                y += grid
        ) {

            g2.drawLine(
                    0,
                    y,
                    getWidth(),
                    y
            );
        }

        // puntos lentos
        for (
                int i = 0;
                i < 12;
                i++
        ) {

            double px =
                    (
                            i * 173
                                    + time * 42
                    )
                            % Math.max(
                            1,
                            getWidth()
                    );

            double py =
                    (
                            i * 97
                                    + Math.sin(
                                    time * 2 + i
                            ) * 25
                                    + getHeight()
                    )
                            % Math.max(
                            1,
                            getHeight()
                    );

            int alpha =
                    18
                            + (
                            i % 3
                    ) * 9;

            g2.setColor(
                    new Color(
                            34,
                            167,
                            255,
                            alpha
                    )
            );

            g2.fillOval(
                    (int) px,
                    (int) py,
                    2,
                    2
            );
        }

        g2.dispose();
    }
}