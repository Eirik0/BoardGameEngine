package game.chess.move;

import game.chess.ChessFunctions;
import game.chess.ChessPosition;

public class KingMove implements IChessMove {
    private final BasicChessMove basicMove;

    public KingMove(BasicChessMove basicMove) {
        this.basicMove = basicMove;
    }

    @Override
    public void applyMove(ChessPosition position) {
        basicMove.applyMove(position);
        position.kingSquares[position.currentPlayer] = basicMove.to;
    }

    @Override
    public void unapplyMove(ChessPosition position) {
        basicMove.unapplyMove(position);
        position.kingSquares[position.currentPlayer] = basicMove.from;
    }

    @Override
    public void updateMaterial(ChessPosition position) {
        if (basicMove.pieceCaptured != 0) {
            position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] - ChessFunctions.getPieceScore(basicMove.pieceCaptured);
            ChessFunctions.removePiece(position, basicMove.to, basicMove.pieceCaptured, position.otherPlayer);
        }
    }

    @Override
    public void unupdateMaterial(ChessPosition position) {
        if (basicMove.pieceCaptured != 0) {
            position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] + ChessFunctions.getPieceScore(basicMove.pieceCaptured);
            ChessFunctions.addPiece(position, basicMove.to, basicMove.pieceCaptured, position.otherPlayer);
        }
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
        KingMove other = (KingMove) obj;
        return basicMove.equals(other.basicMove);
    }

    @Override
    public String toString() {
        return basicMove.toString();
    }
}
