package com.jano.minecraftai.debug.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DebugHeaderPanel
        extends DebugCard {

    private final JLabel mainStatus =
            new JLabel(
                    "INITIALIZING"
            );

    private final DebugStatusIndicator geminiIndicator =
            new DebugStatusIndicator(
                    DebugTheme.WARNING
            );

    private final DebugStatusIndicator sttIndicator =
            new DebugStatusIndicator(
                    DebugTheme.WARNING
            );

    private float pulse =
            0f;

    private boolean pulseForward =
            true;

    private int scanX =
            0;

    private final Timer animationTimer;

    public DebugHeaderPanel() {

        setLayout(
                new BorderLayout(
                        16,
                        0
                )
        );

        JPanel titles =
                new JPanel();

        titles.setOpaque(
                false
        );

        titles.setLayout(
                new BoxLayout(
                        titles,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title =
                new JLabel(
                        "MinecraftAI Debug Console"
                );

        title.setForeground(
                DebugTheme.TEXT
        );

        title.setFont(
                DebugTheme.TITLE
        );

        JLabel subtitle =
                new JLabel(
                        "Context Engine · Scenario Lab"
                );

        subtitle.setForeground(
                DebugTheme.TEXT_MUTED
        );

        subtitle.setFont(
                DebugTheme.SUBTITLE
        );

        titles.add(
                title
        );

        titles.add(
                Box.createVerticalStrut(
                        2
                )
        );

        titles.add(
                subtitle
        );

        JPanel status =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        status.setOpaque(
                false
        );

        JLabel gemini =
                new JLabel(
                        "Gemini"
                );

        gemini.setForeground(
                DebugTheme.TEXT_MUTED
        );

        JLabel stt =
                new JLabel(
                        "STT"
                );

        stt.setForeground(
                DebugTheme.TEXT_MUTED
        );

        mainStatus.setForeground(
                DebugTheme.ACCENT
        );

        mainStatus.setFont(
                DebugTheme.SECTION
        );

        status.add(
                mainStatus
        );

        status.add(
                Box.createHorizontalStrut(
                        10
                )
        );

        status.add(
                geminiIndicator
        );

        status.add(
                gemini
        );

        status.add(
                sttIndicator
        );

        status.add(
                stt
        );

        add(
                titles,
                BorderLayout.WEST
        );

        add(
                status,
                BorderLayout.EAST
        );

        animationTimer =
                new Timer(
                        35,
                        e -> animate()
                );

        animationTimer.start();
    }

    private void animate() {

        if (
                pulseForward
        ) {

            pulse +=
                    0.02f;

            if (
                    pulse >= 1f
            ) {

                pulse =
                        1f;

                pulseForward =
                        false;
            }

        } else {

            pulse -=
                    0.02f;

            if (
                    pulse <= 0f
            ) {

                pulse =
                        0f;

                pulseForward =
                        true;
            }
        }

        scanX +=
                4;

        if (
                scanX > getWidth() + 120
        ) {

            scanX =
                    -120;
        }

        repaint();
    }

    @Override
    protected void paintComponent(
            Graphics g
    ) {

        super.paintComponent(
                g
        );

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        float glowAlpha =
                0.10f
                        + pulse
                        * 0.16f;

        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        glowAlpha
                )
        );

        g2.setColor(
                DebugTheme.ACCENT
        );

        g2.fillRoundRect(
                8,
                getHeight() - 5,
                getWidth() - 16,
                2,
                8,
                8
        );

        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        0.75f
                )
        );

        Color scanColor =
                new Color(
                        DebugTheme.ACCENT.getRed(),
                        DebugTheme.ACCENT.getGreen(),
                        DebugTheme.ACCENT.getBlue(),
                        190
                );

        g2.setColor(
                scanColor
        );

        g2.fillRoundRect(
                scanX,
                getHeight() - 6,
                90,
                3,
                8,
                8
        );

        g2.dispose();
    }

    public void setMainStatus(
            String text
    ) {

        mainStatus.setText(
                text
        );

        if (
                text.contains(
                        "THINKING"
                )
                || text.contains(
                        "LISTENING"
                )
                || text.contains(
                        "TRANSCRIBING"
                )
        ) {

            mainStatus.setForeground(
                    DebugTheme.ACCENT
            );

        } else if (
                text.contains(
                        "OFFLINE"
                )
        ) {

            mainStatus.setForeground(
                    DebugTheme.ERROR
            );

        } else {

            mainStatus.setForeground(
                    DebugTheme.SUCCESS
            );
        }
    }

    public void setGeminiOnline(
            boolean online
    ) {

        geminiIndicator.setColor(
                online
                        ? DebugTheme.SUCCESS
                        : DebugTheme.ERROR
        );
    }

    public void setSttOnline(
            boolean online
    ) {

        sttIndicator.setColor(
                online
                        ? DebugTheme.SUCCESS
                        : DebugTheme.ERROR
        );
    }
}