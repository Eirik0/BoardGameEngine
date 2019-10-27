package bge.game.chess;

import java.awt.Color;

import bge.igame.Coordinate;
import bge.igame.player.TwoPlayers;

public interface ChessConstants {
    int BOARD_WIDTH = 8;
    int BOARD_ARRAY_SIZE = 120;

    int MAX_MOVES = 2048;
    int MAX_REASONABLE_DEPTH = 256;
    int MAX_PIECE_NUM = 10;

    int A_FILE = 7;
    int B_FILE = 6;
    int C_FILE = 5;
    int D_FILE = 4;
    int E_FILE = 3;
    int F_FILE = 2;
    int G_FILE = 1;
    int H_FILE = 0;

    int A1 = 28, A2 = 38, A7 = 88, A8 = 98;
    int B1 = 27, B2 = 37, B7 = 87, B8 = 97;
    int C1 = 26, C2 = 36, C7 = 86, C8 = 96;
    int D1 = 25, D2 = 35, D7 = 85, D8 = 95;
    int E1 = 24, E2 = 34, E7 = 84, E8 = 94;
    int F1 = 23, F2 = 33, F7 = 83, F8 = 93;
    int G1 = 22, G2 = 32, G7 = 82, G8 = 92;
    int H1 = 21, H2 = 31, H7 = 81, H8 = 91;
    int NO_SQUARE = -1;

    int PAWN = 1 << 2;
    int KNIGHT = 2 << 2;
    int BISHOP = 3 << 2;
    int ROOK = 4 << 2;
    int QUEEN = 5 << 2;
    int KING = 6 << 2;
    int PIECE_MASK = ~TwoPlayers.BOTH_PLAYERS;

    int UNPLAYED = TwoPlayers.UNPLAYED;
    int SENTINEL = TwoPlayers.PLAYER_1 | TwoPlayers.PLAYER_2;
    int WHITE_PAWN = TwoPlayers.PLAYER_1 | PAWN;
    int WHITE_KNIGHT = TwoPlayers.PLAYER_1 | KNIGHT;
    int WHITE_BISHOP = TwoPlayers.PLAYER_1 | BISHOP;
    int WHITE_ROOK = TwoPlayers.PLAYER_1 | ROOK;
    int WHITE_QUEEN = TwoPlayers.PLAYER_1 | QUEEN;
    int WHITE_KING = TwoPlayers.PLAYER_1 | KING;
    int BLACK_PAWN = TwoPlayers.PLAYER_2 | PAWN;
    int BLACK_KNIGHT = TwoPlayers.PLAYER_2 | KNIGHT;
    int BLACK_BISHOP = TwoPlayers.PLAYER_2 | BISHOP;
    int BLACK_ROOK = TwoPlayers.PLAYER_2 | ROOK;
    int BLACK_QUEEN = TwoPlayers.PLAYER_2 | QUEEN;
    int BLACK_KING = TwoPlayers.PLAYER_2 | KING;

    int NUM_PIECE_HASHES = BLACK_KING + 1;

    int[] ALL_PIECES = {
            UNPLAYED, // Used for en passant square as well
            WHITE_PAWN,
            WHITE_KNIGHT,
            WHITE_BISHOP,
            WHITE_ROOK,
            WHITE_QUEEN,
            WHITE_KING,
            BLACK_PAWN,
            BLACK_KNIGHT,
            BLACK_BISHOP,
            BLACK_ROOK,
            BLACK_QUEEN,
            BLACK_KING
    };

    int WHITE_KING_CASTLE = 1 << 0;
    int WHITE_QUEEN_CASTLE = 1 << 1;
    int BLACK_KING_CASTLE = 1 << 2;
    int BLACK_QUEEN_CASTLE = 1 << 3;
    int ALL_CASTLES = WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE | BLACK_KING_CASTLE | BLACK_QUEEN_CASTLE;
    int NOT_WHITE_KING_CASTLE = WHITE_QUEEN_CASTLE | BLACK_KING_CASTLE | BLACK_QUEEN_CASTLE;
    int NOT_WHITE_QUEEN_CASTLE = WHITE_KING_CASTLE | BLACK_KING_CASTLE | BLACK_QUEEN_CASTLE;
    int NOT_BLACK_KING_CASTLE = WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE | BLACK_QUEEN_CASTLE;
    int NOT_BLACK_QUEEN_CASTLE = WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE | BLACK_KING_CASTLE;
    int NOT_WHITE_CASTLE = BLACK_KING_CASTLE | BLACK_QUEEN_CASTLE;
    int NOT_BLACK_CASTLE = WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE;
    int NUM_CASTLES = ALL_CASTLES + 1;

    double PAWN_SCORE = 1;
    double KNIGHT_SCORE = 3;
    double BISHOP_SCORE = 3;
    double ROOK_SCORE = 5;
    double QUEEN_SCORE = 9;
    double INITIAL_MATERIAL_SCORE = 8 * PAWN_SCORE + 2 * KNIGHT_SCORE + 2 * BISHOP_SCORE + 2 * ROOK_SCORE + QUEEN_SCORE;

    int[] CASTLING_PERMISSIONS = {
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, NOT_WHITE_KING_CASTLE, ALL_CASTLES, ALL_CASTLES, NOT_WHITE_CASTLE, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, NOT_WHITE_QUEEN_CASTLE,
            ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, NOT_BLACK_KING_CASTLE, ALL_CASTLES, ALL_CASTLES, NOT_BLACK_CASTLE, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, NOT_BLACK_QUEEN_CASTLE,
            ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES,
            ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES, ALL_CASTLES
    };

    static int[] newInitialPosition() {
        return new int[] {
                SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL,
                SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL,
                SENTINEL, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, SENTINEL,
                SENTINEL, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, SENTINEL,
                SENTINEL, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, SENTINEL,
                SENTINEL, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, SENTINEL,
                SENTINEL, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, SENTINEL,
                SENTINEL, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, SENTINEL,
                SENTINEL, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, SENTINEL,
                SENTINEL, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, SENTINEL,
                SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL,
                SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL, SENTINEL
        };
    }

    static int[][] newInitialPawns() {
        return new int[][] { {},
            { A2, B2, C2, D2, E2, F2, G2, H2, NO_SQUARE, NO_SQUARE },
            { A7, B7, C7, D7, E7, F7, G7, H7, NO_SQUARE, NO_SQUARE }
        };
    }

    static int[][] newInitialKnights() {
        return new int[][] { {},
            { B1, G1, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE },
            { B8, G8, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE }
        };
    }

    static int[][] newInitialBishops() {
        return new int[][] { {},
            { C1, F1, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE },
            { C8, F8, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE }
        };
    }

    static int[][] newInitialRooks() {
        return new int[][] { {},
            { A1, H1, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE },
            { A8, H8, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE }
        };
    }

    static int[][] newInitialQueens() {
        return new int[][] { {},
            { D1, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE },
            { D8, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE }
        };
    }

    static int[][] newInitialPieces() {
        return new int[][] {
            {},
            { NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE },
            { NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE, NO_SQUARE }
        };
    }

    static int[] newInitialNumPawns() {
        return new int[] { 0, 8, 8 };
    }

    static int[] newInitialNumKnights() {
        return new int[] { 0, 2, 2 };
    }

    static int[] newInitialNumBishops() {
        return new int[] { 0, 2, 2 };
    }

    static int[] newInitialNumRooks() {
        return new int[] { 0, 2, 2 };
    }

    static int[] newInitialNumQueens() {
        return new int[] { 0, 1, 1 };
    }

    static int[] newInitialKingSquares() {
        return new int[] { NO_SQUARE, E1, E8 };
    }

    static double[] newInitialMaterialScore() {
        return new double[] { 0, INITIAL_MATERIAL_SCORE, INITIAL_MATERIAL_SCORE };
    }

    int PAWN_OFFSET = 10;
    int[] KNIGHT_OFFSETS = { -21, -19, -12, -8, 8, 12, 19, 21 };
    int[] BISHOP_OFFSETS = { -11, -9, 9, 11 };
    int[] ROOK_OFFSETS = { -10, -1, 1, 10 };
    int[] QUEEN_OFFSETS = { -11, -10, -9, -1, 1, 9, 10, 11 };
    int[] KING_OFFSETS = { -11, -10, -9, -1, 1, 9, 10, 11 };

    Coordinate[] SQUARE_TO_COORDINATE = {
            null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null,
            null, coord(0, 0), coord(0, 1), coord(0, 2), coord(0, 3), coord(0, 4), coord(0, 5), coord(0, 6), coord(0, 7), null,
            null, coord(1, 0), coord(1, 1), coord(1, 2), coord(1, 3), coord(1, 4), coord(1, 5), coord(1, 6), coord(1, 7), null,
            null, coord(2, 0), coord(2, 1), coord(2, 2), coord(2, 3), coord(2, 4), coord(2, 5), coord(2, 6), coord(2, 7), null,
            null, coord(3, 0), coord(3, 1), coord(3, 2), coord(3, 3), coord(3, 4), coord(3, 5), coord(3, 6), coord(3, 7), null,
            null, coord(4, 0), coord(4, 1), coord(4, 2), coord(4, 3), coord(4, 4), coord(4, 5), coord(4, 6), coord(4, 7), null,
            null, coord(5, 0), coord(5, 1), coord(5, 2), coord(5, 3), coord(5, 4), coord(5, 5), coord(5, 6), coord(5, 7), null,
            null, coord(6, 0), coord(6, 1), coord(6, 2), coord(6, 3), coord(6, 4), coord(6, 5), coord(6, 6), coord(6, 7), null,
            null, coord(7, 0), coord(7, 1), coord(7, 2), coord(7, 3), coord(7, 4), coord(7, 5), coord(7, 6), coord(7, 7), null,
            null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null
    };

    static Coordinate coord(int x, int y) {
        return Coordinate.valueOf(y, x);
    }

    int[][] SQUARE_64_TO_SQUARE = {
            { 21, 22, 23, 24, 25, 26, 27, 28 },
            { 31, 32, 33, 34, 35, 36, 37, 38 },
            { 41, 42, 43, 44, 45, 46, 47, 48 },
            { 51, 52, 53, 54, 55, 56, 57, 58 },
            { 61, 62, 63, 64, 65, 66, 67, 68 },
            { 71, 72, 73, 74, 75, 76, 77, 78 },
            { 81, 82, 83, 84, 85, 86, 87, 88 },
            { 91, 92, 93, 94, 95, 96, 97, 98 },
    };

    Color DARK_SQUARE_COLOR = new Color(60, 179, 113);
    Color LIGHT_SQUARE_COLOR = new Color(176, 224, 230);

    Color DARK_PIECE_COLOR = Color.BLACK;
    Color LIGHT_PIECE_COLOR = Color.WHITE;

    Color LAST_MOVE_COLOR = Color.RED;
}
