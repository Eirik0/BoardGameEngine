package bge.game.ultimatetictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import bge.game.ArrayMoveList;
import bge.game.Coordinate;
import bge.game.MoveList;
import bge.game.TwoPlayers;
import bge.game.sudoku.SudokuPositionTest;
import bge.game.tictactoe.TicTacToeUtilities;

public class UltimateTicTacToePositionTest {
    @Test
    public void testUnmakeManyMoves() {
        UltimateTicTacToePosition position = new UltimateTicTacToePosition();
        List<Coordinate> moves = new ArrayList<>();
        List<UltimateTicTacToePosition> positions = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            positions.add(position.createCopy());
            checkEqual(position, positions.get(i), -1);
            MoveList<Coordinate> possibleMoves = new ArrayMoveList<>(UltimateTicTacToeGame.MAX_MOVES);
            position.getPossibleMoves(possibleMoves);
            Coordinate move = possibleMoves.get(0);
            moves.add(move);
            position.makeMove(move);
        }
        checkContainsWonBoard(position);
        Collections.reverse(moves);
        Collections.reverse(positions);
        for (int i = 0; i < 20; ++i) {
            position.unmakeMove(moves.get(i));
            checkEqual(positions.get(i), position, i);
        }
    }

    private static void checkContainsWonBoard(UltimateTicTacToePosition position) {
        boolean containsWonBoard = false;
        for (int i = 0; i < UltimateTicTacToePosition.BOARD_WIDTH; ++i) {
            if ((position.wonBoards & (TwoPlayers.BOTH_PLAYERS << (i << 2))) != TwoPlayers.UNPLAYED) {
                containsWonBoard = true;
            }
        }
        assertTrue(containsWonBoard, TicTacToeUtilities.boardToString(position.wonBoards));
    }

    private static void checkEqual(UltimateTicTacToePosition expected, UltimateTicTacToePosition actual, int moveNum) {
        assertEquals(expected.currentPlayer, actual.currentPlayer, "Move " + moveNum);
        assertEquals(expected.currentBoard, actual.currentBoard, "Move " + moveNum);
        assertEquals(TicTacToeUtilities.boardToString(expected.wonBoards), TicTacToeUtilities.boardToString(actual.wonBoards), "Move " + moveNum);
        assertEquals(expected.toString(), actual.toString(), "Move " + moveNum);
    }

    @Test
    public void testCannotMoveIntoFullBoard() {
        UltimateTicTacToePosition position = new UltimateTicTacToePosition();
        position.makeMove(Coordinate.valueOf(4, 4));
        position.makeMove(Coordinate.valueOf(4, 0));
        position.makeMove(Coordinate.valueOf(0, 4));
        position.makeMove(Coordinate.valueOf(4, 1));
        position.makeMove(Coordinate.valueOf(1, 4));
        position.makeMove(Coordinate.valueOf(4, 7));
        position.makeMove(Coordinate.valueOf(7, 2));
        position.makeMove(Coordinate.valueOf(2, 4));
        position.makeMove(Coordinate.valueOf(4, 2));
        position.makeMove(Coordinate.valueOf(2, 3));
        position.makeMove(Coordinate.valueOf(3, 4));
        position.makeMove(Coordinate.valueOf(4, 6));
        position.makeMove(Coordinate.valueOf(6, 5));
        position.makeMove(Coordinate.valueOf(5, 4));
        position.makeMove(Coordinate.valueOf(4, 8));
        position.makeMove(Coordinate.valueOf(8, 4));
        position.makeMove(Coordinate.valueOf(4, 3));
        position.makeMove(Coordinate.valueOf(3, 7));
        position.makeMove(Coordinate.valueOf(7, 4));
        position.makeMove(Coordinate.valueOf(4, 5));
        position.makeMove(Coordinate.valueOf(5, 6));
        position.makeMove(Coordinate.valueOf(6, 4));
        assertEquals(UltimateTicTacToePosition.ANY_BOARD, position.currentBoard);
    }

    @Test
    public void testCountPossibleMoves() {
        SudokuPositionTest.countPos(new UltimateTicTacToePosition(), 0, 81);
        SudokuPositionTest.countPos(new UltimateTicTacToePosition(), 1, 720);
        SudokuPositionTest.countPos(new UltimateTicTacToePosition(), 5, 4020960);
    }
}
