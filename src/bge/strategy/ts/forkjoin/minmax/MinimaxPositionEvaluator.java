package bge.strategy.ts.forkjoin.minmax;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.forkjoin.IDepthBasedPositionEvaluator;

public class MinimaxPositionEvaluator<M, P extends IPosition<M>> implements IDepthBasedPositionEvaluator<M, P> {
    private final IPositionEvaluator<M, P> positionEvaluator;
    private final MoveListProvider<M> moveListProvider;

    private volatile boolean searchCanceled = false;

    public MinimaxPositionEvaluator(IPositionEvaluator<M, P> positionEvaluator, MoveListProvider<M> moveListProvider) {
        this.positionEvaluator = positionEvaluator;
        this.moveListProvider = moveListProvider;
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
        double bestScore = AnalysisResult.LOSS;
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
    public IDepthBasedPositionEvaluator<M, P> createCopy() {
        return new MinimaxPositionEvaluator<>(positionEvaluator, moveListProvider.createCopy());
    }
}
