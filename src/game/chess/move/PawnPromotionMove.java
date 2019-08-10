package game.chess.move;

import java.util.Locale;

import game.chess.ChessFunctions;
import game.chess.ChessPosition;
import game.chess.fen.ForsythEdwardsNotation;

public class PawnPromotionMove implements IChessMove {
    private final BasicChessMove basicMove;
    public final int promotion;
    private final int pawn;

    public PawnPromotionMove(BasicChessMove basicMove, int promotion, int pawn) {
        this.basicMove = basicMove;
        this.promotion = promotion;
        this.pawn = pawn;
    }

    @Override
    public void applyMove(ChessPosition position) {
        position.squares[basicMove.to] = promotion;
        position.squares[basicMove.from] = UNPLAYED;
    }

    @Override
    public void unapplyMove(ChessPosition position) {
        position.squares[basicMove.from] = pawn;
        position.squares[basicMove.to] = basicMove.pieceCaptured;
    }

    @Override
    public void updateMaterial(ChessPosition position) {
        ChessFunctions.removePiece(position, basicMove.from, pawn, position.currentPlayer);
        ChessFunctions.addPiece(position, basicMove.to, promotion, position.currentPlayer);
        position.materialScore[position.currentPlayer] = position.materialScore[position.currentPlayer] + ChessFunctions.getPieceScore(promotion) - PAWN_SCORE;
        if (basicMove.pieceCaptured != 0) {
            position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] - ChessFunctions.getPieceScore(basicMove.pieceCaptured);
            ChessFunctions.removePiece(position, basicMove.to, basicMove.pieceCaptured, position.otherPlayer);
        }
    }

    @Override
    public void unupdateMaterial(ChessPosition position) {
        ChessFunctions.removePiece(position, basicMove.to, promotion, position.currentPlayer);
        ChessFunctions.addPiece(position, basicMove.from, pawn, position.currentPlayer);
        position.materialScore[position.currentPlayer] = position.materialScore[position.currentPlayer] - ChessFunctions.getPieceScore(promotion) + PAWN_SCORE;
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
        int prime = 31;
        return prime * (prime + basicMove.hashCode()) + promotion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PawnPromotionMove other = (PawnPromotionMove) obj;
        return basicMove.equals(other.basicMove) && promotion == other.promotion;
    }

    @Override
    public String toString() {
        return basicMove.toString() + ForsythEdwardsNotation.getPieceString(promotion).toLowerCase(Locale.ENGLISH);
    }
}
