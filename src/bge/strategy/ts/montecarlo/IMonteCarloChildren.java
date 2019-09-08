package bge.strategy.ts.montecarlo;

import bge.igame.IPosition;

public interface IMonteCarloChildren<M> {
    IMonteCarloChildren<M> createNewWith(int numUnexpanded);

    <P extends IPosition<M>> boolean initUnexpanded(MonteCarloGameNode<M, P> parentNode);

    int getNumUnexpanded();

    void setNumUnexpanded(int numUnexpanded);

    int getNextNodeIndex();
}
