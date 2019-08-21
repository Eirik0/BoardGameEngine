package bge.analysis.montecarlo;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import bge.analysis.IPositionEvaluator;
import bge.game.IPosition;
import bge.game.MoveListFactory;
import bge.game.chess.ChessConstants;
import bge.game.chess.ChessGame;
import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionEvaluator;
import bge.game.ultimatetictactoe.UltimateTicTacToeGame;
import bge.game.ultimatetictactoe.UltimateTicTacToePosition;
import bge.game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;
import bge.game.ultimatetictactoe.UltimateTicTacToeUtilities;

public class MonteCarloGameNodeTest {
    @Test
    public void testMonteCarlo_UTTT() {
        doSearch(new UltimateTicTacToePosition(), new UltimateTicTacToePositionEvaluator(), UltimateTicTacToeGame.MAX_MOVES,
                UltimateTicTacToeUtilities.MAX_REASONABLE_DEPTH);
    }

    @Test
    public void testMonteCarlo_Chess() {
        doSearch(new ChessPosition(), new ChessPositionEvaluator(), ChessGame.MAX_MOVES, ChessConstants.MAX_REASONABLE_DEPTH);
    }

    private static <M, P extends IPosition<M>> void doSearch(P position, IPositionEvaluator<M, P> positionEvaluator, int maxMoves, int maxDepth) {
        MonteCarloTreeSearcher<M, P> treeSearcher = new MonteCarloTreeSearcher<>(new RandomMonteCarloChildren<>(0), positionEvaluator,
                new MoveListFactory<>(maxMoves), 25, maxDepth);
        treeSearcher.searchForever(position, false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        treeSearcher.stopSearch(true);

        MonteCarloGameNode<M, P> root = treeSearcher.getRoot();

        int totalPositionsEvaluated = root.statistics.getTotalNodesEvaluated();
        System.out.println("Positions evaluated: " + totalPositionsEvaluated);

        ArrayList<MonteCarloGameNode<M, P>> sortedChildren = new ArrayList<>(root.expandedChildren);
        Collections.sort(sortedChildren,
                (c1, c2) -> Double.compare(c2.statistics.getExpectedValue(totalPositionsEvaluated), c1.statistics.getExpectedValue(totalPositionsEvaluated)));

        for (MonteCarloGameNode<M, P> child : sortedChildren) {
            System.out.println(child.parentMove + " -> " + child.statistics.toString(totalPositionsEvaluated));
        }
    }
}
