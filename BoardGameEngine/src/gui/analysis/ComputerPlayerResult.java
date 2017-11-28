package gui.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import analysis.AnalysisResult;
import analysis.MoveWithScore;

public class ComputerPlayerResult {
	final List<ObservedMoveWithScore> moves;
	final int depth;

	public ComputerPlayerResult(AnalysisResult<Object> analysisResult, List<MoveWithScore<Object>> partialResults, int depth) {
		if (analysisResult == null && partialResults == null) {
			moves = null;
		} else {
			Map<String, ObservedMoveWithScore> moveMap = new HashMap<>();
			if (analysisResult != null) {
				for (MoveWithScore<Object> moveWithScore : analysisResult.getMovesWithScore()) {
					String moveString = moveWithScore.move == null ? "-" : moveWithScore.move.toString();
					moveMap.put(moveString, new ObservedMoveWithScore(moveString, moveWithScore.score, false));
				}
			}
			for (MoveWithScore<Object> moveWithScore : partialResults) {
				String moveString = moveWithScore.move == null ? "-" : moveWithScore.move.toString();
				moveMap.put(moveString, new ObservedMoveWithScore(moveString, moveWithScore.score, true));
			}
			moves = new ArrayList<>(moveMap.values());
			Collections.sort(moves, (move1, move2) -> Double.compare(move2.score, move1.score));
		}
		this.depth = depth;
	}
}
