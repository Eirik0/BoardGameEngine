package bge.game.forkjoinexample;

import bge.gui.gamestate.IGameRenderer;
import bge.igame.IGame;
import gt.component.IMouseTracker;
import gt.gameentity.IGameImageDrawer;

public class ForkJoinExampleGame implements IGame<ForkJoinExampleNode, ForkJoinExampleTree> {
    public static final String NAME = "Fork Join Example";

    public static final int MAX_MOVES = ForkJoinExampleTree.BRANCHING_FACTOR;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getNumberOfPlayers() {
        return 1;
    }

    @Override
    public int getMaxMoves() {
        return MAX_MOVES;
    }

    @Override
    public ForkJoinExampleTree newInitialPosition() {
        ForkJoinExampleTree tree = new ForkJoinExampleTree(ForkJoinExampleTree.DEPTH, ForkJoinExampleTree.BRANCHING_FACTOR);
        ForkJoinExampleThreadTracker.init(tree);
        return tree;
    }

    @Override
    public IGameRenderer<ForkJoinExampleNode, ForkJoinExampleTree> newGameRenderer(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        return new ForkJoinExampleGameRenderer(mouseTracker);
    }
}
