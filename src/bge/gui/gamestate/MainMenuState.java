package bge.gui.gamestate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bge.main.BoardGameEngineMain;
import bge.main.GameRegistry;
import gt.component.IMouseTracker;
import gt.gameentity.Drawable;
import gt.gameentity.IGraphics;
import gt.gamestate.GameState;
import gt.gamestate.GameStateManager;
import gt.gamestate.UserInput;
import gt.util.EMath;

public class MainMenuState implements GameState {
    private final GameStateManager gameStateManager;
    private final IMouseTracker mouseTracker;

    private final List<MenuItem> menuItems = new ArrayList<>();
    double width;
    double height;

    public MainMenuState(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        mouseTracker = gameStateManager.getMouseTracker();
        Set<String> gameNames = GameRegistry.getGameNames();

        double widthPercentStart = 0.25;
        double widthPercentEnd = 0.75;
        double gap = 0.05;
        double height = 1.0 / gameNames.size() - gap * (gameNames.size() + 1) / gameNames.size();

        double currentHeight = gap;
        for (String gameName : gameNames) {
            menuItems.add(new MenuItem(gameName, widthPercentStart, currentHeight, widthPercentEnd, currentHeight + height));
            currentHeight += height + gap;
        }
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void drawOn(IGraphics graphics) {
        graphics.setColor(BoardGameEngineMain.BACKGROUND_COLOR);
        graphics.fillRect(0, 0, width, height);
        for (MenuItem menuItem : menuItems) {
            menuItem.drawOn(graphics);
        }
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void handleUserInput(UserInput input) {
        if (input == UserInput.LEFT_BUTTON_RELEASED) {
            for (MenuItem menuItem : menuItems) {
                if (menuItem.checkContainsCursor()) {
                    String gameName = menuItem.gameName;
                    gameStateManager.setGameState(new BoardGameState<>(gameStateManager, GameRegistry.getGame(gameName)));
                    break;
                }
            }
        }
    }

    class MenuItem implements Drawable {
        final String gameName;

        final double widthPercentStart;
        final double heightPercentStart;
        final double widthPercentEnd;
        final double heightPercentEnd;

        public MenuItem(String gameName, double widthPercentStart, double heightPercentStart, double widthPercentEnd, double heightPercentEnd) {
            this.gameName = gameName;
            this.widthPercentStart = widthPercentStart;
            this.widthPercentEnd = widthPercentEnd;
            this.heightPercentStart = heightPercentStart;
            this.heightPercentEnd = heightPercentEnd;
        }

        @Override
        public void drawOn(IGraphics graphics) {
            graphics.setColor(checkContainsCursor() ? Color.BLUE : Color.RED);
            graphics.drawRect(getX0(), getY0(), getX1() - getX0(), getY1() - getY0());
            graphics.drawCenteredString(gameName, getCenterX(), getCenterY());
        }

        boolean checkContainsCursor() {
            return mouseTracker.isMouseEntered() && mouseTracker.mouseY() >= getY0() && mouseTracker.mouseY() <= getY1()
                    && mouseTracker.mouseX() >= getX0() && mouseTracker.mouseX() <= getX1();
        }

        int getX0() {
            return EMath.round(width * widthPercentStart);
        }

        private int getY0() {
            return EMath.round(height * heightPercentStart);
        }

        private int getX1() {
            return EMath.round(width * widthPercentEnd);
        }

        private int getY1() {
            return EMath.round(height * heightPercentEnd);
        }

        private double getCenterX() {
            return (width * (widthPercentStart + widthPercentEnd)) / 2;
        }

        private double getCenterY() {
            return (height * (heightPercentStart + heightPercentEnd)) / 2;
        }
    }
}
