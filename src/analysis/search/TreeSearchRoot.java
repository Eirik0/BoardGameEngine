package analysis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import analysis.AnalysisResult;
import analysis.AnalyzedMove;
import analysis.MoveAnalysis;
import game.IPosition;

public class TreeSearchRoot<M, P extends IPosition<M>> {
    private final AnalysisResult<M> partialResult;

    private final List<GameTreeSearch<M, P>> branches;

    public TreeSearchRoot() {
        branches = Collections.emptyList();
        partialResult = new AnalysisResult<>(1);
    }

    public TreeSearchRoot(GameTreeSearch<M, P> rootTreeSearch, int player) {
        partialResult = new AnalysisResult<>(player);
        // Always fork once so we can keep track of the searches in progress
        if (rootTreeSearch.isForkable()) {
            branches = rootTreeSearch.fork();
        } else {
            branches = new ArrayList<>();
            branches.add(rootTreeSearch);
        }
    }

    public List<GameTreeSearch<M, P>> getBranches() {
        return branches;
    }

    public AnalysisResult<M> getPartialResult() {
        updatePartialResult();
        AnalysisResult<M> partialResultCopy = new AnalysisResult<>(partialResult.getPlayer());
        for (Entry<M, MoveAnalysis> moveWithScore : partialResult.getMovesWithScore().entrySet()) {
            partialResultCopy.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue());
        }
        return partialResultCopy;
    }

    private synchronized void updatePartialResult() {
        Iterator<GameTreeSearch<M, P>> branchIterator = branches.iterator();
        while (branchIterator.hasNext()) {
            GameTreeSearch<M, P> branch = branchIterator.next();
            AnalysisResult<M> branchResult = branch.getResult();
            if (branchResult != null && branchResult.isSearchComplete()) {
                AnalyzedMove<M> bestMove = branchResult.getBestMove(partialResult.getPlayer());
                if (bestMove == null) {
                    continue;
                }
                partialResult.addMoveWithScore(branch.getParentMove(), bestMove.analysis);
                branchIterator.remove();
            }
        }
    }
}
