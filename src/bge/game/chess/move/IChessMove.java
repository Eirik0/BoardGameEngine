package bge.game.chess.move;

import bge.game.chess.ChessConstants;
import bge.game.chess.ChessPosition;

public interface IChessMove extends ChessConstants {
    void movePieces(ChessPosition position);

    void unMovePieces(ChessPosition position);

    void updateMaterial(ChessPosition position);

    void unupdateMaterial(ChessPosition position);

    long getZobristHash(ChessPosition position);

    int getEnPassantSquare();

    int getPieceCaptured();

    int getFrom();

    int getTo();
}
