package game.chess;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import game.TwoPlayers;

public class ChessEvaluationConstantsTest implements ChessEvaluationConstants {
	@Test
	public void testPawns() {
		testArray(PAWN_SCORES[TwoPlayers.PLAYER_1], PAWN_SCORES[TwoPlayers.PLAYER_2]);
	}

	@Test
	public void testKnight() {
		testArray(KNIGHT_SCORES[TwoPlayers.PLAYER_1], KNIGHT_SCORES[TwoPlayers.PLAYER_2]);
	}

	@Test
	public void testBishop() {
		testArray(BISHOP_SCORES[TwoPlayers.PLAYER_1], BISHOP_SCORES[TwoPlayers.PLAYER_2]);
	}

	@Test
	public void testRook() {
		testArray(ROOK_SCORES[TwoPlayers.PLAYER_1], ROOK_SCORES[TwoPlayers.PLAYER_2]);
	}

	@Test
	public void testQueen() {
		testArray(QUEEN_SCORES[TwoPlayers.PLAYER_1], QUEEN_SCORES[TwoPlayers.PLAYER_2]);
	}

	@Test
	public void testKing() {
		testArray(KING_SCORES[TwoPlayers.PLAYER_1], KING_SCORES[TwoPlayers.PLAYER_2]);
	}

	static void testArray(double[] whiteScores, double[] blackScores) {
		for (int i = 0; i < 12; ++i) {
			for (int j = 0; j < 10; ++j) {
				assertEquals(whiteScores[i * 10 + j], blackScores[110 - i * 10 + j], 0.001);
			}
		}
	}
}
