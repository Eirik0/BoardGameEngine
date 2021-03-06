package bge.strategy.ts.forkjoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import bge.analysis.AnalysisResult;
import bge.analysis.MoveWithScore;

public class ForkJoinRoot<M> {
    private final List<ForkJoinNode<M>> branches;

    private final AnalysisResult<M> partialResult;

    public ForkJoinRoot() {
        branches = Collections.emptyList();
        partialResult = new AnalysisResult<>(1);
    }

    public ForkJoinRoot(ForkJoinNode<M> rootNode, int player) {
        partialResult = new AnalysisResult<>(player);
        // Always fork once so we can keep track of the searches in progress
        if (rootNode.isForkable()) {
            branches = rootNode.fork();
        } else {
            branches = new ArrayList<>();
            branches.add(rootNode);
        }
    }

    public List<ForkJoinNode<M>> getBranches() {
        return branches;
    }

    public AnalysisResult<M> getPartialResult() {
        updatePartialResult();
        AnalysisResult<M> partialResultCopy = new AnalysisResult<>(partialResult.getPlayer());
        for (MoveWithScore<M> moveWithScore : partialResult.getMovesWithScore()) {
            partialResultCopy.addMoveWithScore(moveWithScore);
        }
        return partialResultCopy;
    }

    private synchronized void updatePartialResult() {
        Iterator<ForkJoinNode<M>> branchIterator = branches.iterator();
        while (branchIterator.hasNext()) {
            ForkJoinNode<M> branch = branchIterator.next();
            AnalysisResult<M> branchResult = branch.getResult();
            if (branchResult != null && branchResult.isSearchComplete()) {
                MoveWithScore<M> bestMove = branchResult.getBestMove(partialResult.getPlayer());
                if (bestMove == null) {
                    continue;
                }
                partialResult.addMoveWithScore(new MoveWithScore<>(branch.getParentMove(), bestMove.score));
                branchIterator.remove();
            }
        }
    }
}
