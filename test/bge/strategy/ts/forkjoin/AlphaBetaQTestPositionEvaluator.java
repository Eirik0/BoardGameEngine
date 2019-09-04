package bge.strategy.ts.forkjoin;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.forkjoin.alphabeta.IAlphaBetaPositionEvaluator;

public class AlphaBetaQTestPositionEvaluator<M, P extends IPosition<M>> implements IAlphaBetaPositionEvaluator<M, P> {
    private final IPositionEvaluator<M, P> positionEvaluator;
    private final MoveListProvider<M> moveListProvider;

    private volatile boolean searchCanceled = false;

    public AlphaBetaQTestPositionEvaluator(IPositionEvaluator<M, P> positionEvaluator, MoveListProvider<M> moveListProvider) {
        this.positionEvaluator = positionEvaluator;
        this.moveListProvider = moveListProvider;
    }

    @Override
    public double evaluate(P position, int plies, double alpha, double beta) {
        return max(position, 0, plies, AnalysisResult.LOSS, AnalysisResult.WIN);
    }

    private double max(P position, int ply, int maxPly, double alpha, double beta) {
        if (searchCanceled) {
            return 0;
        }

        MoveList<M> possibleMoves = moveListProvider.getMoveList(ply);
        position.getPossibleMoves(possibleMoves);
        int numMoves = possibleMoves.size();

        if (ply == maxPly) {
            return maxQ(position, maxPly, alpha, beta);
        } else if (numMoves == 0) {
            return positionEvaluator.evaluate(position, possibleMoves);
        }

        int parentPlayer = position.getCurrentPlayer();

        double bestScore = AnalysisResult.LOSS;
        int i = 0;
        do {
            M move = possibleMoves.get(i);
            position.makeMove(move);
            double score = parentPlayer == position.getCurrentPlayer() ? max(position, ply + 1, maxPly, alpha, beta)
                    : min(position, ply + 1, maxPly, alpha, beta);
            position.unmakeMove(move);

            if (!AnalysisResult.isGreater(bestScore, score)) {
                bestScore = score;
                if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta (fail-soft)
                    return beta;
                }
                if (AnalysisResult.isGreater(score, alpha)) {
                    alpha = score;
                }
            }
            ++i;
        } while (i < numMoves);

        return bestScore;
    }

    private double min(P position, int ply, int maxPly, double alpha, double beta) {
        if (searchCanceled) {
            return 0;
        }

        MoveList<M> possibleMoves = moveListProvider.getMoveList(ply);
        position.getPossibleMoves(possibleMoves);
        int numMoves = possibleMoves.size();

        if (ply == maxPly) {
            return minQ(position, maxPly, alpha, beta);
        } else if (numMoves == 0) {
            return -positionEvaluator.evaluate(position, possibleMoves);
        }

        int parentPlayer = position.getCurrentPlayer();

        double bestScore = AnalysisResult.WIN;
        int i = 0;
        do {
            M move = possibleMoves.get(i);
            position.makeMove(move);
            double score = parentPlayer == position.getCurrentPlayer() ? min(position, ply + 1, maxPly, alpha, beta)
                    : max(position, ply + 1, maxPly, alpha, beta);
            position.unmakeMove(move);

            if (!AnalysisResult.isGreater(score, bestScore)) {
                bestScore = score;
                if (!AnalysisResult.isGreater(bestScore, alpha)) { // alpha >= beta
                    return alpha;
                }
                if (AnalysisResult.isGreater(beta, score)) { // score < beta
                    beta = score;
                }
            }
            ++i;
        } while (i < numMoves);

        return bestScore;
    }

    private double maxQ(P position, int ply, double alpha, double beta) {
        if (searchCanceled) {
            return 0;
        }

        MoveList<M> possibleMoves = moveListProvider.getMoveList(ply);
        position.getPossibleMoves(possibleMoves);
        int numMoves = possibleMoves.numDynamicMoves();

        double currentScore = positionEvaluator.evaluate(position, possibleMoves);
        if (numMoves == 0) {
            return currentScore;
        }
        if (!AnalysisResult.isGreater(beta, currentScore)) {
            return currentScore;
        }
        if (AnalysisResult.isGreater(currentScore, alpha)) {
            alpha = currentScore;
        }

        int parentPlayer = position.getCurrentPlayer();

        double bestScore = AnalysisResult.LOSS;
        int i = 0;
        do {
            M move = possibleMoves.get(i);
            position.makeMove(move);
            double score = parentPlayer == position.getCurrentPlayer() ? maxQ(position, ply + 1, alpha, beta) : minQ(position, ply + 1, alpha, beta);
            position.unmakeMove(move);

            if (!AnalysisResult.isGreater(bestScore, score)) {
                bestScore = score;
                if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta (fail-soft)
                    return beta;
                }
                if (AnalysisResult.isGreater(score, alpha)) {
                    alpha = score;
                }
            }
            ++i;
        } while (i < numMoves);

        return bestScore;
    }

    private double minQ(P position, int ply, double alpha, double beta) {
        if (searchCanceled) {
            return 0;
        }

        MoveList<M> possibleMoves = moveListProvider.getMoveList(ply);
        position.getPossibleMoves(possibleMoves);
        int numMoves = possibleMoves.numDynamicMoves();

        double currentScore = -positionEvaluator.evaluate(position, possibleMoves);
        if (numMoves == 0) {
            return currentScore;
        }
        if (!AnalysisResult.isGreater(currentScore, alpha)) { // alpha >= beta
            return currentScore;
        }
        if (AnalysisResult.isGreater(beta, currentScore)) { // score < beta
            beta = currentScore;
        }

        int parentPlayer = position.getCurrentPlayer();

        double bestScore = AnalysisResult.WIN;
        int i = 0;
        do {
            M move = possibleMoves.get(i);
            position.makeMove(move);
            double score = parentPlayer == position.getCurrentPlayer() ? minQ(position, ply + 1, alpha, beta) : maxQ(position, ply + 1, alpha, beta);
            position.unmakeMove(move);

            if (!AnalysisResult.isGreater(score, bestScore)) {
                bestScore = score;
                if (!AnalysisResult.isGreater(bestScore, alpha)) { // alpha >= beta
                    return alpha;
                }
                if (AnalysisResult.isGreater(beta, score)) { // score < beta
                    beta = score;
                }
            }
            ++i;
        } while (i < numMoves);

        return bestScore;
    }

    @Override
    public void stopSearch() {
        searchCanceled = true;
    }

    @Override
    public IAlphaBetaPositionEvaluator<M, P> createCopy() {
        return new AlphaBetaQTestPositionEvaluator<>(positionEvaluator, moveListProvider.createCopy());
    }
}
