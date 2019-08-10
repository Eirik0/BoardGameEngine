package gui;

import java.util.function.Consumer;

import gui.gamestate.GameState;
import gui.gamestate.GameState.UserInput;

public class GameGuiManager {
    private static final MouseTracker mouseTracker = new MouseTracker(GameGuiManager::handleUserInput);

    private static int componentWidth;
    private static int componentHeight;

    private static GameState currentState;

    private static Consumer<String> startGameAction;

    public static int getMouseX() {
        return mouseTracker.mouseX;
    }

    public static int getMouseY() {
        return mouseTracker.mouseY;
    }

    public static void setMouseXY(int mouseX, int mouseY) {
        mouseTracker.setMouseXY(mouseX, mouseY);
    }

    public static boolean isMouseEntered() {
        return mouseTracker.isMouseEntered;
    }

    public static void setMouseEntered(boolean mouseEntered) {
        mouseTracker.setMouseEntered(mouseEntered);
    }

    public static MouseTracker getMouseTracker() {
        return mouseTracker;
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
        if (currentState != null) {
            currentState.componentResized(componentWidth, componentHeight);
        }
    }

    public static GameState getGameState() {
        return currentState;
    }

    public static void setLoadGameAction(Consumer<String> startGameAction) {
        GameGuiManager.startGameAction = startGameAction;
    }

    public static void setGame(String gameName) {
        startGameAction.accept(gameName);
    }

    public static void setGameState(GameState gameState) {
        currentState = gameState;
    }

}
