package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AnalysisResult<M> {
	public static final double WIN = Double.POSITIVE_INFINITY;
	public static final double LOSS = Double.NEGATIVE_INFINITY;
	public static final double DRAW = Double.NaN;

	private final List<MoveWithScore<M>> movesWithScore = new ArrayList<>();
	private final Set<MoveWithScore<M>> decidedMoves = new HashSet<>();
	private int numLost = 0;

	private MoveWithScore<M> max;

	private boolean searchComplete = false;

	public AnalysisResult() {
	}

	public AnalysisResult(M move, double score) {
		addMoveWithScore(move, score, true);
	}

	public void addMoveWithScore(M move, double score) {
		addMoveWithScore(move, score, true);
	}

	public synchronized void addMoveWithScore(M move, double score, boolean isValid) {
		MoveWithScore<M> moveWithScore = new MoveWithScore<>(move, score, isValid);
		movesWithScore.add(moveWithScore);
		if (isValid) {
			if (isGameOver(score)) {
				decidedMoves.add(moveWithScore);
				if (LOSS == score) {
					++numLost;
				}
			}
			if (max == null || isGreater(score, max.score)) {
				max = moveWithScore;
			}
		}
	}

	public void searchCompleted() {
		searchComplete = true;
	}

	public boolean isSeachComplete() {
		return searchComplete;
	}

	public AnalysisResult<M> mergeWith(AnalysisResult<M> resultToMerge) {
		AnalysisResult<M> mergedResult = new AnalysisResult<>();
		Map<M, Double> mergedMoveMap = new HashMap<>();
		for (MoveWithScore<M> moveWithScore : movesWithScore) {
			mergedMoveMap.put(moveWithScore.move, Double.valueOf(moveWithScore.score));
		}
		for (MoveWithScore<M> moveWithScore : resultToMerge.movesWithScore) {
			if (moveWithScore.isValid()) {
				mergedMoveMap.put(moveWithScore.move, Double.valueOf(moveWithScore.score));
			}
		}
		for (Entry<M, Double> moveWithScore : mergedMoveMap.entrySet()) {
			mergedResult.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue().doubleValue());
		}
		return mergedResult;
	}

	public synchronized List<MoveWithScore<M>> getMovesWithScore() {
		return new ArrayList<>(movesWithScore);
	}

	public synchronized MoveWithScore<M> getMax() {
		return max != null && isDraw(max.score) && !isDecided() ? new MoveWithScore<>(max.move, 0.0) : max;
	}

	public M getBestMove() {
		return max == null ? null : max.move;
	}

	public Set<MoveWithScore<M>> getDecidedMoves() {
		return decidedMoves;
	}

	public boolean isWin() {
		return max != null && max.score == AnalysisResult.WIN;
	}

	public boolean isLoss() {
		return max != null && max.score == AnalysisResult.LOSS;
	}

	public synchronized boolean onlyOneMove() {
		return max != null && movesWithScore.size() == numLost + 1;
	}

	public boolean isDecided() {
		return decidedMoves.size() == movesWithScore.size();
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

	public static boolean isGreater(double l, double r) {
		return l > r || (r < 0 && isDraw(l)) || (l >= 0 && isDraw(r));
	}

	public static boolean isDraw(double d) {
		return d != d;
	}

	public static boolean isGameOver(double d) {
		return Double.isInfinite(d) || isDraw(d);
	}
}
