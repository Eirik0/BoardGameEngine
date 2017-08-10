package gui;

import java.util.function.Consumer;

import game.IGame;
import gui.GameState.UserInput;

public class GameGuiManager {
	private static int mouseX;
	private static int mouseY;

	private static boolean mouseEntered;

	private static int componentWidth;
	private static int componentHeight;

	private static GameState currentState;

	private static Consumer<Class<? extends IGame<?, ?>>> startGameAction;

	public static int getMouseX() {
		return mouseX;
	}

	public static int getMouseY() {
		return mouseY;
	}

	public static void setMouseXY(int mouseX, int mouseY) {
		GameGuiManager.mouseX = mouseX;
		GameGuiManager.mouseY = mouseY;
	}

	public static boolean isMouseEntered() {
		return mouseEntered;
	}

	public static void setMouseEntered(boolean mouseEntered) {
		GameGuiManager.mouseEntered = mouseEntered;
	}

	public static void handleUserInput(UserInput input) {
		currentState.handleUserInput(input);
	}

	public static int getComponentWidth() {
		return componentWidth;
	}

	public static int getComponentHeight() {
		return componentHeight;
	}

	public static void setComponentSize(int componentWidth, int componentHeight) {
		GameGuiManager.componentWidth = componentWidth;
		GameGuiManager.componentHeight = componentHeight;
	}

	public static GameState getGameState() {
		return currentState;
	}

	public static void setSetGameAction(Consumer<Class<? extends IGame<?, ?>>> startGameAction) {
		GameGuiManager.startGameAction = startGameAction;
	}

	public static void setGame(Class<? extends IGame<?, ?>> gameClass) {
		startGameAction.accept(gameClass);
	}

	public static void setGameState(GameState gameState) {
		currentState = gameState;
	}
}
