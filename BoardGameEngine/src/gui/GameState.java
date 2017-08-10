package gui;

public interface GameState extends Drawable {
	public void handleUserInput(UserInput input);

	public static enum UserInput {
		LEFT_BUTTON_PRESSED, LEFT_BUTTON_RELEASED
	}
}
