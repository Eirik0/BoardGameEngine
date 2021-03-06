package bge.game.chess;

public interface ChessEvaluationConstants {
    public static final double ZERO = 0.00;

    static final double[] WHITE_PAWN_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.10, 0.10, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.15, 0.15, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.10, 0.10, ZERO, ZERO, ZERO, ZERO,
            ZERO, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, ZERO,
            ZERO, 0.50, 0.50, 0.50, 0.50, 0.50, 0.50, 0.50, 0.50, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };
    static final double[] BLACK_PAWN_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, 0.50, 0.50, 0.50, 0.50, 0.50, 0.50, 0.50, 0.50, ZERO,
            ZERO, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.10, 0.10, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.15, 0.15, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.10, 0.10, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] WHITE_KNIGHT_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.20, ZERO,
            ZERO, -0.10, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, 0.05, ZERO, ZERO, 0.05, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, 0.05, 0.10, 0.10, 0.05, ZERO, -0.10, ZERO,
            ZERO, -0.10, 0.05, 0.15, 0.15, 0.15, 0.15, 0.05, -0.10, ZERO,
            ZERO, -0.10, ZERO, 0.05, 0.05, 0.05, 0.05, ZERO, -0.10, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.20, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] BLACK_KNIGHT_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.20, ZERO,
            ZERO, -0.10, ZERO, 0.05, 0.05, 0.05, 0.05, ZERO, -0.10, ZERO,
            ZERO, -0.10, 0.05, 0.15, 0.15, 0.15, 0.15, 0.05, -0.10, ZERO,
            ZERO, -0.10, ZERO, 0.05, 0.10, 0.10, 0.05, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, 0.05, ZERO, ZERO, 0.05, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.20, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] WHITE_BISHOP_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.20, ZERO,
            ZERO, -0.10, 0.05, ZERO, 0.05, 0.05, ZERO, 0.05, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, 0.10, 0.10, 0.10, 0.10, ZERO, -0.10, ZERO,
            ZERO, -0.10, 0.10, 0.05, 0.10, 0.10, 0.05, 0.10, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.20, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] BLACK_BISHOP_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.20, ZERO,
            ZERO, -0.10, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, 0.10, 0.05, 0.10, 0.10, 0.05, 0.10, -0.10, ZERO,
            ZERO, -0.10, ZERO, 0.10, 0.10, 0.10, 0.10, ZERO, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, 0.05, ZERO, 0.05, 0.05, ZERO, 0.05, -0.10, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.20, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] WHITE_ROOK_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, -0.10, -0.10, 0.05, 0.10, 0.10, 0.05, -0.10, -0.10, ZERO,
            ZERO, -0.10, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, 0.10, 0.20, 0.20, 0.20, 0.20, 0.20, 0.20, 0.10, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] BLACK_ROOK_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, 0.10, 0.20, 0.20, 0.20, 0.20, 0.20, 0.20, 0.10, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.10, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.10, ZERO,
            ZERO, -0.10, -0.10, 0.05, 0.10, 0.10, 0.05, -0.10, -0.10, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] WHITE_QUEEN_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.05, -0.05, -0.10, -0.10, -0.20, ZERO,
            ZERO, -0.05, ZERO, 0.10, 0.05, 0.05, 0.10, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, 0.10, 0.20, 0.20, 0.20, 0.20, 0.20, 0.20, 0.10, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] BLACK_QUEEN_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, 0.10, 0.20, 0.20, 0.20, 0.20, 0.20, 0.20, 0.10, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, -0.05, ZERO,
            ZERO, -0.05, ZERO, 0.10, 0.05, 0.05, 0.10, ZERO, -0.05, ZERO,
            ZERO, -0.20, -0.10, -0.10, -0.05, -0.05, -0.10, -0.10, -0.20, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] WHITE_KING_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, -0.05, 0.15, -0.05, -0.05, -0.05, 0.10, ZERO, -0.05, ZERO,
            ZERO, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    static final double[] BLACK_KING_SCORES = {
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, 0.05, 0.05, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, -0.10, ZERO,
            ZERO, -0.05, 0.15, -0.05, -0.05, -0.05, 0.10, ZERO, -0.05, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO
    };

    public double[][] PAWN_SCORES = { {}, WHITE_PAWN_SCORES, BLACK_PAWN_SCORES };
    public double[][] KNIGHT_SCORES = { {}, WHITE_KNIGHT_SCORES, BLACK_KNIGHT_SCORES };
    public double[][] BISHOP_SCORES = { {}, WHITE_BISHOP_SCORES, BLACK_BISHOP_SCORES };
    public double[][] ROOK_SCORES = { {}, WHITE_ROOK_SCORES, BLACK_ROOK_SCORES };
    public double[][] QUEEN_SCORES = { {}, WHITE_QUEEN_SCORES, BLACK_QUEEN_SCORES };
    public double[][] KING_SCORES = { {}, WHITE_KING_SCORES, BLACK_KING_SCORES };
}
