package game.ultimatetictactoe;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class UltimateTicTacToeUtilitiesTest {
	@Test
	public void testAllCombinations() {
		List<int[]> allBoards = getAllPossibleBoards(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 0);
		for (int[] board : allBoards) {
			assertEquals(Arrays.toString(board), countPossibleWinsSlower(board, 1), UltimateTicTacToeUtilities.countPossibleWins(board, 1));
			assertEquals(Arrays.toString(board), countPossibleWinsSlower(board, 2), UltimateTicTacToeUtilities.countPossibleWins(board, 2));
		}
	}

	@Test
	public void testCountPossibleWinsSpeed() {
		List<int[]> allPossibleBoardsList = getAllPossibleBoards(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 0);
		int[][] allBoards = allPossibleBoardsList.toArray(new int[allPossibleBoardsList.size()][]);
		int timesToTest = 5000;

		long slowStart = System.nanoTime();
		for (int j = 0; j < allBoards.length; ++j) {
			int[] boardj = allBoards[j];
			for (int i = 0; i < timesToTest; ++i) {
				countPossibleWinsSlower(boardj, 1);
				countPossibleWinsSlower(boardj, 2);
			}
		}

		long fastStart = System.nanoTime();
		for (int j = 0; j < allBoards.length; ++j) {
			int[] boardj = allBoards[j];
			for (int i = 0; i < timesToTest; ++i) {
				UltimateTicTacToeUtilities.countPossibleWins(boardj, 1);
				UltimateTicTacToeUtilities.countPossibleWins(boardj, 2);
			}
		}
		long fastTime = (System.nanoTime() - fastStart) / 1000000;

		long slowTime = (System.nanoTime() - slowStart) / 1000000;
		System.out.println("slower = " + slowTime + "ms, faster = " + fastTime + "ms, diff = " + (slowTime - fastTime));
	}

	private static List<int[]> getAllPossibleBoards(int[] board, int pos) {
		List<int[]> boards = new ArrayList<>();
		boards.add(board);
		if (pos < 9) {
			int[] copy1 = new int[9];
			System.arraycopy(board, 0, copy1, 0, 9);
			copy1[pos] = 1;
			boards.addAll(getAllPossibleBoards(copy1, pos + 1));

			int[] copy2 = new int[9];
			System.arraycopy(board, 0, copy2, 0, 9);
			copy2[pos] = 2;
			boards.addAll(getAllPossibleBoards(copy2, pos + 1));
		}
		return boards;
	}

	private static int countPossibleWinsSlower(int[] board, int otherPlayer) {
		int possibleWins = 8;
		// 0 1 2
		// 3 4 5
		// 6 7 8
		if (board[0] == otherPlayer || board[1] == otherPlayer || board[2] == otherPlayer) {
			--possibleWins;
		}
		if (board[3] == otherPlayer || board[4] == otherPlayer || board[5] == otherPlayer) {
			--possibleWins;
		}
		if (board[6] == otherPlayer || board[7] == otherPlayer || board[8] == otherPlayer) {
			--possibleWins;
		}
		if (board[0] == otherPlayer || board[3] == otherPlayer || board[6] == otherPlayer) {
			--possibleWins;
		}
		if (board[1] == otherPlayer || board[4] == otherPlayer || board[7] == otherPlayer) {
			--possibleWins;
		}
		if (board[2] == otherPlayer || board[5] == otherPlayer || board[8] == otherPlayer) {
			--possibleWins;
		}
		if (board[0] == otherPlayer || board[4] == otherPlayer || board[8] == otherPlayer) {
			--possibleWins;
		}
		if (board[2] == otherPlayer || board[4] == otherPlayer || board[6] == otherPlayer) {
			--possibleWins;
		}
		return possibleWins;
	}
}
