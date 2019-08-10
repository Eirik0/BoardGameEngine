package bge.analysis;

import bge.game.IPosition;
import bge.game.MoveList;

public interface IPositionEvaluator<M, P extends IPosition<M>> {
    double evaluate(P position, MoveList<M> possibleMoves);
}
