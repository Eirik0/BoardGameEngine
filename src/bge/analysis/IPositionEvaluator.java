package bge.analysis;

import bge.igame.IPosition;
import bge.igame.MoveList;

public interface IPositionEvaluator<M, P extends IPosition<M>> {
    double evaluate(P position, MoveList<M> possibleMoves);
}
