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
	final boolean isDecided;

	public ComputerPlayerResult(AnalysisResult<Object> analysisResult, List<MoveWithScore<Object>> partialResults, int depth) {
		if (analysisResult == null && partialResults.size() == 0) {
			moves = null;
			isDecided = false;
		} else {
			Map<String, ObservedMoveWithScore> moveMap = new HashMap<>();
			if (analysisResult != null) {
				addMovesToMap(moveMap, analysisResult.getMovesWithScore(), false);
			}
			addMovesToMap(moveMap, partialResults, true);
			moves = new ArrayList<>(moveMap.values());
			Collections.sort(moves, (move1, move2) -> {
				if (move1.score == move2.score || (AnalysisResult.isDraw(move1.score) && AnalysisResult.isDraw(move2.score))) {
					return move1.move.compareTo(move2.move);
				}
				return AnalysisResult.isGreater(move1.score, move2.score) ? -1 : 1;
			});
			boolean decided = true;
			for (ObservedMoveWithScore moveWithScore : moves) {
				if (!AnalysisResult.isGameOver(moveWithScore.score)) {
					decided = false;
					break;
				}
			}
			isDecided = decided && analysisResult != null && moves.size() == analysisResult.getMovesWithScore().size();
		}
		this.depth = depth;
	}

	private void addMovesToMap(Map<String, ObservedMoveWithScore> moveMap, List<MoveWithScore<Object>> movesWithScore, boolean partial) {
		for (MoveWithScore<Object> moveWithScore : movesWithScore) {
			String moveString = moveWithScore.move == null ? "-" : moveWithScore.move.toString();
			moveMap.put(moveString, new ObservedMoveWithScore(moveString, moveWithScore.score, partial));
		}
	}
}
