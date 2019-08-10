package bge.game.photosynthesis;

import bge.game.IPosition;
import bge.game.MoveList;

public class PhotosynthesisPosition implements IPosition<IPhotosynthesisMove> {
    final int numPlayers;
    int currentPlayer;

    public PhotosynthesisPosition(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    @Override
    public void getPossibleMoves(MoveList<IPhotosynthesisMove> moveList) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void makeMove(IPhotosynthesisMove move) {
        move.applyMove(this);
    }

    @Override
    public void unmakeMove(IPhotosynthesisMove move) {
        move.unapplyMove(this);
    }

    @Override
    public IPosition<IPhotosynthesisMove> createCopy() {
        // TODO Auto-generated method stub
        return null;
    }
}
