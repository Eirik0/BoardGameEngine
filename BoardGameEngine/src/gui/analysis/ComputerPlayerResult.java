package gui.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import analysis.AnalysisResult;
import analysis.MoveWithScore;

public class ComputerPlayerResult {
	final List<MoveWithScore<Object>> moves;
	final int depth;

	public ComputerPlayerResult(AnalysisResult<Object> analysisResult, int depth) {
		moves = analysisResult == null ? null : new ArrayList<>(analysisResult.getMovesWithScore());
		if (analysisResult != null) {
			Collections.sort(moves, (move1, move2) -> Double.compare(move2.score, move1.score));
		}
		this.depth = depth;
	}
}
