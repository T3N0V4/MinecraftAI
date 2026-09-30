package com.jano.minecraftai.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MinecraftAIScreen
        extends Screen {

    private static final int PANEL_WIDTH =
            360;

    private static final int PANEL_HEIGHT =
            300;

    private TextFieldWidget input;

    private int panelX;
    private int panelY;

    private int scrollOffset =
            0;

    private boolean dragging =
            false;

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

        int actualWidth =
                Math.min(
                        PANEL_WIDTH,
                        width - 30
                );

        int actualHeight =
                Math.min(
                        PANEL_HEIGHT,
                        height - 30
                );

        if (
                AIOverlayState.panelX < 0
                || AIOverlayState.panelY < 0
        ) {

            panelX =
                    width
                            - actualWidth
                            - 15;

            panelY =
                    20;

        } else {

            panelX =
                    Math.max(
                            5,
                            Math.min(
                                    AIOverlayState.panelX,
                                    width
                                            - actualWidth
                                            - 5
                            )
                    );

            panelY =
                    Math.max(
                            5,
                            Math.min(
                                    AIOverlayState.panelY,
                                    height
                                            - actualHeight
                                            - 5
                            )
                    );
        }

        int inputY =
                panelY
                        + actualHeight
                        - 32;

        input =
                new TextFieldWidget(
                        textRenderer,
                        panelX + 10,
                        inputY,
                        actualWidth - 55,
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

        addDrawableChild(
                input
        );

        addDrawableChild(
                ButtonWidget.builder(
                                Text.literal("→"),
                                button ->
                                        sendQuestion()
                        )
                        .dimensions(
                                panelX
                                        + actualWidth
                                        - 40,
                                inputY,
                                30,
                                20
                        )
                        .build()
        );

        setInitialFocus(
                input
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

        if (question.isEmpty()) {
            return;
        }

        input.setText("");

        scrollOffset =
                0;

        MinecraftAIClient.ask(
                client,
                question
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

            if (
                    client != null
            ) {
                client.setScreen(
                        null
                );
            }

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
                && isInsideTitleBar(
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
                    Math.min(
                            PANEL_WIDTH,
                            width - 30
                    );

            int actualHeight =
                    Math.min(
                            PANEL_HEIGHT,
                            height - 30
                    );

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
                            5,
                            Math.min(
                                    panelX,
                                    width
                                            - actualWidth
                                            - 5
                            )
                    );

            panelY =
                    Math.max(
                            5,
                            Math.min(
                                    panelY,
                                    height
                                            - actualHeight
                                            - 5
                            )
                    );

            AIOverlayState.panelX =
                    panelX;

            AIOverlayState.panelY =
                    panelY;

            /*
             * Reconstruimos widgets para que input
             * y botón acompañen al panel.
             */
            clearChildren();
            init();

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

        int actualWidth =
                Math.min(
                        PANEL_WIDTH,
                        width - 30
                );

        return mouseX >= panelX
                && mouseX <= panelX + actualWidth
                && mouseY >= panelY
                && mouseY <= panelY + 28;
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        int actualWidth =
                Math.min(
                        PANEL_WIDTH,
                        width - 30
                );

        int actualHeight =
                Math.min(
                        PANEL_HEIGHT,
                        height - 30
                );

        /*
         * No llamamos renderBackground().
         * Queremos ver el juego detrás.
         */

        context.fill(
                panelX,
                panelY,
                panelX + actualWidth,
                panelY + actualHeight,
                0xDD0D1117
        );

        /*
         * Barra superior.
         */
        context.fill(
                panelX,
                panelY,
                panelX + actualWidth,
                panelY + 28,
                0xEE161B22
        );

        context.fill(
                panelX,
                panelY,
                panelX + 3,
                panelY + actualHeight,
                0xFF66D9EF
        );

        context.drawTextWithShadow(
                textRenderer,
                "MinecraftAI",
                panelX + 12,
                panelY + 10,
                0xFFFFFFFF
        );

        String status =
                AIOverlayState.getStatus();

        int statusWidth =
                textRenderer
                        .getWidth(status);

        context.drawTextWithShadow(
                textRenderer,
                status,
                panelX
                        + actualWidth
                        - statusWidth
                        - 12,
                panelY + 10,
                AIOverlayState.isThinking()
                        ? 0xFFFFC857
                        : 0xFF8BE28B
        );

        renderHistory(
                context,
                actualWidth,
                actualHeight
        );

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );
    }

    private void renderHistory(
            DrawContext context,
            int actualWidth,
            int actualHeight
    ) {

        List<RenderedLine> lines =
                buildLines(
                        actualWidth - 28
                );

        int top =
                panelY + 38;

        int bottom =
                panelY
                        + actualHeight
                        - 42;

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
                                end
                                - start
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
                    panelX + 12,
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

        for (
                AIOverlayState.Message message :
                AIOverlayState.getHistory()
        ) {

            String prefix;
            int color;

            switch (
                    message.role
            ) {

                case USER -> {

                    prefix =
                            "Vos: ";

                    color =
                            0xFF9CDCFE;
                }

                case ASSISTANT -> {

                    prefix =
                            "AI: ";

                    color =
                            0xFFFFFFFF;
                }

                default -> {

                    prefix =
                            "";

                    color =
                            0xFFAAAAAA;
                }
            }

            List<OrderedText> wrapped =
                    textRenderer.wrapLines(
                            Text.literal(
                                    prefix
                                            + message.text
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
                                color
                        )
                );
            }

            /*
             * Separación entre mensajes.
             */
            result.add(
                    new RenderedLine(
                            OrderedText.EMPTY,
                            color
                    )
            );
        }

        return result;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private record RenderedLine(
            OrderedText text,
            int color
    ) {
    }
}