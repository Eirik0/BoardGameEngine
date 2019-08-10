package bge.game.chess.move;

import bge.game.chess.ChessConstants;
import bge.game.chess.ChessPosition;

public interface IChessMove extends ChessConstants {
    public void applyMove(ChessPosition position);

    public void unapplyMove(ChessPosition position);

    public void updateMaterial(ChessPosition position);

    public void unupdateMaterial(ChessPosition position);

    public int getEnPassantSquare();

    public int getPieceCaptured();

    public int getFrom();

    public int getTo();
}
