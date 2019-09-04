package bge.strategy.ts.forkjoin;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bge.analysis.AnalysisResult;
import bge.game.value.TestGameEvaluator;
import bge.game.value.TestGameNode;
import bge.game.value.TestGamePosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.forkjoin.minmax.ForkableMinimaxFactory;
import bge.strategy.ts.forkjoin.minmax.MinimaxPositionEvaluator;
import gt.async.ThreadWorker;

public class TreeSearchWorkerTest {
    @Test
    public void testJoinThread() {
        ThreadWorker worker = new ThreadWorker("test");
        worker.joinThread();
    }

    private static ForkJoinNode<TestGameNode> newGameTreeSearch(IJoin<TestGameNode> join) {
        MoveListFactory<TestGameNode> moveListFactory = new MoveListFactory<>(2);
        TestGamePosition position = TestGamePosition.createTestPosition();
        MoveList<TestGameNode> moveList = moveListFactory.newAnalysisMoveList();
        position.getPossibleMoves(moveList);
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> strategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
                new MoveListProvider<>(moveListFactory));
        return new ForkJoinNode<>(null, new ForkableMinimaxFactory<>(strategy).createNew(position, moveList, moveListFactory, 0), join);
    }

    @Test
    public void testDoWork() {
        ResultTransfer<TestGameNode> resultTransfer = new ResultTransfer<>();
        ThreadWorker worker = new ThreadWorker("test");
        worker.workOn(newGameTreeSearch((canceled, moveWithResult) -> {
            resultTransfer.putResult(moveWithResult.getSecond());
        })::search);
        resultTransfer.awaitResult();
        worker.joinThread();
    }

    @Test
    public void testRescheduleWorkOnComplete() throws InterruptedException {
        List<AnalysisResult<TestGameNode>> resultList = new ArrayList<>();
        ThreadWorker worker = new ThreadWorker("test", finishedWorker -> {
            synchronized (this) {
                if (resultList.size() < 2) {
                    finishedWorker.workOn(newGameTreeSearch((canceled, moveWithResult) -> {
                        synchronized (this) {
                            resultList.add(moveWithResult.getSecond());
                            notify();
                        }
                    })::search);
                }
            }
        });
        worker.workOn(newGameTreeSearch((canceled, moveWithResult) -> {
            synchronized (this) {
                resultList.add(moveWithResult.getSecond());
                notify();
            }
        })::search);
        synchronized (this) {
            while (resultList.size() < 2) {
                wait();
            }
        }
        worker.joinThread();
    }
}
