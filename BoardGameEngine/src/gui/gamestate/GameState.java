package gui.gamestate;

import gui.Drawable;

public interface GameState extends Drawable {
	public void componentResized();

	public void handleUserInput(UserInput input);

	public static enum UserInput {
		LEFT_BUTTON_PRESSED, LEFT_BUTTON_RELEASED
	}
}