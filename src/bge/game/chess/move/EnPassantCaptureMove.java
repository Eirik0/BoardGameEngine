package bge.game.chess.move;

import bge.game.chess.ChessFunctions;
import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionHasher;

public class EnPassantCaptureMove implements IChessMove {
    private final BasicChessMove basicMove;
    private final int captureSquare;

    public EnPassantCaptureMove(BasicChessMove basicMove, int captureSquare) {
        this.basicMove = basicMove;
        this.captureSquare = captureSquare;
    }

    @Override
    public void applyMove(ChessPosition position) {
        basicMove.applyMove(position);

        // Assume we (incorrectly) hashed out the captured piece from the dest square
        position.zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[basicMove.pieceCaptured][basicMove.getTo()];
        position.zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[basicMove.pieceCaptured][captureSquare];
        position.squares[captureSquare] = UNPLAYED;
    }

    @Override
    public void unapplyMove(ChessPosition position) {
        position.squares[captureSquare] = basicMove.pieceCaptured;
        position.squares[basicMove.from] = position.squares[basicMove.to];
        position.squares[basicMove.to] = UNPLAYED;
    }

    @Override
    public void updateMaterial(ChessPosition position) {
        ChessFunctions.updatePiece(position, basicMove.from, basicMove.to, PAWN, position.currentPlayer);
        position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] - ChessFunctions.getPieceScore(basicMove.pieceCaptured);
        ChessFunctions.removePiece(position, captureSquare, basicMove.pieceCaptured, position.otherPlayer);
    }

    @Override
    public void unupdateMaterial(ChessPosition position) {
        ChessFunctions.updatePiece(position, basicMove.to, basicMove.from, PAWN, position.currentPlayer);
        position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] + ChessFunctions.getPieceScore(basicMove.pieceCaptured);
        ChessFunctions.addPiece(position, captureSquare, basicMove.pieceCaptured, position.otherPlayer);
    }

    @Override
    public int getEnPassantSquare() {
        return NO_SQUARE;
    }

    @Override
    public int getPieceCaptured() {
        return basicMove.pieceCaptured;
    }

    @Override
    public int getFrom() {
        return basicMove.from;
    }

    @Override
    public int getTo() {
        return basicMove.to;
    }

    public int getCaptureSquare() {
        return captureSquare;
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
        EnPassantCaptureMove other = (EnPassantCaptureMove) obj;
        return basicMove.equals(other.basicMove);
    }

    @Override
    public String toString() {
        return basicMove.toString();
    }
}
