package com.jano.minecraftai.debug.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class DebugHeaderPanel
        extends DebugCard {

    private final JLabel mainStatus =
            new JLabel(
                    "Listo"
            );

    private final AIOrbPanel orb =
            new AIOrbPanel();

    private final DebugStatusIndicator geminiIndicator =
            new DebugStatusIndicator(
                    DebugTheme.WARNING
            );

    private final DebugStatusIndicator sttIndicator =
            new DebugStatusIndicator(
                    DebugTheme.WARNING
            );

    public DebugHeaderPanel() {

        setLayout(
                new BorderLayout(
                        20,
                        0
                )
        );

        setPreferredSize(
                new Dimension(
                        100,
                        98
                )
        );

        JPanel titles =
                new JPanel();

        titles.setOpaque(false);

        titles.setLayout(
                new BoxLayout(
                        titles,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title =
                new JLabel(
                        "MinecraftAI Debug"
                );

        title.setForeground(
                DebugTheme.TEXT
        );

        title.setFont(
                DebugTheme.TITLE
        );

        JLabel subtitle =
                new JLabel(
                        "Playground de contexto, memoria, tools y prompt"
                );

        subtitle.setForeground(
                DebugTheme.TEXT_MUTED
        );

        subtitle.setFont(
                DebugTheme.SUBTITLE
        );

        titles.add(
                Box.createVerticalGlue()
        );

        titles.add(title);

        titles.add(
                Box.createVerticalStrut(
                        5
                )
        );

        titles.add(subtitle);

        titles.add(
                Box.createVerticalGlue()
        );

        JPanel right =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        right.setOpaque(false);

        orb.setPreferredSize(
                new Dimension(
                        92,
                        82
                )
        );

        JPanel status =
                new JPanel();

        status.setOpaque(false);

        status.setLayout(
                new BoxLayout(
                        status,
                        BoxLayout.Y_AXIS
                )
        );

        mainStatus.setForeground(
                DebugTheme.SUCCESS
        );

        mainStatus.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        14
                )
        );

        status.add(
                Box.createVerticalGlue()
        );

        status.add(mainStatus);

        status.add(
                Box.createVerticalStrut(
                        8
                )
        );

        status.add(
                createStatusRow(
                        geminiIndicator,
                        "Gemini"
                )
        );

        status.add(
                Box.createVerticalStrut(
                        3
                )
        );

        status.add(
                createStatusRow(
                        sttIndicator,
                        "STT"
                )
        );

        status.add(
                Box.createVerticalGlue()
        );

        right.add(
                orb,
                BorderLayout.WEST
        );

        right.add(
                status,
                BorderLayout.CENTER
        );

        add(
                titles,
                BorderLayout.WEST
        );

        add(
                right,
                BorderLayout.EAST
        );
    }

    private JPanel createStatusRow(
            DebugStatusIndicator indicator,
            String text
    ) {

        JPanel row =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                3,
                                0
                        )
                );

        row.setOpaque(false);

        JLabel label =
                new JLabel(text);

        label.setForeground(
                DebugTheme.TEXT_MUTED
        );

        label.setFont(
                DebugTheme.SMALL
        );

        row.add(indicator);

        row.add(label);

        return row;
    }

    public void setMainStatus(
            String text
    ) {

        if (
                text == null
        ) {

            return;
        }

        String lower =
                text.toLowerCase();

        if (
                lower.contains(
                        "pensando"
                )
                || lower.contains(
                        "thinking"
                )
        ) {

            mainStatus.setText(
                    "Pensando"
            );

            mainStatus.setForeground(
                    DebugTheme.ACCENT
            );

            orb.setState(
                    AIOrbPanel.State.THINKING
            );

            return;
        }

        if (
                lower.contains(
                        "escuchando"
                )
                || lower.contains(
                        "listening"
                )
        ) {

            mainStatus.setText(
                    "Escuchando"
            );

            mainStatus.setForeground(
                    DebugTheme.ACCENT_BRIGHT
            );

            orb.setState(
                    AIOrbPanel.State.LISTENING
            );

            return;
        }

        if (
                lower.contains(
                        "transcrib"
                )
        ) {

            mainStatus.setText(
                    "Transcribiendo"
            );

            mainStatus.setForeground(
                    DebugTheme.VIOLET
            );

            orb.setState(
                    AIOrbPanel.State.TRANSCRIBING
            );

            return;
        }

        if (
                lower.contains(
                        "error"
                )
                || lower.contains(
                        "offline"
                )
                || lower.contains(
                        "no disponible"
                )
        ) {

            mainStatus.setText(text);

            mainStatus.setForeground(
                    DebugTheme.ERROR
            );

            orb.setState(
                    AIOrbPanel.State.ERROR
            );

            return;
        }

        if (
                lower.contains(
                        "listo"
                )
                || lower.contains(
                        "ready"
                )
                || lower.contains(
                        "conectado"
                )
        ) {

            mainStatus.setText(
                    text
            );

            mainStatus.setForeground(
                    DebugTheme.SUCCESS
            );

            orb.setState(
                    AIOrbPanel.State.READY
            );

            return;
        }

        mainStatus.setText(text);

        mainStatus.setForeground(
                DebugTheme.TEXT
        );
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