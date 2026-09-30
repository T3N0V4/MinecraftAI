package com.jano.minecraftai.debug.ui;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class AIOrbPanel
        extends JPanel {

    public enum State {
        READY,
        LISTENING,
        TRANSCRIBING,
        THINKING,
        ERROR
    }

    private State state =
            State.READY;

    private double time =
            0.0;

    private final Timer timer;

    public AIOrbPanel() {

        setOpaque(false);

        timer =
                new Timer(
                        16,
                        e -> {
                            time += 0.035;
                            repaint();
                        }
                );

        timer.start();
    }

    public void setState(
            State state
    ) {

        if (
                state != null
        ) {

            this.state =
                    state;

            repaint();
        }
    }

    public State getState() {

        return state;
    }

    private Color currentColor() {

        if (
                state == State.ERROR
        ) {

            return DebugTheme.ERROR;
        }

        if (
                state == State.LISTENING
        ) {

            return DebugTheme.ACCENT_BRIGHT;
        }

        if (
                state == State.TRANSCRIBING
        ) {

            return DebugTheme.VIOLET;
        }

        return DebugTheme.ACCENT;
    }

    @Override
    protected void paintComponent(
            Graphics g
    ) {

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int cx =
                getWidth() / 2;

        int cy =
                getHeight() / 2;

        int base =
                Math.min(
                        getWidth(),
                        getHeight()
                );

        Color color =
                currentColor();

        double pulse =
                0.5
                        + 0.5
                        * Math.sin(time * 1.6);

        // halo exterior
        for (
                int i = 5;
                i >= 1;
                i--
        ) {

            float alpha =
                    (float) (
                            0.025
                                    + pulse
                                    * 0.012
                    );

            g2.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            alpha
                    )
            );

            g2.setColor(color);

            int r =
                    (int) (
                            base * 0.23
                                    + i * 6
                                    + pulse * 3
                    );

            g2.fillOval(
                    cx - r,
                    cy - r,
                    r * 2,
                    r * 2
            );
        }

        g2.setComposite(
                AlphaComposite.SrcOver
        );

        // anillos
        for (
                int i = 0;
                i < 3;
                i++
        ) {

            int r =
                    (int) (
                            base * (0.28 + i * 0.09)
                    );

            float alpha =
                    0.18f
                            + i * 0.05f;

            g2.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            alpha
                    )
            );

            g2.setColor(color);

            g2.setStroke(
                    new BasicStroke(
                            i == 0
                                    ? 1.8f
                                    : 1.0f
                    )
            );

            int start =
                    (int) (
                            Math.toDegrees(
                                    time
                                            * (
                                            i % 2 == 0
                                                    ? 0.65
                                                    : -0.45
                                    )
                            )
                    );

            g2.drawArc(
                    cx - r,
                    cy - r,
                    r * 2,
                    r * 2,
                    start,
                    i == 1
                            ? 245
                            : 300
            );
        }

        g2.setComposite(
                AlphaComposite.SrcOver
        );

        // partículas/nodos
        int nodes =
                state == State.THINKING
                        ? 7
                        : 4;

        for (
                int i = 0;
                i < nodes;
                i++
        ) {

            double angle =
                    time
                            * (
                            state == State.THINKING
                                    ? 1.35
                                    : 0.55
                    )
                            + (
                            Math.PI
                                    * 2
                                    * i
                                    / nodes
                    );

            double radius =
                    base
                            * (
                            state == State.TRANSCRIBING
                                    ? 0.18
                                            + Math.abs(
                                            Math.sin(
                                                    time * 1.8 + i
                                            )
                                    ) * 0.20
                                    : 0.36
                    );

            int x =
                    (int) (
                            cx
                                    + Math.cos(angle)
                                    * radius
                    );

            int y =
                    (int) (
                            cy
                                    + Math.sin(angle)
                                    * radius
                    );

            int dot =
                    state == State.THINKING
                            ? 5
                            : 4;

            g2.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            0.85f
                    )
            );

            g2.setColor(color);

            g2.fillOval(
                    x - dot / 2,
                    y - dot / 2,
                    dot,
                    dot
            );
        }

        // listening = ondas
        if (
                state == State.LISTENING
        ) {

            for (
                    int i = 0;
                    i < 3;
                    i++
            ) {

                double phase =
                        (
                                time * 1.35
                                        + i * 0.33
                        ) % 1.0;

                int r =
                        (int) (
                                base
                                        * (
                                        0.22
                                                + phase * 0.35
                                )
                        );

                float alpha =
                        (float) (
                                (1.0 - phase)
                                        * 0.24
                        );

                g2.setComposite(
                        AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER,
                                alpha
                        )
                );

                g2.setColor(color);

                g2.setStroke(
                        new BasicStroke(
                                1.3f
                        )
                );

                g2.drawOval(
                        cx - r,
                        cy - r,
                        r * 2,
                        r * 2
                );
            }
        }

        // centro
        int core =
                (int) (
                        base
                                * (
                                0.12
                                        + pulse * 0.015
                        )
                );

        for (
                int i = 4;
                i >= 1;
                i--
        ) {

            float alpha =
                    0.035f
                            * i;

            g2.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            alpha
                    )
            );

            g2.setColor(color);

            int r =
                    core + i * 6;

            g2.fillOval(
                    cx - r,
                    cy - r,
                    r * 2,
                    r * 2
            );
        }

        g2.setComposite(
                AlphaComposite.SrcOver
        );

        g2.setColor(
                Color.WHITE
        );

        g2.fillOval(
                cx - core,
                cy - core,
                core * 2,
                core * 2
        );

        int inner =
                Math.max(
                        3,
                        core - 4
                );

        g2.setColor(color);

        g2.fillOval(
                cx - inner,
                cy - inner,
                inner * 2,
                inner * 2
        );

        g2.dispose();
    }
}