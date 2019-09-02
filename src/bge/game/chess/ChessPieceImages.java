package bge.game.chess;

import bge.main.PieceImages;
import gt.component.JavaGameImage;
import gt.gameentity.IGameImage;

public class ChessPieceImages implements ChessConstants {
    public final JavaGameImage whitePawn;
    public final JavaGameImage whiteKnight;
    public final JavaGameImage whiteBishop;
    public final JavaGameImage whiteRook;
    public final JavaGameImage whiteQueen;
    public final JavaGameImage whiteKing;
    public final JavaGameImage blackPawn;
    public final JavaGameImage blackKnight;
    public final JavaGameImage blackBishop;
    public final JavaGameImage blackRook;
    public final JavaGameImage blackQueen;
    public final JavaGameImage blackKing;

    private static ChessPieceImages instance;

    public static synchronized ChessPieceImages getInstance() {
        if (instance == null) {
            instance = new ChessPieceImages();
        }
        return instance;
    }

    private ChessPieceImages() {
        whitePawn = loadImage("pawn16", true);
        whiteKnight = loadImage("knight16", true);
        whiteBishop = loadImage("bishop16", true);
        whiteRook = loadImage("rook16", true);
        whiteQueen = loadImage("queen16", true);
        whiteKing = loadImage("king16", true);
        blackPawn = loadImage("pawn16", false);
        blackKnight = loadImage("knight16", false);
        blackBishop = loadImage("bishop16", false);
        blackRook = loadImage("rook16", false);
        blackQueen = loadImage("queen16", false);
        blackKing = loadImage("king16", false);
    }

    private static JavaGameImage loadImage(String name, boolean white) {
        return PieceImages.toJavaGameImage(PieceImages.loadImage("chess", name), white ? LIGHT_PIECE_COLOR : DARK_PIECE_COLOR);
    }

    public IGameImage getPieceImage(int piece) {
        switch (piece) {
        case BLACK_KING:
            return blackKing;
        case BLACK_QUEEN:
            return blackQueen;
        case BLACK_ROOK:
            return blackRook;
        case BLACK_BISHOP:
            return blackBishop;
        case BLACK_KNIGHT:
            return blackKnight;
        case BLACK_PAWN:
            return blackPawn;
        case UNPLAYED:
            return null;
        case WHITE_PAWN:
            return whitePawn;
        case WHITE_KNIGHT:
            return whiteKnight;
        case WHITE_BISHOP:
            return whiteBishop;
        case WHITE_ROOK:
            return whiteRook;
        case WHITE_QUEEN:
            return whiteQueen;
        case WHITE_KING:
            return whiteKing;
        default:
            throw new UnsupportedOperationException("Unknown piece: " + piece);
        }
    }
}
