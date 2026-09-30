package com.jano.minecraftai.debug;

import javax.swing.*;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.List;
import java.util.Locale;

public class AutoCompleteTextField
        extends JTextField {

    private final List<String> values;

    private final JPopupMenu popup =
            new JPopupMenu();

    private final DefaultListModel<String> model =
            new DefaultListModel<>();

    private final JList<String> list =
            new JList<>(
                    model
            );

    private boolean updating =
            false;

    public AutoCompleteTextField(
            List<String> values
    ) {

        this.values =
                values;

        list.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll =
                new JScrollPane(
                        list
                );

        scroll.setBorder(
                null
        );

        scroll.setPreferredSize(
                new java.awt.Dimension(
                        300,
                        140
                )
        );

        popup.setBorder(
                BorderFactory.createLineBorder(
                        new java.awt.Color(
                                75,
                                78,
                                85
                        )
                )
        );

        popup.add(
                scroll
        );

        getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {
                                refresh();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {
                                refresh();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {
                                refresh();
                            }
                        }
                );

        addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyPressed(
                            KeyEvent e
                    ) {

                        if (
                                !popup.isVisible()
                        ) {
                            return;
                        }

                        if (
                                e.getKeyCode()
                                        == KeyEvent.VK_DOWN
                        ) {

                            int next =
                                    Math.min(
                                            model.size() - 1,
                                            list.getSelectedIndex() + 1
                                    );

                            list.setSelectedIndex(
                                    Math.max(
                                            0,
                                            next
                                    )
                            );

                            list.ensureIndexIsVisible(
                                    list.getSelectedIndex()
                            );

                            e.consume();

                        } else if (
                                e.getKeyCode()
                                        == KeyEvent.VK_UP
                        ) {

                            int previous =
                                    Math.max(
                                            0,
                                            list.getSelectedIndex() - 1
                                    );

                            list.setSelectedIndex(
                                    previous
                            );

                            list.ensureIndexIsVisible(
                                    previous
                            );

                            e.consume();

                        } else if (
                                e.getKeyCode()
                                        == KeyEvent.VK_ENTER
                                || e.getKeyCode()
                                        == KeyEvent.VK_TAB
                        ) {

                            if (
                                    list.getSelectedValue()
                                            != null
                            ) {

                                choose(
                                        list.getSelectedValue()
                                );

                                e.consume();
                            }
                        }
                    }
                }
        );

        list.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        if (
                                e.getClickCount() == 2
                                && list.getSelectedValue()
                                != null
                        ) {

                            choose(
                                    list.getSelectedValue()
                            );
                        }
                    }
                }
        );
    }

    private void refresh() {

        if (
                updating
        ) {
            return;
        }

        SwingUtilities.invokeLater(
                () -> {

                    String text =
                            getText()
                                    .trim()
                                    .toLowerCase(
                                            Locale.ROOT
                                    );

                    model.clear();

                    if (
                            text.isBlank()
                    ) {

                        popup.setVisible(
                                false
                        );

                        return;
                    }

                    for (
                            String value :
                            values
                    ) {

                        String lower =
                                value.toLowerCase(
                                        Locale.ROOT
                                );

                        if (
                                lower.contains(
                                        text
                                )
                        ) {

                            model.addElement(
                                    value
                            );
                        }
                    }

                    if (
                            model.isEmpty()
                            || !isShowing()
                    ) {

                        popup.setVisible(
                                false
                        );

                        return;
                    }

                    list.setSelectedIndex(
                            0
                    );

                    popup.show(
                            this,
                            0,
                            getHeight()
                    );
                }
        );
    }

    private void choose(
            String value
    ) {

        updating =
                true;

        setText(
                value
        );

        setCaretPosition(
                value.length()
        );

        popup.setVisible(
                false
        );

        updating =
                false;

        postActionEvent();
    }
}