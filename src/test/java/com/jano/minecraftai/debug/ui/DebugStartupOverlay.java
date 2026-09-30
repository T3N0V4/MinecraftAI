package com.jano.minecraftai.debug.ui;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DebugStartupOverlay
        extends JComponent {

    private long startedAt;

    private final Timer timer;

    private DebugStartupOverlay() {

        setOpaque(false);

        timer =
                new Timer(
                        16,
                        e -> repaint()
                );
    }

    public static void show(
            JFrame frame
    ) {

        DebugStartupOverlay overlay =
                new DebugStartupOverlay();

        frame.setGlassPane(overlay);

        overlay.startedAt =
                System.currentTimeMillis();

        overlay.setVisible(true);

        overlay.timer.start();
    }

    @Override
    protected void paintComponent(
            Graphics g
    ) {

        long elapsed =
                System.currentTimeMillis()
                        - startedAt;

        if (
                elapsed > 2100
        ) {

            timer.stop();

            setVisible(false);

            return;
        }

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        double fadeIn =
                Math.min(
                        1.0,
                        elapsed / 350.0
                );

        double fadeOut =
                elapsed > 1750
                        ? Math.max(
                        0.0,
                        1.0
                                - (
                                elapsed - 1750
                        ) / 350.0
                )
                        : 1.0;

        float alpha =
                (float) (
                        fadeIn
                                * fadeOut
                );

        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        alpha
                )
        );

        g2.setColor(
                new Color(
                        4,
                        8,
                        13,
                        246
                )
        );

        g2.fillRect(
                0,
                0,
                getWidth(),
                getHeight()
        );

        int cx =
                getWidth() / 2;

        int cy =
                getHeight() / 2 - 55;

        double time =
                elapsed / 1000.0;

        double pulse =
                0.5
                        + 0.5
                        * Math.sin(
                        time * 3.2
                );

        for (
                int i = 3;
                i >= 0;
                i--
        ) {

            int radius =
                    31
                            + i * 15
                            + (int) (
                            pulse * 4
                    );

            g2.setColor(
                    new Color(
                            34,
                            167,
                            255,
                            18 + i * 8
                    )
            );

            g2.fillOval(
                    cx - radius,
                    cy - radius,
                    radius * 2,
                    radius * 2
            );
        }

        g2.setColor(
                DebugTheme.ACCENT
        );

        g2.setStroke(
                new BasicStroke(
                        1.3f
                )
        );

        int ring =
                58;

        g2.drawOval(
                cx - ring,
                cy - ring,
                ring * 2,
                ring * 2
        );

        g2.setColor(
                Color.WHITE
        );

        g2.fillOval(
                cx - 10,
                cy - 10,
                20,
                20
        );

        g2.setColor(
                DebugTheme.ACCENT
        );

        g2.fillOval(
                cx - 6,
                cy - 6,
                12,
                12
        );

        drawCentered(
                g2,
                "MinecraftAI Debug",
                cx,
                cy + 100,
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        27
                ),
                DebugTheme.TEXT
        );

        if (
                elapsed > 550
        ) {

            drawCentered(
                    g2,
                    "Gemini",
                    cx,
                    cy + 136,
                    DebugTheme.BODY,
                    DebugTheme.TEXT_MUTED
            );

            drawDot(
                    g2,
                    cx - 65,
                    cy + 132,
                    DebugTheme.SUCCESS
            );
        }

        if (
                elapsed > 850
        ) {

            drawCentered(
                    g2,
                    "STT",
                    cx,
                    cy + 160,
                    DebugTheme.BODY,
                    DebugTheme.TEXT_MUTED
            );

            drawDot(
                    g2,
                    cx - 65,
                    cy + 156,
                    DebugTheme.SUCCESS
            );
        }

        if (
                elapsed > 1150
        ) {

            drawCentered(
                    g2,
                    "Contexto listo",
                    cx,
                    cy + 184,
                    DebugTheme.BODY,
                    DebugTheme.TEXT_MUTED
            );

            drawDot(
                    g2,
                    cx - 65,
                    cy + 180,
                    DebugTheme.SUCCESS
            );
        }

        g2.dispose();
    }

    private void drawDot(
            Graphics2D g2,
            int x,
            int y,
            Color color
    ) {

        g2.setColor(color);

        g2.fillOval(
                x,
                y,
                6,
                6
        );
    }

    private void drawCentered(
            Graphics2D g2,
            String text,
            int x,
            int y,
            Font font,
            Color color
    ) {

        g2.setFont(font);

        g2.setColor(color);

        FontMetrics fm =
                g2.getFontMetrics();

        int width =
                fm.stringWidth(text);

        g2.drawString(
                text,
                x - width / 2,
                y
        );
    }
}