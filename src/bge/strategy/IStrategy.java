package bge.strategy;

import bge.igame.IPosition;

public interface IStrategy<M> {
    M getMove(IPosition<M> position);
}
