package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AnalysisResult<M> {
	private final List<MoveWithScore<M>> movesWithScore = new ArrayList<>();
	private final List<M> unanalyzedMoves = new ArrayList<>();

	private volatile double min = Double.POSITIVE_INFINITY;
	private volatile double max = Double.NEGATIVE_INFINITY;
	private volatile M bestMove;

	public AnalysisResult() {

	}

	public AnalysisResult(List<MoveWithScore<M>> movesWithScore) {
		for (MoveWithScore<M> moveWithScore : movesWithScore) {
			addMoveWithScore(moveWithScore.move, moveWithScore.score);
		}
	}

	public void addMoveWithScore(M move, double score) {
		if (score < min) {
			min = score;
		}
		if (score > max || bestMove == null) {
			max = score;
			bestMove = move;
		}
		movesWithScore.add(new MoveWithScore<>(move, score));
	}

	public AnalysisResult<M> mergeWith(AnalysisResult<M> resultToMerge) {
		AnalysisResult<M> mergedResult = new AnalysisResult<>();
		Map<M, Double> mergedMoveMap = new HashMap<>();
		for (MoveWithScore<M> moveWithScore : movesWithScore) {
			mergedMoveMap.put(moveWithScore.move, moveWithScore.score);
		}
		for (MoveWithScore<M> moveWithScore : resultToMerge.movesWithScore) {
			mergedMoveMap.put(moveWithScore.move, moveWithScore.score);
		}
		for (Entry<M, Double> moveWithScore : mergedMoveMap.entrySet()) {
			mergedResult.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue());
		}
		return mergedResult;
	}

	public void addUnanalyzedMove(M move) {
		unanalyzedMoves.add(move);
	}

	public List<MoveWithScore<M>> getMovesWithScore() {
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

	public synchronized M getBestMove() {
		return bestMove;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<MoveWithScore<M>> moveScoreIter = movesWithScore.iterator();
		while (moveScoreIter.hasNext()) {
			MoveWithScore<M> scoreMove = moveScoreIter.next();
			sb.append(scoreMove.move).append(": ").append(scoreMove.score);
			if (moveScoreIter.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
