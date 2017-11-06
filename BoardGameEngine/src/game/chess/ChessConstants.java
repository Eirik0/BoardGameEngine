package game.chess;

import java.awt.Color;

import game.Coordinate;
import game.TwoPlayers;

public interface ChessConstants {
	static final int BOARD_WIDTH = 8;

	static final int MAX_MOVES = 2048;

	static final int RANK_1 = 0;
	static final int RANK_2 = 1;
	static final int RANK_7 = 6;
	static final int RANK_8 = 7;

	static final int A_FILE = 7;
	static final int B_FILE = 6;
	static final int C_FILE = 5;
	static final int D_FILE = 4;
	static final int E_FILE = 3;
	static final int F_FILE = 2;
	static final int G_FILE = 1;
	static final int H_FILE = 0;

	static final Coordinate A1 = Coordinate.valueOf(A_FILE, RANK_1);
	static final Coordinate A8 = Coordinate.valueOf(A_FILE, RANK_8);
	static final Coordinate E1 = Coordinate.valueOf(E_FILE, RANK_1);
	static final Coordinate E8 = Coordinate.valueOf(E_FILE, RANK_8);
	static final Coordinate H1 = Coordinate.valueOf(H_FILE, RANK_1);
	static final Coordinate H8 = Coordinate.valueOf(H_FILE, RANK_8);

	static final int PAWN = 1 << 2;
	static final int KNIGHT = 2 << 2;
	static final int BISHOP = 4 << 2;
	static final int ROOK = 8 << 2;
	static final int QUEEN = 16 << 2;
	static final int KING = 32 << 2;
	static final int ALL_PIECES = PAWN | KNIGHT | BISHOP | ROOK | QUEEN;

	static final int UNPLAYED = TwoPlayers.UNPLAYED;
	static final int WHITE_PAWN = TwoPlayers.PLAYER_1 | PAWN;
	static final int WHITE_KNIGHT = TwoPlayers.PLAYER_1 | KNIGHT;
	static final int WHITE_BISHOP = TwoPlayers.PLAYER_1 | BISHOP;
	static final int WHITE_ROOK = TwoPlayers.PLAYER_1 | ROOK;
	static final int WHITE_QUEEN = TwoPlayers.PLAYER_1 | QUEEN;
	static final int WHITE_KING = TwoPlayers.PLAYER_1 | KING;
	static final int BLACK_PAWN = TwoPlayers.PLAYER_2 | PAWN;
	static final int BLACK_KNIGHT = TwoPlayers.PLAYER_2 | KNIGHT;
	static final int BLACK_BISHOP = TwoPlayers.PLAYER_2 | BISHOP;
	static final int BLACK_ROOK = TwoPlayers.PLAYER_2 | ROOK;
	static final int BLACK_QUEEN = TwoPlayers.PLAYER_2 | QUEEN;
	static final int BLACK_KING = TwoPlayers.PLAYER_2 | KING;

	static final int WHITE_KING_CASTLE = 1 << 0;
	static final int WHITE_QUEEN_CASTLE = 1 << 1;
	static final int BLACK_KING_CASTLE = 1 << 2;
	static final int BLACK_QUEEN_CASTLE = 1 << 3;
	static final int INITIAL_CASTLE_STATE = WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE | BLACK_QUEEN_CASTLE | BLACK_KING_CASTLE;

	static final double PAWN_SCORE = 1;
	static final double KNIGHT_SCORE = 3;
	static final double BISHOP_SCORE = 3;
	static final double ROOK_SCORE = 5;
	static final double QUEEN_SCORE = 9;
	static final double INITIAL_MATERIAL_SCORE = 8 * PAWN_SCORE + 2 * KNIGHT_SCORE + 2 * BISHOP_SCORE + 2 * ROOK_SCORE + QUEEN_SCORE;

	static int[][] newInitialPosition() {
		return new int[][] {
				{ WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK },
				{ WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN },
				{ UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED },
				{ UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED },
				{ UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED },
				{ UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED },
				{ BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN },
				{ BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK }
		};
	}

	static Coordinate[] newInitialKingSquares() {
		return new Coordinate[] { null, E1, E8 };
	}

	static double[] newInitialMaterialScore() {
		return new double[] { 0, INITIAL_MATERIAL_SCORE, INITIAL_MATERIAL_SCORE };
	}

	static final Color DARK_SQUARE_COLOR = new Color(60, 179, 113);
	static final Color LIGHT_SQUARE_COLOR = new Color(176, 224, 230);

	static final Color DARK_PIECE_COLOR = Color.BLACK;
	static final Color LIGHT_PIECE_COLOR = Color.WHITE;

	static final Color LAST_MOVE_COLOR = Color.RED;
}
