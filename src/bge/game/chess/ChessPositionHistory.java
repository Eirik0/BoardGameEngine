package bge.game.chess;

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
        undoChessMoves[plyCount++] = new UndoChessMove(position.castleState, position.enPassantSquare, position.halfMoveClock, position.zobristHash);
    }

    public void unmakeMove(ChessPosition position) {
        UndoChessMove undoChessMove = undoChessMoves[--plyCount];
        position.castleState = undoChessMove.priorCastleState;
        position.enPassantSquare = undoChessMove.priorEnPassantSquare;
        position.halfMoveClock = undoChessMove.priorHalfMoveClock;
        position.zobristHash = undoChessMove.zobristHash;
    }

    public ChessPositionHistory createCopy() {
        UndoChessMove[] undoChessMovesCopy = new UndoChessMove[MAX_MOVES];
        System.arraycopy(undoChessMoves, 0, undoChessMovesCopy, 0, plyCount);
        return new ChessPositionHistory(undoChessMovesCopy, plyCount);
    }

    static class UndoChessMove {
        final int priorCastleState;
        final int priorEnPassantSquare;
        final int priorHalfMoveClock;
        final long zobristHash;

        public UndoChessMove(int priorCastleState, int priorEnPassantSquare, int priorHalfMoveClock, long zobristHash) {
            this.priorCastleState = priorCastleState;
            this.priorEnPassantSquare = priorEnPassantSquare;
            this.priorHalfMoveClock = priorHalfMoveClock;
            this.zobristHash = zobristHash;
        }
    }
}
