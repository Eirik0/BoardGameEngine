package bge.analysis.montecarlo;

import bge.game.IPosition;
import bge.game.MoveList;

public interface IMonteCarloChildren<M> {
    public IMonteCarloChildren<M> createNewWith(int numUnexpanded);

    public <P extends IPosition<M>> boolean initUnexpanded(MonteCarloGameNode<M, P> parentNode);

    public int getNumUnexpanded();

    public void setNumUnexpanded(int numUnexpanded);

    public int getNextNodeIndex();

    public int getNextMoveIndex(MoveList<M> moveList);

}
