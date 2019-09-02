package bge.game.ultimatetictactoe;

import bge.gui.gamestate.IGameRenderer;
import bge.igame.Coordinate;
import bge.igame.IGame;
import bge.igame.player.TwoPlayers;
import gt.component.IMouseTracker;
import gt.gameentity.IGameImageDrawer;

public class UltimateTicTacToeGame implements IGame<Coordinate, UltimateTicTacToePosition> {
    public static final String NAME = "Ultimate Tic Tac Toe";
    public static final int MAX_MOVES = UltimateTicTacToePosition.BOARD_WIDTH * UltimateTicTacToePosition.BOARD_WIDTH;

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
    public UltimateTicTacToePosition newInitialPosition() {
        return new UltimateTicTacToePosition();
    }

    @Override
    public IGameRenderer<Coordinate, UltimateTicTacToePosition> newGameRenderer(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        return new UltimateTicTacToeGameRenderer(mouseTracker);
    }
}
