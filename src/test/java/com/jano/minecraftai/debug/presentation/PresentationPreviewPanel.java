package com.jano.minecraftai.debug.presentation;

import com.jano.minecraftai.debug.ui.DebugTheme;
import com.jano.minecraftai.presentation.PresentationInput;
import com.jano.minecraftai.presentation.PresentationMapper;
import com.jano.minecraftai.presentation.PresentationType;
import com.jano.minecraftai.presentation.ResponsePresentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PresentationPreviewPanel extends JPanel {

    public enum State {
        READY,
        LISTENING,
        THINKING,
        SPEAKING
    }

    private State state =
            State.READY;

    private final JLabel stateLabel =
            new JLabel();

    private final JLabel typeLabel =
            new JLabel("CHAT");

    private final JTextArea voiceArea =
            new JTextArea();

    private final JTextArea displayArea =
            new JTextArea();

    private final JTextArea historyArea =
            new JTextArea();

    private final JPanel temporaryCard =
            new JPanel(new BorderLayout());

    private final List<ResponsePresentation> history =
            new ArrayList<>();

    private final Timer hideTimer;

    private final SimulatedClientBridge clientBridge =
            new SimulatedClientBridge();

    private final DebugTtsClient tts =
            new DebugTtsClient();

    private PresentationInput currentInput =
            PresentationInput.chat(
                    "Sí, estoy acá. ¿Qué querés mirar?"
            );

    public PresentationPreviewPanel() {

        setLayout(
                new BorderLayout(
                        12,
                        12
                )
        );

        setBackground(
                DebugTheme.BG
        );

        setBorder(
                new EmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
        );

        add(
                createTopBar(),
                BorderLayout.NORTH
        );

        add(
                createCenter(),
                BorderLayout.CENTER
        );

        add(
                createControls(),
                BorderLayout.SOUTH
        );

        hideTimer =
                new Timer(
                        4500,
                        e -> temporaryCard.setVisible(false)
                );

        hideTimer.setRepeats(false);

        setState(
                State.READY
        );

        showChat();
    }


    private JPanel createTopBar() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setOpaque(false);

        JLabel title =
                new JLabel(
                        "MINECRAFTAI // PRESENTATION PREVIEW"
                );

        title.setFont(
                DebugTheme.TITLE
        );

        title.setForeground(
                DebugTheme.TEXT
        );

        stateLabel.setFont(
                DebugTheme.SECTION
        );

        panel.add(
                title,
                BorderLayout.WEST
        );

        panel.add(
                stateLabel,
                BorderLayout.EAST
        );

        return panel;
    }


    private Component createCenter() {

        JPanel root =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                12,
                                0
                        )
                );

        root.setOpaque(false);

        root.add(
                createCurrentResponsePanel()
        );

        root.add(
                createHistoryPanel()
        );

        return root;
    }


    private JPanel createCurrentResponsePanel() {

        JPanel panel =
                cardPanel();

        panel.setLayout(
                new BorderLayout(
                        8,
                        8
                )
        );

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setOpaque(false);

        JLabel label =
                new JLabel(
                        "RESPUESTA ACTUAL"
                );

        label.setForeground(
                DebugTheme.TEXT_MUTED
        );

        label.setFont(
                DebugTheme.SMALL
        );

        typeLabel.setForeground(
                DebugTheme.ACCENT_BRIGHT
        );

        typeLabel.setFont(
                DebugTheme.SECTION
        );

        header.add(
                label,
                BorderLayout.WEST
        );

        header.add(
                typeLabel,
                BorderLayout.EAST
        );

        panel.add(
                header,
                BorderLayout.NORTH
        );


        JPanel body =
                new JPanel();

        body.setOpaque(false);

        body.setLayout(
                new BoxLayout(
                        body,
                        BoxLayout.Y_AXIS
                )
        );


        JLabel voiceTitle =
                sectionLabel(
                        "LO QUE DIRÍA LA VOZ"
                );

        body.add(
                voiceTitle
        );

        body.add(
                Box.createVerticalStrut(
                        5
                )
        );

        setupArea(
                voiceArea
        );

        voiceArea.setRows(
                4
        );

        body.add(
                scroll(
                        voiceArea
                )
        );

        body.add(
                Box.createVerticalStrut(
                        14
                )
        );


        JLabel displayTitle =
                sectionLabel(
                        "INFORMACIÓN EN PANTALLA"
                );

        body.add(
                displayTitle
        );

        body.add(
                Box.createVerticalStrut(
                        5
                )
        );

        setupArea(
                displayArea
        );

        displayArea.setRows(
                11
        );

        body.add(
                scroll(
                        displayArea
                )
        );

        body.add(
                Box.createVerticalStrut(
                        14
                )
        );


        temporaryCard.setBackground(
                DebugTheme.PANEL_ALT
        );

        temporaryCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                DebugTheme.ACCENT,
                                1
                        ),
                        new EmptyBorder(
                                12,
                                12,
                                12,
                                12
                        )
                )
        );

        JLabel temporaryLabel =
                new JLabel(
                        "TARJETA TEMPORAL ACTIVA"
                );

        temporaryLabel.setForeground(
                DebugTheme.ACCENT_BRIGHT
        );

        temporaryLabel.setFont(
                DebugTheme.SECTION
        );

        temporaryCard.add(
                temporaryLabel,
                BorderLayout.CENTER
        );

        body.add(
                temporaryCard
        );

        panel.add(
                body,
                BorderLayout.CENTER
        );

        return panel;
    }


    private JPanel createHistoryPanel() {

        JPanel panel =
                cardPanel();

        panel.setLayout(
                new BorderLayout(
                        8,
                        8
                )
        );

        JLabel title =
                sectionLabel(
                        "PANEL COMPLETO / HISTORIAL"
                );

        panel.add(
                title,
                BorderLayout.NORTH
        );

        setupArea(
                historyArea
        );

        panel.add(
                scroll(
                        historyArea
                ),
                BorderLayout.CENTER
        );

        JButton clear =
                button(
                        "Limpiar historial"
                );

        clear.addActionListener(
                e -> {
                    history.clear();
                    historyArea.setText("");
                }
        );

        panel.add(
                clear,
                BorderLayout.SOUTH
        );

        return panel;
    }


    private JPanel createControls() {

        JPanel root =
                new JPanel();

        root.setOpaque(false);

        root.setLayout(
                new BoxLayout(
                        root,
                        BoxLayout.Y_AXIS
                )
        );


        JPanel stateButtons =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                8,
                                0
                        )
                );

        stateButtons.setOpaque(false);

        JButton ready =
                button(
                        "READY"
                );

        JButton listening =
                button(
                        "ESCUCHANDO"
                );

        JButton thinking =
                button(
                        "PENSANDO"
                );

        JButton speaking =
                button(
                        "HABLANDO"
                );

        ready.addActionListener(
                e -> setState(
                        State.READY
                )
        );

        listening.addActionListener(
                e -> setState(
                        State.LISTENING
                )
        );

        thinking.addActionListener(
                e -> setState(
                        State.THINKING
                )
        );

        speaking.addActionListener(
                e -> setState(
                        State.SPEAKING
                )
        );

        stateButtons.add(
                ready
        );

        stateButtons.add(
                listening
        );

        stateButtons.add(
                thinking
        );

        stateButtons.add(
                speaking
        );


        JPanel typeButtons =
                new JPanel(
                        new GridLayout(
                                1,
                                5,
                                8,
                                0
                        )
                );

        typeButtons.setOpaque(false);

        JButton chat =
                button(
                        "CHAT"
                );

        JButton block =
                button(
                        "BLOCK"
                );

        JButton entity =
                button(
                        "ENTITY"
                );

        JButton inventory =
                button(
                        "INVENTORY"
                );

        JButton recipe =
                button(
                        "RECIPE"
                );

        chat.addActionListener(
                e -> showChat()
        );

        block.addActionListener(
                e -> showBlock()
        );

        entity.addActionListener(
                e -> showEntity()
        );

        inventory.addActionListener(
                e -> showInventory()
        );

        recipe.addActionListener(
                e -> showRecipe()
        );

        typeButtons.add(
                chat
        );

        typeButtons.add(
                block
        );

        typeButtons.add(
                entity
        );

        typeButtons.add(
                inventory
        );

        typeButtons.add(
                recipe
        );


        root.add(
                stateButtons
        );

        root.add(
                Box.createVerticalStrut(
                        8
                )
        );

        root.add(
                typeButtons
        );

        root.add(
                Box.createVerticalStrut(
                        8
                )
        );

        JButton simulate =
                button(
                        "SIMULAR RESPUESTA"
                );

        simulate.addActionListener(
                e -> simulateCurrentResponse()
        );

        root.add(
                simulate
        );

        root.add(
                Box.createVerticalStrut(
                        8
                )
        );

        JButton serverResponse =
                button(
                        "SIMULAR RESPUESTA DEL SERVIDOR"
                );

        serverResponse.addActionListener(
                e -> simulateServerResponse()
        );

        root.add(
                serverResponse
        );

        return root;
    }


    private void showChat() {

        currentInput =
                PresentationInput.chat(
                        "Sí, estoy acá. ¿Qué querés mirar?"
                );

        showPresentation(
                currentInput
        );
    }


    private void showBlock() {

        currentInput =
                new PresentationInput(
                        "Es mena de diamante de Minecraft.",
                        "get_target_block",
                        """
                        Diamond Ore

                        ID: minecraft:diamond_ore
                        Mod: Minecraft
                        Posición: 143, -32, 81
                        """
                );

        showPresentation(
                currentInput
        );
    }


    private void showEntity() {

        currentInput =
                new PresentationInput(
                        "Tenés un zombie bastante cerca.",
                        "get_target_entity",
                        """
                        Zombie

                        ID: minecraft:zombie
                        Distancia: 4.2 bloques
                        Vida: 16/20
                        Hostil: Sí
                        """
                );

        showPresentation(
                currentInput
        );
    }


    private void showInventory() {

        currentInput =
                new PresentationInput(
                        "Tenés diamantes y una espada de hierro.",
                        "get_inventory",
                        """
                        Inventario

                        64x Cobblestone
                        3x Diamond
                        1x Iron Sword
                        """
                );

        showPresentation(
                currentInput
        );
    }


    private void showRecipe() {

        currentInput =
                new PresentationInput(
                        "Para hacerla necesitás tres diamantes y dos palos.",
                        "get_recipe",
                        """
                        Diamond Pickaxe

                        3x Diamond
                        2x Stick
                        """
                );

        showPresentation(
                currentInput
        );
    }


    private void simulateServerResponse() {

        setState(
                State.THINKING
        );

        Timer serverDelay =
                new Timer(
                        900,
                        e -> {

                            MockServerResponse serverResponse =
                                    new MockServerResponse(
                                            "Tenés un creeper muy cerca.",
                                            "get_target_entity",
                                            """
                                            Creeper

                                            ID: minecraft:creeper
                                            Distancia: 2.1 bloques
                                            Vida: 20/20
                                            Hostil: Sí
                                            """
                                    );

                            ResponsePresentation presentation =
                                    clientBridge.receive(
                                            serverResponse
                                    );

                            showPresentation(
                                    new PresentationInput(
                                            presentation.getVoiceText(),
                                            serverResponse.getToolName(),
                                            presentation.getDisplayText()
                                    )
                            );

                            setState(
                                    State.SPEAKING
                            );

                            tts.speak(
                                    presentation.getVoiceText()
                            );

                            Timer speakingTimer =
                                    new Timer(
                                            1800,
                                            event -> setState(
                                                    State.READY
                                            )
                                    );

                            speakingTimer.setRepeats(
                                    false
                            );

                            speakingTimer.start();
                        }
                );

        serverDelay.setRepeats(
                false
        );

        serverDelay.start();
    }

    private void simulateCurrentResponse() {

        setState(
                State.THINKING
        );

        Timer thinkingTimer =
                new Timer(
                        900,
                        e -> {

                            showPresentation(
                                    currentInput
                            );

                            setState(
                                    State.SPEAKING
                            );

                            Timer speakingTimer =
                                    new Timer(
                                            1800,
                                            event -> setState(
                                                    State.READY
                                            )
                                    );

                            speakingTimer.setRepeats(
                                    false
                            );

                            speakingTimer.start();
                        }
                );

        thinkingTimer.setRepeats(
                false
        );

        thinkingTimer.start();
    }

    private void showPresentation(
            PresentationInput input
    ) {

        ResponsePresentation presentation =
                PresentationMapper.map(
                        input
                );

        typeLabel.setText(
                presentation.getType().name()
        );

        voiceArea.setText(
                presentation.getVoiceText()
        );

        displayArea.setText(
                presentation.getDisplayText()
        );

        history.add(
                presentation
        );

        appendHistory(
                presentation
        );

        if (
                presentation.getType()
                        != PresentationType.CHAT
        ) {

            temporaryCard.setVisible(
                    true
            );

            hideTimer.restart();

        } else {

            temporaryCard.setVisible(
                    false
            );
        }
    }


    private void appendHistory(
            ResponsePresentation presentation
    ) {

        if (
                !historyArea.getText().isBlank()
        ) {
            historyArea.append(
                    "\n\n"
            );
        }

        historyArea.append(
                "[" +
                        presentation.getType().name() +
                        "]\n"
        );

        historyArea.append(
                presentation.getDisplayText()
        );

        historyArea.setCaretPosition(
                historyArea.getDocument()
                        .getLength()
        );
    }


    public void setState(
            State state
    ) {

        this.state =
                state;

        switch (
                state
        ) {

            case READY -> {
                stateLabel.setText(
                        "● READY"
                );

                stateLabel.setForeground(
                        DebugTheme.SUCCESS
                );
            }

            case LISTENING -> {
                stateLabel.setText(
                        "● ESCUCHANDO"
                );

                stateLabel.setForeground(
                        DebugTheme.ACCENT_BRIGHT
                );
            }

            case THINKING -> {
                stateLabel.setText(
                        "● PENSANDO"
                );

                stateLabel.setForeground(
                        DebugTheme.VIOLET
                );
            }

            case SPEAKING -> {
                stateLabel.setText(
                        "● HABLANDO"
                );

                stateLabel.setForeground(
                        DebugTheme.WARNING
                );
            }
        }
    }


    private JPanel cardPanel() {

        JPanel panel =
                new JPanel();

        panel.setBackground(
                DebugTheme.PANEL
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                DebugTheme.BORDER,
                                1
                        ),
                        new EmptyBorder(
                                14,
                                14,
                                14,
                                14
                        )
                )
        );

        return panel;
    }


    private JLabel sectionLabel(
            String text
    ) {

        JLabel label =
                new JLabel(
                        text
                );

        label.setForeground(
                DebugTheme.TEXT_MUTED
        );

        label.setFont(
                DebugTheme.SECTION
        );

        return label;
    }


    private void setupArea(
            JTextArea area
    ) {

        area.setEditable(
                false
        );

        area.setLineWrap(
                true
        );

        area.setWrapStyleWord(
                true
        );

        area.setBackground(
                DebugTheme.FIELD
        );

        area.setForeground(
                DebugTheme.TEXT
        );

        area.setCaretColor(
                DebugTheme.ACCENT
        );

        area.setFont(
                DebugTheme.BODY
        );

        area.setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );
    }


    private JScrollPane scroll(
            JTextArea area
    ) {

        JScrollPane scroll =
                new JScrollPane(
                        area
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        DebugTheme.BORDER_SOFT
                )
        );

        return scroll;
    }


    private JButton button(
            String text
    ) {

        JButton button =
                new JButton(
                        text
                );

        button.setFocusPainted(
                false
        );

        button.setBackground(
                DebugTheme.PANEL_ALT
        );

        button.setForeground(
                DebugTheme.TEXT
        );

        button.setFont(
                DebugTheme.SECTION
        );

        return button;
    }
}