package com.jano.minecraftai.debug;

import com.jano.minecraftai.ai.AIRequest;
import com.jano.minecraftai.ai.AIResponse;
import com.jano.minecraftai.ai.ConversationMemory;
import com.jano.minecraftai.ai.MinecraftAIPrompt;
import com.jano.minecraftai.ai.providers.GeminiProvider;

import com.jano.minecraftai.context.PlayerContext;
import com.jano.minecraftai.context.TargetContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.util.concurrent.CompletableFuture;

public class MinecraftAIDebugGui {

    private static final Color BG =
            new Color(22, 23, 26);

    private static final Color PANEL =
            new Color(31, 33, 37);

    private static final Color PANEL_2 =
            new Color(39, 42, 47);

    private static final Color FIELD =
            new Color(46, 49, 55);

    private static final Color TEXT =
            new Color(225, 228, 233);

    private static final Color MUTED =
            new Color(155, 160, 170);

    private static final Color ACCENT =
            new Color(115, 145, 255);

    private static final Color SUCCESS =
            new Color(110, 210, 150);

    private static final Color ERROR =
            new Color(235, 105, 105);

    private final GeminiProvider gemini =
            new GeminiProvider(
                    DebugEnv.get(
                            "GEMINI_API_KEY"
                    )
            );

    private final ConversationMemory memory =
            new ConversationMemory();

    private final UUID playerId =
            UUID.randomUUID();
private final List<String> inputHistory =
            new ArrayList<>();

    private int historyIndex =
            0;

    private JFrame frame;

    private JTextArea chatArea;
    private JTextArea promptArea;
    private JTextArea historyArea;

    private JTextField inputField;

    private JLabel statusLabel;

    private JTextField playerField;
    private JComboBox<String> biomeBox;
    private JComboBox<String> dimensionBox;
    private JComboBox<String> gamemodeBox;

    private JTextField healthField;
    private JTextField hungerField;
    private JTextField xpField;

    private AutoCompleteTextField targetIdField;
    private JTextField targetNameField;
    private JComboBox<String> targetTypeBox;

    private JComboBox<String> structureIdBox;
    private JComboBox<String> structureNameBox;

    private boolean syncingStructure = false;

    private String lastPrompt =
            "";

    private final DebugSttClient stt =
            new DebugSttClient();

    private JButton micButton;


    private volatile boolean voiceRecording =
            false;

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> new MinecraftAIDebugGui().start()
        );
    }

    private void start() {

        configureUi();

        frame =
                new JFrame(
                        "MinecraftAI Debug"
                );

        frame.setDefaultCloseOperation(
                WindowConstants.EXIT_ON_CLOSE
        );

        frame.setMinimumSize(
                new Dimension(
                        1100,
                        700
                )
        );

        frame.setSize(
                1280,
                800
        );

        frame.setLocationRelativeTo(
                null
        );

        frame.setContentPane(
                createMainPanel()
        );

        resetContext();

        appendSystem(
                "MinecraftAI Debug iniciado."
        );

        if (
                gemini.isAvailable()
        ) {

            setStatus(
                    "Gemini conectado",
                    SUCCESS
            );

        } else {

            setStatus(
                    "GEMINI_API_KEY no disponible",
                    ERROR
            );
        }

        frame.addWindowListener(
                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent e
                    ) {

                        stt.close();
                    }
                }
        );

        frame.setVisible(
                true
        );

        inputField.requestFocusInWindow();
    }

    private void configureUi() {

        UIManager.put(
                "Panel.background",
                BG
        );

        UIManager.put(
                "OptionPane.background",
                PANEL
        );

        UIManager.put(
                "OptionPane.messageForeground",
                TEXT
        );

        UIManager.put(
                "ScrollPane.background",
                PANEL
        );

        UIManager.put(
                "Viewport.background",
                PANEL
        );

        UIManager.put(
                "TabbedPane.background",
                PANEL
        );

        UIManager.put(
                "TabbedPane.foreground",
                TEXT
        );

        UIManager.put(
                "TabbedPane.selected",
                PANEL_2
        );

        UIManager.put(
                "ComboBox.background",
                FIELD
        );

        UIManager.put(
                "ComboBox.foreground",
                TEXT
        );

        UIManager.put(
                "List.background",
                FIELD
        );

        UIManager.put(
                "List.foreground",
                TEXT
        );
    }

    private JPanel createMainPanel() {

        JPanel root =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        root.setBackground(
                BG
        );

        root.setBorder(
                new EmptyBorder(
                        12,
                        12,
                        12,
                        12
                )
        );

        root.add(
                createHeader(),
                BorderLayout.NORTH
        );

        JSplitPane split =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        createContextPanel(),
                        createWorkPanel()
                );

        split.setDividerLocation(
                340
        );

        split.setResizeWeight(
                0
        );

        split.setBorder(
                null
        );

        split.setBackground(
                BG
        );

        root.add(
                split,
                BorderLayout.CENTER
        );

        return root;
    }

    private JPanel createHeader() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                BG
        );

        JLabel title =
                new JLabel(
                        "MinecraftAI Debug"
                );

        title.setForeground(
                TEXT
        );

        title.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        22
                )
        );

        JLabel subtitle =
                new JLabel(
                        "Playground de contexto, memoria, tools y prompt"
                );

        subtitle.setForeground(
                MUTED
        );

        JPanel titles =
                new JPanel();

        titles.setLayout(
                new BoxLayout(
                        titles,
                        BoxLayout.Y_AXIS
                )
        );

        titles.setBackground(
                BG
        );

        titles.add(
                title
        );

        titles.add(
                subtitle
        );

        statusLabel =
                new JLabel(
                        "Inicializando..."
                );

        statusLabel.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        13
                )
        );

        panel.add(
                titles,
                BorderLayout.WEST
        );

        panel.add(
                statusLabel,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel createContextPanel() {

        JPanel root =
                new JPanel();

        root.setLayout(
                new BoxLayout(
                        root,
                        BoxLayout.Y_AXIS
                )
        );

        root.setBackground(
                PANEL
        );

        root.setBorder(
                new EmptyBorder(
                        12,
                        12,
                        12,
                        12
                )
        );

        root.add(
                createGeneralContextPanel()
        );

        root.add(
                Box.createVerticalStrut(
                        10
                )
        );

        root.add(
                createTargetPanel()
        );

        root.add(
                Box.createVerticalGlue()
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                8,
                                0
                        )
                );

        buttons.setBackground(
                PANEL
        );

        JButton reset =
                button(
                        "Reset"
                );

        reset.addActionListener(
                e -> resetAll()
        );

        JButton clear =
                button(
                        "Limpiar chat"
                );

        clear.addActionListener(
                e -> clearConversation()
        );

        buttons.add(
                reset
        );

        buttons.add(
                clear
        );

        root.add(
                buttons
        );

        return root;
    }

    private JPanel createGeneralContextPanel() {

        JPanel panel =
                section(
                        "Contexto"
                );

        panel.setLayout(
                new GridLayout(
                        0,
                        2,
                        6,
                        6
                )
        );

        playerField =
                field();

        biomeBox =
                new JComboBox<>(
                        DebugCatalog.BIOMES
                                .stream()
                                .sorted()
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                biomeBox
        );

        dimensionBox =
                new JComboBox<>(
                        DebugCatalog.DIMENSIONS
                                .stream()
                                .sorted()
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                dimensionBox
        );

        gamemodeBox =
                new JComboBox<>(
                        DebugCatalog.GAMEMODES
                                .stream()
                                .sorted()
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                gamemodeBox
        );

        healthField =
                field();

        hungerField =
                field();

        xpField =
                field();

        structureIdBox =
                new JComboBox<>(
                        DebugCatalog.STRUCTURES
                                .stream()
                                .sorted()
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                structureIdBox
        );

        structureNameBox =
                new JComboBox<>(
                        DebugCatalog.STRUCTURE_NAMES
                                .values()
                                .stream()
                                .sorted()
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                structureNameBox
        );

        structureIdBox.addActionListener(
                e -> syncStructureFromId()
        );

        structureNameBox.addActionListener(
                e -> syncStructureFromName()
        );

        addField(
                panel,
                "Jugador",
                playerField
        );

        addField(
                panel,
                "Bioma",
                biomeBox
        );

        addField(
                panel,
                "Dimensión",
                dimensionBox
        );

        addField(
                panel,
                "Gamemode",
                gamemodeBox
        );

        addField(
                panel,
                "Vida",
                healthField
        );

        addField(
                panel,
                "Hambre",
                hungerField
        );

        addField(
                panel,
                "XP",
                xpField
        );

        addField(
                panel,
                "Estructura ID",
                structureIdBox
        );

        addField(
                panel,
                "Nombre estructura",
                structureNameBox
        );

        return panel;
    }

    private JPanel createTargetPanel() {

        JPanel panel =
                section(
                        "Target"
                );

        panel.setLayout(
                new GridLayout(
                        0,
                        2,
                        6,
                        6
                )
        );

        targetTypeBox =
                new JComboBox<>(
                        new String[]{
                                "none",
                                "block",
                                "entity"
                        }
                );

        styleCombo(
                targetTypeBox
        );

        targetIdField =
                new AutoCompleteTextField(
                        DebugCatalog.TARGETS
                );

        styleField(
                targetIdField
        );

        targetNameField =
                field();

        targetIdField.addActionListener(
                e -> {

                    String name =
                            DebugCatalog.DISPLAY_NAMES.get(
                                    targetIdField
                                            .getText()
                                            .trim()
                            );

                    if (
                            name != null
                    ) {

                        targetNameField.setText(
                                name
                        );
                    }
                }
        );

        panel.add(
                label(
                        "Tipo"
                )
        );

        panel.add(
                targetTypeBox
        );

        addField(
                panel,
                "ID",
                targetIdField
        );

        addField(
                panel,
                "Nombre",
                targetNameField
        );

        return panel;
    }

    private JPanel createWorkPanel() {

        JPanel root =
                new JPanel(
                        new BorderLayout(
                                8,
                                8
                        )
                );

        root.setBackground(
                PANEL
        );

        JTabbedPane tabs =
                new JTabbedPane();

        tabs.setBackground(
                PANEL
        );

        tabs.setForeground(
                TEXT
        );

        chatArea =
                textArea();

        chatArea.setEditable(
                false
        );

        promptArea =
                textArea();

        promptArea.setEditable(
                false
        );

        historyArea =
                textArea();

        historyArea.setEditable(
                false
        );

        tabs.addTab(
                "Chat",
                scroll(
                        chatArea
                )
        );

        tabs.addTab(
                "Prompt",
                scroll(
                        promptArea
                )
        );

        tabs.addTab(
                "Memoria",
                scroll(
                        historyArea
                )
        );

        root.add(
                tabs,
                BorderLayout.CENTER
        );

        root.add(
                createInputPanel(),
                BorderLayout.SOUTH
        );

        return root;
    }

    private JPanel createInputPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                8,
                                0
                        )
                );

        panel.setBackground(
                PANEL
        );

        inputField =
                field();

        inputField.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.PLAIN,
                        15
                )
        );

        inputField.addActionListener(
                e -> submitInput()
        );

        inputField.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyPressed(
                            KeyEvent e
                    ) {

                        if (
                                e.getKeyCode()
                                        == KeyEvent.VK_UP
                        ) {

                            previousHistory();
                            e.consume();

                        } else if (
                                e.getKeyCode()
                                        == KeyEvent.VK_DOWN
                        ) {

                            nextHistory();
                            e.consume();

                        } else if (
                                e.getKeyCode()
                                        == KeyEvent.VK_TAB
                        ) {

                            autocomplete();
                            e.consume();
                        }
                    }
                }
        );

        JButton send =
                button(
                        "Enviar"
                );

        send.setPreferredSize(
                new Dimension(
                        100,
                        38
                )
        );

        send.addActionListener(
                e -> submitInput()
        );

        micButton =
                button(
                        "Mic"
                );

        micButton.setPreferredSize(
                new Dimension(
                        80,
                        38
                )
        );

        micButton.addActionListener(
                e -> toggleMicrophone()
        );


        JLabel hint =
                new JLabel(
                        "Enter enviar   ↑↓ historial   Tab autocompletar"
                );

        hint.setForeground(
                MUTED
        );

        hint.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.PLAIN,
                        11
                )
        );

        JPanel bottom =
                new JPanel(
                        new BorderLayout()
                );

        bottom.setBackground(
                PANEL
        );

        bottom.add(
                hint,
                BorderLayout.WEST
        );

        panel.add(
                inputField,
                BorderLayout.CENTER
        );

        JPanel actions =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                6,
                                0
                        )
                );

        actions.setBackground(
                PANEL
        );


        actions.add(
                micButton
        );

        actions.add(
                send
        );

        panel.add(
                actions,
                BorderLayout.EAST
        );

        panel.add(
                bottom,
                BorderLayout.SOUTH
        );

        return panel;
    }

    private void toggleMicrophone() {

        if (
                voiceRecording
        ) {

            stopMicrophone();
            return;
        }

        micButton.setEnabled(
                false
        );

        setStatus(
                "Iniciando STT...",
                ACCENT
        );

        CompletableFuture.runAsync(
                () -> {

                    boolean ready =
                            stt.ensureRunning();

                    SwingUtilities.invokeLater(
                            () -> {

                                if (
                                        !ready
                                ) {

                                    micButton.setEnabled(
                                            true
                                    );

                                    setStatus(
                                            "STT no disponible",
                                            ERROR
                                    );

                                    appendError(
                                            "No se pudo iniciar el servicio STT."
                                    );

                                    return;
                                }

                                try {

                                    stt.startRecording();

                                    voiceRecording =
                                            true;

                                    micButton.setText(
                                            "Detener"
                                    );

                                    micButton.setEnabled(
                                            true
                                    );

                                    setStatus(
                                            "Escuchando...",
                                            ACCENT
                                    );

                                } catch (
                                        Exception ex
                                ) {

                                    micButton.setEnabled(
                                            true
                                    );

                                    appendError(
                                            ex.getMessage()
                                    );

                                    setStatus(
                                            "Error STT",
                                            ERROR
                                    );
                                }
                            }
                    );
                }
        );
    }

    private void stopMicrophone() {

        micButton.setEnabled(
                false
        );

        setStatus(
                "Transcribiendo...",
                ACCENT
        );

        CompletableFuture
                .supplyAsync(
                        () -> {

                            try {

                                return stt.stopRecording();

                            } catch (
                                    Exception e
                            ) {

                                throw new RuntimeException(
                                        e
                                );
                            }
                        }
                )
                .whenComplete(
                        (text, error) ->

                                SwingUtilities.invokeLater(
                                        () -> {

                                            voiceRecording =
                                                    false;

                                            micButton.setText(
                                                    "Mic"
                                            );

                                            micButton.setEnabled(
                                                    true
                                            );

                                            if (
                                                    error != null
                                            ) {

                                                appendError(
                                                        error.getCause() == null
                                                                ? error.getMessage()
                                                                : error.getCause().getMessage()
                                                );

                                                setStatus(
                                                        "Error STT",
                                                        ERROR
                                                );

                                                return;
                                            }

                                            if (
                                                    text == null
                                                    || text.isBlank()
                                            ) {

                                                setStatus(
                                                        "No se detectó voz",
                                                        MUTED
                                                );

                                                return;
                                            }

                                            setStatus(
                                                    "Transcripción lista",
                                                    SUCCESS
                                            );

                                            /*
                                             * La transcripción se manda
                                             * directamente al chat.
                                             *
                                             * askGemini() ya agrega el mensaje
                                             * del usuario visualmente antes
                                             * de consultar a Gemini.
                                             */
                                            askGemini(
                                                    text
                                            );
                                        }
                                )
                );
    }
    private void submitInput() {

        String text =
                inputField.getText()
                        .trim();

        if (
                text.isBlank()
        ) {
            return;
        }

        inputHistory.add(
                text
        );

        historyIndex =
                inputHistory.size();

        inputField.setText(
                ""
        );

        if (
                text.startsWith(
                        "/"
                )
        ) {

            handleCommand(
                    text
            );

            return;
        }

        askGemini(
                text
        );
    }

    private void askGemini(
            String question
    ) {

        if (
                !gemini.isAvailable()
        ) {

            appendError(
                    "GEMINI_API_KEY no está disponible."
            );

            return;
        }

        PlayerContext context =
                buildContext();

        String history =
                memory.getHistoryText(
                        playerId
                );

        AIRequest request =
                new AIRequest(
                        question,
                        new byte[0],
                        context,
                        history
                );

        addAutomaticToolResults(
                request,
                context
        );

        lastPrompt =
                MinecraftAIPrompt.build(
                        request
                );

        promptArea.setText(
                lastPrompt
        );

        appendUser(
                question
        );

        setStatus(
                "Pensando...",
                ACCENT
        );

        CompletableFuture
                .supplyAsync(
                        () -> gemini.respond(
                                request
                        )
                )
                .thenAccept(
                        response ->
                                SwingUtilities.invokeLater(
                                        () -> handleResponse(
                                                question,
                                                response
                                        )
                                )
                );
    }

    private void addAutomaticToolResults(
            AIRequest request,
            PlayerContext context
    ) {

        String structureId =
                String.valueOf(
                        structureIdBox
                                .getSelectedItem()
                );

        String structureName =
                String.valueOf(
                        structureNameBox
                                .getSelectedItem()
                );

        if (
                structureId != null
                && !structureId.isBlank()
                && !"none".equals(
                        structureId
                )
        ) {

            request.addToolResult(
                    "get_current_structure",
                    "Nombre: "
                            + structureName
                            + "\nID: "
                            + structureId
                            + "\nOrigen: "
                            + DebugCatalog.getModOrigin(
                                    structureId
                            )
            );
        }

        TargetContext target =
                context.target;

        if (
                target == null
                || "none".equals(
                        target.type
                )
                || target.id == null
                || target.id.isBlank()
        ) {

            return;
        }

        String origin =
                DebugCatalog.getModOrigin(
                        target.id
                );

        request.addToolResult(
                "get_mod_origin",
                "ID: "
                        + target.id
                        + "\nOrigen: "
                        + origin
        );

        if (
                "block".equals(
                        target.type
                )
        ) {

            request.addToolResult(
                    "get_target_block",
                    "Nombre: "
                            + target.name
                            + "\nID: "
                            + target.id
            );

            request.addToolResult(
                    "get_block_info",
                    "Nombre: "
                            + target.name
                            + "\nID: "
                            + target.id
                            + "\nOrigen: "
                            + origin
            );

        } else if (
                "entity".equals(
                        target.type
                )
        ) {

            request.addToolResult(
                    "get_target_entity",
                    "Nombre: "
                            + target.name
                            + "\nID: "
                            + target.id
            );
        }
    }

    private void syncStructureFromId() {

        if (
                syncingStructure
        ) {
            return;
        }

        syncingStructure =
                true;

        try {

            String id =
                    String.valueOf(
                            structureIdBox
                                    .getSelectedItem()
                    );

            String name =
                    DebugCatalog.STRUCTURE_NAMES.get(
                            id
                    );

            if (
                    name != null
            ) {

                structureNameBox.setSelectedItem(
                        name
                );
            }

        } finally {

            syncingStructure =
                    false;
        }
    }

    private void syncStructureFromName() {

        if (
                syncingStructure
        ) {
            return;
        }

        syncingStructure =
                true;

        try {

            String selectedName =
                    String.valueOf(
                            structureNameBox
                                    .getSelectedItem()
                    );

            for (
                    String id :
                    DebugCatalog.STRUCTURES
            ) {

                String name =
                        DebugCatalog.STRUCTURE_NAMES.get(
                                id
                        );

                if (
                        selectedName.equals(
                                name
                        )
                ) {

                    structureIdBox.setSelectedItem(
                            id
                    );

                    return;
                }
            }

        } finally {

            syncingStructure =
                    false;
        }
    }

    private void handleResponse(
            String question,
            AIResponse response
    ) {

        if (
                response == null
                || !response.success
        ) {

            appendError(
                    response == null
                            ? "Gemini devolvió null."
                            : response.content
            );

            setStatus(
                    "Error",
                    ERROR
            );

            return;
        }

        appendAssistant(
                response.content
        );

        memory.addUser(
                playerId,
                question
        );

        memory.addAssistant(
                playerId,
                response.content
        );

        refreshMemory();

        setStatus(
                "Listo · "
                        + response.durationMs
                        + " ms",
                SUCCESS
        );
    }

    private PlayerContext buildContext() {

        PlayerContext context =
                new PlayerContext();

        context.playerName =
                valueOr(
                        playerField,
                        "Jano"
                );

        context.biome =
                String.valueOf(
                        biomeBox
                                .getSelectedItem()
                );

        context.dimension =
                String.valueOf(
                        dimensionBox
                                .getSelectedItem()
                );

        context.gameMode =
                String.valueOf(
                        gamemodeBox
                                .getSelectedItem()
                );

        context.health =
                parseFloat(
                        healthField,
                        20
                );

        context.maxHealth =
                20;

        context.hunger =
                parseInt(
                        hungerField,
                        20
                );

        context.saturation =
                5;

        context.xpLevel =
                parseInt(
                        xpField,
                        0
                );

        context.air =
                300;

        context.direction =
                "north";

        context.onGround =
                true;

        context.weather =
                "clear";

        context.difficulty =
                "normal";

        context.lightLevel =
                15;

        context.worldTime =
                6000;

        context.target =
                buildTarget();

        return context;
    }

    private TargetContext buildTarget() {

        String type =
                String.valueOf(
                        targetTypeBox
                                .getSelectedItem()
                );

        if (
                "none".equals(
                        type
                )
        ) {

            return TargetContext.none();
        }

        String id =
                targetIdField
                        .getText()
                        .trim();

        String name =
                targetNameField
                        .getText()
                        .trim();

        if (
                name.isBlank()
        ) {

            name =
                    id;
        }

        return new TargetContext(
                type,
                id,
                name,
                0,
                64,
                0,
                3
        );
    }

    private void handleCommand(
            String command
    ) {

        appendSystem(
                command
        );

        if (
                command.equalsIgnoreCase(
                        "/clear"
                )
        ) {

            clearConversation();
            return;
        }

        if (
                command.equalsIgnoreCase(
                        "/reset"
                )
        ) {

            resetAll();
            return;
        }

        if (
                command.equalsIgnoreCase(
                        "/prompt"
                )
        ) {

            promptArea.setText(
                    lastPrompt.isBlank()
                            ? "Todavía no hay prompt."
                            : lastPrompt
            );

            return;
        }

        if (
                command.equalsIgnoreCase(
                        "/history"
                )
        ) {

            refreshMemory();
            return;
        }

        if (
                command.startsWith(
                        "/biome "
                )
        ) {

            biomeBox.setSelectedItem(
                    command.substring(
                            7
                    ).trim()
            );

            return;
        }

        if (
                command.startsWith(
                        "/target "
                )
        ) {

            targetTypeBox.setSelectedItem(
                    "block"
            );

            targetIdField.setText(
                    command.substring(
                            8
                    ).trim()
            );

            targetNameField.setText(
                    command.substring(
                            8
                    ).trim()
            );

            return;
        }

        if (
                command.startsWith(
                        "/structure "
                )
        ) {

            structureIdBox.setSelectedItem(
                    command.substring(
                            11
                    ).trim()
            );

            syncStructureFromId();

            return;
        }

        appendError(
                "Comando desconocido."
        );
    }

    private void autocomplete() {

        String current =
                inputField.getText();

        String[] commands = {
                "/clear",
                "/reset",
                "/prompt",
                "/history",
                "/biome ",
                "/target ",
                "/structure "
        };

        for (
                String command :
                commands
        ) {

            if (
                    command.startsWith(
                            current
                    )
                    && !command.equals(
                            current
                    )
            ) {

                inputField.setText(
                        command
                );

                inputField.setCaretPosition(
                        command.length()
                );

                return;
            }
        }
    }

    private void previousHistory() {

        if (
                inputHistory.isEmpty()
        ) {
            return;
        }

        historyIndex =
                Math.max(
                        0,
                        historyIndex - 1
                );

        inputField.setText(
                inputHistory.get(
                        historyIndex
                )
        );
    }

    private void nextHistory() {

        if (
                inputHistory.isEmpty()
        ) {
            return;
        }

        historyIndex =
                Math.min(
                        inputHistory.size(),
                        historyIndex + 1
                );

        if (
                historyIndex
                        >= inputHistory.size()
        ) {

            inputField.setText(
                    ""
            );

            return;
        }

        inputField.setText(
                inputHistory.get(
                        historyIndex
                )
        );
    }

    private void resetContext() {

        playerField.setText(
                "Jano"
        );

        biomeBox.setSelectedItem(
                "minecraft:plains"
        );

        dimensionBox.setSelectedItem(
                "minecraft:overworld"
        );

        gamemodeBox.setSelectedItem(
                "survival"
        );

        healthField.setText(
                "20"
        );

        hungerField.setText(
                "20"
        );

        xpField.setText(
                "0"
        );

        structureIdBox.setSelectedItem(
                "none"
        );

        structureNameBox.setSelectedItem(
                "Ninguna"
        );

        targetTypeBox.setSelectedItem(
                "none"
        );

        targetIdField.setText(
                ""
        );

        targetNameField.setText(
                ""
        );
    }

    private void resetAll() {

        resetContext();

        memory.clear(
                playerId
        );

        chatArea.setText(
                ""
        );

        promptArea.setText(
                ""
        );

        historyArea.setText(
                ""
        );

        lastPrompt =
                "";

        appendSystem(
                "Debugger reiniciado."
        );
    }

    private void clearConversation() {

        memory.clear(
                playerId
        );

        chatArea.setText(
                ""
        );

        historyArea.setText(
                ""
        );

        appendSystem(
                "Conversación borrada."
        );
    }

    private void refreshMemory() {

        historyArea.setText(
                memory.getHistoryText(
                        playerId
                )
        );
    }

    private void appendUser(
            String text
    ) {

        appendChat(
                "\nVOS\n",
                text
        );
    }

    private void appendAssistant(
            String text
    ) {

        appendChat(
                "\nMINECRAFTAI\n",
                text
        );
    }

    private void appendSystem(
            String text
    ) {

        appendChat(
                "\nDEBUG\n",
                text
        );
    }

    private void appendError(
            String text
    ) {

        appendChat(
                "\nERROR\n",
                text
        );
    }

    private void appendChat(
            String title,
            String text
    ) {

        chatArea.append(
                title
        );

        chatArea.append(
                text
        );

        chatArea.append(
                "\n"
        );

        chatArea.setCaretPosition(
                chatArea
                        .getDocument()
                        .getLength()
        );
    }

    private void setStatus(
            String text,
            Color color
    ) {

        statusLabel.setText(
                text
        );

        statusLabel.setForeground(
                color
        );
    }

    private JPanel section(
            String title
    ) {

        JPanel panel =
                new JPanel();

        panel.setBackground(
                PANEL_2
        );

        TitledBorder border =
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                FIELD
                        ),
                        title
                );

        border.setTitleColor(
                TEXT
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        border,
                        new EmptyBorder(
                                8,
                                8,
                                8,
                                8
                        )
                )
        );

        return panel;
    }

    private JTextField field() {

        JTextField field =
                new JTextField();

        styleField(
                field
        );

        return field;
    }

    private void styleField(
            JTextField field
    ) {

        field.setBackground(
                FIELD
        );

        field.setForeground(
                TEXT
        );

        field.setCaretColor(
                TEXT
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        65,
                                        68,
                                        75
                                )
                        ),
                        new EmptyBorder(
                                6,
                                8,
                                6,
                                8
                        )
                )
        );
    }

    private JTextArea textArea() {

        JTextArea area =
                new JTextArea();

        area.setBackground(
                new Color(
                        25,
                        27,
                        30
                )
        );

        area.setForeground(
                TEXT
        );

        area.setCaretColor(
                TEXT
        );

        area.setLineWrap(
                true
        );

        area.setWrapStyleWord(
                true
        );

        area.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );

        area.setBorder(
                new EmptyBorder(
                        12,
                        12,
                        12,
                        12
                )
        );

        return area;
    }

    private JScrollPane scroll(
            Component component
    ) {

        JScrollPane scroll =
                new JScrollPane(
                        component
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        FIELD
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

        button.setBackground(
                FIELD
        );

        button.setForeground(
                TEXT
        );

        button.setFocusPainted(
                false
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        70,
                                        73,
                                        80
                                )
                        ),
                        new EmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );

        return button;
    }

    private JLabel label(
            String text
    ) {

        JLabel label =
                new JLabel(
                        text
                );

        label.setForeground(
                MUTED
        );

        return label;
    }

    private void addField(
            JPanel panel,
            String label,
            JComponent component
    ) {

        panel.add(
                label(
                        label
                )
        );

        panel.add(
                component
        );
    }

    private void styleCombo(
            JComboBox<?> box
    ) {

        box.setBackground(
                FIELD
        );

        box.setForeground(
                TEXT
        );

        box.setFocusable(
                false
        );
    }

    private String valueOr(
            JTextField field,
            String fallback
    ) {

        String value =
                field.getText()
                        .trim();

        return value.isBlank()
                ? fallback
                : value;
    }

    private float parseFloat(
            JTextField field,
            float fallback
    ) {

        try {

            return Float.parseFloat(
                    field.getText()
            );

        } catch (
                Exception e
        ) {

            return fallback;
        }
    }

    private int parseInt(
            JTextField field,
            int fallback
    ) {

        try {

            return Integer.parseInt(
                    field.getText()
            );

        } catch (
                Exception e
        ) {

            return fallback;
        }
    }
}