package bge.game.papersoccer;

import bge.gui.gamestate.IGameRenderer;
import bge.igame.IGame;
import gt.component.IMouseTracker;
import gt.gameentity.IGameImageDrawer;

public class PaperSoccerGame implements IGame<Integer, PaperSoccerPosition> {
    public static final String NAME = "Paper Soccer";
    public static final int MAX_MOVES = 8;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public int getMaxMoves() {
        return MAX_MOVES;
    }

    @Override
    public PaperSoccerPosition newInitialPosition() {
        return new PaperSoccerPosition();
    }

    @Override
    public IGameRenderer<Integer, PaperSoccerPosition> newGameRenderer(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        return new PaperSoccerGameRenderer(mouseTracker);
    }
}
