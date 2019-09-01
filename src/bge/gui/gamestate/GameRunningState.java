package bge.gui.gamestate;

import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.PositionChangedInfo;
import bge.igame.player.GuiPlayer;
import gt.gameentity.IGameImage;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gamestate.GameState;
import gt.gamestate.UserInput;

public class GameRunningState<M> implements GameState {
    private final IGameImageDrawer imageDrawer;

    private final IGameRenderer<M, IPosition<M>> gameRenderer;
    private final IPositionObserver<M, IPosition<M>> positionObserver;

    private final IGameImage boardImage;

    private IPosition<M> position;
    private MoveList<M> possibleMoves;
    private M lastMove;

    @SuppressWarnings("unchecked")
    public GameRunningState(IGameImageDrawer imageDrawer, IGameRenderer<M, IPosition<M>> gameRenderer) {
        this.imageDrawer = imageDrawer;
        this.gameRenderer = gameRenderer;
        positionObserver = gameRenderer instanceof IPositionObserver<?, ?> ? (IPositionObserver<M, IPosition<M>>) gameRenderer : null;
        boardImage = imageDrawer.newGameImage();
    }

    public void positionChanged(PositionChangedInfo<M> changeInfo) {
        if (positionObserver != null) {
            positionObserver.notifyPositionChanged(changeInfo.position, changeInfo.possibleMoves);
        }
        position = changeInfo.position;
        possibleMoves = changeInfo.possibleMoves;
        lastMove = changeInfo.lastMove;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void drawOn(IGraphics graphics) {
        imageDrawer.drawImage(graphics, boardImage, 0, 0);
        gameRenderer.drawPosition(graphics, position, possibleMoves, lastMove);
    }

    @Override
    public void setSize(double width, double height) {
        boardImage.setSize(width, height);
        gameRenderer.initializeAndDrawBoard(boardImage.getGraphics(), width, height);
    }

    @Override
    public void handleUserInput(UserInput input) {
        M move = gameRenderer.maybeGetUserMove(input, position, possibleMoves);
        if (move != null) {
            if (possibleMoves.contains(move)) {
                GuiPlayer.HUMAN.setMove(move);
            }
        }
    }
}
