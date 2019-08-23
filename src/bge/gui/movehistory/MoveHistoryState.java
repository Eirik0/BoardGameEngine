package bge.gui.movehistory;

import java.awt.Graphics2D;

import bge.game.GameRunner;
import bge.game.IPosition;
import bge.game.MoveHistory;
import bge.gui.MouseTracker;
import bge.gui.gamestate.GameState;
import bge.gui.movehistory.GuiMoveHistory.MoveMenuItem;
import bge.main.BoardGameEngineMain;
import gt.gameentity.SizedSizable;

public class MoveHistoryState<M> implements SizedSizable, GameState {
    int width;
    int height;

    public final MouseTracker mouseTracker = new MouseTracker(this::handleUserInput);
    private GameRunner<M, IPosition<M>> gameRunner;
    private GuiMoveHistory<M> guiMoveHistory;

    public MoveHistoryState() {
        guiMoveHistory = new GuiMoveHistory<>(new MoveHistory<>(1), mouseTracker);
    }

    public void setGameRunner(GameRunner<M, IPosition<M>> gameRunner) {
        this.gameRunner = gameRunner;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return Math.max(guiMoveHistory.getHeight(), height);
    }

    @Override
    public void drawOn(Graphics2D graphics) {
        fillRect(graphics, 0, 0, width, getHeight(), BoardGameEngineMain.BACKGROUND_COLOR);
        guiMoveHistory.drawOn(graphics);
    }

    public void setMoveHistory(MoveHistory<M> moveHistory) {
        guiMoveHistory = new GuiMoveHistory<>(moveHistory, mouseTracker);
    }

    @Override
    public void componentResized(int width, int height) {
        setSize(width, height);
    }

    @Override
    public void handleUserInput(UserInput input) {
        if (UserInput.LEFT_BUTTON_RELEASED == input) {
            MoveMenuItem selectedMove = guiMoveHistory.getSelectedMove();
            if (selectedMove != null) {
                gameRunner.setPositionFromHistory(selectedMove.moveNum, selectedMove.playerNum);
            }
        }
    }
}
