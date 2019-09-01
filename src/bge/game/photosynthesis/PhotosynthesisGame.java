package bge.game.photosynthesis;

import bge.igame.IGame;

public class PhotosynthesisGame implements IGame<IPhotosynthesisMove> {
    @Override
    public String getName() {
        return "Photosynthesis";
    }

    @Override
    public int getNumberOfPlayers() {
        return 4;
    }

    @Override
    public int getPlayerIndexOffset() {
        return 0;
    }

    @Override
    public int getMaxMoves() {
        return 128;
    }

    @Override
    public PhotosynthesisPosition newInitialPosition() {
        return new PhotosynthesisPosition(getNumberOfPlayers());
    }
}
