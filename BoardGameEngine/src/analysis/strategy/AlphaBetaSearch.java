package analysis.strategy;

import analysis.AnalysisResult;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class AlphaBetaSearch<M, P extends IPosition<M>> extends AbstractAlphaBetaSearch<M, P> {
	public AlphaBetaSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies, IDepthBasedStrategy<M, P> strategy) {
		super(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
	}

	@Override
	protected AlphaBetaSearch<M, P> newSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies, IDepthBasedStrategy<M, P> strategy) {
		return new AlphaBetaSearch<>(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
	}

	@Override
	protected AnalysisResult<M> searchNonForkable() {
		AnalysisResult<M> result = new AnalysisResult<>(parentMove, strategy.evaluate(position, plies));
		result.searchCompleted();
		return result;
	}

	@Override
	protected AnalysisResult<M> searchWithStrategy() {
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		AnalysisResult<M> analysisResult = new AnalysisResult<>();
		do {
			M move = movesToSearch.get(branchIndex.get());
			position.makeMove(move);
			double score = player == position.getCurrentPlayer() ? strategy.evaluate(position, plies - 1, alpha, beta) : -strategy.evaluate(position, plies - 1, -beta, -alpha);
			position.unmakeMove(move);
			if (searchCanceled) { // we need to check search canceled after making the call to evaluate
				break;
			} else {
				analysisResult.addMoveWithScore(move, score);
				if (AnalysisResult.isGreater(score, alpha)) {
					alpha = score;
				}
				if (!AnalysisResult.isGreater(beta, alpha)) { // alpha >= beta
					branchIndex.set(movesToSearch.size()); // force search complete
					break;
				}
			}
		} while (branchIndex.incrementAndGet() < movesToSearch.size());

		if (branchIndex.get() == movesToSearch.size()) {
			analysisResult.searchCompleted();
		}

		return analysisResult;
	}
}