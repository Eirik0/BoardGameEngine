package analysis.strategy;

import analysis.AnalysisResult;
import analysis.AnalyzedMove;

public class AlphaBetaPreSearch {
	public final double alpha;
	public final double beta;

	public AlphaBetaPreSearch(AnalysisResult<?> currentResult, boolean samePlayer) {
		AnalyzedMove<?> max = currentResult.getBestMove(samePlayer);
		if (max != null) {
			alpha = samePlayer ? max.analysis.score : Double.NEGATIVE_INFINITY;
			beta = samePlayer ? Double.POSITIVE_INFINITY : max.analysis.score;
		} else {
			alpha = Double.NEGATIVE_INFINITY;
			beta = Double.POSITIVE_INFINITY;
		}
	}
}
