package com.jano.minecraftai.debug;

import com.jano.minecraftai.ai.AIRequest;
import com.jano.minecraftai.ai.AIResponse;
import com.jano.minecraftai.ai.ConversationMemory;
import com.jano.minecraftai.ai.MinecraftAIPrompt;
import com.jano.minecraftai.ai.providers.GeminiProvider;

import com.jano.minecraftai.context.ItemContext;
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

    private JComboBox<String> targetIdBox;
    private JComboBox<String> targetNameBox;
    private JComboBox<String> targetTypeBox;

    private boolean syncingTarget = false;

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

    private final List<DebugPreset> presets =
            DebugPresets.all();

    private JComboBox<String> presetBox;

    private JTextArea presetSummaryArea;

    private JPanel advancedPanel;

    private JButton advancedToggleButton;

    // Mundo
    private JComboBox<String> weatherBox;
    private JComboBox<String> difficultyBox;
    private JComboBox<String> directionBox;

    private JSpinner timeSpinner;
    private JSpinner lightSpinner;

    // Estado
    private JSpinner saturationSpinner;
    private JSpinner airSpinner;

    // Equipo
    private JComboBox<String> mainHandBox;
    private JComboBox<String> offHandBox;

    private JComboBox<String> helmetBox;
    private JComboBox<String> chestplateBox;
    private JComboBox<String> leggingsBox;
    private JComboBox<String> bootsBox;

    // Inventario
    private JComboBox<String> inventoryItemBox;
    private JSpinner inventoryCountSpinner;

    private final DefaultListModel<String> inventoryListModel =
            new DefaultListModel<>();

    private final List<DebugInventoryItem> inventoryItems =
            new ArrayList<>();

    // Efectos
    private JComboBox<String> effectBox;
    private JSpinner effectLevelSpinner;
    private JSpinner effectSecondsSpinner;

    private final DefaultListModel<String> effectListModel =
            new DefaultListModel<>();

    private final List<DebugEffect> debugEffects =
            new ArrayList<>();

    // Entidades visibles
    private JComboBox<String> visibleEntityBox;
    private JSpinner visibleDistanceSpinner;
    private JSpinner visibleHealthSpinner;
    private JCheckBox visibleHostileCheck;

    private final DefaultListModel<String> visibleEntityListModel =
            new DefaultListModel<>();

    private final List<DebugVisibleEntity> visibleEntities =
            new ArrayList<>();

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
                        createContextScrollPane(),
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

    private JScrollPane createContextScrollPane() {

        JScrollPane scrollPane =
                new JScrollPane(
                        createContextPanel()
                );

        scrollPane.setBorder(
                null
        );

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(
                        16
                );

        return scrollPane;
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
                createPresetPanel()
        );

        root.add(
                Box.createVerticalStrut(
                        10
                )
        );

        advancedToggleButton =
                button(
                        "Editar manualmente"
                );

        advancedToggleButton.addActionListener(
                e -> toggleAdvancedMode()
        );

        root.add(
                advancedToggleButton
        );

        root.add(
                Box.createVerticalStrut(
                        10
                )
        );

        advancedPanel =
                new JPanel();

        advancedPanel.setLayout(
                new BoxLayout(
                        advancedPanel,
                        BoxLayout.Y_AXIS
                )
        );

        advancedPanel.setBackground(
                PANEL
        );

        advancedPanel.add(
                createGeneralContextPanel()
        );

        advancedPanel.add(
                Box.createVerticalStrut(
                        8
                )
        );

        advancedPanel.add(
                createTargetPanel()
        );

        advancedPanel.add(
                Box.createVerticalStrut(
                        8
                )
        );

        advancedPanel.add(
                createEquipmentPanel()
        );

        advancedPanel.add(
                Box.createVerticalStrut(
                        8
                )
        );

        advancedPanel.add(
                createInventoryPanel()
        );

        advancedPanel.add(
                Box.createVerticalStrut(
                        8
                )
        );

        advancedPanel.add(
                createEffectsPanel()
        );

        advancedPanel.add(
                Box.createVerticalStrut(
                        8
                )
        );

        advancedPanel.add(
                createVisibleEntitiesPanel()
        );

        advancedPanel.setVisible(
                false
        );

        root.add(
                advancedPanel
        );

        root.add(
                Box.createVerticalStrut(
                        12
                )
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

        if (
                !presets.isEmpty()
        ) {

            applyPreset(
                    presets.get(
                            0
                    )
            );
        }

        return root;
    }

    private JPanel createPresetPanel() {

        JPanel panel =
                section(
                        "Escenario"
                );

        panel.setLayout(
                new BorderLayout(
                        8,
                        8
                )
        );

        presetBox =
                new JComboBox<>(
                        presets.stream()
                                .map(
                                        preset -> preset.name
                                )
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                presetBox
        );

        presetBox.addActionListener(
                e -> {

                    DebugPreset preset =
                            findPreset(
                                    String.valueOf(
                                            presetBox
                                                    .getSelectedItem()
                                    )
                            );

                    if (
                            preset != null
                    ) {

                        applyPreset(
                                preset
                        );
                    }
                }
        );

        presetSummaryArea =
                textArea();

        presetSummaryArea.setEditable(
                false
        );

        presetSummaryArea.setRows(
                14
        );

        presetSummaryArea.setLineWrap(
                true
        );

        presetSummaryArea.setWrapStyleWord(
                true
        );

        panel.add(
                presetBox,
                BorderLayout.NORTH
        );

        panel.add(
                scroll(
                        presetSummaryArea
                ),
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel createGeneralContextPanel() {

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

        root.add(
                createPlayerInfoPanel()
        );

        root.add(
                Box.createVerticalStrut(
                        8
                )
        );

        root.add(
                createEnvironmentPanel()
        );

        root.add(
                Box.createVerticalStrut(
                        8
                )
        );

        root.add(
                createWorldPanel()
        );

        root.add(
                Box.createVerticalStrut(
                        8
                )
        );

        root.add(
                createPlayerStatePanel()
        );

        return root;
    }

    private JPanel createPlayerInfoPanel() {

        JPanel panel =
                section(
                        "Información del jugador"
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

        addField(
                panel,
                "Jugador",
                playerField
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

        return panel;
    }

    private JPanel createEnvironmentPanel() {

        JPanel panel =
                section(
                        "Entorno"
                );

        panel.setLayout(
                new GridLayout(
                        0,
                        2,
                        6,
                        6
                )
        );

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

    private JPanel createWorldPanel() {

        JPanel panel =
                section(
                        "Mundo"
                );

        panel.setLayout(
                new GridLayout(
                        0,
                        2,
                        6,
                        6
                )
        );

        weatherBox =
                combo(
                        DebugCatalog.WEATHER
                );

        difficultyBox =
                combo(
                        DebugCatalog.DIFFICULTIES
                );

        directionBox =
                combo(
                        DebugCatalog.DIRECTIONS
                );

        timeSpinner =
                spinner(
                        6000,
                        0,
                        23999,
                        100
                );

        lightSpinner =
                spinner(
                        15,
                        0,
                        15,
                        1
                );

        addField(
                panel,
                "Clima",
                weatherBox
        );

        addField(
                panel,
                "Dificultad",
                difficultyBox
        );

        addField(
                panel,
                "Hora Minecraft",
                timeSpinner
        );

        addField(
                panel,
                "Luz",
                lightSpinner
        );

        addField(
                panel,
                "Dirección",
                directionBox
        );

        return panel;
    }

    private JPanel createPlayerStatePanel() {

        JPanel panel =
                section(
                        "Estado"
                );

        panel.setLayout(
                new GridLayout(
                        0,
                        2,
                        6,
                        6
                )
        );

        saturationSpinner =
                spinner(
                        5,
                        0,
                        20,
                        1
                );

        airSpinner =
                spinner(
                        300,
                        0,
                        300,
                        20
                );

        addField(
                panel,
                "Saturación",
                saturationSpinner
        );

        addField(
                panel,
                "Aire",
                airSpinner
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

        targetIdBox =
                new JComboBox<>(
                        DebugCatalog.TARGETS
                                .stream()
                                .sorted()
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                targetIdBox
        );

        targetNameBox =
                new JComboBox<>(
                        DebugCatalog.DISPLAY_NAMES
                                .values()
                                .stream()
                                .sorted()
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                targetNameBox
        );

        targetIdBox.addActionListener(
                e -> syncTargetFromId()
        );

        targetNameBox.addActionListener(
                e -> syncTargetFromName()
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
                targetIdBox
        );

        addField(
                panel,
                "Nombre",
                targetNameBox
        );

        return panel;
    }

    private JPanel createEquipmentPanel() {

        JPanel panel =
                section(
                        "Equipo"
                );

        panel.setLayout(
                new GridLayout(
                        0,
                        2,
                        6,
                        6
                )
        );

        mainHandBox =
                combo(
                        DebugCatalog.ITEMS
                );

        offHandBox =
                combo(
                        DebugCatalog.ITEMS
                );

        helmetBox =
                combo(
                        DebugCatalog.ARMOR
                );

        chestplateBox =
                combo(
                        DebugCatalog.ARMOR
                );

        leggingsBox =
                combo(
                        DebugCatalog.ARMOR
                );

        bootsBox =
                combo(
                        DebugCatalog.ARMOR
                );

        addField(
                panel,
                "Mano principal",
                mainHandBox
        );

        addField(
                panel,
                "Mano secundaria",
                offHandBox
        );

        addField(
                panel,
                "Casco",
                helmetBox
        );

        addField(
                panel,
                "Pechera",
                chestplateBox
        );

        addField(
                panel,
                "Pantalones",
                leggingsBox
        );

        addField(
                panel,
                "Botas",
                bootsBox
        );

        return panel;
    }

    private JPanel createInventoryPanel() {

        JPanel panel =
                section(
                        "Inventario"
                );

        panel.setLayout(
                new BorderLayout(
                        6,
                        6
                )
        );

        JPanel controls =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                6,
                                6
                        )
                );

        controls.setBackground(
                PANEL_2
        );

        inventoryItemBox =
                combo(
                        DebugCatalog.ITEMS
                );

        inventoryCountSpinner =
                spinner(
                        1,
                        1,
                        64,
                        1
                );

        addField(
                controls,
                "Item",
                inventoryItemBox
        );

        addField(
                controls,
                "Cantidad",
                inventoryCountSpinner
        );

        JButton add =
                button(
                        "Agregar"
                );

        add.addActionListener(
                e -> addInventoryItem()
        );

        JButton remove =
                button(
                        "Quitar"
                );

        JList<String> list =
                new JList<>(
                        inventoryListModel
                );

        styleList(
                list
        );

        remove.addActionListener(
                e -> {

                    int index =
                            list.getSelectedIndex();

                    if (
                            index >= 0
                    ) {

                        inventoryItems.remove(
                                index
                        );

                        refreshInventory();
                    }
                }
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                6,
                                0
                        )
                );

        buttons.setBackground(
                PANEL_2
        );

        buttons.add(
                add
        );

        buttons.add(
                remove
        );

        JPanel top =
                new JPanel(
                        new BorderLayout(
                                6,
                                6
                        )
                );

        top.setBackground(
                PANEL_2
        );

        top.add(
                controls,
                BorderLayout.CENTER
        );

        top.add(
                buttons,
                BorderLayout.SOUTH
        );

        panel.add(
                top,
                BorderLayout.NORTH
        );

        panel.add(
                scroll(
                        list
                ),
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel createEffectsPanel() {

        JPanel panel =
                section(
                        "Efectos"
                );

        panel.setLayout(
                new BorderLayout(
                        6,
                        6
                )
        );

        JPanel controls =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                6,
                                6
                        )
                );

        controls.setBackground(
                PANEL_2
        );

        effectBox =
                combo(
                        DebugCatalog.EFFECTS
                );

        effectLevelSpinner =
                spinner(
                        1,
                        1,
                        10,
                        1
                );

        effectSecondsSpinner =
                spinner(
                        60,
                        1,
                        3600,
                        10
                );

        addField(
                controls,
                "Efecto",
                effectBox
        );

        addField(
                controls,
                "Nivel",
                effectLevelSpinner
        );

        addField(
                controls,
                "Segundos",
                effectSecondsSpinner
        );

        JButton add =
                button(
                        "Agregar"
                );

        JList<String> list =
                new JList<>(
                        effectListModel
                );

        styleList(
                list
        );

        add.addActionListener(
                e -> addEffect()
        );

        JButton remove =
                button(
                        "Quitar"
                );

        remove.addActionListener(
                e -> {

                    int index =
                            list.getSelectedIndex();

                    if (
                            index >= 0
                    ) {

                        debugEffects.remove(
                                index
                        );

                        refreshEffects();
                    }
                }
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                6,
                                0
                        )
                );

        buttons.setBackground(
                PANEL_2
        );

        buttons.add(
                add
        );

        buttons.add(
                remove
        );

        JPanel top =
                new JPanel(
                        new BorderLayout(
                                6,
                                6
                        )
                );

        top.setBackground(
                PANEL_2
        );

        top.add(
                controls,
                BorderLayout.CENTER
        );

        top.add(
                buttons,
                BorderLayout.SOUTH
        );

        panel.add(
                top,
                BorderLayout.NORTH
        );

        panel.add(
                scroll(
                        list
                ),
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel createVisibleEntitiesPanel() {

        JPanel panel =
                section(
                        "Entidades visibles"
                );

        panel.setLayout(
                new BorderLayout(
                        6,
                        6
                )
        );

        JPanel controls =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                6,
                                6
                        )
                );

        controls.setBackground(
                PANEL_2
        );

        visibleEntityBox =
                combo(
                        DebugCatalog.ENTITIES
                );

        visibleDistanceSpinner =
                spinner(
                        5,
                        1,
                        24,
                        1
                );

        visibleHealthSpinner =
                spinner(
                        20,
                        0,
                        500,
                        1
                );

        visibleHostileCheck =
                new JCheckBox(
                        "Hostil"
                );

        visibleHostileCheck.setBackground(
                PANEL_2
        );

        visibleHostileCheck.setForeground(
                TEXT
        );

        addField(
                controls,
                "Entidad",
                visibleEntityBox
        );

        addField(
                controls,
                "Distancia",
                visibleDistanceSpinner
        );

        addField(
                controls,
                "Vida",
                visibleHealthSpinner
        );

        controls.add(
                label(
                        "Comportamiento"
                )
        );

        controls.add(
                visibleHostileCheck
        );

        JButton add =
                button(
                        "Agregar"
                );

        JList<String> list =
                new JList<>(
                        visibleEntityListModel
                );

        styleList(
                list
        );

        add.addActionListener(
                e -> addVisibleEntity()
        );

        JButton remove =
                button(
                        "Quitar"
                );

        remove.addActionListener(
                e -> {

                    int index =
                            list.getSelectedIndex();

                    if (
                            index >= 0
                    ) {

                        visibleEntities.remove(
                                index
                        );

                        refreshVisibleEntities();
                    }
                }
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                6,
                                0
                        )
                );

        buttons.setBackground(
                PANEL_2
        );

        buttons.add(
                add
        );

        buttons.add(
                remove
        );

        JPanel top =
                new JPanel(
                        new BorderLayout(
                                6,
                                6
                        )
                );

        top.setBackground(
                PANEL_2
        );

        top.add(
                controls,
                BorderLayout.CENTER
        );

        top.add(
                buttons,
                BorderLayout.SOUTH
        );

        panel.add(
                top,
                BorderLayout.NORTH
        );

        panel.add(
                scroll(
                        list
                ),
                BorderLayout.CENTER
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
                selected(
                        structureIdBox
                );

        String structureName =
                selected(
                        structureNameBox
                );

        if (
                !"none".equals(
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
                target != null
                && !"none".equals(
                        target.type
                )
                && target.id != null
        ) {

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

        request.addToolResult(
                "get_held_item",
                buildHeldItemToolResult(
                        context
                )
        );

        request.addToolResult(
                "get_inventory",
                buildInventoryToolResult()
        );

        request.addToolResult(
                "get_visible_entities",
                buildVisibleEntitiesToolResult()
        );
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
                selected(
                        biomeBox
                );

        context.dimension =
                selected(
                        dimensionBox
                );

        context.gameMode =
                selected(
                        gamemodeBox
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
                ((Number) saturationSpinner
                        .getValue())
                        .floatValue();

        context.xpLevel =
                parseInt(
                        xpField,
                        0
                );

        context.air =
                ((Number) airSpinner
                        .getValue())
                        .intValue();

        context.direction =
                selected(
                        directionBox
                );

        context.onGround =
                true;

        context.weather =
                selected(
                        weatherBox
                );

        context.difficulty =
                selected(
                        difficultyBox
                );

        context.lightLevel =
                ((Number) lightSpinner
                        .getValue())
                        .intValue();

        context.worldTime =
                ((Number) timeSpinner
                        .getValue())
                        .longValue();

        context.mainHand =
                createItemContext(
                        selected(
                                mainHandBox
                        ),
                        1
                );

        context.offHand =
                createItemContext(
                        selected(
                                offHandBox
                        ),
                        1
                );

        addArmor(
                context,
                helmetBox
        );

        addArmor(
                context,
                chestplateBox
        );

        addArmor(
                context,
                leggingsBox
        );

        addArmor(
                context,
                bootsBox
        );

        for (
                DebugEffect effect :
                debugEffects
        ) {

            context.effects.add(
                    effect.id()
                            + " nivel "
                            + effect.level()
                            + " ("
                            + effect.seconds()
                            + "s)"
            );
        }

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
                String.valueOf(
                        targetIdBox
                                .getSelectedItem()
                );

        String name =
                String.valueOf(
                        targetNameBox
                                .getSelectedItem()
                );

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

    private void syncTargetFromId() {

        if (
                syncingTarget
        ) {
            return;
        }

        syncingTarget =
                true;

        try {

            String id =
                    String.valueOf(
                            targetIdBox
                                    .getSelectedItem()
                    );

            String name =
                    DebugCatalog.DISPLAY_NAMES.get(
                            id
                    );

            String type =
                    DebugCatalog.TARGET_TYPES.get(
                            id
                    );

            if (
                    name != null
            ) {
                targetNameBox.setSelectedItem(
                        name
                );
            }

            if (
                    type != null
            ) {
                targetTypeBox.setSelectedItem(
                        type
                );
            }

        } finally {

            syncingTarget =
                    false;
        }
    }

    private void syncTargetFromName() {

        if (
                syncingTarget
        ) {
            return;
        }

        syncingTarget =
                true;

        try {

            String selectedName =
                    String.valueOf(
                            targetNameBox
                                    .getSelectedItem()
                    );

            for (
                    String id :
                    DebugCatalog.TARGETS
            ) {

                if (
                        selectedName.equals(
                                DebugCatalog.DISPLAY_NAMES.get(
                                        id
                                )
                        )
                ) {

                    targetIdBox.setSelectedItem(
                            id
                    );

                    targetTypeBox.setSelectedItem(
                            DebugCatalog.TARGET_TYPES.get(
                                    id
                            )
                    );

                    return;
                }
            }

        } finally {

            syncingTarget =
                    false;
        }
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

            targetIdBox.setSelectedItem(
                    command.substring(
                            8
                    ).trim()
            );

            syncTargetFromId();

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

    private DebugPreset findPreset(
            String name
    ) {

        for (
                DebugPreset preset :
                presets
        ) {

            if (
                    preset.name.equals(
                            name
                    )
            ) {

                return preset;
            }
        }

        return null;
    }

    private void applyPreset(
            DebugPreset preset
    ) {

        if (
                preset == null
        ) {
            return;
        }

        playerField.setText(
                preset.playerName
        );

        gamemodeBox.setSelectedItem(
                preset.gameMode
        );

        healthField.setText(
                String.valueOf(
                        preset.health
                )
        );

        hungerField.setText(
                String.valueOf(
                        preset.hunger
                )
        );

        xpField.setText(
                String.valueOf(
                        preset.xp
                )
        );

        biomeBox.setSelectedItem(
                preset.biome
        );

        dimensionBox.setSelectedItem(
                preset.dimension
        );

        structureIdBox.setSelectedItem(
                preset.structureId
        );

        syncStructureFromId();

        targetIdBox.setSelectedItem(
                preset.targetId
        );

        syncTargetFromId();

        weatherBox.setSelectedItem(
                preset.weather
        );

        difficultyBox.setSelectedItem(
                preset.difficulty
        );

        directionBox.setSelectedItem(
                preset.direction
        );

        timeSpinner.setValue(
                preset.worldTime
        );

        lightSpinner.setValue(
                preset.light
        );

        saturationSpinner.setValue(
                preset.saturation
        );

        airSpinner.setValue(
                preset.air
        );

        mainHandBox.setSelectedItem(
                preset.mainHand
        );

        offHandBox.setSelectedItem(
                preset.offHand
        );

        helmetBox.setSelectedItem(
                preset.helmet
        );

        chestplateBox.setSelectedItem(
                preset.chestplate
        );

        leggingsBox.setSelectedItem(
                preset.leggings
        );

        bootsBox.setSelectedItem(
                preset.boots
        );

        inventoryItems.clear();

        for (
                DebugPreset.InventoryEntry entry :
                preset.inventory
        ) {

            inventoryItems.add(
                    new DebugInventoryItem(
                            entry.id(),
                            DebugCatalog.ITEM_NAMES
                                    .getOrDefault(
                                            entry.id(),
                                            entry.id()
                                    ),
                            entry.count()
                    )
            );
        }

        debugEffects.clear();

        for (
                DebugPreset.EffectEntry entry :
                preset.effects
        ) {

            debugEffects.add(
                    new DebugEffect(
                            entry.id(),
                            entry.level(),
                            entry.seconds()
                    )
            );
        }

        visibleEntities.clear();

        for (
                DebugPreset.VisibleEntityEntry entry :
                preset.visibleEntities
        ) {

            visibleEntities.add(
                    new DebugVisibleEntity(
                            entry.id(),
                            DebugCatalog.ENTITY_NAMES
                                    .getOrDefault(
                                            entry.id(),
                                            entry.id()
                                    ),
                            entry.distance(),
                            entry.hostile(),
                            entry.health()
                    )
            );
        }

        refreshInventory();
        refreshEffects();
        refreshVisibleEntities();

        refreshPresetSummary(
                preset
        );
    }

    private void refreshPresetSummary(
            DebugPreset preset
    ) {

        String structureName =
                DebugCatalog.STRUCTURE_NAMES
                        .getOrDefault(
                                preset.structureId,
                                preset.structureId
                        );

        String targetName =
                DebugCatalog.DISPLAY_NAMES
                        .getOrDefault(
                                preset.targetId,
                                preset.targetId
                        );

        String mainHandName =
                DebugCatalog.ITEM_NAMES
                        .getOrDefault(
                                preset.mainHand,
                                "Vacío"
                        );

        String offHandName =
                DebugCatalog.ITEM_NAMES
                        .getOrDefault(
                                preset.offHand,
                                "Vacío"
                        );

        StringBuilder text =
                new StringBuilder();

        text.append(
                preset.description
        );

        text.append(
                "\n\nJUGADOR\n"
        );

        text.append(
                preset.playerName
                        + " · "
                        + preset.gameMode
                        + " · "
                        + preset.health
                        + "/20 vida"
        );

        text.append(
                "\n\nENTORNO\n"
        );

        text.append(
                preset.biome
                        + "\n"
                        + preset.dimension
        );

        if (
                !"none".equals(
                        preset.structureId
                )
        ) {

            text.append(
                    "\n"
                            + structureName
            );
        }

        text.append(
                "\n\nTARGET\n"
        );

        text.append(
                "none".equals(
                        preset.targetId
                )
                        ? "Ninguno"
                        : targetName
                                + " ["
                                + preset.targetId
                                + "]"
        );

        text.append(
                "\n\nEQUIPO\n"
        );

        text.append(
                "Principal: "
                        + mainHandName
                        + "\nSecundaria: "
                        + offHandName
        );

        text.append(
                "\n\nINVENTARIO\n"
        );

        if (
                preset.inventory.isEmpty()
        ) {

            text.append(
                    "Vacío"
            );

        } else {

            for (
                    DebugPreset.InventoryEntry entry :
                    preset.inventory
            ) {

                text.append(
                        DebugCatalog.ITEM_NAMES
                                .getOrDefault(
                                        entry.id(),
                                        entry.id()
                                )
                                + " x"
                                + entry.count()
                                + "\n"
                );
            }
        }

        text.append(
                "\nENTIDADES VISIBLES\n"
        );

        if (
                preset.visibleEntities.isEmpty()
        ) {

            text.append(
                    "Ninguna"
            );

        } else {

            for (
                    DebugPreset.VisibleEntityEntry entity :
                    preset.visibleEntities
            ) {

                text.append(
                        DebugCatalog.ENTITY_NAMES
                                .getOrDefault(
                                        entity.id(),
                                        entity.id()
                                )
                                + " a "
                                + entity.distance()
                                + " bloques"
                                + (
                                entity.hostile()
                                        ? " · hostil"
                                        : ""
                        )
                                + "\n"
                );
            }
        }

        text.append(
                "\nEFECTOS\n"
        );

        if (
                preset.effects.isEmpty()
        ) {

            text.append(
                    "Ninguno"
            );

        } else {

            for (
                    DebugPreset.EffectEntry effect :
                    preset.effects
            ) {

                text.append(
                        effect.id()
                                + " nivel "
                                + effect.level()
                                + "\n"
                );
            }
        }

        presetSummaryArea.setText(
                text.toString()
                        .trim()
        );

        presetSummaryArea.setCaretPosition(
                0
        );
    }

    private void toggleAdvancedMode() {

        boolean show =
                !advancedPanel.isVisible();

        advancedPanel.setVisible(
                show
        );

        advancedToggleButton.setText(
                show
                        ? "Ocultar edición manual"
                        : "Editar manualmente"
        );

        advancedPanel.revalidate();
        advancedPanel.repaint();

        if (
                frame != null
        ) {

            frame.revalidate();
            frame.repaint();
        }
    }

    private void addInventoryItem() {

        String id =
                selected(
                        inventoryItemBox
                );

        if (
                "none".equals(
                        id
                )
        ) {
            return;
        }

        int count =
                ((Number) inventoryCountSpinner
                        .getValue())
                        .intValue();

        inventoryItems.add(
                new DebugInventoryItem(
                        id,
                        DebugCatalog.ITEM_NAMES.getOrDefault(
                                id,
                                id
                        ),
                        count
                )
        );

        refreshInventory();
    }

    private void refreshInventory() {

        inventoryListModel.clear();

        for (
                DebugInventoryItem item :
                inventoryItems
        ) {

            inventoryListModel.addElement(
                    item.name()
                            + " x"
                            + item.count()
                            + "  ["
                            + item.id()
                            + "]"
            );
        }
    }

    private void addEffect() {

        debugEffects.add(
                new DebugEffect(
                        selected(
                                effectBox
                        ),
                        ((Number) effectLevelSpinner
                                .getValue())
                                .intValue(),
                        ((Number) effectSecondsSpinner
                                .getValue())
                                .intValue()
                )
        );

        refreshEffects();
    }

    private void refreshEffects() {

        effectListModel.clear();

        for (
                DebugEffect effect :
                debugEffects
        ) {

            effectListModel.addElement(
                    effect.id()
                            + " · nivel "
                            + effect.level()
                            + " · "
                            + effect.seconds()
                            + "s"
            );
        }
    }

    private void addVisibleEntity() {

        String id =
                selected(
                        visibleEntityBox
                );

        visibleEntities.add(
                new DebugVisibleEntity(
                        id,
                        DebugCatalog.ENTITY_NAMES.getOrDefault(
                                id,
                                id
                        ),
                        ((Number) visibleDistanceSpinner
                                .getValue())
                                .doubleValue(),
                        visibleHostileCheck.isSelected(),
                        ((Number) visibleHealthSpinner
                                .getValue())
                                .floatValue()
                )
        );

        refreshVisibleEntities();
    }

    private void refreshVisibleEntities() {

        visibleEntityListModel.clear();

        for (
                DebugVisibleEntity entity :
                visibleEntities
        ) {

            visibleEntityListModel.addElement(
                    entity.name()
                            + " · "
                            + entity.distance()
                            + " bloques"
                            + (
                            entity.hostile()
                                    ? " · hostil"
                                    : ""
                    )
            );
        }
    }

    private String buildInventoryToolResult() {

        StringBuilder json =
                new StringBuilder();

        json.append(
                "{\n  \"items\": [\n"
        );

        for (
                int i = 0;
                i < inventoryItems.size();
                i++
        ) {

            DebugInventoryItem item =
                    inventoryItems.get(
                            i
                    );

            json.append(
                    "    {\n"
                            + "      \"slot\": "
                            + i
                            + ",\n"
                            + "      \"id\": \""
                            + item.id()
                            + "\",\n"
                            + "      \"name\": \""
                            + item.name()
                            + "\",\n"
                            + "      \"mod_id\": \""
                            + namespace(
                                    item.id()
                            )
                            + "\",\n"
                            + "      \"count\": "
                            + item.count()
                            + "\n"
                            + "    }"
            );

            if (
                    i < inventoryItems.size() - 1
            ) {

                json.append(
                        ","
                );
            }

            json.append(
                    "\n"
            );
        }

        json.append(
                "  ]\n}"
        );

        return json.toString();
    }

    private String buildVisibleEntitiesToolResult() {

        StringBuilder json =
                new StringBuilder();

        json.append(
                "{\n  \"range\": 24,\n  \"entities\": [\n"
        );

        for (
                int i = 0;
                i < visibleEntities.size();
                i++
        ) {

            DebugVisibleEntity entity =
                    visibleEntities.get(
                            i
                    );

            json.append(
                    "    {\n"
                            + "      \"id\": \""
                            + entity.id()
                            + "\",\n"
                            + "      \"name\": \""
                            + entity.name()
                            + "\",\n"
                            + "      \"mod_id\": \""
                            + namespace(
                                    entity.id()
                            )
                            + "\",\n"
                            + "      \"distance\": "
                            + entity.distance()
                            + ",\n"
                            + "      \"hostile\": "
                            + entity.hostile()
                            + ",\n"
                            + "      \"health\": "
                            + entity.health()
                            + ",\n"
                            + "      \"max_health\": 20.0\n"
                            + "    }"
            );

            if (
                    i < visibleEntities.size() - 1
            ) {

                json.append(
                        ","
                );
            }

            json.append(
                    "\n"
            );
        }

        json.append(
                "  ]\n}"
        );

        return json.toString();
    }

    private String buildHeldItemToolResult(
            PlayerContext context
    ) {

        return "Mano principal: "
                + context.mainHand
                + "\nMano secundaria: "
                + context.offHand;
    }

    private ItemContext createItemContext(
            String id,
            int count
    ) {

        if (
                id == null
                || "none".equals(
                        id
                )
        ) {

            return new ItemContext(
                    null,
                    "Vacío",
                    0,
                    0,
                    0
            );
        }

        return new ItemContext(
                id,
                DebugCatalog.ITEM_NAMES
                        .getOrDefault(
                                id,
                                DebugCatalog.ARMOR_NAMES
                                        .getOrDefault(
                                                id,
                                                id
                                        )
                        ),
                count,
                0,
                0
        );
    }

    private void addArmor(
            PlayerContext context,
            JComboBox<String> box
    ) {

        String id =
                selected(
                        box
                );

        if (
                !"none".equals(
                        id
                )
        ) {

            context.armor.add(
                    createItemContext(
                            id,
                            1
                    )
            );
        }
    }

    private String namespace(
            String id
    ) {

        if (
                id == null
                || !id.contains(":")
        ) {
            return "unknown";
        }

        return id.substring(
                0,
                id.indexOf(':')
        );
    }

    private JComboBox<String> combo(
            List<String> values
    ) {

        JComboBox<String> combo =
                new JComboBox<>(
                        values.stream()
                                .sorted()
                                .toArray(
                                        String[]::new
                                )
                );

        styleCombo(
                combo
        );

        return combo;
    }

    private JSpinner spinner(
            int value,
            int min,
            int max,
            int step
    ) {

        JSpinner spinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                value,
                                min,
                                max,
                                step
                        )
                );

        return spinner;
    }

    private String selected(
            JComboBox<String> box
    ) {

        Object selected =
                box.getSelectedItem();

        return selected == null
                ? ""
                : selected.toString();
    }

    private void styleList(
            JList<String> list
    ) {

        list.setBackground(
                FIELD
        );

        list.setForeground(
                TEXT
        );

        list.setSelectionBackground(
                ACCENT.darker()
        );

        list.setVisibleRowCount(
                4
        );
    }

    private record DebugInventoryItem(
            String id,
            String name,
            int count
    ) {
    }

    private record DebugEffect(
            String id,
            int level,
            int seconds
    ) {
    }

    private record DebugVisibleEntity(
            String id,
            String name,
            double distance,
            boolean hostile,
            float health
    ) {
    }

    private void resetContext() {

        if (
                presets.isEmpty()
        ) {
            return;
        }

        if (
                presetBox != null
        ) {

            presetBox.setSelectedIndex(
                    0
            );
        }

        applyPreset(
                presets.get(
                        0
                )
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