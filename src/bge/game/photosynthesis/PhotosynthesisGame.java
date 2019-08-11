package bge.game.photosynthesis;

import bge.game.IGame;

public class PhotosynthesisGame implements IGame<IPhotosynthesisMove, PhotosynthesisPosition> {
    @Override
    public String getName() {
        return "Photosynthesis";
    }

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public int getPlayerIndexOffset() {
        return 0;
    }

    @Override
    public int getMaxMoves() {
        return 64;
    }

    @Override
    public PhotosynthesisPosition newInitialPosition() {
        return new PhotosynthesisPosition(2);
    }
}
