package bge.strategy.ts.forkjoin;

import org.junit.jupiter.api.Test;

import bge.analysis.IPositionEvaluator;
import bge.game.chess.ChessGame;
import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionEvaluator;
import bge.game.chess.move.IChessMove;
import bge.igame.IPosition;
import bge.igame.MoveListFactory;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory.ForkableType;

public class AlphaBetaQPositionEvaluatorTest {
    @Test
    public void testAlphaBetaQNegaMax() {
        MoveListFactory<IChessMove> moveListFactory = new MoveListFactory<>(ChessGame.MAX_MOVES);

        ForkableTreeSearchFactory<IChessMove, ChessPosition> strat1 = new ForkableAlphaBetaQTestFactory<>(
                ForkableType.ALPHA_BETA_Q, new ChessPositionEvaluator(), moveListFactory);
        ForkableTreeSearchFactory<IChessMove, ChessPosition> strat2 = new ForkableTreeSearchFactory<>(
                ForkableType.ALPHA_BETA_Q, new ChessPositionEvaluator(), moveListFactory);

        ForkableAlphaBetaTest.compareStrategies(new ChessPosition(), moveListFactory, strat1, strat2, 4, 5);
    }

    private static class ForkableAlphaBetaQTestFactory<M, P extends IPosition<M>> extends ForkableTreeSearchFactory<M, P> {
        public ForkableAlphaBetaQTestFactory(ForkableType forkableType, IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory) {
            super(forkableType, positionEvaluator, moveListFactory);
        }

        @Override
        public IDepthBasedPositionEvaluator<M, P> newStrategy() {
            return new AlphaBetaQTestPositionEvaluator<>(positionEvaluator, moveListFactory);
        }
    }
}
