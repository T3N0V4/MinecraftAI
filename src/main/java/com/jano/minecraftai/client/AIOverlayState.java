package com.jano.minecraftai.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AIOverlayState {

    public enum Role {
        USER,
        ASSISTANT,
        SYSTEM
    }

    public static class Message {

        public final Role role;
        public final String text;

        public Message(
                Role role,
                String text
        ) {
            this.role = role;
            this.text = text;
        }
    }

    private static final List<Message> history =
            new ArrayList<>();

    private static String status =
            "Listo";

    private static boolean thinking =
            false;

    /*
     * Posición del panel durante esta sesión.
     * -1 significa que todavía usamos posición automática.
     */
    public static int panelX =
            -1;

    public static int panelY =
            -1;

    public static synchronized void addUser(
            String text
    ) {

        history.add(
                new Message(
                        Role.USER,
                        text
                )
        );

        trimHistory();
    }

    public static synchronized void addAssistant(
            String text
    ) {

        history.add(
                new Message(
                        Role.ASSISTANT,
                        text
                )
        );

        trimHistory();
    }

    public static synchronized void addSystem(
            String text
    ) {

        history.add(
                new Message(
                        Role.SYSTEM,
                        text
                )
        );

        trimHistory();
    }

    public static synchronized List<Message> getHistory() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        history
                )
        );
    }

    public static synchronized void setStatus(
            String value
    ) {

        status =
                value == null
                        ? ""
                        : value;
    }

    public static synchronized String getStatus() {
        return status;
    }

    public static synchronized void setThinking(
            boolean value
    ) {

        thinking =
                value;
    }

    public static synchronized boolean isThinking() {
        return thinking;
    }

    private static void trimHistory() {

        int maxMessages =
                50;

        while (
                history.size()
                > maxMessages
        ) {

            history.remove(0);
        }
    }

    private AIOverlayState() {
    }
}