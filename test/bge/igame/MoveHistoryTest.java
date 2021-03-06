package bge.igame;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import bge.game.chess.ChessGame;
import bge.game.chess.ChessPosition;
import bge.game.chess.move.IChessMove;
import bge.igame.MoveHistory.MoveIndex;

public class MoveHistoryTest {
    private static MoveHistory<IChessMove> createMoveHistory(int movesToMake) {
        ChessPosition chessPosition = new ChessPosition();
        MoveHistory<IChessMove> moveHistory = new MoveHistory<>(new ChessGame().getNumberOfPlayers());
        makeMoves(chessPosition, moveHistory, ChessGame.MAX_MOVES, movesToMake);
        return moveHistory;
    }

    private static <M> void makeMoves(IPosition<M> position, MoveHistory<M> moveHistory, int maxMoves, int movesToMake) {
        moveHistory.addMove(null, -1);
        for (int i = 0; i < movesToMake; ++i) {
            int playerNum = position.getCurrentPlayer();
            M move = getMove(position, 0, maxMoves);
            position.makeMove(move);
            moveHistory.addMove(move, playerNum);
        }
    }

    private static <M> M getMove(IPosition<M> position, int index, int maxMoves) {
        MoveList<M> moveList = new ArrayMoveList<>(maxMoves);
        position.getPossibleMoves(moveList);
        M move = moveList.get(index);
        return move;
    }

    private static <M> void checkMove(MoveHistory<M> moveHistory, int index, boolean... expectedPlayers) {
        M[] moves = moveHistory.getMoveHistoryListCopy().get(index).moves;
        boolean[] actualPlayers = new boolean[moves.length];
        for (int i = 0; i < moves.length; ++i) {
            actualPlayers[i] = (moves[i] != null);
        }
        assertArrayEquals(expectedPlayers, actualPlayers);
    }

    private static <M> void checkIndex(MoveHistory<M> moveHistory, MoveIndex expectedSelectedMoveIndex, MoveIndex expectedMaxMoveIndex) {
        assertEquals(expectedSelectedMoveIndex, moveHistory.selectedMoveIndex);
        assertEquals(expectedMaxMoveIndex, moveHistory.maxMoveIndex);
    }

    @Test
    public void testAddOneMove() {
        MoveHistory<IChessMove> moveHistory = createMoveHistory(1);
        assertEquals(1, moveHistory.getMoveHistoryListCopy().size());
        checkMove(moveHistory, 0, true, false);
        checkIndex(moveHistory, new MoveIndex(0, 0), new MoveIndex(0, 0));
    }

    @Test
    public void testAddTwoMove() {
        MoveHistory<IChessMove> moveHistory = createMoveHistory(2);
        assertEquals(1, moveHistory.getMoveHistoryListCopy().size());
        checkMove(moveHistory, 0, true, true);
        checkIndex(moveHistory, new MoveIndex(0, 1), new MoveIndex(0, 1));
    }

    @Test
    public void testAddThreeMove() {
        MoveHistory<IChessMove> moveHistory = createMoveHistory(3);
        assertEquals(2, moveHistory.getMoveHistoryListCopy().size());
        checkMove(moveHistory, 0, true, true);
        checkMove(moveHistory, 1, true, false);
        checkIndex(moveHistory, new MoveIndex(1, 0), new MoveIndex(1, 0));
    }

    @Test
    public void testSelectMove() {
        MoveHistory<IChessMove> moveHistory = createMoveHistory(5);
        assertEquals(3, moveHistory.getMoveHistoryListCopy().size());
        moveHistory.setPositionFromHistory(new ChessPosition(), 1, 1);
        checkMove(moveHistory, 0, true, true);
        checkMove(moveHistory, 1, true, true);
        checkMove(moveHistory, 2, true, false);
        checkIndex(moveHistory, new MoveIndex(1, 1), new MoveIndex(2, 0));
    }

    @Test
    public void testSelectMoveAndMakeSameMove() {
        MoveHistory<IChessMove> moveHistory = createMoveHistory(5);
        assertEquals(3, moveHistory.getMoveHistoryListCopy().size());
        ChessPosition position = new ChessPosition();
        moveHistory.setPositionFromHistory(position, 1, 0);
        IChessMove move = getMove(position, 0, ChessGame.MAX_MOVES);
        moveHistory.addMove(move, position.getCurrentPlayer());
        assertEquals(3, moveHistory.getMoveHistoryListCopy().size());
        checkMove(moveHistory, 0, true, true);
        checkMove(moveHistory, 1, true, true);
        checkMove(moveHistory, 2, true, false);
        checkIndex(moveHistory, new MoveIndex(1, 1), new MoveIndex(2, 0));
    }

    @Test
    public void testSelectMoveAndMakeDifferentMove() {
        MoveHistory<IChessMove> moveHistory = createMoveHistory(5);
        assertEquals(3, moveHistory.getMoveHistoryListCopy().size());
        ChessPosition position = new ChessPosition();
        moveHistory.setPositionFromHistory(position, 1, 0);
        IChessMove move = getMove(position, 1, ChessGame.MAX_MOVES);
        moveHistory.addMove(move, position.getCurrentPlayer());
        assertEquals(2, moveHistory.getMoveHistoryListCopy().size());
        checkMove(moveHistory, 0, true, true);
        checkMove(moveHistory, 1, true, true);
        checkIndex(moveHistory, new MoveIndex(1, 1), new MoveIndex(1, 1));
    }

    @Test
    public void testPlayerIndexGoesBackwards() {
        MoveHistory<String> moveHistory = new MoveHistory<>(3);
        moveHistory.addMove("1", 1);
        moveHistory.addMove("2", 2);
        moveHistory.addMove("3", 3);
        checkIndex(moveHistory, new MoveIndex(0, 2), new MoveIndex(0, 2));
        checkMove(moveHistory, 0, true, true, true);
        moveHistory.addMove("1", 1);
        moveHistory.addMove("2", 2);
        moveHistory.addMove("1", 1);
        checkIndex(moveHistory, new MoveIndex(2, 0), new MoveIndex(2, 0));
        checkMove(moveHistory, 1, true, true, false);
        checkMove(moveHistory, 2, true, false, false);
        moveHistory.addMove("1", 1);
        moveHistory.addMove("3", 3);
        moveHistory.addMove("2", 2);
        checkIndex(moveHistory, new MoveIndex(4, 1), new MoveIndex(4, 1));
        checkMove(moveHistory, 3, true, false, true);
        checkMove(moveHistory, 4, false, true, false);
    }
}
