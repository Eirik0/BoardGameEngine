package bge.analysis.strategy;

import java.util.Map;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;

public class AlphaBetaQStrategy<M, P extends IPosition<M>> implements IAlphaBetaStrategy<M, P> {
    private final IPositionEvaluator<M, P> positionEvaluator;
    private final MoveListProvider<M> moveListProvider;

    private volatile boolean searchCanceled = false;

    public AlphaBetaQStrategy(IPositionEvaluator<M, P> positionEvaluator, MoveListProvider<M> moveListProvider) {
        this.positionEvaluator = positionEvaluator;
        this.moveListProvider = moveListProvider;
    }

    @Override
    public IForkable<M, P> newForkableSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies,
            IDepthBasedStrategy<M, P> strategy) {
        return new AlphaBetaSearch<>(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
    }

    @Override
    public double evaluate(P position, int plies, double alpha, double beta) {
        return alphaBeta(position, 0, plies, alpha, beta, false);
    }

    private double alphaBeta(P position, int ply, int maxPly, double alpha, double beta, boolean quiescent) {
        if (searchCanceled) {
            return 0;
        }

        MoveList<M> possibleMoves = moveListProvider.getMoveList(ply);
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
        double bestScore = AnalysisResult.LOSS;
        M move;
        int i = 0;
        do {
            move = possibleMoves.get(i);
            position.makeMove(move);
            double score = parentPlayer == position.getCurrentPlayer() ? alphaBeta(position, ply + 1, maxPly, alpha, beta, quiescent)
                    : -alphaBeta(position, ply + 1, maxPly, -beta, -alpha, quiescent);
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
    public void stopSearch() {
        searchCanceled = true;
    }

    @Override
    public void join(P parentPosition, AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults) {
        MinimaxStrategy.joinSearch(partialResult, movesWithResults);
    }

    @Override
    public IDepthBasedStrategy<M, P> createCopy() {
        return new AlphaBetaQStrategy<>(positionEvaluator, moveListProvider.createCopy());
    }
}
