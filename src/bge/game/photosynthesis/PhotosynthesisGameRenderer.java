package bge.game.photosynthesis;

import java.awt.Graphics2D;

import bge.game.MoveList;
import bge.gui.gamestate.GameState.UserInput;
import bge.gui.gamestate.IGameRenderer;

public class PhotosynthesisGameRenderer implements IGameRenderer<IPhotosynthesisMove, PhotosynthesisPosition> {
    @Override
    public void initializeAndDrawBoard(Graphics2D g) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawPosition(Graphics2D g, PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves, IPhotosynthesisMove lastMove) {
        // TODO Auto-generated method stub

    }

    @Override
    public IPhotosynthesisMove maybeGetUserMove(UserInput input, PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves) {
        // TODO Auto-generated method stub
        return null;
    }
}
