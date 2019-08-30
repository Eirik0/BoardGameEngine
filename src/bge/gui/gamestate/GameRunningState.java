package bge.gui.gamestate;

import java.awt.Graphics2D;

import bge.game.GameRunner;
import bge.game.IPosition;
import bge.gui.GameGuiManager;
import bge.gui.GameImage;
import bge.gui.GuiPlayer;
import gt.gamestate.UserInput;

public class GameRunningState<M> implements GameState {
    private final GameRunner<M, IPosition<M>> gameRunner;
    private final IGameRenderer<M, IPosition<M>> gameRenderer;
    private final GameImage boardImage = new GameImage();

    @SuppressWarnings("unchecked")
    public GameRunningState(GameRunner<M, IPosition<M>> gameRunner, IGameRenderer<M, IPosition<M>> gameRenderer) {
        this.gameRunner = gameRunner;
        this.gameRenderer = gameRenderer;
        if (gameRenderer instanceof IPositionObserver<?, ?>) {
            gameRunner.setPositionObserver((IPositionObserver<M, IPosition<M>>) gameRenderer);
        }
        componentResized(GameGuiManager.getComponentWidth(), GameGuiManager.getComponentHeight());
    }

    @Override
    public void drawOn(Graphics2D graphics) {
        graphics.drawImage(boardImage.getImage(), 0, 0, null);
        gameRenderer.drawPosition(graphics, gameRunner.getCurrentPositionCopy(), gameRunner.getPossibleMovesCopy(), gameRunner.getLastMove());
    }

    @Override
    public void componentResized(int width, int height) {
        boardImage.checkResized(width, height);
        gameRenderer.initializeAndDrawBoard(boardImage.getGraphics());
    }

    @Override
    public void handleUserInput(UserInput input) {
        M move = gameRenderer.maybeGetUserMove(input, gameRunner.getCurrentPositionCopy(), gameRunner.getPossibleMovesCopy());
        if (move != null) {
            if (gameRunner.getPossibleMovesCopy().contains(move)) {
                GuiPlayer.HUMAN.setMove(move);
            }
        }
    }
}
