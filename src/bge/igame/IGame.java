package bge.igame;

import bge.gui.gamestate.IGameRenderer;
import gt.component.IMouseTracker;
import gt.gameentity.IGameImageDrawer;

public interface IGame<M, P extends IPosition<M>> {
    String getName();

    int getNumberOfPlayers();

    default int getPlayerIndexOffset() {
        return 1;
    }

    int getMaxMoves();

    IPosition<M> newInitialPosition();

    IGameRenderer<M, P> newGameRenderer(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer);
}
