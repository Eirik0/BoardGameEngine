package game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import game.MoveHistory.MoveIndex;
import game.chess.ChessGame;
import game.chess.ChessPosition;
import game.chess.move.IChessMove;

public class MoveHistoryTest {
	private static MoveHistory<IChessMove, ChessPosition> createMoveHistory(int movesToMake) {
		ChessPosition chessPosition = new ChessPosition();
		MoveHistory<IChessMove, ChessPosition> moveHistory = new MoveHistory<>(TwoPlayers.NUMBER_OF_PLAYERS);
		makeMoves(chessPosition, moveHistory, ChessGame.MAX_MOVES, movesToMake);
		return moveHistory;
	}

	private static <M, P extends IPosition<M, P>> void makeMoves(P position, MoveHistory<M, P> moveHistory, int maxMoves, int movesToMake) {
		moveHistory.addMove(null, -1);
		for (int i = 0; i < movesToMake; ++i) {
			int playerNum = position.getCurrentPlayer();
			M move = getMove(position, 0, maxMoves);
			position.makeMove(move);
			moveHistory.addMove(move, playerNum);
		}
	}

	private static <P extends IPosition<M, P>, M> M getMove(P position, int index, int maxMoves) {
		MoveList<M> moveList = new ArrayMoveList<>(maxMoves);
		position.getPossibleMoves(moveList);
		M move = moveList.get(index);
		return move;
	}

	private static <M, P extends IPosition<M, P>> void checkMove(MoveHistory<M, P> moveHistory, int index, boolean p1Expected, boolean p2Expected) {
		M[] moves = moveHistory.getMoveHistoryListCopy().get(index).moves;
		assertEquals("P1", p1Expected, moves[0] != null);
		assertEquals("P2", p2Expected, moves[1] != null);
	}

	private static <M, P extends IPosition<M, P>> void checkIndex(MoveHistory<M, P> moveHistory, MoveIndex expectedSelectedMoveIndex, MoveIndex expectedMaxMoveIndex) {
		assertEquals(expectedSelectedMoveIndex, moveHistory.selectedMoveIndex);
		assertEquals(expectedMaxMoveIndex, moveHistory.maxMoveIndex);
	}

	@Test
	public void testAddOneMove() {
		MoveHistory<IChessMove, ChessPosition> moveHistory = createMoveHistory(1);
		assertEquals(1, moveHistory.getMoveHistoryListCopy().size());
		checkMove(moveHistory, 0, true, false);
		checkIndex(moveHistory, new MoveIndex(0, 0), new MoveIndex(0, 0));
	}

	@Test
	public void testAddTwoMove() {
		MoveHistory<IChessMove, ChessPosition> moveHistory = createMoveHistory(2);
		assertEquals(1, moveHistory.getMoveHistoryListCopy().size());
		checkMove(moveHistory, 0, true, true);
		checkIndex(moveHistory, new MoveIndex(0, 1), new MoveIndex(0, 1));
	}

	@Test
	public void testAddThreeMove() {
		MoveHistory<IChessMove, ChessPosition> moveHistory = createMoveHistory(3);
		assertEquals(2, moveHistory.getMoveHistoryListCopy().size());
		checkMove(moveHistory, 0, true, true);
		checkMove(moveHistory, 1, true, false);
		checkIndex(moveHistory, new MoveIndex(1, 0), new MoveIndex(1, 0));
	}

	@Test
	public void testSelectMove() {
		MoveHistory<IChessMove, ChessPosition> moveHistory = createMoveHistory(5);
		assertEquals(3, moveHistory.getMoveHistoryListCopy().size());
		moveHistory.setPositionFromHistory(new ChessPosition(), 1, 1);
		checkMove(moveHistory, 0, true, true);
		checkMove(moveHistory, 1, true, true);
		checkMove(moveHistory, 2, true, false);
		checkIndex(moveHistory, new MoveIndex(1, 1), new MoveIndex(2, 0));
	}

	@Test
	public void testSelectMoveAndMakeSameMove() {
		MoveHistory<IChessMove, ChessPosition> moveHistory = createMoveHistory(5);
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
		MoveHistory<IChessMove, ChessPosition> moveHistory = createMoveHistory(5);
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
}
