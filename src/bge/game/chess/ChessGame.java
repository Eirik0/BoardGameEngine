package bge.game.chess;

import bge.game.chess.move.IChessMove;
import bge.igame.IGame;
import bge.igame.player.TwoPlayers;

public class ChessGame implements IGame<IChessMove> {
    public static final String NAME = "Chess";
    public static final int MAX_MOVES = 256;

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
    public ChessPosition newInitialPosition() {
        return new ChessPosition();
    }
}
