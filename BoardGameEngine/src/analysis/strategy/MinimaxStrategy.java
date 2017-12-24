package analysis.strategy;

import java.util.Map;
import java.util.Map.Entry;

import analysis.AnalysisResult;
import analysis.AnalyzedMove;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class MinimaxStrategy<M, P extends IPosition<M>> implements IAlphaBetaStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;
	private final MoveListProvider<M> moveListProvider;

	private volatile boolean searchCanceled = false;

	public MinimaxStrategy(IPositionEvaluator<M, P> positionEvaluator, MoveListProvider<M> moveListProvider) {
		this.positionEvaluator = positionEvaluator;
		this.moveListProvider = moveListProvider;
	}

	@Override
	public IForkable<M, P> newForkableSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies, IDepthBasedStrategy<M, P> strategy) {
		return new MinimaxSearch<>(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
	}

	@Override
	public double evaluate(P position, int plies, double alpha, double beta) {
		return evaluate(position, plies);
	}

	@Override
	public double evaluate(P position, int plies) {
		return negamax(position, plies);
	}

	private double negamax(P position, int depth) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = moveListProvider.getMoveList(depth);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (numMoves == 0 || depth == 0) {
			return positionEvaluator.evaluate(position, possibleMoves);
		}

		int parentPlayer = position.getCurrentPlayer();

		boolean gameOver = true;
		double bestScore = Double.NEGATIVE_INFINITY;
		int i = 0;
		do {
			M move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? negamax(position, depth - 1) : -negamax(position, depth - 1);
			position.unmakeMove(move);

			gameOver = gameOver && AnalysisResult.isGameOver(score);
			if (AnalysisResult.isGreater(score, bestScore)) {
				bestScore = score;
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
	public void join(P parentPosition, AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults) {
		joinSearch(partialResult, movesWithResults);
	}

	public static <M> void joinSearch(AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults) {
		for (Entry<M, AnalysisResult<M>> moveWithResult : movesWithResults.entrySet()) {
			M move = moveWithResult.getKey();
			AnalysisResult<M> result = moveWithResult.getValue();
			AnalyzedMove<M> moveWithScore = result.getBestMove(partialResult.getPlayer());
			if (moveWithScore == null) {
				continue;
			}
			partialResult.addMoveWithScore(move, moveWithScore.analysis, result.isSearchComplete());
		}
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new MinimaxStrategy<>(positionEvaluator, moveListProvider.createCopy());
	}
}
