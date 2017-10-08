package game.sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import analysis.IPositionEvaluator;
import game.Coordinate;
import game.ultimatetictactoe.UltimateTicTacToeUtilities;

public class SudokuPositionEvaluator implements IPositionEvaluator<SudokuMove, SudokuPosition> {
	@Override
	public double evaluate(SudokuPosition position, int player) {
		List<Coordinate> openSquares = new ArrayList<>();
		for (int n = 0; n < SudokuPosition.BOARD_WIDTH; ++n) {
			for (int m = 0; m < SudokuPosition.BOARD_WIDTH; ++m) {
				if (position.squares[n][m] == SudokuPosition.UNPLAYED) {
					openSquares.add(UltimateTicTacToeUtilities.getBoardXY(n, m));
				}
			}
		}
		List<SudokuMove> possibleMoves = position.getPossibleMoves();
		if (possibleMoves.size() == 0) {
			return openSquares.size() == 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		}
		Set<Coordinate> playableCoordinates = new HashSet<>();
		for (SudokuMove move : possibleMoves) {
			playableCoordinates.add(move.coordinate);
		}
		return openSquares.size() == playableCoordinates.size() ? 0 : Double.NEGATIVE_INFINITY;
	}
}
