package bge.gui.gamestate;

import bge.gui.Drawable;
import gt.gamestate.UserInput;

public interface GameState extends Drawable {
    void componentResized(int width, int height);

    void handleUserInput(UserInput input);
}
