package bge.game.chess.move;

import java.util.Locale;

import bge.game.chess.ChessFunctions;
import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionHasher;
import bge.game.chess.fen.ForsythEdwardsNotation;

public class PawnPromotionMove implements IChessMove {
    private final BasicChessMove basicMove;
    private final int promotion;
    private final int pawn;

    public PawnPromotionMove(BasicChessMove basicMove, int promotion, int pawn) {
        this.basicMove = basicMove;
        this.promotion = promotion;
        this.pawn = pawn;
    }

    @Override
    public void movePieces(ChessPosition position) {
        position.squares[basicMove.to] = promotion;
        position.squares[basicMove.from] = UNPLAYED;
    }

    @Override
    public void unMovePieces(ChessPosition position) {
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
    public long getZobristHash(ChessPosition position) {
        long zobristHash = ChessPositionHasher.PIECE_POSITION_HASHES[position.squares[basicMove.from]][basicMove.from] // remove from
                ^ ChessPositionHasher.PIECE_POSITION_HASHES[promotion][basicMove.to]; // add promotion
        return basicMove.pieceCaptured == UNPLAYED ? zobristHash
                : zobristHash ^ ChessPositionHasher.PIECE_POSITION_HASHES[basicMove.pieceCaptured][basicMove.to];
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
