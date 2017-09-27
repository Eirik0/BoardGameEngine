package game.chess;

import java.awt.Color;

import game.TwoPlayers;

interface ChessConstants {
	static final int BOARD_WIDTH = 8;

	static final int UNPLAYED = TwoPlayers.UNPLAYED;
	static final int WHITE_PAWN = 1;
	static final int WHITE_KNIGHT = 2;
	static final int WHITE_BISHOP = 3;
	static final int WHITE_ROOK = 4;
	static final int WHITE_QUEEN = 5;
	static final int WHITE_KING = 6;
	static final int BLACK_PAWN = -1;
	static final int BLACK_KNIGHT = -2;
	static final int BLACK_BISHOP = -3;
	static final int BLACK_ROOK = -4;
	static final int BLACK_QUEEN = -5;
	static final int BLACK_KING = -6;

	static final int[][] INITIAL_POSITION = {
			{ WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK },
			{ WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN },
			{ UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED },
			{ UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED },
			{ UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED },
			{ UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED, UNPLAYED },
			{ BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN },
			{ BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK }
	};

	static final Color DARK_SQUARE_COLOR = new Color(60, 179, 113);
	static final Color LIGHT_SQUARE_COLOR = new Color(176, 224, 230);

	static final Color DARK_PIECE_COLOR = Color.BLACK;
	static final Color LIGHT_PIECE_COLOR = Color.WHITE;
}
