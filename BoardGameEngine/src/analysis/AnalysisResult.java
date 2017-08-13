package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.Pair;

public class AnalysisResult<M> {
	private final List<Pair<M, Double>> movesWithScore = new ArrayList<>();
	private final List<M> unanalyzedMoves = new ArrayList<>();

	private volatile double min = Double.POSITIVE_INFINITY;
	private volatile double max = Double.NEGATIVE_INFINITY;
	private volatile M bestMove;

	public AnalysisResult() {
	}

	public void addMoveWithScore(M move, double score) {
		if (score < min) {
			min = score;
		}
		if (score > max) {
			max = score;
			bestMove = move;
		}
		movesWithScore.add(Pair.valueOf(move, score));
	}

	public AnalysisResult<M> mergeWith(AnalysisResult<M> result) {
		AnalysisResult<M> mergedResult = new AnalysisResult<>();
		Map<M, Double> mergedMoveMap = new HashMap<>();
		for (Pair<M, Double> moveWithScore : movesWithScore) {
			mergedMoveMap.put(moveWithScore.getFirst(), moveWithScore.getSecond());
		}
		for (Pair<M, Double> moveWithScore : result.movesWithScore) {
			mergedMoveMap.put(moveWithScore.getFirst(), moveWithScore.getSecond());
		}
		for (Entry<M, Double> moveWithScore : mergedMoveMap.entrySet()) {
			mergedResult.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue());
		}
		return mergedResult;
	}

	public void addUnanalyzedMove(M move) {
		unanalyzedMoves.add(move);
	}

	public List<Pair<M, Double>> getMovesWithScore() {
		return movesWithScore;
	}

	public List<M> getUnanalyzedMoves() {
		return unanalyzedMoves;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public M getBestMove() {
		return bestMove;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Pair<M, Double>> moveScoreIter = movesWithScore.iterator();
		while (moveScoreIter.hasNext()) {
			Pair<M, Double> scoreMove = moveScoreIter.next();
			sb.append(scoreMove.getFirst()).append(": ").append(scoreMove.getSecond());
			if (moveScoreIter.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
