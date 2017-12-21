package analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AnalysisResult<M> {
	public static final double WIN = Double.POSITIVE_INFINITY;
	public static final double LOSS = Double.NEGATIVE_INFINITY;
	public static final double DRAW = Double.NaN;

	private final Map<M, MoveAnalysis> allValidMoves = new LinkedHashMap<>();
	private final Map<M, MoveAnalysis> wonAndDrawnMoves = new HashMap<>();
	private final Map<M, MoveAnalysis> lostMoves = new HashMap<>();
	private final Set<M> invalidMoves = new HashSet<>();

	private AnalyzedMove<M> bestMove;

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
		if (isValid) {
			MoveAnalysis analysis = new MoveAnalysis(score);
			allValidMoves.put(move, analysis);
			if (bestMove == null || AnalysisResult.isGreater(score, bestMove.analysis.score)) {
				bestMove = new AnalyzedMove<>(move, score);
			}
			if (AnalysisResult.WIN == score || AnalysisResult.isDraw(score)) {
				wonAndDrawnMoves.put(move, analysis);
			} else if (AnalysisResult.LOSS == score) {
				lostMoves.put(move, analysis);
			}
		} else {
			invalidMoves.add(move);
		}
	}

	public void searchCompleted() {
		searchComplete = true;
	}

	public boolean isSeachComplete() {
		return searchComplete;
	}

	public synchronized AnalysisResult<M> mergeWith(AnalysisResult<M> resultToMerge) {
		AnalysisResult<M> mergedResult = new AnalysisResult<>();
		Map<M, MoveAnalysis> mergedMoveMap = new HashMap<>(allValidMoves);
		mergedMoveMap.putAll(resultToMerge.allValidMoves);
		for (Entry<M, MoveAnalysis> moveWithScore : mergedMoveMap.entrySet()) {
			mergedResult.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue().score);
		}
		return mergedResult;
	}

	public synchronized Map<M, MoveAnalysis> getMovesWithScore() {
		return new LinkedHashMap<>(allValidMoves);
	}

	public synchronized AnalyzedMove<M> getBestMove(boolean isCurrentPlayer) {
		if (bestMove == null) {
			return null;
		}
		return isDraw(bestMove.analysis.score) && !isDecided() ? new AnalyzedMove<>(bestMove.move, 0.0) : bestMove.transform(isCurrentPlayer);
	}

	public synchronized List<M> getBestMoves() {
		if (bestMove == null) {
			return Collections.emptyList();
		}
		List<M> bestMoves = new ArrayList<>();
		double maxScore = bestMove.analysis.score;
		for (Entry<M, MoveAnalysis> moveWithScore : allValidMoves.entrySet()) {
			double score = moveWithScore.getValue().score;
			if (maxScore == score || isDraw(maxScore) && isDraw(score)) {
				bestMoves.add(moveWithScore.getKey());
			}
		}
		return bestMoves;
	}

	public synchronized Map<M, MoveAnalysis> getDecidedMoves() {
		Map<M, MoveAnalysis> decidedMoves = new HashMap<>(wonAndDrawnMoves);
		decidedMoves.putAll(lostMoves);
		return decidedMoves;
	}

	public synchronized Set<M> getInvalidMoves() {
		return invalidMoves;
	}

	public synchronized boolean isWin() {
		return bestMove != null && bestMove.analysis.score == AnalysisResult.WIN;
	}

	public synchronized boolean isLoss() {
		return bestMove != null && bestMove.analysis.score == AnalysisResult.LOSS;
	}

	public synchronized boolean onlyOneMove() {
		return  allValidMoves.size() == lostMoves.size() + 1;
	}

	public synchronized boolean isDecided() {
		return allValidMoves.size() > 0 && wonAndDrawnMoves.size() + lostMoves.size() == allValidMoves.size();
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<M, MoveAnalysis>> moveScoreIter = allValidMoves.entrySet().iterator();
		while (moveScoreIter.hasNext()) {
			Entry<M, MoveAnalysis> moveWithScore = moveScoreIter.next();
			sb.append(AnalyzedMove.toString(moveWithScore.getKey(), moveWithScore.getValue()));
			if (moveScoreIter.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
