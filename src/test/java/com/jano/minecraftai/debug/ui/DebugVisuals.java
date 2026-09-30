package com.jano.minecraftai.debug.ui;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

public final class DebugVisuals {

    public static void apply(
            Component component
    ) {

        style(component);

        if (
                component instanceof Container container
        ) {

            for (
                    Component child :
                    container.getComponents()
            ) {

                apply(child);
            }
        }
    }

    private static void style(
            Component component
    ) {

        component.setFont(
                DebugTheme.BODY
        );

        if (
                component instanceof JLabel label
        ) {

            if (
                    label.getForeground() == null
            ) {

                label.setForeground(
                        DebugTheme.TEXT
                );
            }
        }

        if (
                component instanceof JPanel panel
        ) {

            Border border =
                    panel.getBorder();

            if (
                    border instanceof TitledBorder titled
            ) {

                panel.setOpaque(false);

                panel.setBorder(
                        new SectionBorder(
                                titled.getTitle()
                        )
                );
            }
        }

        if (
                component instanceof JButton button
        ) {

            styleButton(button);
        }

        if (
                component instanceof JTextField field
        ) {

            field.setBackground(
                    DebugTheme.FIELD
            );

            field.setForeground(
                    DebugTheme.TEXT
            );

            field.setCaretColor(
                    DebugTheme.ACCENT_BRIGHT
            );

            field.setSelectionColor(
                    DebugTheme.ACCENT
            );

            field.setSelectedTextColor(
                    Color.WHITE
            );

            field.setBorder(
                    new RoundedBorder(
                            DebugTheme.BORDER,
                            12,
                            new Insets(
                                    7,
                                    10,
                                    7,
                                    10
                            )
                    )
            );
        }

        if (
                component instanceof JTextArea area
        ) {

            area.setBackground(
                    DebugTheme.FIELD
            );

            area.setForeground(
                    DebugTheme.TEXT
            );

            area.setCaretColor(
                    DebugTheme.ACCENT_BRIGHT
            );

            area.setSelectionColor(
                    DebugTheme.ACCENT
            );

            area.setSelectedTextColor(
                    Color.WHITE
            );

            area.setBorder(
                    BorderFactory.createEmptyBorder(
                            10,
                            12,
                            10,
                            12
                    )
            );
        }

        if (
                component instanceof JComboBox<?> combo
        ) {

            combo.setBackground(
                    DebugTheme.FIELD
            );

            combo.setForeground(
                    DebugTheme.TEXT
            );

            combo.setBorder(
                    new RoundedBorder(
                            DebugTheme.BORDER,
                            10,
                            new Insets(
                                    4,
                                    7,
                                    4,
                                    7
                            )
                    )
            );
        }

        if (
                component instanceof JSpinner spinner
        ) {

            spinner.setBackground(
                    DebugTheme.FIELD
            );

            spinner.setForeground(
                    DebugTheme.TEXT
            );

            spinner.setBorder(
                    new RoundedBorder(
                            DebugTheme.BORDER,
                            10,
                            new Insets(
                                    3,
                                    6,
                                    3,
                                    6
                            )
                    )
            );
        }

        if (
                component instanceof JList<?> list
        ) {

            list.setBackground(
                    DebugTheme.FIELD
            );

            list.setForeground(
                    DebugTheme.TEXT
            );

            list.setSelectionBackground(
                    DebugTheme.PANEL_HOVER
            );

            list.setSelectionForeground(
                    DebugTheme.ACCENT_BRIGHT
            );

            list.setBorder(
                    BorderFactory.createEmptyBorder(
                            6,
                            7,
                            6,
                            7
                    )
            );
        }

        if (
                component instanceof JScrollPane scroll
        ) {

            scroll.setBorder(
                    new RoundedBorder(
                            DebugTheme.BORDER_SOFT,
                            14,
                            new Insets(
                                    1,
                                    1,
                                    1,
                                    1
                            )
                    )
            );

            scroll.setBackground(
                    DebugTheme.PANEL
            );

            JViewport viewport =
                    scroll.getViewport();

            viewport.setBackground(
                    DebugTheme.FIELD
            );

            JScrollBar vertical =
                    scroll.getVerticalScrollBar();

            if (
                    vertical != null
            ) {

                vertical.setUI(
                        new ModernScrollBarUI()
                );

                vertical.setPreferredSize(
                        new Dimension(
                                9,
                                9
                        )
                );
            }

            JScrollBar horizontal =
                    scroll.getHorizontalScrollBar();

            if (
                    horizontal != null
            ) {

                horizontal.setUI(
                        new ModernScrollBarUI()
                );

                horizontal.setPreferredSize(
                        new Dimension(
                                9,
                                9
                        )
                );
            }
        }

        if (
                component instanceof JTabbedPane tabs
        ) {

            tabs.setUI(
                    new ModernTabbedPaneUI()
            );

            tabs.setBackground(
                    DebugTheme.BG
            );

            tabs.setForeground(
                    DebugTheme.TEXT_MUTED
            );
        }
    }

    private static void styleButton(
            JButton button
    ) {

        button.setUI(
                new ModernButtonUI()
        );

        button.setForeground(
                DebugTheme.TEXT
        );

        button.setContentAreaFilled(false);

        button.setBorderPainted(false);

        button.setFocusPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        14,
                        8,
                        14
                )
        );
    }

    private static final class ModernButtonUI
            extends BasicButtonUI {

        @Override
        public void paint(
                Graphics g,
                JComponent c
        ) {

            AbstractButton button =
                    (AbstractButton) c;

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color bg =
                    DebugTheme.PANEL_ALT;

            Color border =
                    DebugTheme.BORDER;

            if (
                    button.getModel().isPressed()
            ) {

                bg =
                        new Color(
                                21,
                                73,
                                105
                        );

                border =
                        DebugTheme.ACCENT_BRIGHT;

            } else if (
                    button.getModel().isRollover()
            ) {

                bg =
                        DebugTheme.PANEL_HOVER;

                border =
                        DebugTheme.ACCENT;
            }

            g2.setColor(bg);

            g2.fillRoundRect(
                    0,
                    0,
                    c.getWidth() - 1,
                    c.getHeight() - 1,
                    14,
                    14
            );

            g2.setColor(border);

            g2.drawRoundRect(
                    0,
                    0,
                    c.getWidth() - 1,
                    c.getHeight() - 1,
                    14,
                    14
            );

            g2.dispose();

            super.paint(g, c);
        }
    }

    private static final class ModernTabbedPaneUI
            extends BasicTabbedPaneUI {

        @Override
        protected void installDefaults() {

            super.installDefaults();

            tabAreaInsets =
                    new Insets(
                            4,
                            4,
                            4,
                            4
                    );

            tabInsets =
                    new Insets(
                            8,
                            16,
                            8,
                            16
                    );

            contentBorderInsets =
                    new Insets(
                            6,
                            0,
                            0,
                            0
                    );
        }

        @Override
        protected void paintTabBackground(
                Graphics g,
                int tabPlacement,
                int tabIndex,
                int x,
                int y,
                int w,
                int h,
                boolean isSelected
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    isSelected
                            ? DebugTheme.PANEL_HOVER
                            : DebugTheme.PANEL
            );

            g2.fillRoundRect(
                    x + 2,
                    y + 2,
                    w - 4,
                    h - 4,
                    13,
                    13
            );

            if (
                    isSelected
            ) {

                g2.setColor(
                        DebugTheme.ACCENT
                );

                g2.drawRoundRect(
                        x + 2,
                        y + 2,
                        w - 4,
                        h - 4,
                        13,
                        13
                );
            }

            g2.dispose();
        }

        @Override
        protected void paintContentBorder(
                Graphics g,
                int tabPlacement,
                int selectedIndex
        ) {

            // sin borde clásico
        }

        @Override
        protected void paintFocusIndicator(
                Graphics g,
                int tabPlacement,
                java.awt.Rectangle[] rects,
                int tabIndex,
                java.awt.Rectangle iconRect,
                java.awt.Rectangle textRect,
                boolean isSelected
        ) {

            // sin focus rect clásico
        }
    }

    private static final class ModernScrollBarUI
            extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {

            trackColor =
                    DebugTheme.BG;

            thumbColor =
                    DebugTheme.BORDER;
        }

        @Override
        protected JButton createDecreaseButton(
                int orientation
        ) {

            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(
                int orientation
        ) {

            return zeroButton();
        }

        private JButton zeroButton() {

            JButton button =
                    new JButton();

            button.setPreferredSize(
                    new Dimension(
                            0,
                            0
                    )
            );

            return button;
        }

        @Override
        protected void paintThumb(
                Graphics g,
                JComponent c,
                java.awt.Rectangle thumbBounds
        ) {

            if (
                    thumbBounds.isEmpty()
            ) {

                return;
            }

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    isDragging
                            ? DebugTheme.ACCENT
                            : DebugTheme.BORDER
            );

            g2.fillRoundRect(
                    thumbBounds.x + 2,
                    thumbBounds.y + 2,
                    thumbBounds.width - 4,
                    thumbBounds.height - 4,
                    8,
                    8
            );

            g2.dispose();
        }
    }

    private static final class SectionBorder
            extends AbstractBorder {

        private final String title;

        private SectionBorder(
                String title
        ) {

            this.title =
                    title == null
                            ? ""
                            : title;
        }

        @Override
        public Insets getBorderInsets(
                Component c
        ) {

            return new Insets(
                    29,
                    12,
                    12,
                    12
            );
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    DebugTheme.PANEL
            );

            g2.fillRoundRect(
                    x + 1,
                    y + 7,
                    width - 3,
                    height - 9,
                    18,
                    18
            );

            g2.setColor(
                    DebugTheme.BORDER_SOFT
            );

            g2.setStroke(
                    new BasicStroke(
                            1f
                    )
            );

            g2.drawRoundRect(
                    x + 1,
                    y + 7,
                    width - 3,
                    height - 9,
                    18,
                    18
            );

            g2.setFont(
                    DebugTheme.SECTION
            );

            FontMetrics fm =
                    g2.getFontMetrics();

            int textWidth =
                    fm.stringWidth(title);

            g2.setColor(
                    DebugTheme.PANEL
            );

            g2.fillRect(
                    x + 13,
                    y,
                    textWidth + 14,
                    20
            );

            g2.setColor(
                    DebugTheme.ACCENT_BRIGHT
            );

            g2.drawString(
                    title,
                    x + 20,
                    y + 16
            );

            g2.dispose();
        }
    }

    private static final class RoundedBorder
            extends AbstractBorder {

        private final Color color;

        private final int radius;

        private final Insets insets;

        private RoundedBorder(
                Color color,
                int radius,
                Insets insets
        ) {

            this.color =
                    color;

            this.radius =
                    radius;

            this.insets =
                    insets;
        }

        @Override
        public Insets getBorderInsets(
                Component c
        ) {

            return insets;
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);

            g2.drawRoundRect(
                    x,
                    y,
                    width - 1,
                    height - 1,
                    radius,
                    radius
            );

            g2.dispose();
        }
    }

    private DebugVisuals() {
    }
}