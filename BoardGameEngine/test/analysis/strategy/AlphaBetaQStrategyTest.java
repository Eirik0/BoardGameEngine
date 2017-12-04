package analysis.strategy;

import org.junit.Test;

import game.MoveListFactory;
import game.chess.ChessGame;
import game.chess.ChessPosition;
import game.chess.ChessPositionEvaluator;
import game.chess.move.IChessMove;

public class AlphaBetaQStrategyTest {
	@Test
	public void testAlphaBetaQNegaMax() {
		MoveListFactory<IChessMove> moveListFactory = new MoveListFactory<>(ChessGame.MAX_MOVES);

		AlphaBetaQTestStrategy<IChessMove, ChessPosition> minmaxStrategy = new AlphaBetaQTestStrategy<>(moveListFactory, new ChessPositionEvaluator());
		AlphaBetaQStrategy<IChessMove, ChessPosition> alphabetaStrategy = new AlphaBetaQStrategy<>(moveListFactory, new ChessPositionEvaluator());

		AlphaBetaStrategyTest.compareStrategies(new ChessPosition(), moveListFactory, minmaxStrategy, alphabetaStrategy, 4, 5);
	}
}
