package com.jano.minecraftai.debug.ui;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DebugCard
        extends JPanel {

    private boolean highlighted;

    public DebugCard() {

        setOpaque(false);

        setBorder(
                new EmptyBorder(
                        14,
                        16,
                        14,
                        16
                )
        );
    }

    public void setHighlighted(
            boolean highlighted
    ) {

        this.highlighted =
                highlighted;

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

        int w =
                getWidth() - 1;

        int h =
                getHeight() - 1;

        if (
                highlighted
        ) {

            g2.setColor(
                    DebugTheme.ACCENT_SOFT
            );

            g2.fillRoundRect(
                    0,
                    0,
                    w,
                    h,
                    20,
                    20
            );
        }

        g2.setColor(
                DebugTheme.PANEL
        );

        g2.fillRoundRect(
                2,
                2,
                w - 4,
                h - 4,
                18,
                18
        );

        g2.setColor(
                highlighted
                        ? DebugTheme.ACCENT
                        : DebugTheme.BORDER
        );

        g2.drawRoundRect(
                2,
                2,
                w - 4,
                h - 4,
                18,
                18
        );

        g2.dispose();

        super.paintComponent(g);
    }
}