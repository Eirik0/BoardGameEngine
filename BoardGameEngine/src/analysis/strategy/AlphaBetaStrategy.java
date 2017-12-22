package analysis.strategy;

import java.util.Map;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class AlphaBetaStrategy<M, P extends IPosition<M>> implements IDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;
	private final MoveListProvider<M> moveListProvider;

	private volatile boolean searchCanceled = false;

	private AlphaBetaPreSearch preSearch = new AlphaBetaPreSearch(new AnalysisResult<>(), true);

	public AlphaBetaStrategy(IPositionEvaluator<M, P> positionEvaluator, MoveListProvider<M> moveListProvider) {
		this.positionEvaluator = positionEvaluator;
		this.moveListProvider = moveListProvider;
	}

	@Override
	public IForkable<M, P> newForkableSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies, IDepthBasedStrategy<M, P> strategy) {
		return new MinimaxSearch<>(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
	}

	@Override
	public void preSearch(AnalysisResult<M> currentResult, boolean isCurrentPlayer) {
		preSearch = new AlphaBetaPreSearch(currentResult, isCurrentPlayer);
	}

	@Override
	public double evaluate(P position, int plies) {
		return alphaBeta(position, 0, plies, preSearch.alpha, preSearch.beta);
	}

	private double alphaBeta(P position, int ply, int maxPly, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = moveListProvider.getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (numMoves == 0 || ply == maxPly) {
			return positionEvaluator.evaluate(position, possibleMoves);
		}

		int parentPlayer = position.getCurrentPlayer();

		boolean gameOver = true;
		double bestScore = Double.NEGATIVE_INFINITY;
		int i = 0;
		do {
			M move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? alphaBeta(position, ply + 1, maxPly, alpha, beta) : -alphaBeta(position, ply + 1, maxPly, -beta, -alpha);
			position.unmakeMove(move);

			gameOver = gameOver && AnalysisResult.isGameOver(score);
			if (!AnalysisResult.isGreater(bestScore, score)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta (fail-soft)
					break;
				}
				if (AnalysisResult.isGreater(score, alpha)) {
					alpha = score;
				}
			}
			++i;
		} while (i < numMoves);

		if (!gameOver && AnalysisResult.isDraw(bestScore)) {
			return 0.0;
		}

		return bestScore;
	}

	@Override
	public void stopSearch() {
		searchCanceled = true;
	}

	@Override
	public void join(P parentPosition, int parentPlayer, int currentPlayer, AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults) {
		MinimaxStrategy.joinSearch(parentPlayer, currentPlayer, partialResult, movesWithResults);
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new AlphaBetaStrategy<>(positionEvaluator, moveListProvider.createCopy());
	}
}
