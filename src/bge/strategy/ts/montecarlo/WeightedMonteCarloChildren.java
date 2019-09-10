package bge.strategy.ts.montecarlo;

import java.util.Arrays;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.igame.IPosition;
import bge.igame.MoveList;

public class WeightedMonteCarloChildren<M> implements IMonteCarloChildren<M> {
    private int numUnexpanded;
    private ScoredMove[] unexpandedIndexes;

    public WeightedMonteCarloChildren(int numUnexpanded) {
        this.numUnexpanded = numUnexpanded;
    }

    @Override
    public IMonteCarloChildren<M> createNewWith(int numUnexpanded) {
        return new WeightedMonteCarloChildren<>(numUnexpanded);
    }

    @Override
    public <P extends IPosition<M>> boolean initUnexpanded(MonteCarloGameNode<M, P> parentNode) {
        P position = parentNode.position;
        MoveList<M> moveList = parentNode.moveList;
        IPositionEvaluator<M, P> positionEvaluator = parentNode.positionEvaluator;

        unexpandedIndexes = new ScoredMove[moveList.size()];

        boolean won = false;
        boolean allChildrenDecided = true;

        MoveList<M> possibleMoves = parentNode.moveListFactory.newAnalysisMoveList();
        int index = 0;
        int i = 0;
        do {
            M move = moveList.get(i);
            position.makeMove(move);
            possibleMoves.clear();
            position.getPossibleMoves(possibleMoves);
            double score = positionEvaluator.evaluate(position, possibleMoves);
            if (AnalysisResult.isGameOver(score)) {
                MonteCarloGameNode<M, P> childNode = new MonteCarloGameNode<>(parentNode, move, position, this, positionEvaluator, parentNode.moveListFactory,
                        parentNode.numSimulations, parentNode.maxDepth);
                childNode.statistics.setResult(new MonteCarloStatistics(position.getCurrentPlayer(), score));
                won = won || childNode.statistics.isWin(parentNode.statistics.player);
                parentNode.expandedChildren.add(childNode);
            } else {
                allChildrenDecided = false;
                unexpandedIndexes[index++] = new ScoredMove(i, score);
            }
            position.unmakeMove(move);
        } while (++i < moveList.size());

        numUnexpanded = index;
        if (won || allChildrenDecided) {
            return false;
        }
        Arrays.sort(unexpandedIndexes, 0, numUnexpanded, (m1, m2) -> Double.compare(m1.score, m2.score));
        return true;
    }

    @Override
    public int getNumUnexpanded() {
        return numUnexpanded;
    }

    @Override
    public void setNumUnexpanded(int numUnexpanded) {
        this.numUnexpanded = numUnexpanded;
    }

    @Override
    public int getNextNodeIndex() {
        return unexpandedIndexes[--numUnexpanded].index;
    }

    private static class ScoredMove {
        final int index;
        final double score;

        public ScoredMove(int index, double score) {
            this.index = index;
            this.score = score;
        }

        @Override
        public String toString() {
            return index + ". " + score;
        }
    }
}
