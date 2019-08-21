package bge.game.ultimatetictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UltimateTicTacToeUtilitiesTest {
    @Test
    public void testAllCombinations() {
        List<int[]> allBoards = getAllPossibleBoards(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 0);
        for (int[] board : allBoards) {
            int boardInt = boardToInt(board);
            assertEquals(countPossibleWinsSlower(board, 1), UltimateTicTacToeUtilities.countPossibleWins(boardInt, 1), Arrays.toString(board));
            assertEquals(countPossibleWinsSlower(board, 2), UltimateTicTacToeUtilities.countPossibleWins(boardInt, 2), Arrays.toString(board));
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testCountPossibleWinsSpeed() {
        List<int[]> allPossibleBoardsList = getAllPossibleBoards(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 0);
        int[][] allBoards = allPossibleBoardsList.toArray(new int[allPossibleBoardsList.size()][]);
        int timesToTest = 5000;

        long slowStart = System.nanoTime();
        for (int j = 0; j < allBoards.length; ++j) {
            int[] boardj = allBoards[j];
            for (int i = 0; i < timesToTest; ++i) {
                int one = countPossibleWinsSlower(boardj, 1);
                int two = countPossibleWinsSlower(boardj, 2);
            }
        }

        int[] allBoardsInt = new int[allBoards.length];
        for (int i = 0; i < allBoards.length; ++i) {
            allBoardsInt[i] = boardToInt(allBoards[i]);
        }
        long fastStart = System.nanoTime();
        for (int j = 0; j < allBoardsInt.length; ++j) {
            int boardj = allBoardsInt[j];
            for (int i = 0; i < timesToTest; ++i) {
                int one = UltimateTicTacToeUtilities.countPossibleWins(boardj, 1);
                int two = UltimateTicTacToeUtilities.countPossibleWins(boardj, 2);
            }
        }
        long fastTime = (System.nanoTime() - fastStart) / 1000000;

        long slowTime = (System.nanoTime() - slowStart) / 1000000;
        System.out.println("slower = " + slowTime + "ms, faster = " + fastTime + "ms, diff = " + (slowTime - fastTime));
    }

    private static int boardToInt(int[] board) {
        int boardInt0 = board[0] << 0;
        int boardInt1 = board[1] << 2;
        int boardInt2 = board[2] << 4;
        int boardInt3 = board[3] << 6;
        int boardInt4 = board[4] << 8;
        int boardInt5 = board[5] << 10;
        int boardInt6 = board[6] << 12;
        int boardInt7 = board[7] << 14;
        int boardInt8 = board[8] << 16;
        return boardInt0 | boardInt1 | boardInt2 | boardInt3 | boardInt4 | boardInt5 | boardInt6 | boardInt7 | boardInt8;
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
