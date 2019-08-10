package bge.gui.gamestate;

import bge.gui.Drawable;

public interface GameState extends Drawable {
    public void componentResized(int width, int height);

    public void handleUserInput(UserInput input);

    public static enum UserInput {
        LEFT_BUTTON_PRESSED, LEFT_BUTTON_RELEASED
    }
}
