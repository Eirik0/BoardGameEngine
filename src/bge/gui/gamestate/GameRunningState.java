package bge.gui.gamestate;

import bge.igame.GameRunner;
import bge.igame.IPosition;
import bge.igame.player.GuiPlayer;
import gt.gameentity.IGameImage;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gamestate.GameState;
import gt.gamestate.GameStateManager;
import gt.gamestate.UserInput;

public class GameRunningState<M> implements GameState {
    private final IGameImageDrawer imageDrawer;
    private final GameRunner<M, IPosition<M>> gameRunner;
    private final IGameRenderer<M, IPosition<M>> gameRenderer;
    private final IGameImage boardImage;

    @SuppressWarnings("unchecked")
    public GameRunningState(GameStateManager gameStateManager, GameRunner<M, IPosition<M>> gameRunner, IGameRenderer<M, IPosition<M>> gameRenderer) {
        imageDrawer = gameStateManager.getImageDrawer();
        this.gameRunner = gameRunner;
        this.gameRenderer = gameRenderer;
        boardImage = imageDrawer.newGameImage();
        if (gameRenderer instanceof IPositionObserver<?, ?>) { // TODO (re)move this
            gameRunner.setPositionObserver((IPositionObserver<M, IPosition<M>>) gameRenderer);
        }
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void drawOn(IGraphics graphics) {
        imageDrawer.drawImage(graphics, boardImage, 0, 0);
        gameRenderer.drawPosition(graphics, gameRunner.getCurrentPositionCopy(), gameRunner.getPossibleMovesCopy(), gameRunner.getLastMove());
    }

    @Override
    public void setSize(double width, double height) {
        boardImage.setSize(width, height);
        gameRenderer.initializeAndDrawBoard(boardImage.getGraphics(), width, height);
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
