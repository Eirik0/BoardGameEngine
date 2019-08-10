package bge.analysis.montecarlo;

import java.util.ArrayList;
import java.util.List;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.analysis.strategy.MoveListProvider;
import bge.game.IPosition;
import bge.game.MoveList;
import bge.game.MoveListFactory;
import bge.game.forkjoinexample.ForkObserver;

public class MonteCarloGameNode<M, P extends IPosition<M>> {
    private MonteCarloGameNode<M, P> parentNode;

    final M parentMove;
    final P position;
    final IPositionEvaluator<M, P> positionEvaluator;
    final MoveListFactory<M> moveListFactory;
    private MoveListProvider<M> moveListProdiver;
    final int maxDepth;
    final int numSimulations;

    final MoveList<M> moveList;

    final IMonteCarloChildren<M> unexpandedChildren;
    List<MonteCarloGameNode<M, P>> expandedChildren;

    final MonteCarloStatistics statistics;

    final ForkObserver<M> expandObserver;

    private boolean isSearching = false;
    private volatile boolean stopRequested = false;

    public MonteCarloGameNode(M parentMove, P position, IMonteCarloChildren<M> children, IPositionEvaluator<M, P> positionEvaluator,
            MoveListFactory<M> moveListFactory, int numSimulations,
            int maxDepth) {
        this(null, parentMove, position, children, positionEvaluator, moveListFactory, numSimulations, maxDepth, null);
    }

    public MonteCarloGameNode(MonteCarloGameNode<M, P> parentNode, M parentMove, P position, IMonteCarloChildren<M> children,
            IPositionEvaluator<M, P> positionEvaluator,
            MoveListFactory<M> moveListFactory, int numSimulations, int maxDepth, ForkObserver<M> expandObserver) {
        this.parentNode = parentNode;
        this.parentMove = parentMove;
        this.position = position;
        this.positionEvaluator = positionEvaluator;
        this.moveListFactory = moveListFactory;
        this.numSimulations = numSimulations;
        this.maxDepth = maxDepth;
        this.expandObserver = expandObserver;
        moveListProdiver = new MoveListProvider<>(moveListFactory);
        moveList = moveListFactory.newAnalysisMoveList();
        position.getPossibleMoves(moveList);
        unexpandedChildren = children.createNewWith(moveList.size());
        statistics = new MonteCarloStatistics(position.getCurrentPlayer());
    }

    public void searchRoot(boolean escapeEarly) {
        isSearching = true;
        if (moveList.size() == 0) {
            statistics.setResult(new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, moveList)));
        } else {
            expandedChildren = new ArrayList<>(moveList.size());
            if (unexpandedChildren.initUnexpanded(this)) {
                do {
                    search();
                    if (escapeEarly && statistics.isDecided && statistics.numWon > 0) {
                        break;
                    } else if (statistics.isDecided) {
                        boolean allDecided = true;
                        int i = 0;
                        do {
                            MonteCarloStatistics childStatistics = expandedChildren.get(i++).statistics;
                            allDecided = allDecided && childStatistics.isDecided;
                        } while (allDecided && i < expandedChildren.size());
                        if (allDecided) {
                            break;
                        }
                    }
                } while (!stopRequested);
            } else {
                updateStatistics();
            }
        }
        synchronized (this) {
            isSearching = false;
            notify();
        }
    }

    private void search() {
        MonteCarloGameNode<M, P> nodeToExpand = this;
        while (nodeToExpand.unexpandedChildren.getNumUnexpanded() == 0) {
            nodeToExpand = nodeToExpand.select();
        }
        MonteCarloGameNode<M, P> nodeToSimulate = nodeToExpand.expand();
        if (nodeToSimulate == null) {
            nodeToExpand.updateStatistics();
            nodeToExpand.backPropagate();
        } else if (nodeToSimulate.moveList.size() == 0) {
            nodeToSimulate.statistics
                    .setResult(new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, nodeToSimulate.moveList)));
            setDecided();
            nodeToSimulate.backPropagate();
        } else {
            MonteCarloStatistics result = nodeToSimulate.simulate();
            nodeToSimulate.statistics.updateWith(result);
            nodeToSimulate.backPropagate();
        }
    }

    private MonteCarloGameNode<M, P> select() {
        MonteCarloGameNode<M, P> bestChild = null;
        double bestExpectedValue = MonteCarloStatistics.LOSS;
        int i = 0;
        do {
            MonteCarloGameNode<M, P> child = expandedChildren.get(i);
            if (child.statistics.isDecided) {
                continue;
            }
            double meanValue = statistics.player == child.statistics.player ? child.statistics.getMeanValue() : -child.statistics.getMeanValue();
            double childExpectedValue = meanValue + child.statistics.getUncertainty(statistics.numUncertain);
            if (bestChild == null || childExpectedValue > bestExpectedValue) {
                bestExpectedValue = childExpectedValue;
                bestChild = child;
            }
        } while (++i < expandedChildren.size());
        position.makeMove(bestChild.parentMove);
        return bestChild;
    }

    private MonteCarloGameNode<M, P> expand() {
        if (expandedChildren == null) {
            expandedChildren = new ArrayList<>(moveList.size());
            if (!unexpandedChildren.initUnexpanded(this)) {
                return null;
            }
        }

        M move = moveList.get(unexpandedChildren.getNextNodeIndex());

        if (expandObserver != null) {
            expandObserver.notifyForked(move);
        }

        position.makeMove(move);
        MonteCarloGameNode<M, P> childNode = new MonteCarloGameNode<>(this, move, position, unexpandedChildren, positionEvaluator, moveListFactory,
                numSimulations, maxDepth, expandObserver);
        expandedChildren.add(childNode);
        return childNode;
    }

    private MonteCarloStatistics simulate() {
        MonteCarloStatistics result = new MonteCarloStatistics(statistics.player);

        MoveList<M> possibleMoves = moveListProdiver.getMoveList(0);
        position.getPossibleMoves(possibleMoves);
        if (possibleMoves.size() == 0) {
            return new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, possibleMoves));
        }

        int simulation = 0;
        List<M> movesMade = new ArrayList<>();
        do {
            int i = 0;
            while (possibleMoves.size() > 0 && i <= maxDepth) {
                M move = possibleMoves.get(unexpandedChildren.getNextMoveIndex(possibleMoves));
                position.makeMove(move);
                movesMade.add(move);
                possibleMoves.clear();
                position.getPossibleMoves(possibleMoves);
                ++i;
            }

            if (i == maxDepth) {
                result.addScore(AnalysisResult.DRAW);
            } else {
                result.addScore(statistics.player == position.getCurrentPlayer() ? positionEvaluator.evaluate(position, possibleMoves)
                        : -positionEvaluator.evaluate(position, possibleMoves));
            }

            while (--i >= 0) {
                position.unmakeMove(movesMade.get(i));
            }

            movesMade.clear();
            possibleMoves.clear();
            position.getPossibleMoves(possibleMoves);
        } while (!stopRequested && ++simulation < numSimulations);

        return result;
    }

    private void backPropagate() {
        position.unmakeMove(parentMove);
        MonteCarloGameNode<M, P> parent = parentNode;
        while (parent != null) {
            parent.updateStatistics();
            if (parent.parentMove != null) {
                position.unmakeMove(parent.parentMove);
            }
            parent = parent.parentNode;
        }
    }

    private void updateStatistics() {
        boolean allChildrenDecided = unexpandedChildren.getNumUnexpanded() == 0;
        boolean won = false;
        MonteCarloStatistics newStatistics = new MonteCarloStatistics(statistics.player);

        int i = 0;
        do {
            MonteCarloGameNode<M, P> child = expandedChildren.get(i);
            newStatistics.updateWith(child.statistics);
            won = won || child.statistics.isWin(statistics.player);
            allChildrenDecided = allChildrenDecided && child.statistics.isDecided;
        } while (++i < expandedChildren.size());

        statistics.setResult(newStatistics);

        if (allChildrenDecided || won) {
            setDecided();
        }
    }

    void setDecided() {
        statistics.setDecided();
        if (parentNode != null) {
            unexpandedChildren.setNumUnexpanded(0);
            expandedChildren = null;
        }
    }

    public synchronized void stopSearch() {
        stopRequested = true;
        while (isSearching) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String toString() {
        return parentMove.toString();
    }
}
