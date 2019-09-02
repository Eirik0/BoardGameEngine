package bge.game.photosynthesis;

import bge.gui.gamestate.IGameRenderer;
import bge.igame.IGame;
import gt.component.IMouseTracker;
import gt.gameentity.IGameImageDrawer;

public class PhotosynthesisGame implements IGame<IPhotosynthesisMove, PhotosynthesisPosition> {
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

    @Override
    public IGameRenderer<IPhotosynthesisMove, PhotosynthesisPosition> newGameRenderer(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        return new PhotosynthesisGameRenderer(mouseTracker, imageDrawer);
    }
}
