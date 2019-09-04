package bge.strategy.ts.forkjoin.alphabeta;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.strategy.ts.MoveListProvider;

public class AlphaBetaPositionEvaluator<M, P extends IPosition<M>> implements IAlphaBetaPositionEvaluator<M, P> {
    private final IPositionEvaluator<M, P> positionEvaluator;
    private final MoveListProvider<M> moveListProvider;

    private volatile boolean searchCanceled = false;

    public AlphaBetaPositionEvaluator(IPositionEvaluator<M, P> positionEvaluator, MoveListProvider<M> moveListProvider) {
        this.positionEvaluator = positionEvaluator;
        this.moveListProvider = moveListProvider;
    }

    @Override
    public double evaluate(P position, int plies, double alpha, double beta) {
        return alphaBeta(position, plies, alpha, beta);
    }

    private double alphaBeta(P position, int depth, double alpha, double beta) {
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
        double bestScore = AnalysisResult.LOSS;
        int i = 0;
        do {
            M move = possibleMoves.get(i);
            position.makeMove(move);
            double score = parentPlayer == position.getCurrentPlayer() ? alphaBeta(position, depth - 1, alpha, beta)
                    : -alphaBeta(position, depth - 1, -beta, -alpha);
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
    public AlphaBetaPositionEvaluator<M, P> createCopy() {
        return new AlphaBetaPositionEvaluator<>(positionEvaluator, moveListProvider.createCopy());
    }
}
