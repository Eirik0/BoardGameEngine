package bge.game.sudoku;

import bge.gui.gamestate.IGameRenderer;
import bge.igame.IGame;
import gt.component.IMouseTracker;
import gt.gameentity.IGameImageDrawer;

public class SudokuGame implements IGame<SudokuMove, SudokuPosition>, SudokuConstants {
    public static final String NAME = "Sudoku Generator";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getNumberOfPlayers() {
        return 1;
    }

    @Override
    public int getMaxMoves() {
        return MAX_MOVES;
    }

    @Override
    public SudokuPosition newInitialPosition() {
        return new SudokuPosition();
    }

    @Override
    public IGameRenderer<SudokuMove, SudokuPosition> newGameRenderer(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        return new SudokuGameRenderer(mouseTracker);
    }
}
