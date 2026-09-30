package com.jano.minecraftai.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MinecraftAIScreen
        extends Screen {

    private static final int PANEL_WIDTH =
            390;

    private static final int PANEL_HEIGHT =
            320;

    private static final int HEADER_HEIGHT =
            38;

    private static final int FOOTER_HEIGHT =
            46;


    private static final long MESSAGE_ENTRY_MS =
            180;

    private static final double USER_CHARS_PER_SECOND =
            90.0;

    private static final double AI_CHARS_PER_SECOND =
            55.0;


    /*
     * Palette futurista/minimalista.
     */
    private static final int COLOR_PANEL =
            0xE60A0F16;

    private static final int COLOR_PANEL_ALT =
            0xF00D141D;

    private static final int COLOR_HEADER =
            0xF2101720;

    private static final int COLOR_BORDER =
            0xFF253440;

    private static final int COLOR_BORDER_SOFT =
            0xAA1B2933;

    private static final int COLOR_ACCENT =
            0xFF65E6FF;

    private static final int COLOR_ACCENT_DIM =
            0xFF328A9E;

    private static final int COLOR_TEXT =
            0xFFF2F7FA;

    private static final int COLOR_TEXT_DIM =
            0xFF8D9AA5;

    private static final int COLOR_USER =
            0xFF8EDCFF;

    private static final int COLOR_SUCCESS =
            0xFF7EE787;

    private static final int COLOR_WARNING =
            0xFFFFC857;

    private static final int COLOR_VIOLET =
            0xFFB69CFF;

    private static final int COLOR_ERROR =
            0xFFFF6B7A;

    private static final int COLOR_INPUT =
            0xD90B1119;


    private TextFieldWidget input;

    private int panelX;
    private int panelY;

    private int scrollOffset =
            0;

    private boolean dragging =
            false;

    private boolean openingStarted =
            false;

    private boolean closing =
            false;

    private final MinecraftAIAnimation.Tween openAnimation =
            new MinecraftAIAnimation.Tween(
                    0.0
            );

    private final MinecraftAIAnimation.Tween closeAnimation =
            new MinecraftAIAnimation.Tween(
                    0.0
            );

    private final MinecraftAIAnimation.Tween stateCardAnimation =
            new MinecraftAIAnimation.Tween(
                    0.0
            );

    private String lastVisualStatus =
            "";

    private double dragOffsetX;
    private double dragOffsetY;


    public MinecraftAIScreen() {

        super(
                Text.literal(
                        "MinecraftAI"
                )
        );
    }


    @Override
    protected void init() {

        if (
                !openingStarted
        ) {

            openingStarted =
                    true;

            openAnimation.start(
                    0.0,
                    1.0,
                    220,
                    MinecraftAIAnimation.Curve
                            .EASE_OUT_QUINT
            );
        }

        int actualWidth =
                getActualWidth();

        int actualHeight =
                getActualHeight();

        if (
                AIOverlayState.panelX < 0
                        || AIOverlayState.panelY < 0
        ) {

            panelX =
                    width
                            - actualWidth
                            - 18;

            panelY =
                    20;

        } else {

            panelX =
                    Math.max(
                            6,
                            Math.min(
                                    AIOverlayState.panelX,
                                    width
                                            - actualWidth
                                            - 6
                            )
                    );

            panelY =
                    Math.max(
                            6,
                            Math.min(
                                    AIOverlayState.panelY,
                                    height
                                            - actualHeight
                                            - 6
                            )
                    );
        }

        createInput();
    }


    private void createInput() {

        int actualWidth =
                getActualWidth();

        int actualHeight =
                getActualHeight();

        int inputX =
                panelX + 14;

        int inputY =
                panelY
                        + actualHeight
                        - 34;

        int inputWidth =
                actualWidth - 74;

        input =
                new TextFieldWidget(
                        textRenderer,
                        inputX,
                        inputY,
                        inputWidth,
                        20,
                        Text.literal(
                                "Preguntale algo..."
                        )
                );

        input.setMaxLength(
                2048
        );

        input.setPlaceholder(
                Text.literal(
                        "Preguntale algo..."
                )
        );

        input.setDrawsBackground(
                false
        );

        addDrawableChild(
                input
        );

        setInitialFocus(
                input
        );
    }


    private int getActualWidth() {

        return Math.min(
                PANEL_WIDTH,
                width - 30
        );
    }


    private int getActualHeight() {

        return Math.min(
                PANEL_HEIGHT,
                height - 30
        );
    }


    private void sendQuestion() {

        if (
                input == null
                        || client == null
        ) {
            return;
        }

        String question =
                input.getText()
                        .trim();

        if (
                question.isEmpty()
        ) {
            return;
        }

        input.setText(
                ""
        );

        scrollOffset =
                0;

        MinecraftAIClient.ask(
                client,
                question
        );
    }


    private void startClosing() {

        if (
                closing
        ) {
            return;
        }

        closing =
                true;

        closeAnimation.start(
                0.0,
                1.0,
                160,
                MinecraftAIAnimation.Curve
                        .EASE_IN_OUT_CUBIC
        );
    }


    @Override
    public void tick() {

        super.tick();

        String currentStatus =
                AIOverlayState.getStatus();

        if (
                !currentStatus.equals(
                        lastVisualStatus
                )
        ) {

            lastVisualStatus =
                    currentStatus;

            if (
                    isIdleStatus(
                            currentStatus
                    )
            ) {

                stateCardAnimation.start(
                        stateCardAnimation.get(),
                        0.0,
                        160,
                        MinecraftAIAnimation.Curve
                                .EASE_IN_OUT_CUBIC
                );

            } else {

                stateCardAnimation.start(
                        0.0,
                        1.0,
                        190,
                        MinecraftAIAnimation.Curve
                                .EASE_OUT_QUINT
                );
            }
        }

        if (
                closing
                        && !closeAnimation.isRunning()
                        && client != null
        ) {

            client.setScreen(
                    null
            );
        }
    }


    private boolean isIdleStatus(
            String status
    ) {

        if (
                status == null
        ) {
            return true;
        }

        String normalized =
                status
                        .trim()
                        .toLowerCase();

        return normalized.equals(
                "listo"
        )
                || normalized.equals(
                        "ready"
                );
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (
                MinecraftAIClient.isOpenKey(
                        keyCode,
                        scanCode
                )
        ) {

            startClosing();

            return true;
        }

        if (
                keyCode
                        == GLFW.GLFW_KEY_ESCAPE
        ) {

            startClosing();

            return true;
        }

        if (
                keyCode
                        == GLFW.GLFW_KEY_ENTER
        ) {

            sendQuestion();

            return true;
        }

        return super.keyPressed(
                keyCode,
                scanCode,
                modifiers
        );
    }


    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double amount
    ) {

        if (
                amount > 0
        ) {

            scrollOffset +=
                    3;

        } else if (
                amount < 0
        ) {

            scrollOffset -=
                    3;
        }

        scrollOffset =
                Math.max(
                        0,
                        scrollOffset
                );

        return true;
    }


    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (
                button
                        == GLFW.GLFW_MOUSE_BUTTON_LEFT
        ) {

            if (
                    isInsideVoiceToggle(
                            mouseX,
                            mouseY
                    )
            ) {

                boolean enabled =
                        MinecraftAIClientConfig
                                .toggleVoice();

                if (
                        !enabled
                ) {

                    MinecraftAITtsClient.stop();
                }

                return true;
            }

            if (
                    isInsideSendButton(
                            mouseX,
                            mouseY
                    )
            ) {

                sendQuestion();

                return true;
            }

            if (
                    isInsideTitleBar(
                            mouseX,
                            mouseY
                    )
            ) {

                dragging =
                        true;

                dragOffsetX =
                        mouseX
                                - panelX;

                dragOffsetY =
                        mouseY
                                - panelY;

                return true;
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }


    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {

        if (
                dragging
                        && button
                        == GLFW.GLFW_MOUSE_BUTTON_LEFT
        ) {

            int actualWidth =
                    getActualWidth();

            int actualHeight =
                    getActualHeight();

            panelX =
                    (int) (
                            mouseX
                                    - dragOffsetX
                    );

            panelY =
                    (int) (
                            mouseY
                                    - dragOffsetY
                    );

            panelX =
                    Math.max(
                            6,
                            Math.min(
                                    panelX,
                                    width
                                            - actualWidth
                                            - 6
                            )
                    );

            panelY =
                    Math.max(
                            6,
                            Math.min(
                                    panelY,
                                    height
                                            - actualHeight
                                            - 6
                            )
                    );

            AIOverlayState.panelX =
                    panelX;

            AIOverlayState.panelY =
                    panelY;

            clearChildren();

            createInput();

            return true;
        }

        return super.mouseDragged(
                mouseX,
                mouseY,
                button,
                deltaX,
                deltaY
        );
    }


    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        dragging =
                false;

        return super.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }


    private boolean isInsideTitleBar(
            double mouseX,
            double mouseY
    ) {

        return mouseX >= panelX
                && mouseX <= panelX
                        + getActualWidth()
                && mouseY >= panelY
                && mouseY <= panelY
                        + HEADER_HEIGHT
                && !isInsideVoiceToggle(
                        mouseX,
                        mouseY
                );
    }


    private boolean isInsideVoiceToggle(
            double mouseX,
            double mouseY
    ) {

        int x =
                panelX
                        + getActualWidth()
                        - 72;

        int y =
                panelY + 10;

        return mouseX >= x
                && mouseX <= x + 60
                && mouseY >= y
                && mouseY <= y + 16;
    }


    private boolean isInsideSendButton(
            double mouseX,
            double mouseY
    ) {

        int actualWidth =
                getActualWidth();

        int actualHeight =
                getActualHeight();

        int x =
                panelX
                        + actualWidth
                        - 50;

        int y =
                panelY
                        + actualHeight
                        - 35;

        return mouseX >= x
                && mouseX <= x + 36
                && mouseY >= y
                && mouseY <= y + 22;
    }


    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        int actualWidth =
                getActualWidth();

        int actualHeight =
                getActualHeight();

        double openProgress =
                openAnimation.get();

        double closeProgress =
                closing
                        ? closeAnimation.get()
                        : 0.0;

        double visibility =
                openProgress
                        * (
                                1.0
                                        - closeProgress
                        );

        float scale =
                (float) MinecraftAIAnimation.lerp(
                        0.97,
                        1.0,
                        visibility
                );

        float slideY =
                (float) MinecraftAIAnimation.lerp(
                        10.0,
                        0.0,
                        visibility
                );

        float centerX =
                panelX
                        + actualWidth
                        / 2.0f;

        float centerY =
                panelY
                        + actualHeight
                        / 2.0f;

        context.getMatrices().push();

        context.getMatrices().translate(
                centerX,
                centerY + slideY,
                0.0f
        );

        context.getMatrices().scale(
                scale,
                scale,
                1.0f
        );

        context.getMatrices().translate(
                -centerX,
                -centerY,
                0.0f
        );


        renderPanel(
                context,
                actualWidth,
                actualHeight
        );

        renderHeader(
                context,
                actualWidth
        );

        renderStateCard(
                context,
                actualWidth
        );

        renderHistory(
                context,
                actualWidth,
                actualHeight
        );

        renderFooter(
                context,
                actualWidth,
                actualHeight,
                mouseX,
                mouseY
        );


        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );

        context.getMatrices().pop();
    }


    private void renderPanel(
            DrawContext context,
            int actualWidth,
            int actualHeight
    ) {

        /*
         * Sombra simple.
         */
        context.fill(
                panelX + 4,
                panelY + 5,
                panelX + actualWidth + 4,
                panelY + actualHeight + 5,
                0x66000000
        );

        /*
         * Cuerpo.
         */
        context.fill(
                panelX,
                panelY,
                panelX + actualWidth,
                panelY + actualHeight,
                COLOR_PANEL
        );

        /*
         * Línea superior cyan.
         */
        context.fill(
                panelX,
                panelY,
                panelX + actualWidth,
                panelY + 1,
                COLOR_ACCENT
        );

        /*
         * Bordes finos.
         */
        context.fill(
                panelX,
                panelY,
                panelX + 1,
                panelY + actualHeight,
                COLOR_BORDER
        );

        context.fill(
                panelX + actualWidth - 1,
                panelY,
                panelX + actualWidth,
                panelY + actualHeight,
                COLOR_BORDER
        );

        context.fill(
                panelX,
                panelY + actualHeight - 1,
                panelX + actualWidth,
                panelY + actualHeight,
                COLOR_BORDER
        );
    }


    private void renderHeader(
            DrawContext context,
            int actualWidth
    ) {

        context.fill(
                panelX + 1,
                panelY + 1,
                panelX + actualWidth - 1,
                panelY + HEADER_HEIGHT,
                COLOR_HEADER
        );

        /*
         * Marca.
         */
        context.fill(
                panelX + 12,
                panelY + 12,
                panelX + 17,
                panelY + 17,
                COLOR_ACCENT
        );

        context.drawText(
                textRenderer,
                "MINECRAFTAI",
                panelX + 24,
                panelY + 11,
                COLOR_TEXT,
                false
        );

        /*
         * Estado.
         */
        String status =
                AIOverlayState
                        .getStatus()
                        .toUpperCase();

        int statusColor =
                AIOverlayState.isThinking()
                        ? COLOR_WARNING
                        : COLOR_SUCCESS;

        int statusWidth =
                textRenderer.getWidth(
                        status
                );

        int statusX =
                panelX
                        + actualWidth
                        - 88
                        - statusWidth;

        context.fill(
                statusX - 8,
                panelY + 14,
                statusX - 4,
                panelY + 18,
                statusColor
        );

        context.drawText(
                textRenderer,
                status,
                statusX,
                panelY + 12,
                statusColor,
                false
        );

        renderVoiceToggle(
                context,
                actualWidth
        );

        context.fill(
                panelX + 12,
                panelY + HEADER_HEIGHT,
                panelX + actualWidth - 12,
                panelY + HEADER_HEIGHT + 1,
                COLOR_BORDER_SOFT
        );
    }


    private void renderVoiceToggle(
            DrawContext context,
            int actualWidth
    ) {

        boolean enabled =
                MinecraftAIClientConfig
                        .isVoiceEnabled();

        int x =
                panelX
                        + actualWidth
                        - 72;

        int y =
                panelY + 10;

        int background =
                enabled
                        ? 0x5537B6CF
                        : 0x44232C34;

        int border =
                enabled
                        ? COLOR_ACCENT
                        : COLOR_TEXT_DIM;

        context.fill(
                x,
                y,
                x + 60,
                y + 16,
                background
        );

        context.fill(
                x,
                y,
                x + 1,
                y + 16,
                border
        );

        context.fill(
                x + 59,
                y,
                x + 60,
                y + 16,
                border
        );

        context.drawText(
                textRenderer,
                enabled
                        ? "VOICE  ON"
                        : "VOICE OFF",
                x + 6,
                y + 4,
                enabled
                        ? COLOR_ACCENT
                        : COLOR_TEXT_DIM,
                false
        );
    }


    private void renderStateCard(
            DrawContext context,
            int actualWidth
    ) {

        double progress =
                stateCardAnimation.get();

        if (
                progress <= 0.001
        ) {
            return;
        }

        String status =
                AIOverlayState.getStatus();

        int accent =
                getStateColor(
                        status
                );

        String description =
                getStateDescription(
                        status
                );

        int cardX =
                panelX + 14;

        int targetY =
                panelY
                        + HEADER_HEIGHT
                        + 8;

        int cardY =
                targetY
                        + (int) Math.round(
                                MinecraftAIAnimation.lerp(
                                        -6.0,
                                        0.0,
                                        progress
                                )
                        );

        int cardWidth =
                actualWidth - 28;

        int cardHeight =
                25;

        int background =
                MinecraftAIAnimation.alpha(
                        0xFF101923,
                        progress * 0.94
                );

        int border =
                MinecraftAIAnimation.alpha(
                        accent,
                        progress * 0.75
                );

        int textColor =
                MinecraftAIAnimation.alpha(
                        COLOR_TEXT,
                        progress
                );

        int dimColor =
                MinecraftAIAnimation.alpha(
                        COLOR_TEXT_DIM,
                        progress
                );


        context.fill(
                cardX,
                cardY,
                cardX + cardWidth,
                cardY + cardHeight,
                background
        );

        context.fill(
                cardX,
                cardY,
                cardX + 2,
                cardY + cardHeight,
                border
        );


        /*
         * Pulso suave.
         */
        double wave =
                (
                        Math.sin(
                                System.nanoTime()
                                        / 180_000_000.0
                        )
                                + 1.0
                )
                        / 2.0;

        double pulse =
                0.45
                        + wave * 0.55;

        int pulseColor =
                MinecraftAIAnimation.alpha(
                        accent,
                        progress * pulse
                );

        context.fill(
                cardX + 10,
                cardY + 8,
                cardX + 15,
                cardY + 13,
                pulseColor
        );


        context.drawText(
                textRenderer,
                normalizeStateName(
                        status
                ),
                cardX + 22,
                cardY + 5,
                textColor,
                false
        );


        int descriptionWidth =
                textRenderer.getWidth(
                        description
                );

        context.drawText(
                textRenderer,
                description,
                cardX
                        + cardWidth
                        - descriptionWidth
                        - 10,
                cardY + 5,
                dimColor,
                false
        );
    }


    private int getStateColor(
            String status
    ) {

        String value =
                status == null
                        ? ""
                        : status
                                .toLowerCase();

        if (
                value.contains(
                        "escuch"
                )
        ) {
            return COLOR_ACCENT;
        }

        if (
                value.contains(
                        "transcrib"
                )
        ) {
            return COLOR_VIOLET;
        }

        if (
                value.contains(
                        "hablando"
                )
        ) {
            return COLOR_ACCENT;
        }

        if (
                value.contains(
                        "error"
                )
                        || value.contains(
                                "no disponible"
                        )
        ) {
            return COLOR_ERROR;
        }

        if (
                value.contains(
                        "observ"
                )
                        || value.contains(
                                "pens"
                        )
                        || value.contains(
                                "envi"
                        )
        ) {
            return COLOR_WARNING;
        }

        return COLOR_ACCENT;
    }


    private String getStateDescription(
            String status
    ) {

        String value =
                status == null
                        ? ""
                        : status
                                .toLowerCase();

        if (
                value.contains(
                        "escuch"
                )
        ) {
            return "MIC ACTIVO";
        }

        if (
                value.contains(
                        "transcrib"
                )
        ) {
            return "PROCESANDO VOZ";
        }

        if (
                value.contains(
                        "hablando"
                )
        ) {
            return "VOICE OUTPUT";
        }

        if (
                value.contains(
                        "observ"
                )
        ) {
            return "ANALIZANDO";
        }

        if (
                value.contains(
                        "envi"
                )
        ) {
            return "NETWORK";
        }

        if (
                value.contains(
                        "error"
                )
                        || value.contains(
                                "no disponible"
                        )
        ) {
            return "ERROR";
        }

        return "PROCESANDO";
    }


    private String normalizeStateName(
            String status
    ) {

        if (
                status == null
                        || status.isBlank()
        ) {
            return "MINECRAFTAI";
        }

        return status
                .replace(
                        "...",
                        ""
                )
                .trim()
                .toUpperCase();
    }

    private void renderFooter(
            DrawContext context,
            int actualWidth,
            int actualHeight,
            int mouseX,
            int mouseY
    ) {

        int footerY =
                panelY
                        + actualHeight
                        - FOOTER_HEIGHT;

        context.fill(
                panelX + 1,
                footerY,
                panelX + actualWidth - 1,
                panelY + actualHeight - 1,
                COLOR_PANEL_ALT
        );

        context.fill(
                panelX + 12,
                footerY,
                panelX + actualWidth - 12,
                footerY + 1,
                COLOR_BORDER_SOFT
        );


        /*
         * Caja input custom.
         */
        int inputX =
                panelX + 12;

        int inputY =
                panelY
                        + actualHeight
                        - 36;

        int inputRight =
                panelX
                        + actualWidth
                        - 62;

        context.fill(
                inputX,
                inputY,
                inputRight,
                inputY + 24,
                COLOR_INPUT
        );

        context.fill(
                inputX,
                inputY + 23,
                inputRight,
                inputY + 24,
                input != null
                        && input.isFocused()
                                ? COLOR_ACCENT
                                : COLOR_BORDER
        );


        /*
         * Botón SEND custom.
         */
        int sendX =
                panelX
                        + actualWidth
                        - 50;

        int sendY =
                panelY
                        + actualHeight
                        - 35;

        boolean hovered =
                mouseX >= sendX
                        && mouseX <= sendX + 36
                        && mouseY >= sendY
                        && mouseY <= sendY + 22;

        context.fill(
                sendX,
                sendY,
                sendX + 36,
                sendY + 22,
                hovered
                        ? 0x6637B6CF
                        : 0x332A8294
        );

        context.fill(
                sendX,
                sendY + 21,
                sendX + 36,
                sendY + 22,
                hovered
                        ? COLOR_ACCENT
                        : COLOR_ACCENT_DIM
        );

        context.drawCenteredTextWithShadow(
                textRenderer,
                "→",
                sendX + 18,
                sendY + 7,
                hovered
                        ? COLOR_TEXT
                        : COLOR_ACCENT
        );
    }


    private void renderHistory(
            DrawContext context,
            int actualWidth,
            int actualHeight
    ) {

        List<RenderedLine> lines =
                buildLines(
                        actualWidth - 36
                );

        int stateCardSpace =
                (int) Math.round(
                        31.0
                                * stateCardAnimation.get()
                );

        int top =
                panelY
                        + HEADER_HEIGHT
                        + 12
                        + stateCardSpace;

        int bottom =
                panelY
                        + actualHeight
                        - FOOTER_HEIGHT
                        - 8;

        int availableHeight =
                bottom - top;

        int lineHeight =
                10;

        int maxVisible =
                Math.max(
                        1,
                        availableHeight
                                / lineHeight
                );

        int end =
                Math.max(
                        0,
                        lines.size()
                                - scrollOffset
                );

        int start =
                Math.max(
                        0,
                        end - maxVisible
                );

        int y =
                bottom
                        - (
                                end - start
                        )
                        * lineHeight;

        for (
                int i = start;
                i < end;
                i++
        ) {

            RenderedLine line =
                    lines.get(i);

            context.drawText(
                    textRenderer,
                    line.text,
                    panelX
                            + 18
                            + line.xOffset,
                    y,
                    line.color,
                    false
            );

            y +=
                    lineHeight;
        }
    }


    private List<RenderedLine> buildLines(
            int maxWidth
    ) {

        List<RenderedLine> result =
                new ArrayList<>();

        long now =
                System.nanoTime();

        for (
                AIOverlayState.Message message :
                AIOverlayState.getHistory()
        ) {

            String label;

            int baseColor;

            switch (
                    message.role
            ) {

                case USER -> {

                    label =
                            "YOU";

                    baseColor =
                            COLOR_USER;
                }

                case ASSISTANT -> {

                    label =
                            "MINECRAFTAI";

                    baseColor =
                            COLOR_TEXT;
                }

                default -> {

                    label =
                            "SYSTEM";

                    baseColor =
                            COLOR_TEXT_DIM;
                }
            }


            long ageNanos =
                    Math.max(
                            0L,
                            now
                                    - message.createdAtNanos
                    );

            double ageMs =
                    ageNanos
                            / 1_000_000.0;


            double entryProgress =
                    MinecraftAIAnimation.ease(
                            Math.min(
                                    1.0,
                                    ageMs
                                            / MESSAGE_ENTRY_MS
                            ),
                            MinecraftAIAnimation.Curve
                                    .EASE_OUT_CUBIC
                    );


            double charactersPerSecond =
                    switch (
                            message.role
                    ) {

                        case USER ->
                                USER_CHARS_PER_SECOND;

                        case ASSISTANT ->
                                getAssistantTypingSpeed(
                                        message.text
                                );

                        default ->
                                200.0;
                    };


            int visibleCharacters =
                    (int) Math.floor(
                            (
                                    ageNanos
                                            / 1_000_000_000.0
                            )
                                    * charactersPerSecond
                    );


            visibleCharacters =
                    Math.max(
                            0,
                            Math.min(
                                    message.text.length(),
                                    visibleCharacters
                            )
                    );


            String visibleText =
                    message.text.substring(
                            0,
                            visibleCharacters
                    );


            if (
                    visibleCharacters
                            < message.text.length()
            ) {

                visibleText +=
                        "▌";
            }


            int labelColor =
                    MinecraftAIAnimation.alpha(
                            message.role
                                    == AIOverlayState.Role.USER
                                            ? COLOR_ACCENT
                                            : COLOR_TEXT_DIM,
                            entryProgress
                    );

            int textColor =
                    MinecraftAIAnimation.alpha(
                            baseColor,
                            entryProgress
                    );


            int xOffset =
                    (int) Math.round(
                            MinecraftAIAnimation.lerp(
                                    message.role
                                            == AIOverlayState.Role.USER
                                                    ? 8.0
                                                    : -8.0,
                                    0.0,
                                    entryProgress
                            )
                    );


            result.add(
                    new RenderedLine(
                            Text.literal(
                                    label
                            )
                                    .asOrderedText(),
                            labelColor,
                            xOffset
                    )
            );


            List<OrderedText> wrapped =
                    textRenderer.wrapLines(
                            Text.literal(
                                    visibleText
                            ),
                            maxWidth
                    );


            for (
                    OrderedText line :
                    wrapped
            ) {

                result.add(
                        new RenderedLine(
                                line,
                                textColor,
                                xOffset
                        )
                );
            }


            result.add(
                    new RenderedLine(
                            OrderedText.EMPTY,
                            textColor,
                            0
                    )
            );
        }

        return result;
    }


    private double getAssistantTypingSpeed(
            String text
    ) {

        if (
                text == null
                        || text.isEmpty()
        ) {
            return AI_CHARS_PER_SECOND;
        }

        if (
                text.length() > 700
        ) {
            return 220.0;
        }

        if (
                text.length() > 350
        ) {
            return 150.0;
        }

        if (
                text.length() > 160
        ) {
            return 95.0;
        }

        return AI_CHARS_PER_SECOND;
    }


    @Override
    public boolean shouldPause() {
        return false;
    }


    private record RenderedLine(
            OrderedText text,
            int color,
            int xOffset
    ) {
    }
}