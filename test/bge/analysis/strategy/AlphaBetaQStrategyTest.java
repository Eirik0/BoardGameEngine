package bge.analysis.strategy;

import org.junit.Test;

import bge.game.MoveListFactory;
import bge.game.chess.ChessGame;
import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionEvaluator;
import bge.game.chess.move.IChessMove;

public class AlphaBetaQStrategyTest {
    @Test
    public void testAlphaBetaQNegaMax() {
        MoveListFactory<IChessMove> moveListFactory = new MoveListFactory<>(ChessGame.MAX_MOVES);

        AlphaBetaQTestStrategy<IChessMove, ChessPosition> minmaxStrategy = new AlphaBetaQTestStrategy<>(new ChessPositionEvaluator(),
                new MoveListProvider<>(moveListFactory));
        AlphaBetaQStrategy<IChessMove, ChessPosition> alphabetaStrategy = new AlphaBetaQStrategy<>(new ChessPositionEvaluator(),
                new MoveListProvider<>(moveListFactory));

        AlphaBetaStrategyTest.compareStrategies(new ChessPosition(), moveListFactory, minmaxStrategy, alphabetaStrategy, 4, 5);
    }
}
