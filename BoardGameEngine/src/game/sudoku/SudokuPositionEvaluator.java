package game.sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.ArrayMoveList;
import game.Coordinate;
import game.MoveList;
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
		MoveList<SudokuMove> possibleMoves = new ArrayMoveList<>(MoveList.MAX_SIZE);
		if (possibleMoves.size() == 0) {
			return openSquares.size() == 0 ? AnalysisResult.WIN : AnalysisResult.LOSS;
		}
		Set<Coordinate> playableCoordinates = new HashSet<>();
		int i = 0;
		do {
			playableCoordinates.add(possibleMoves.get(i).coordinate);
			++i;
		} while (i < possibleMoves.size());
		return openSquares.size() == playableCoordinates.size() ? possibleMoves.size() : AnalysisResult.LOSS;
	}
}
