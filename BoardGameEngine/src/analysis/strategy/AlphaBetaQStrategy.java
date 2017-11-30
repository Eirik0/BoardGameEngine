package analysis.strategy;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class AlphaBetaQStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;

	public AlphaBetaQStrategy(MoveListFactory<M> moveListFactory, IPositionEvaluator<M, P> positionEvaluator) {
		super(moveListFactory);
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public double evaluate(P position, int player, int plies) {
		return alphaBeta(position, player, 0, plies, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, player == position.getCurrentPlayer(), false);
	}

	private double alphaBeta(P position, int player, int ply, int maxPly, double alpha, double beta, boolean max, boolean quiescent) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = quiescent ? possibleMoves.numDynamicMoves() : possibleMoves.size();

		if (numMoves == 0 || ply == maxPly || quiescent) {
			double eval = positionEvaluator.evaluate(position, possibleMoves, player);
			if (possibleMoves.numDynamicMoves() == 0) {
				return eval;
			}
			double score = max ? eval : -eval;
			if (!AnalysisResult.isGreater(beta, score)) { // score >= beta
				return beta;
			}
			if (AnalysisResult.isGreater(score, alpha)) {
				alpha = score;
			}
			quiescent = true;
			++maxPly;
		}

		M move;
		int i = 0;
		do {
			move = possibleMoves.get(i);
			position.makeMove(move);
			double ab;
			if (max == (player == position.getCurrentPlayer())) {
				ab = alphaBeta(position, player, ply + 1, maxPly, alpha, beta, player == position.getCurrentPlayer(), quiescent);
			} else {
				ab = alphaBeta(position, player, ply + 1, maxPly, -beta, -alpha, player == position.getCurrentPlayer(), quiescent);
			}
			double score = max ? ab : -ab;

			position.unmakeMove(move);

			if (AnalysisResult.isGreater(score, alpha)) {
				alpha = score;
				if (!AnalysisResult.isGreater(beta, alpha)) { // alpha >= beta
					break;
				}
			}
			++i;
		} while (i < numMoves);

		return max ? alpha : -alpha;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new AlphaBetaStrategy<>(moveListFactory, positionEvaluator);
	}
}
