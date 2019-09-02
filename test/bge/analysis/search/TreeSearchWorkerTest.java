package bge.analysis.search;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bge.analysis.AnalysisResult;
import bge.analysis.strategy.MinimaxStrategy;
import bge.analysis.strategy.MoveListProvider;
import bge.game.value.TestGameEvaluator;
import bge.game.value.TestGameNode;
import bge.game.value.TestGamePosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import gt.async.ThreadWorker;

public class TreeSearchWorkerTest {
    @Test
    public void testJoinThread() {
        ThreadWorker worker = new ThreadWorker("test", finishedWorker -> {
        });
        worker.joinThread();
    }

    private static GameTreeSearch<TestGameNode> newGameTreeSearch(IGameTreeSearchJoin<TestGameNode> join) {
        MoveListFactory<TestGameNode> moveListFactory = new MoveListFactory<>(2);
        TestGamePosition position = TestGamePosition.createTestPosition();
        MoveList<TestGameNode> moveList = moveListFactory.newAnalysisMoveList();
        position.getPossibleMoves(moveList);
        MinimaxStrategy<TestGameNode, TestGamePosition> strategy = new MinimaxStrategy<>(new TestGameEvaluator(), new MoveListProvider<>(moveListFactory));
        return new GameTreeSearch<>(strategy.newForkableSearch(null, position, moveList, moveListFactory, 0), join);
    }

    @Test
    public void testDoWork() {
        ResultTransfer<TestGameNode> resultTransfer = new ResultTransfer<>();
        ThreadWorker worker = new ThreadWorker("test", finishedWorker -> {
        });
        worker.workOn(newGameTreeSearch((canceled, moveWithResult) -> {
            resultTransfer.putResult(moveWithResult.result);
        })::search);
        resultTransfer.awaitResult();
        worker.joinThread();
    }

    @Test
    public void testRescheduleWorkOnComplete() throws InterruptedException {
        List<AnalysisResult<TestGameNode>> resultList = new ArrayList<>();
        GameTreeSearch<TestGameNode> treeSearch = newGameTreeSearch((canceled, moveWithResult) -> {
            synchronized (this) {
                resultList.add(moveWithResult.result);
                notify();
            }
        });
        ThreadWorker worker = new ThreadWorker("test", finishedWorker -> {
            synchronized (this) {
                if (resultList.size() < 2) {
                    finishedWorker.workOn(treeSearch::search);
                }
            }
        });
        worker.workOn(treeSearch::search);
        synchronized (this) {
            while (resultList.size() < 2) {
                wait();
            }
        }
        worker.joinThread();
    }
}
