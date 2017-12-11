package analysis.strategy;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class AlphaBetaQStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;

	private AlphaBetaPreSearch preSearch = new AlphaBetaPreSearch(new AnalysisResult<>(), true);

	public AlphaBetaQStrategy(MoveListFactory<M> moveListFactory, IPositionEvaluator<M, P> positionEvaluator) {
		super(moveListFactory);
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public void preSearch(AnalysisResult<M> currentResult, boolean isCurrentPlayer) {
		preSearch = new AlphaBetaPreSearch(currentResult, isCurrentPlayer);
	}

	@Override
	public double evaluate(P position, int plies) {
		return alphaBeta(position, 0, plies, preSearch.alpha, preSearch.beta, false);
	}

	private double alphaBeta(P position, int ply, int maxPly, double alpha, double beta, boolean quiescent) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numDynamicMoves = possibleMoves.numDynamicMoves();
		int numMoves = quiescent ? numDynamicMoves : possibleMoves.size();

		if (numMoves == 0 || ply == maxPly || quiescent) {
			double score = positionEvaluator.evaluate(position, possibleMoves);
			if (numDynamicMoves == 0 || !AnalysisResult.isGreater(beta, score)) { // no moves or score >= beta
				return score;
			} else if (AnalysisResult.isGreater(score, alpha)) {
				alpha = score;
			}
			numMoves = numDynamicMoves;
			quiescent = true;
			++maxPly;
		}

		int parentPlayer = position.getCurrentPlayer();

		boolean gameOver = numMoves == possibleMoves.size(); // only for quiescent searches that look at all moves
		double bestScore = Double.NEGATIVE_INFINITY;
		M move;
		int i = 0;
		do {
			move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? alphaBeta(position, ply + 1, maxPly, alpha, beta, quiescent) : -alphaBeta(position, ply + 1, maxPly, -beta, -alpha, quiescent);
			position.unmakeMove(move);

			gameOver = gameOver && AnalysisResult.isGameOver(score);
			if (!AnalysisResult.isGreater(bestScore, score)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta
					bestScore = beta;
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
	public IDepthBasedStrategy<M, P> createCopy() {
		return new AlphaBetaQStrategy<>(moveListFactory, positionEvaluator);
	}
}
