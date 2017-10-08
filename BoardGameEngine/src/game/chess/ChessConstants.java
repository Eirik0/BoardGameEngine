package game.chess;

import java.awt.Color;

import game.TwoPlayers;

public interface ChessConstants {
	static final int BOARD_WIDTH = 8;

	static int RANK_1 = 0;
	static int RANK_2 = 1;
	static int RANK_7 = 6;
	static int RANK_8 = 7;

	static final int PAWN = 1 << 2;
	static final int KNIGHT = 2 << 2;
	static final int BISHOP = 4 << 2;
	static final int ROOK = 8 << 2;
	static final int QUEEN = 16 << 2;
	static final int KING = 32 << 2;

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

	static final Color DARK_SQUARE_COLOR = new Color(60, 179, 113);
	static final Color LIGHT_SQUARE_COLOR = new Color(176, 224, 230);

	static final Color DARK_PIECE_COLOR = Color.BLACK;
	static final Color LIGHT_PIECE_COLOR = Color.WHITE;
}
