package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AnalysisResult<M> {
	public static final double WIN = Double.POSITIVE_INFINITY;
	public static final double LOSS = Double.NEGATIVE_INFINITY;
	public static final double DRAW = Double.NaN;

	private final List<MoveWithScore<M>> movesWithScore = new ArrayList<>();
	private final List<M> unanalyzedMoves = new ArrayList<>();

	private MoveWithScore<M> min;
	private MoveWithScore<M> max;

	public AnalysisResult() {
	}

	public AnalysisResult(M move, double score) {
		addMoveWithScore(move, score, true);
	}

	public void addMoveWithScore(M move, double score) {
		addMoveWithScore(move, score, true);
	}

	public void addMoveWithScore(M move, double score, boolean isValid) {
		MoveWithScore<M> moveWithScore = new MoveWithScore<>(move, score, isValid);
		movesWithScore.add(moveWithScore);
		if (isValid) {
			if (min == null || score < min.score || (moveWithScore.isDraw && min.score > 0) || (min.isDraw && moveWithScore.score <= 0)) {
				min = moveWithScore;
			}
			if (max == null || score > max.score || (moveWithScore.isDraw && max.score < 0) || (max.isDraw && moveWithScore.score >= 0)) {
				max = moveWithScore;
			}
		}
	}

	public AnalysisResult<M> mergeWith(AnalysisResult<M> resultToMerge) {
		AnalysisResult<M> mergedResult = new AnalysisResult<>();
		Map<M, Double> mergedMoveMap = new HashMap<>();
		for (MoveWithScore<M> moveWithScore : movesWithScore) {
			mergedMoveMap.put(moveWithScore.move, moveWithScore.score);
		}
		for (MoveWithScore<M> moveWithScore : resultToMerge.movesWithScore) {
			if (moveWithScore.isValid()) {
				mergedMoveMap.put(moveWithScore.move, moveWithScore.score);
			}
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

	public MoveWithScore<M> getMin() {
		return min;
	}

	public MoveWithScore<M> getMax() {
		return max;
	}

	public M getBestMove() {
		return max == null ? null : max.move;
	}

	public boolean isWin() {
		return max != null && max.score == AnalysisResult.WIN;
	}

	public boolean isLoss() {
		return max != null && max.score == AnalysisResult.LOSS;
	}

	public boolean isDraw() {
		return max != null && max.isDraw;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<MoveWithScore<M>> moveScoreIter = movesWithScore.iterator();
		while (moveScoreIter.hasNext()) {
			MoveWithScore<M> scoreMoved = moveScoreIter.next();
			sb.append(scoreMoved.toString());
			if (moveScoreIter.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static boolean isDraw(double d) {
		return d != d;
	}
}
