package bge.gui;

import java.util.function.Consumer;

import bge.gui.gamestate.GameState.UserInput;

public class MouseTracker {
    public boolean isMouseEntered = false;
    public int mouseX;
    public int mouseY;

    private final Consumer<UserInput> userInputConsumer;

    public MouseTracker(Consumer<UserInput> userInputConsumer) {
        this.userInputConsumer = userInputConsumer;
    }

    public void handleUserInput(UserInput input) {
        userInputConsumer.accept(input);
    }

    public void setMouseEntered(boolean mouseEntered) {
        this.isMouseEntered = mouseEntered;
    }

    public void setMouseXY(int x, int y) {
        mouseX = x;
        mouseY = y;
    }
}
