package bge.game.photosynthesis;

import bge.game.IGame;

public class PhotosynthesisGame implements IGame<IPhotosynthesisMove, PhotosynthesisPosition> {
    @Override
    public String getName() {
        return "Photosynthesis";
    }

    @Override
    public int getNumberOfPlayers() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxMoves() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public PhotosynthesisPosition newInitialPosition() {
        // TODO Auto-generated method stub
        return null;
    }
}
