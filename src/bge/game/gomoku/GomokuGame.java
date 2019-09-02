package bge.game.gomoku;

import bge.gui.gamestate.IGameRenderer;
import bge.igame.IGame;
import bge.igame.player.TwoPlayers;
import gt.component.IMouseTracker;
import gt.gameentity.IGameImageDrawer;

public class GomokuGame implements IGame<Integer, GomokuPosition> {
    public static final String NAME = "Gomoku";
    public static final int MAX_MOVES = GomokuUtilities.BOARD_WIDTH * GomokuUtilities.BOARD_WIDTH;

    public static final int MAX_REASONABLE_DEPTH = 25;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getNumberOfPlayers() {
        return TwoPlayers.NUMBER_OF_PLAYERS;
    }

    @Override
    public int getMaxMoves() {
        return MAX_MOVES;
    }

    @Override
    public GomokuPosition newInitialPosition() {
        return new GomokuPosition();
    }

    @Override
    public IGameRenderer<Integer, GomokuPosition> newGameRenderer(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        return new GomokuGameRenderer(mouseTracker);
    }
}
