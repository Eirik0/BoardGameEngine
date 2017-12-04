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
			Collections.sort(moves, (move1, move2) -> {
				if (move1.score == move2.score || (AnalysisResult.isDraw(move1.score) && AnalysisResult.isDraw(move2.score))) {
					return move1.move.compareTo(move2.move);
				}
				return AnalysisResult.isGreater(move1.score, move2.score) ? -1 : 1;
			});
		}
		this.depth = depth;
	}
}
