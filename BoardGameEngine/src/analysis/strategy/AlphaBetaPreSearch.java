package analysis.strategy;

import analysis.AnalysisResult;
import analysis.MoveWithScore;

public class AlphaBetaPreSearch {
	public final double alpha;
	public final double beta;

	public AlphaBetaPreSearch(AnalysisResult<?> currentResult, boolean samePlayer) {
		MoveWithScore<?> max = currentResult.getMax();
		if (max != null) {
			if (samePlayer) {
				alpha = max.score;
				beta = Double.POSITIVE_INFINITY;
			} else {
				alpha = Double.NEGATIVE_INFINITY;
				beta = -max.score;
			}
		} else {
			alpha = Double.NEGATIVE_INFINITY;
			beta = Double.POSITIVE_INFINITY;
		}
	}
}
