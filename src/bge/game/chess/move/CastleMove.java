package bge.game.chess.move;

import bge.game.chess.ChessFunctions;
import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionHasher;

public class CastleMove implements IChessMove {
    private final BasicChessMove basicMove;
    private final int rookFrom;
    private final int rookTo;

    public CastleMove(BasicChessMove basicMove, int rookFrom, int rookTo) {
        this.basicMove = basicMove;
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
    }

    @Override
    public void movePieces(ChessPosition position) {
        int king = position.squares[basicMove.from];
        int rook = position.squares[rookFrom];

        position.squares[basicMove.from] = UNPLAYED;
        position.squares[rookFrom] = UNPLAYED;
        position.squares[basicMove.to] = king;
        position.squares[rookTo] = rook;
        position.kingSquares[position.currentPlayer] = basicMove.to;
    }

    @Override
    public void unMovePieces(ChessPosition position) {
        int king = position.squares[basicMove.to];
        int rook = position.squares[rookTo];

        position.squares[basicMove.to] = UNPLAYED;
        position.squares[rookTo] = UNPLAYED;
        position.squares[basicMove.from] = king;
        position.squares[rookFrom] = rook;
        position.kingSquares[position.currentPlayer] = basicMove.from;
    }

    @Override
    public void updateMaterial(ChessPosition position) {
        ChessFunctions.updatePiece(position, rookFrom, rookTo, ROOK, position.currentPlayer);
    }

    @Override
    public void unupdateMaterial(ChessPosition position) {
        ChessFunctions.updatePiece(position, rookTo, rookFrom, ROOK, position.currentPlayer);
    }

    @Override
    public long getZobristHash(ChessPosition position) {
        int rook = position.squares[rookFrom];
        return basicMove.getZobristHash(position) ^ ChessPositionHasher.PIECE_POSITION_HASHES[rook][rookFrom] ^
                ChessPositionHasher.PIECE_POSITION_HASHES[rook][rookTo];
    }

    @Override
    public int getEnPassantSquare() {
        return NO_SQUARE;
    }

    @Override
    public int getPieceCaptured() {
        return UNPLAYED;
    }

    @Override
    public int getFrom() {
        return basicMove.from;
    }

    @Override
    public int getTo() {
        return basicMove.to;
    }

    @Override
    public int hashCode() {
        return basicMove.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CastleMove other = (CastleMove) obj;
        return basicMove.equals(other.basicMove);
    }

    @Override
    public String toString() {
        return rookFrom == H1 || rookFrom == H8 ? "o-o" : "o-o-o";
    }
}
