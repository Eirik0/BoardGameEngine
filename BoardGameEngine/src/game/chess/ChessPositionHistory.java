package game.chess;

import game.Coordinate;

public class ChessPositionHistory implements ChessConstants {
	public final UndoChessMove[] undoChessMoves;
	public int plyCount;

	public ChessPositionHistory() {
		this(new UndoChessMove[MAX_MOVES], 0);
	}

	public ChessPositionHistory(int plyCount) {
		this(new UndoChessMove[MAX_MOVES], plyCount);
	}

	private ChessPositionHistory(UndoChessMove[] undoChessMoves, int plyCount) {
		this.undoChessMoves = undoChessMoves;
		this.plyCount = plyCount;
	}

	public void saveState(ChessPosition position) {
		undoChessMoves[plyCount++] = new UndoChessMove(position.castleState, position.enPassantSquare, position.halfMoveClock);
	}

	public void unmakeMove(ChessPosition position) {
		UndoChessMove undoChessMove = undoChessMoves[--plyCount];
		position.castleState = undoChessMove.priorCastleState;
		position.enPassantSquare = undoChessMove.priorEnPassantSquare;
		position.halfMoveClock = undoChessMove.priorHalfMoveClock;
	}

	public ChessPositionHistory createCopy() {
		UndoChessMove[] undoChessMovesCopy = new UndoChessMove[MAX_MOVES];
		System.arraycopy(undoChessMoves, 0, undoChessMovesCopy, 0, plyCount);
		return new ChessPositionHistory(undoChessMovesCopy, plyCount);
	}

	static class UndoChessMove {
		final int priorCastleState;
		final Coordinate priorEnPassantSquare;
		final int priorHalfMoveClock;

		// XXX hash of position for 3 fold repetition check
		public UndoChessMove(int priorCastleState, Coordinate priorEnPassantSquare, int priorHalfMoveClock) {
			this.priorCastleState = priorCastleState;
			this.priorEnPassantSquare = priorEnPassantSquare;
			this.priorHalfMoveClock = priorHalfMoveClock;
		}
	}
}
