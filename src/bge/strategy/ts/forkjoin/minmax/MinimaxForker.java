package bge.strategy.ts.forkjoin.minmax;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import bge.analysis.AnalysisResult;
import bge.analysis.MoveWithScore;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.forkjoin.ForkJoinNode;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory;
import bge.strategy.ts.forkjoin.IForkable;
import bge.strategy.ts.forkjoin.IJoin;
import gt.util.Pair;

public class MinimaxForker {
    public static <M, P extends IPosition<M>> List<ForkJoinNode<M>> fork(ForkableTreeSearchFactory<M, P> forkableFactory, P position,
            MoveList<M> unanalyzedMoves, MoveListFactory<M> moveListFactory, int plies, IJoin<M> parentJoin, M parentMove, AnalysisResult<M> partialResult) {
        int expectedResults = unanalyzedMoves.size();
        MinimaxJoin<M> join = new MinimaxJoin<>(parentJoin, parentMove, partialResult, expectedResults);
        List<ForkJoinNode<M>> forks = new ArrayList<>();
        int i = 0;
        do {
            M move = unanalyzedMoves.get(i);
            position.makeMove(move);
            MoveList<M> subMoves = moveListFactory.newAnalysisMoveList();
            position.getPossibleMoves(subMoves);
            IForkable<M> fork = forkableFactory.createNew(position, subMoves, moveListFactory, plies - 1);
            forks.add(new ForkJoinNode<>(move, fork, join));
            position.unmakeMove(move);
            ++i;
        } while (i < unanalyzedMoves.size());
        return forks;
    }

    public static <M> void combineWithPartial(AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults) { // TODO this can go in analysis result?
        for (Entry<M, AnalysisResult<M>> moveWithResult : movesWithResults.entrySet()) {
            M move = moveWithResult.getKey();
            AnalysisResult<M> result = moveWithResult.getValue();
            MoveWithScore<M> moveWithScore = result.getBestMove(partialResult.getPlayer());
            if (moveWithScore == null) {
                continue;
            }
            partialResult.addMoveWithScore(move, moveWithScore.score, result.isSearchComplete());
        }
    }

    private static class MinimaxJoin<M> implements IJoin<M> {
        private final IJoin<M> parentJoin;
        private final M parentMove;

        private final AnalysisResult<M> partialResult;
        private final int expectedResults;

        private final Map<M, AnalysisResult<M>> movesWithResults;
        private final AnalysisResult<M> remainderResult;

        private final AtomicBoolean parentAwaitingJoin = new AtomicBoolean(true);

        public MinimaxJoin(IJoin<M> parentJoin, M parentMove, AnalysisResult<M> partialResult, int expectedResults) {
            this.parentJoin = parentJoin;
            this.parentMove = parentMove;
            this.partialResult = partialResult;
            this.expectedResults = expectedResults;
            movesWithResults = new LinkedHashMap<>();
            remainderResult = new AnalysisResult<M>(partialResult.getPlayer()).mergeWith(partialResult);
        }

        @Override
        public synchronized void join(boolean searchCanceled, Pair<M, AnalysisResult<M>> moveWithResult) {
            if (parentAwaitingJoin.get()) {
                joinSynchronized(searchCanceled, moveWithResult);
            }
        }

        private synchronized void joinSynchronized(boolean searchCanceled, Pair<M, AnalysisResult<M>> moveWithResult) {
            movesWithResults.put(moveWithResult.getFirst(), moveWithResult.getSecond());
            MoveWithScore<M> bestMove = moveWithResult.getSecond().getBestMove(remainderResult.getPlayer());
            if (bestMove != null) {
                remainderResult.addMoveWithScore(moveWithResult.getFirst(), bestMove.score);
            }
            if (searchCanceled || !moveWithResult.getSecond().isSearchComplete()) {
                if (parentAwaitingJoin.compareAndSet(true, false)) {
                    MinimaxForker.combineWithPartial(partialResult, movesWithResults);
                    parentJoin.join(searchCanceled, Pair.valueOf(parentMove, partialResult));
                }
            } else if (movesWithResults.size() == expectedResults) {
                partialResult.searchCompleted();
                if (parentAwaitingJoin.compareAndSet(true, false)) {
                    MinimaxForker.combineWithPartial(partialResult, movesWithResults);
                    parentJoin.join(searchCanceled, Pair.valueOf(parentMove, partialResult));
                }
            }
        }
    }
}
