package bge.strategy.ts.forkjoin;

import org.junit.jupiter.api.Test;

import bge.game.chess.ChessGame;
import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionEvaluator;
import bge.game.chess.move.IChessMove;
import bge.igame.MoveListFactory;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.forkjoin.alphabeta.AlphaBetaQPositionEvaluator;
import bge.strategy.ts.forkjoin.alphabeta.ForkableAlphaBetaFactory;

public class AlphaBetaQPositionEvaluatorTest {
    @Test
    public void testAlphaBetaQNegaMax() {
        MoveListFactory<IChessMove> moveListFactory = new MoveListFactory<>(ChessGame.MAX_MOVES);

        ForkableAlphaBetaFactory<IChessMove, ChessPosition> strat1 = new ForkableAlphaBetaFactory<>(
                new AlphaBetaQTestPositionEvaluator<>(new ChessPositionEvaluator(), new MoveListProvider<>(moveListFactory)));
        ForkableAlphaBetaFactory<IChessMove, ChessPosition> strat2 = new ForkableAlphaBetaFactory<>(
                new AlphaBetaQPositionEvaluator<>(new ChessPositionEvaluator(), new MoveListProvider<>(moveListFactory)));

        ForkableAlphaBetaTest.compareStrategies(new ChessPosition(), moveListFactory, strat1, strat2, 4, 5);
    }
}
