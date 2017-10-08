package game.sudoku;

import java.util.ArrayList;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.ultimatetictactoe.UltimateTicTacToeUtilities;

public class SudokuPosition implements IPosition<SudokuMove, SudokuPosition> {
	static final int BOARD_WIDTH = 9;
	static final int UNPLAYED = 0;

	int[][] squares;
	int[][] rows;
	int[][] columns;

	public SudokuPosition() {
		this(new int[BOARD_WIDTH][BOARD_WIDTH], new int[BOARD_WIDTH][BOARD_WIDTH], new int[BOARD_WIDTH][BOARD_WIDTH]);
	}

	private SudokuPosition(int[][] squares, int[][] rows, int[][] columns) {
		this.squares = squares;
		this.rows = rows;
		this.columns = columns;
	}

	@Override
	public List<SudokuMove> getPossibleMoves() {
		List<SudokuMove> possibleMoves = new ArrayList<>();
		for (int n = 0; n < BOARD_WIDTH; ++n) {
			int[] square = squares[n];
			boolean[] canNotPlayOrig = new boolean[BOARD_WIDTH + 1];
			setCanNotPlay(canNotPlayOrig, square);
			for (int m = 0; m < BOARD_WIDTH; ++m) {
				if (square[m] == UNPLAYED) {
					boolean[] canNotPlay = new boolean[BOARD_WIDTH + 1];
					Coordinate boardXY = UltimateTicTacToeUtilities.getBoardXY(n, m);
					System.arraycopy(canNotPlayOrig, 1, canNotPlay, 1, BOARD_WIDTH);
					setCanNotPlay(canNotPlay, rows[boardXY.x]);
					setCanNotPlay(canNotPlay, columns[boardXY.y]);
					for (int i = 1; i < BOARD_WIDTH + 1; ++i) {
						if (!canNotPlay[i]) {
							possibleMoves.add(new SudokuMove(boardXY, i));
						}
					}
				}
			}
		}
		return possibleMoves;
	}

	private void setCanNotPlay(boolean[] canNotPlay, int[] square) {
		for (int m = 0; m < BOARD_WIDTH; ++m) {
			if (square[m] != 0) {
				canNotPlay[square[m]] = true;
			}
		}
	}

	@Override
	public int getCurrentPlayer() {
		return 1;
	}

	@Override
	public void makeMove(SudokuMove move) {
		Coordinate boardNM = UltimateTicTacToeUtilities.getBoardNM(move.coordinate.x, move.coordinate.y);
		squares[boardNM.x][boardNM.y] = move.number;
		columns[move.coordinate.y][move.coordinate.x] = move.number;
		rows[move.coordinate.x][move.coordinate.y] = move.number;
	}

	@Override
	public void unmakeMove(SudokuMove move) {
		Coordinate boardNM = UltimateTicTacToeUtilities.getBoardNM(move.coordinate.x, move.coordinate.y);
		squares[boardNM.x][boardNM.y] = UNPLAYED;
		columns[move.coordinate.y][move.coordinate.x] = UNPLAYED;
		rows[move.coordinate.x][move.coordinate.y] = UNPLAYED;
	}

	@Override
	public SudokuPosition createCopy() {
		int[][] squaresCopy = new int[BOARD_WIDTH][BOARD_WIDTH];
		for (int y = 0; y < BOARD_WIDTH; y++) {
			System.arraycopy(squares[y], 0, squaresCopy[y], 0, BOARD_WIDTH);
		}
		int[][] rowsCopy = new int[BOARD_WIDTH][BOARD_WIDTH];
		for (int y = 0; y < BOARD_WIDTH; y++) {
			System.arraycopy(rows[y], 0, rowsCopy[y], 0, BOARD_WIDTH);
		}
		int[][] columnsCopy = new int[BOARD_WIDTH][BOARD_WIDTH];
		for (int y = 0; y < BOARD_WIDTH; y++) {
			System.arraycopy(columns[y], 0, columnsCopy[y], 0, BOARD_WIDTH);
		}
		return new SudokuPosition(squaresCopy, rowsCopy, columnsCopy);
	}
}
