package bge.game.sudoku;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.game.MoveList;

public class SudokuPositionEvaluator implements IPositionEvaluator<SudokuMove, SudokuPosition>, SudokuConstants {
    @Override
    public double evaluate(SudokuPosition position, MoveList<SudokuMove> possibleMoves) {
        if (possibleMoves.size() == 0) {
            return position.numUndecided == 0 ? AnalysisResult.WIN : AnalysisResult.LOSS;
        }
        int i = 0;
        do {
            if (position.cells[position.undecidedCells[i]].getPossibleDigits().length == 0) {
                return AnalysisResult.LOSS;
            }
        } while (++i < position.numUndecided);
        return possibleMoves.size();
    }
}
